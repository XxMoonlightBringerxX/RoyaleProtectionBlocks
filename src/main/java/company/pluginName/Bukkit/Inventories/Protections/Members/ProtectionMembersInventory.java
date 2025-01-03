package company.pluginName.Bukkit.Inventories.Protections.Members;

import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Bukkit.Inventories.Shared.SearchPlayerInventory;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.PandaCachedPlayersService;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.Objects.PandaCachedPlayer;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemGenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Item;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Paged.PagedChestInventoryData;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberAddRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberRemoveRequestInput;

@Inventory("protections_members")
public class ProtectionMembersInventory extends PagedChestInventoryObject<UUID> {

	private static final String ENTITY_DELETELORELINE_PATH = "Entity.Delete-lore-line";

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	@PandaInject
	private static PandaCachedPlayersService cachedPlayersService;

	private Protection protection;

	public ProtectionMembersInventory(Player player, Protection protection) {
		super(player);

		this.protection = protection;
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).setReplacements(new Replacement("{protection}",
				() -> protection.getDisplayName() != null ? protection.getDisplayName() : protection.getProtectionId()))
				.process().toString();
	}

	@Override
	protected List<UUID> getEntityList() {
		return protection.getMembers();
	}

	@ItemGenerator("Search-button")
	private ItemStack generateSearchButton(Item item) {
		return ProtectionUtilities.canAddMember(protection, getPlayer()) ? item.getItems().get(Item.DISPLAYITEM_KEY)
				: null;
	}

	@ItemExecutor("Close-button")
	private void executeCloseButton() {
		goToPreviousInventory();
	}

	@ItemExecutor("Search-button")
	private void executeSearchButton() {
		closeInventory();
		new SearchPlayerInventory(getPlayer(), player -> {
			if (player != null) {
				try {
					playerInteractionsService.protectionMemberAddRequest(
							ProtectionMemberAddRequestInput.inst(getPlayer(), protection, player.getUuid()));
					updateEntityList();
				} catch (RoyaleProtectionBlocksException e) {
					e.sendError(getPlayer());
				}
				openInventory();
			} else {
				openInventory();
			}
		}, (player) -> !protection.isOwner(player.getUuid()) && !protection.isMember(player.getUuid())).openInventory();
	}

	@Override
	protected ItemStack generateEntityItem(UUID entity) {
		PandaCachedPlayer pl = cachedPlayersService.getCachedPlayer(entity);

		final boolean canRemove = ProtectionUtilities.canRemoveMember(protection, getPlayer());

		ItemBuilder builder = ItemBuilder.inst().setMaterial(Material.PLAYER_HEAD)
				.fromMap(getChestInventoryData().getCustomFields(), PagedChestInventoryData.ENTITY_PATH)
				.setReplacements(new Replacement("{player}", () -> pl.getName()));

		List<String> lore = builder.getLore();
		if (canRemove) {
			lore.add(" ");
			lore.add(getChestInventoryData().getCustomFields().get(ENTITY_DELETELORELINE_PATH).toString());
		}
		builder.setLore(lore);

		return processPlayerHead(builder, entity);
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, UUID entity) {
		final boolean canRemove = ProtectionUtilities.canRemoveMember(protection, getPlayer());

		if (canRemove) {
			new ConfirmationInventory(getPlayer(), () -> {
				try {
					playerInteractionsService.protectionMemberRemoveRequest(
							ProtectionMemberRemoveRequestInput.inst(getPlayer(), protection, entity));
					updateEntityList();
				} catch (RoyaleProtectionBlocksException ex) {
					ex.sendError(getPlayer());
				}

				if (!entity.equals(getPlayer().getUniqueId())) {
					openInventory();
				}
			}).setPreviousInventory(null).openInventory();
		}
	}

}

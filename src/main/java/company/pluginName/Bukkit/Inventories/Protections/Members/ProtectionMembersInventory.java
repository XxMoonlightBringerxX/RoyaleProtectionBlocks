package company.pluginName.Bukkit.Inventories.Protections.Members;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Bukkit.Inventories.Shared.SearchPlayerInventory;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemGenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Item;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Paged.PagedChestInventoryData;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Paged.PagedChestInventoryObject;

@Inventory("protections_members")
public class ProtectionMembersInventory extends PagedChestInventoryObject<UUID> {

	private static final String ENTITY_DELETELORELINE_PATH = "Entity.Delete-lore-line";

	private Protection protection;

	public ProtectionMembersInventory(Player player, Protection protection) {
		super(player);

		this.protection = protection;
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).setReplacements(new Replacement("{protection}",
				() -> protection.getDisplayName() != null ? protection.getDisplayName() : protection.getRegionId()))
				.process().toString();
	}

	@Override
	protected List<UUID> getEntityList() {
		return new ArrayList<>(protection.getMembers().list());
	}

	@ItemGenerator("Search-button")
	private ItemStack generateSearchButton(Item item) {
		return ProtectionUtilities.canAddMember(protection, getPlayer()) ? item.getItems().get(Item.DISPLAYITEM_KEY)
				: null;
	}

	@ItemExecutor("Close-button")
	private void executeCloseButton() {
		goToPreviousHolder();
	}

	@ItemExecutor("Search-button")
	private void executeSearchButton() {
		closeInventory();
		new SearchPlayerInventory(getPlayer(), player -> {
			if (player != null) {
				try {
					protection.getMembers().add(getPlayer(), player.getUniqueId());
				} catch (RoyaleProtectionBlocksException e1) {
					e1.sendError(getPlayer());
				}
				openInventory();
			} else {
				openInventory();
			}
		}).openInventory();
	}

	@Override
	protected ItemStack generateEntityItem(UUID entity) {
		OfflinePlayer pl = OfflinePlayerUtilities.getOfflinePlayer(entity);

		final boolean canRemove = ProtectionUtilities.canRemoveMember(protection, getPlayer());

		ItemBuilder builder = ItemBuilder.inst().setMaterial(Material.PLAYER_HEAD)
				.fromMap(getChestInventoryData().getCustomFields(), PagedChestInventoryData.ENTITY_PATH)
				.setReplacements(new Replacement("{player}", () -> pl.getName()));

		List<String> lore = new ArrayList<>(Arrays.asList(builder.getLore()));
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
					protection.getMembers().remove(getPlayer(), entity);
				} catch (RoyaleProtectionBlocksException e1) {
					e1.sendError(getPlayer());
				}

				if (!entity.equals(getPlayer().getUniqueId())) {
					openInventory();
				}
			}).setPreviousInventory(null).openInventory();
		}
	}

}

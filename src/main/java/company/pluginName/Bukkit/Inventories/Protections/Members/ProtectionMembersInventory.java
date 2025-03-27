package company.pluginName.Bukkit.Inventories.Protections.Members;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Bukkit.Inventories.Shared.SearchPlayerInventory;
import company.pluginName.Modules.FilePckg.Settings;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberAddRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberRemoveRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Owners.ProtectionOwnerAddRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Owners.ProtectionOwnerRemoveRequestInput;

@Inventory("protections_members")
public class ProtectionMembersInventory extends PagedChestInventoryObject<UUID> {

	private static final String ENTITY_PROMOTELORELINE_PATH = "Entity.Promote-lore-line";
	private static final String ENTITY_DEMOTELORELINE_PATH = "Entity.Demote-lore-line";
	private static final String ENTITY_DELETELORELINE_PATH = "Entity.Remove-lore-line";

	@AllArgsConstructor
	@Getter
	private static enum Filter {
		ALL((uuid, protection) -> true), MEMBERS((uuid, protection) -> !protection.isOwner(uuid)),
		OWNERS((uuid, protection) -> protection.isOwner(uuid));

		private BiFunction<UUID, Protection, Boolean> filterFunction;

		public Filter previous() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}

		public Filter next() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}
	}

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	@PandaInject
	private static PandaCachedPlayersService cachedPlayersService;

	private Protection protection;
	private Filter currentFilter = Filter.ALL;

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
		return protection.getMembers().stream()
				.filter(member -> this.currentFilter.getFilterFunction().apply(member, protection))
				.collect(Collectors.toList());
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
				} catch (RoyaleProtectionBlocksException e) {
					e.sendError(getPlayer());
				}
				updateEntityList();
			}
			openInventory();
		}, (player) -> !protection.isMainOwner(player.getUuid()) && !protection.isOwner(player.getUuid())
				&& !protection.isMember(player.getUuid())).openInventory();
	}

	@Override
	protected ItemStack generateEntityItem(UUID entity) {
		PandaCachedPlayer pl = cachedPlayersService.getCachedPlayer(entity);

		final boolean canAddOwner = ProtectionUtilities.canAddOwner(protection, getPlayer());
		final boolean canRemoveOwner = ProtectionUtilities.canRemoveOwner(protection, getPlayer());
		final boolean canRemoveMember = ProtectionUtilities.canRemoveMember(protection, getPlayer());

		ItemBuilder builder = ItemBuilder.inst().setMaterial(Material.PLAYER_HEAD)
				.fromMap(getChestInventoryData().getCustomFields(), PagedChestInventoryData.ENTITY_PATH)
				.setReplacements(new Replacement("{player}", () -> pl != null ? pl.getName() : "???"));

		List<String> extraLore = new ArrayList<>();

		if (!protection.isOwner(entity)) {
			if (Settings.SETTINGS_PROTECTION_ALLOWMEMBERPROMOTIONS.isTrue() && canAddOwner) {
				extraLore.add(getChestInventoryData().getCustomFields().get(ENTITY_PROMOTELORELINE_PATH).toString());
			}
		} else {
			if (canRemoveOwner) {
				extraLore.add(getChestInventoryData().getCustomFields().get(ENTITY_DEMOTELORELINE_PATH).toString());
			}
		}

		if (canRemoveMember) {
			extraLore.add(getChestInventoryData().getCustomFields().get(ENTITY_DELETELORELINE_PATH).toString());
		}

		if (!extraLore.isEmpty()) {
			extraLore.add(0, "&0");
			builder.getLore().addAll(extraLore);
		}

		return processPlayerHead(builder, entity);
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, UUID entity) {
		if (e.isLeftClick()) {
			if (!protection.isOwner(entity)) {
				if (Settings.SETTINGS_PROTECTION_ALLOWMEMBERPROMOTIONS.isTrue()
						&& ProtectionUtilities.canAddOwner(protection, getPlayer())) {
					new ConfirmationInventory(getPlayer(), () -> {
						try {
							playerInteractionsService.protectionOwnerAddRequest(
									ProtectionOwnerAddRequestInput.inst(getPlayer(), protection, entity));
						} catch (RoyaleProtectionBlocksException ex) {
							ex.sendError(getPlayer());
						}

						if (!entity.equals(getPlayer().getUniqueId())) {
							updateEntityList();
							openInventory();
						}
					}, false).openInventory();
				}
			} else {
				if (ProtectionUtilities.canRemoveOwner(protection, getPlayer())) {
					new ConfirmationInventory(getPlayer(), () -> {
						try {
							playerInteractionsService.protectionOwnerRemoveRequest(
									ProtectionOwnerRemoveRequestInput.inst(getPlayer(), protection, entity));
						} catch (RoyaleProtectionBlocksException ex) {
							ex.sendError(getPlayer());
						}

						if (!entity.equals(getPlayer().getUniqueId())) {
							updateEntityList();
							openInventory();
						}
					}, false).openInventory();
				}
			}
		} else if (e.isRightClick()) {
			if (ProtectionUtilities.canRemoveMember(protection, getPlayer())) {
				new ConfirmationInventory(getPlayer(), () -> {
					try {
						playerInteractionsService.protectionMemberRemoveRequest(
								ProtectionMemberRemoveRequestInput.inst(getPlayer(), protection, entity));
					} catch (RoyaleProtectionBlocksException ex) {
						ex.sendError(getPlayer());
					}

					if (!entity.equals(getPlayer().getUniqueId())) {
						updateEntityList();
						openInventory();
					}
				}, false).openInventory();
			}
		}
	}

	@ItemGenerator("All-filter-button")
	private ItemStack generateAllFilterItem(Item item) {
		return this.currentFilter == Filter.ALL ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemGenerator("Members-filter-button")
	private ItemStack generateOwnFilterItem(Item item) {
		return this.currentFilter == Filter.MEMBERS ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemGenerator("Owners-filter-button")
	private ItemStack generateOthersFilterItem(Item item) {
		return this.currentFilter == Filter.OWNERS ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemExecutor("All-filter-button")
	@ItemExecutor("Members-filter-button")
	@ItemExecutor("Owners-filter-button")
	private void executeFilter() {
		this.currentFilter = this.currentFilter.next();
		updateEntityList();
		updateInventory();
	}

}

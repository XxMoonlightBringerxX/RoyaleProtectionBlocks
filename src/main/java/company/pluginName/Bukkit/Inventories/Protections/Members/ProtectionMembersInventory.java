package company.pluginName.Bukkit.Inventories.Protections.Members;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Bukkit.Inventories.Shared.SearchPlayerInventory;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData.InvitationRequirement;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.PandaCachedPlayersService;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.Objects.PandaCachedPlayer;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemGenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.GeneratedItem;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Item;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Paged.PagedChestInventoryData;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberAddRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberInviteRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberRemoveRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Owners.ProtectionOwnerRemoveRequestInput;

@Inventory("protections_members")
public class ProtectionMembersInventory extends PagedChestInventoryObject<UUID> {

	private static final String ENTITY_LEFTCLICKLORELINE_PATH = "Entity.Left-click-lore-line";
	private static final String ENTITY_RIGHTCLICKLORELINE_PATH = "Entity.Right-click-lore-line";
	private static final String ENTITY_PROMOTEACTIONLINE_PATH = "Entity.Promote-action-lore-line";
	private static final String ENTITY_DEMOTEACTIONLINE_PATH = "Entity.Demote-action-lore-line";
	private static final String ENTITY_REMOVEACTIONLINE_PATH = "Entity.Remove-action-lore-line";

	@AllArgsConstructor
	@Getter
	private static enum Filter {
		ALL((uuid, protection) -> true), MEMBERS((uuid, protection) -> !protection.isOwner(uuid)),
		OWNERS((uuid, protection) -> protection.isOwner(uuid));

		private BiFunction<UUID, Protection, Boolean> filterFunction;

		public Filter previous() {
			return values()[(values().length + ordinal() - 1) % values().length];
		}

		public Filter next() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}
	}

	@AllArgsConstructor
	@Getter
	private static enum OptionSet {
		PRIMARY_OPTION_SET((event) -> {
			if (!event.getInventory().protection.isOwner(event.getEntity())) {
				if (Settings.SETTINGS_PROTECTION_ALLOWMEMBERPROMOTIONS.isTrue() && ProtectionUtilities
						.canAddOwner(event.getInventory().protection, event.getInventory().getPlayer())) {
					new ConfirmationInventory(event.getInventory().getPlayer(), () -> {
						try {
							playerInteractionsService.protectionMemberAddRequest(
									ProtectionMemberAddRequestInput.inst(event.getInventory().getPlayer(),
											event.getInventory().protection, event.getEntity(), true));
						} catch (RoyaleProtectionBlocksException ex) {
							ex.sendError(event.getInventory().getPlayer());
						}

						if (!event.getEntity().equals(event.getInventory().getPlayer().getUniqueId())) {
							event.getInventory().updateEntityList();
							event.getInventory().openInventory();
						}
					}, false).openInventory();
				}
			} else {
				if (ProtectionUtilities.canRemoveOwner(event.getInventory().protection,
						event.getInventory().getPlayer())) {
					new ConfirmationInventory(event.getInventory().getPlayer(), () -> {
						try {
							playerInteractionsService.protectionOwnerRemoveRequest(
									ProtectionOwnerRemoveRequestInput.inst(event.getInventory().getPlayer(),
											event.getInventory().protection, event.getEntity()));
						} catch (RoyaleProtectionBlocksException ex) {
							ex.sendError(event.getInventory().getPlayer());
						}

						if (!event.getEntity().equals(event.getInventory().getPlayer().getUniqueId())) {
							event.getInventory().updateEntityList();
							event.getInventory().openInventory();
						}
					}, false).openInventory();
				}
			}
		}), SECONDARY_OPTION_SET((event) -> {
			if (ProtectionUtilities.canRemoveMember(event.getInventory().protection,
					event.getInventory().getPlayer())) {
				new ConfirmationInventory(event.getInventory().getPlayer(), () -> {
					try {
						playerInteractionsService.protectionMemberRemoveRequest(ProtectionMemberRemoveRequestInput.inst(
								event.getInventory().getPlayer(), event.getInventory().protection, event.getEntity()));
					} catch (RoyaleProtectionBlocksException ex) {
						ex.sendError(event.getInventory().getPlayer());
					}

					if (!event.getEntity().equals(event.getInventory().getPlayer().getUniqueId())) {
						event.getInventory().updateEntityList();
						event.getInventory().openInventory();
					}
				}, false).openInventory();
			}

		});

		@Data
		@AllArgsConstructor
		public static class OptionClickEvent {

			private ProtectionMembersInventory inventory;
			private InventoryClickEvent event;
			private UUID entity;

		}

		private Consumer<OptionClickEvent> function;

		public OptionSet previous() {
			return values()[(values().length + ordinal() - 1) % values().length];
		}

		public OptionSet next() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}
	}

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	@PandaInject
	private static PlayerDataService playerDataService;

	@PandaInject
	private static PandaCachedPlayersService cachedPlayersService;

	private Protection protection;
	private Filter currentFilter = Filter.ALL;

	private OptionSet leftClickOptionSet = OptionSet.PRIMARY_OPTION_SET;
	private OptionSet rightClickOptionSet = OptionSet.SECONDARY_OPTION_SET;

	public ProtectionMembersInventory(Player player, Protection protection) {
		super(player);

		this.protection = protection;

		setReplacements(new Replacement[] {
				new Replacement("{protection}", () -> protection.getDisplayName() != null ? protection.getDisplayName()
						: protection.getProtectionId()) });
		setTitleReplacements(getReplacements());
	}

	@Override
	protected List<UUID> getEntityList() {
		return protection.getMembers().stream()
				.filter(member -> this.currentFilter.getFilterFunction().apply(member, protection))
				.collect(Collectors.toList());
	}

	@Override
	protected ItemStack generateEntityItem(UUID entity) {
		PandaCachedPlayer pl = cachedPlayersService.getCachedPlayer(entity);

		final boolean canAddOwner = ProtectionUtilities.canAddOwner(protection, getPlayer());
		final boolean canRemoveOwner = ProtectionUtilities.canRemoveOwner(protection, getPlayer());
		final boolean canRemoveMember = ProtectionUtilities.canRemoveMember(protection, getPlayer());

		List<Replacement> replacements = new ArrayList<>();
		replacements.add(new Replacement("{player}", () -> pl != null ? pl.getName() : "???"));

		ItemBuilder builder = ItemBuilder.inst().setMaterial(Material.PLAYER_HEAD)
				.fromMap(getChestInventoryData().getCustomFields(), PagedChestInventoryData.ENTITY_PATH);

		List<String> extraLore = new ArrayList<>();

		String leftClickAction = this.leftClickOptionSet == OptionSet.PRIMARY_OPTION_SET
				? getPrimaryOptionSetLoreLine(entity, canAddOwner, canRemoveOwner)
				: this.getSecondaryOptionSetLoreLine(entity, canRemoveMember);

		if (leftClickAction != null) {
			extraLore.add(getChestInventoryData().getCustomFields().get(ENTITY_LEFTCLICKLORELINE_PATH).toString());
			replacements.add(new Replacement("{left_click_action}", () -> leftClickAction));
		}

		String rightClickAction = this.leftClickOptionSet == OptionSet.SECONDARY_OPTION_SET
				? getPrimaryOptionSetLoreLine(entity, canAddOwner, canRemoveOwner)
				: this.getSecondaryOptionSetLoreLine(entity, canRemoveMember);

		if (rightClickAction != null) {
			extraLore.add(getChestInventoryData().getCustomFields().get(ENTITY_RIGHTCLICKLORELINE_PATH).toString());
			replacements.add(new Replacement("{right_click_action}", () -> rightClickAction));
		}

		if (!extraLore.isEmpty()) {
			extraLore.add(0, "&0");
			builder.getLore().addAll(extraLore);
		}

		return processPlayerHead(builder.setReplacements(replacements.toArray(new Replacement[replacements.size()])),
				entity);
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, UUID entity) {
		if (e.isLeftClick()) {
			this.leftClickOptionSet.function.accept(new OptionSet.OptionClickEvent(this, e, entity));
		} else if (e.isRightClick()) {
			this.rightClickOptionSet.function.accept(new OptionSet.OptionClickEvent(this, e, entity));
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
					PlayerData playerData = playerDataService.getPlayerData(player.getUuid(), true);

					if (playerData != null) {
						if (playerData.getInvitationRequirement() == InvitationRequirement.ALL_DENIED) {
							throw Exceptions.Protections.Invitations.INVITATIONBLOCKED.generateException();
						} else if (playerData.getInvitationRequirement() == InvitationRequirement.ALL_ACCEPTED) {
							playerInteractionsService.protectionMemberAddRequest(
									ProtectionMemberAddRequestInput.inst(getPlayer(), protection, player.getUuid()));
						} else {
							playerInteractionsService.protectionMemberInviteRequest(
									ProtectionMemberInviteRequestInput.inst(getPlayer(), protection, player.getUuid()));

							MessageTemplate
									.inst(Messages.MESSAGE_PROTECTIONS_INVITATIONS_INVITEDSUCCESSFULLY.applyPrefix())
									.process().sendMessage(getPlayer());
						}
					} else {
						throw Exceptions.Protections.Invitations.UNKNOWN.generateException();
					}

				} catch (RoyaleProtectionBlocksException e) {
					e.sendError(getPlayer());
				}
				updateEntityList();
			}
			openInventory();
		}, (player) -> !protection.isMainOwner(player.getUuid()) && !protection.isOwner(player.getUuid())
				&& !protection.isMember(player.getUuid())).openInventory();
	}

	@ItemExecutor("Alternate-options-button")
	private void executeAlternateOptionsButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		if (e.isLeftClick()) {
			this.leftClickOptionSet = this.leftClickOptionSet.next();
			this.rightClickOptionSet = this.rightClickOptionSet.next();
			this.updateInventory();
		} else if (e.isRightClick()) {
			this.leftClickOptionSet = this.leftClickOptionSet.previous();
			this.rightClickOptionSet = this.rightClickOptionSet.previous();
			this.updateInventory();
		}
	}

	private String getPrimaryOptionSetLoreLine(UUID entity, boolean canPromote, boolean canDemote) {
		if (!protection.isOwner(entity)) {
			if (Settings.SETTINGS_PROTECTION_ALLOWMEMBERPROMOTIONS.isTrue() && canPromote) {
				return getChestInventoryData().getCustomFields().get(ENTITY_PROMOTEACTIONLINE_PATH).toString();
			}
		} else {
			if (canDemote) {
				return getChestInventoryData().getCustomFields().get(ENTITY_DEMOTEACTIONLINE_PATH).toString();
			}
		}

		return null;
	}

	private String getSecondaryOptionSetLoreLine(UUID entity, boolean canRemoveMember) {
		if (canRemoveMember) {
			return getChestInventoryData().getCustomFields().get(ENTITY_REMOVEACTIONLINE_PATH).toString();
		}

		return null;
	}

}

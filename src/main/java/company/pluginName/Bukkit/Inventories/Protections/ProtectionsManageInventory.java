package company.pluginName.Bukkit.Inventories.Protections;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Bukkit.Inventories.Protections.Banneds.ProtectionBannedsInventory;
import company.pluginName.Bukkit.Inventories.Protections.Flags.ProtectionFlagsInventory;
import company.pluginName.Bukkit.Inventories.Protections.Members.ProtectionMembersInventory;
import company.pluginName.Bukkit.Inventories.Protections.Merge.ProtectionMergeInventory;
import company.pluginName.Bukkit.Inventories.Protections.Owners.ProtectionOwnersInventory;
import company.pluginName.Bukkit.Inventories.Protections.Split.ProtectionSplitInventory;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemGenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemPregenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.ChestInventoryObject;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.GeneratedItem;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Item;
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Exceptions.PlayerAlreadyListeningException;
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Services.PandaMessageListenerService;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionRemovalInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionHideBlockRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionRenameRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionShowBlockRequestInput;

@Inventory("protections_manage")
public class ProtectionsManageInventory extends ChestInventoryObject {

	public static final String MESSAGES_TYPENEWNAMEINFO_PATH = "Messages.Type-new-name-info";
	public static final String MESSAGES_TYPENEWDISPLAYITEMINFO_PATH = "Messages.Type-new-display-item-info";

	public static final String TOGGLEBLOCKBUTTON_SHOWBLOCKITEM_PATH = "Show-block-item";
	public static final String TOGGLEBLOCKBUTTON_HIDEBLOCKITEM_PATH = "Hide-block-item";

	public static final String TOGGLEBOUNDARYBUTTON_SHOWBOUNDARYITEM_PATH = "Show-boundary-item";
	public static final String TOGGLEBOUNDARYBUTTON_HIDEBOUNDARYITEM_PATH = "Hide-boundary-item";

	@PandaInject
	private static PandaMessageListenerService messageListenerService;

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	private Protection protection;

	public ProtectionsManageInventory(Player player, Protection protection) {
		super(player);

		this.protection = protection;
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).setReplacements(getAvailableVariables()).process().toString();
	}

	@ItemPregenerator("Toggle-block-button")
	private static void pregenerateToggleBlockButton(Item item) {
		item.getItems().put(TOGGLEBLOCKBUTTON_SHOWBLOCKITEM_PATH,
				ItemBuilder.inst().fromMap(item.getData(), TOGGLEBLOCKBUTTON_SHOWBLOCKITEM_PATH).build());
		item.getItems().put(TOGGLEBLOCKBUTTON_HIDEBLOCKITEM_PATH,
				ItemBuilder.inst().fromMap(item.getData(), TOGGLEBLOCKBUTTON_HIDEBLOCKITEM_PATH).build());
	}

	@ItemPregenerator("Toggle-boundary-button")
	private static void pregenerateToggleBoundaryButton(Item item) {
		item.getItems().put(TOGGLEBOUNDARYBUTTON_SHOWBOUNDARYITEM_PATH,
				ItemBuilder.inst().fromMap(item.getData(), TOGGLEBOUNDARYBUTTON_SHOWBOUNDARYITEM_PATH).build());
		item.getItems().put(TOGGLEBOUNDARYBUTTON_HIDEBOUNDARYITEM_PATH,
				ItemBuilder.inst().fromMap(item.getData(), TOGGLEBOUNDARYBUTTON_HIDEBOUNDARYITEM_PATH).build());
	}

	@ItemGenerator("Protection-info")
	private ItemStack generateProtectionInfo(Item item) {
		OfflinePlayer owner = OfflinePlayerUtilities.getOfflinePlayer(protection.getOwnerUuid());

		return processPlayerHead(ItemBuilder.inst().setMaterial(Material.PLAYER_HEAD)
				.fromMap(item.getData(), Item.DISPLAYITEM_KEY).setReplacements(getAvailableVariables()),
				owner.getUniqueId());
	}

	@ItemGenerator("Change-display-item-button")
	private ItemStack generateDisplayItemButton(Item item) {
		return ItemBuilder.inst().fromMap(item.getData(), Item.DISPLAYITEM_KEY)
				.apply(this.protection.getDisplayItem().getOrDefault().clone());
	}

	@ItemGenerator("Close-button")
	private ItemStack generateCloseButton(Item item) {
		return getPreviousInventory() != null
				&& !(getPreviousInventory().getHolder() instanceof ProtectionsManageInventory)
						? item.getItems().get(Item.DISPLAYITEM_KEY)
						: null;
	}

	@ItemGenerator("Toggle-block-button")
	private ItemStack generateToggleBlockButton(Item item) {
		return (protection.getUtils().isProtectionBlockShown()
				? ProtectionUtilities.canHideBlock(protection, getPlayer())
				: ProtectionUtilities.canShowBlock(protection, getPlayer()))
						? item.getItems()
								.get(protection.getUtils().isProtectionBlockShown()
										? TOGGLEBLOCKBUTTON_HIDEBLOCKITEM_PATH
										: TOGGLEBLOCKBUTTON_SHOWBLOCKITEM_PATH)
						: null;
	}

	@ItemGenerator("Toggle-boundary-button")
	private ItemStack generateToggleBoundaryButton(Item item) {
		return ProtectionUtilities.canViewBoundaries(protection, getPlayer()) ? item.getItems()
				.get(protection.getBoundaries().isProtectionViewActive() ? TOGGLEBOUNDARYBUTTON_HIDEBOUNDARYITEM_PATH
						: TOGGLEBOUNDARYBUTTON_SHOWBOUNDARYITEM_PATH)
				: null;
	}

	@ItemGenerator("Merge-button")
	private ItemStack generateMergeButton(Item item) {
		return ProtectionUtilities.canMerge(protection, getPlayer()) ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemGenerator("Delete-button")
	private ItemStack generateDeleteButton(Item item) {
		return ProtectionUtilities.canDelete(protection, getPlayer()) ? item.getItems().get(Item.DISPLAYITEM_KEY)
				: null;
	}

	@ItemExecutor("Close-button")
	private void executeCloseButton() {
		goToPreviousInventory();
	}

	@ItemExecutor("Change-display-item-button")
	private void executeChangeDisplayItemButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		try {
			if (e.getCursor() != null && e.getCursor().getType() != Material.AIR) {
				protection.getDisplayItem().setAndSave(e.getCursor());
				updateInventory();
			} else if (protection.getDisplayItem() != null) {
				protection.getDisplayItem().resetAndSave(getPlayer());
				updateInventory();
			}
		} catch (RoyaleProtectionBlocksExceptionImpl e1) {
			e1.sendError(getPlayer());
		}
	}

	@ItemExecutor("Owners-button")
	private void executeOwnersButton() {
		new ProtectionOwnersInventory(getPlayer(), protection).openInventory();
	}

	@ItemExecutor("Members-button")
	private void executeMembersButton() {
		new ProtectionMembersInventory(getPlayer(), protection).openInventory();
	}

	@ItemExecutor("Banneds-button")
	private void executeBannedsButton() {
		new ProtectionBannedsInventory(getPlayer(), protection).openInventory();
	}

	@ItemExecutor("Flags-button")
	private void executeFlagsButton() {
		new ProtectionFlagsInventory(getPlayer(), protection).openInventory();
	}

	@ItemExecutor("Rename-button")
	private void executeRenameButton() {
		try {
			messageListenerService.getListener().startListening(getPlayer().getUniqueId(), (message) -> {
				if (!message.equalsIgnoreCase("cancel")) {
					try {
						playerInteractionsService.protectionRenameRequest(
								ProtectionRenameRequestInput.inst(getPlayer(), protection, message));
						MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_RENAMEDSUCCESSFULLY.applyPrefix()).process()
								.sendMessage(getPlayer());
					} catch (RoyaleProtectionBlocksException e1) {
						e1.sendError(getPlayer());
					}
				}
				openInventory();
				return true;
			});
			closeInventory();
			MessageTemplate
					.inst(PandaPrefixedStringField.applyPrefix(
							getChestInventoryData().getCustomFields().get(MESSAGES_TYPENEWNAMEINFO_PATH).toString()))
					.process().sendMessage(getPlayer());
		} catch (PlayerAlreadyListeningException ex) {
			MessageTemplate.inst(Messages.ERROR_CHATPROMPT_ALREADYPROMPTED.toString()).process()
					.sendMessage(getPlayer());
		}
	}

	@ItemExecutor("Toggle-block-button")
	private void executeToggleBlockButton() {
		try {
			if (protection.getUtils().isProtectionBlockShown()) {
				playerInteractionsService
						.protectionHideBlockRequest(ProtectionHideBlockRequestInput.inst(getPlayer(), protection));
				updateInventory();
			} else {
				playerInteractionsService
						.protectionShowBlockRequest(ProtectionShowBlockRequestInput.inst(getPlayer(), protection));
				updateInventory();
			}
		} catch (RoyaleProtectionBlocksException e) {
			e.sendError(getPlayer());
		}
	}

	@ItemExecutor("Toggle-boundary-button")
	private void executeToggleBoundaryButton() {
		protection.getBoundaries().toggleProtectionView();
		updateInventory();
	}

	@ItemExecutor("Merge-button")
	private void executeMergeButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		if (e.isLeftClick()) {
			new ProtectionMergeInventory(getPlayer(), protection).openInventory();
		} else if (e.isRightClick()) {
			new ProtectionSplitInventory(getPlayer(), protection).openInventory();
		}
	}

	@ItemExecutor("Delete-button")
	private void executeDeleteButton() {
		try {
			playerInteractionsService.openProtectionRemovalInventoryRequest(
					OpenProtectionRemovalInventoryRequestInput.inst(getPlayer(), protection));
		} catch (RoyaleProtectionBlocksException e) {
			e.sendError(getPlayer());
		}
	}

	private Replacement[] availableVariables = null;

	private Replacement[] getAvailableVariables() {
		return getAvailableVariables(false);
	}

	private Replacement[] getAvailableVariables(boolean renew) {
		if (this.availableVariables == null || renew) {
			Location loc = protection.getBukkitLocation();
			ProtectionBlock block = protection.getProtectionBlock().getObject();
			OfflinePlayer owner = OfflinePlayerUtilities.getOfflinePlayer(protection.getOwnerUuid());

			Replacement protectionDisplayName = new Replacement("{protection}",
					() -> protection.getDisplayName() != null ? protection.getDisplayName() : protection.getRegionId());
			protectionDisplayName.getMessageFragment().setCacheText(false);

			this.availableVariables = new Replacement[] { protectionDisplayName,
					new Replacement("{owner}", () -> owner.getName()),
					new Replacement("{world}", () -> protection.getWorldName()),
					new Replacement("{location_x}", () -> loc != null ? String.valueOf(loc.getBlockX()) : "???"),
					new Replacement("{location_y}", () -> loc != null ? String.valueOf(loc.getBlockY()) : "???"),
					new Replacement("{location_z}", () -> loc != null ? String.valueOf(loc.getBlockZ()) : "???"),
					new Replacement("{size_x}",
							() -> block != null ? String.valueOf((block.getInformation().getBlocksX() * 2) + 1)
									: "???"),
					new Replacement("{size_y}",
							() -> block != null ? (block.getInformation().getBlocksY() == -1
									? Messages.MESSAGE_GENERAL_NOLIMIT.toString()
									: String.valueOf((block.getInformation().getBlocksY() * 2) + 1)) : "???"),
					new Replacement("{size_z}",
							() -> block != null ? String.valueOf((block.getInformation().getBlocksZ() * 2) + 1)
									: "???") };
		}
		return this.availableVariables;
	}

}
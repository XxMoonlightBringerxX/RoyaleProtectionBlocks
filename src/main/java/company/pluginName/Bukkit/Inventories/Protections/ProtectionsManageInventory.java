package company.pluginName.Bukkit.Inventories.Protections;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionParticlesPckg.ProtectionParticlesService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsServiceImpl;
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
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Listeners.PandaMessageListener.Callback;
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Services.PandaMessageListenerService;
import darkpanda73.PandaUtils.Utilities.Java.Arrays.ArrayUtilities;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.ProtectionBlocks.IProtectionBlock;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionRemovalInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionHideBlockRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionRenameRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSellRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionShowBlockRequestInput;

@Inventory("protections_manage")
public class ProtectionsManageInventory extends ChestInventoryObject {

	private static final String MESSAGES_TYPENEWNAMEINFO_PATH = "Messages.Type-new-name-info";
	private static final String MESSAGES_PRICESPECIFYINFO_PATH = "Messages.Price-specify-info";

	private static final String TOGGLEBLOCKBUTTON_SHOWBLOCKITEM_PATH = "Show-block-item";
	private static final String TOGGLEBLOCKBUTTON_HIDEBLOCKITEM_PATH = "Hide-block-item";

	private static final String TOGGLEBOUNDARYBUTTON_SHOWBOUNDARYITEM_PATH = "Show-boundary-item";
	private static final String TOGGLEBOUNDARYBUTTON_HIDEBOUNDARYITEM_PATH = "Hide-boundary-item";

	private static final String PRICEBUTTON_NOTSETITEM_PATH = "Not-set-item";

	@PandaInject
	private static PandaMessageListenerService messageListenerService;

	@PandaInject
	private static ProtectionsServiceImpl protectionsService;

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	@PandaInject
	private static ProtectionParticlesService protectionParticlesService;

	@PandaInject
	private static PlayerDataService playerDataService;

	@PandaInject
	private static PlaceholdersService placeholdersService;

	private Protection protection;

	public ProtectionsManageInventory(Player player, Protection protection) {
		super(player);

		this.protection = protection;

		Location loc = protection.getBukkitLocation();
		IProtectionBlock block = protection.getProtectionBlock();

		Replacement protectionDisplayName = new Replacement("{protection}",
				() -> protection.getDisplayName() != null ? protection.getDisplayName() : protection.getProtectionId());
		protectionDisplayName.getMessageFragment().setCacheText(false);

		Replacement[] playerReplacements = placeholdersService.getPlayerReplacements(getPlayer());
		Replacement[] protectionReplacements = placeholdersService.getProtectionReplacements(protection);
		Replacement[] protectionBlockReplacements = placeholdersService
				.getProtectionBlockReplacements(protection.getProtectionBlock());
		Replacement[] customReplacements = new Replacement[] { protectionDisplayName,
				new Replacement("{owner}", () -> protection.getOwnerName()),
				new Replacement("{world}", () -> protection.getWorldName()),
				new Replacement("{location_x}", () -> loc != null ? String.valueOf(loc.getBlockX()) : "???"),
				new Replacement("{location_y}", () -> loc != null ? String.valueOf(loc.getBlockY()) : "???"),
				new Replacement("{location_z}", () -> loc != null ? String.valueOf(loc.getBlockZ()) : "???"),
				new Replacement("{size_x}", () -> block != null ? String.valueOf((block.getBlocksX() * 2) + 1) : "???"),
				new Replacement("{size_y}",
						() -> block != null ? (block.getBlocksY() == -1 ? Messages.MESSAGE_GENERAL_NOLIMIT.toString()
								: String.valueOf((block.getBlocksY() * 2) + 1)) : "???"),
				new Replacement("{size_z}",
						() -> block != null ? String.valueOf((block.getBlocksZ() * 2) + 1) : "???") };

		setReplacements(ArrayUtilities.join(
				new Replacement[playerReplacements.length + protectionReplacements.length
						+ protectionBlockReplacements.length + customReplacements.length],
				playerReplacements, protectionReplacements, protectionBlockReplacements, customReplacements));
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).setReplacements(getReplacements()).process().toString();
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
				.fromMap(item.getData(), Item.DISPLAYITEM_KEY).setReplacements(getReplacements()), owner.getUniqueId());
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

	@ItemGenerator("Price-button")
	private ItemStack generatePriceButton(Item item) {
		return ItemBuilder.inst().fromMap(item.getData(),
				this.protection.isForSale() ? Item.DISPLAYITEM_KEY : PRICEBUTTON_NOTSETITEM_PATH).build();
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

	@ItemGenerator("Toggle-boundary-button")
	private ItemStack generateToggleBoundaryButton(Item item) {
		return item.getItems()
				.get(protectionParticlesService.isActive(getPlayer()) ? TOGGLEBOUNDARYBUTTON_HIDEBOUNDARYITEM_PATH
						: TOGGLEBOUNDARYBUTTON_SHOWBOUNDARYITEM_PATH);
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
		messageListenerService.getListener().replaceListening(getPlayer().getUniqueId(), new Callback() {

			@Override
			public boolean message(String message) {
				try {
					playerInteractionsService.protectionRenameRequest(
							ProtectionRenameRequestInput.inst(getPlayer(), protection, message));
					MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_RENAMEDSUCCESSFULLY.applyPrefix()).process()
							.sendMessage(getPlayer());
				} catch (RoyaleProtectionBlocksException e1) {
					e1.sendError(getPlayer());
				}
				return true;
			}

			public void cancel() {
				openInventory();
			}

		});
		closeInventory();
		MessageTemplate
				.inst(PandaPrefixedStringField.applyPrefix(
						getChestInventoryData().getCustomFields().get(MESSAGES_TYPENEWNAMEINFO_PATH).toString()))
				.process().sendMessage(getPlayer());
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

	@ItemExecutor("Price-button")
	private void executePriceButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		if (e.getClick() == ClickType.LEFT) {
			messageListenerService.getListener().replaceListening(getPlayer().getUniqueId(), new Callback() {

				@Override
				public boolean message(String message) {
					try {
						double price = Double.parseDouble(message);
						try {
							RoyaleProtectionBlocksAPI.getInstance().getPlayerInteractionsService()
									.protectionSellRequest(
											ProtectionSellRequestInput.inst(getPlayer(), protection, price));
						} catch (RoyaleProtectionBlocksException e) {
							e.sendError(getPlayer());
						}
					} catch (NumberFormatException e1) {
						MessageTemplate.inst(Messages.ERROR_INVALIDNUMBER.applyPrefix()).process()
								.sendMessage(e.getWhoClicked());
					}
					return true;
				}

				public void cancel() {
					openInventory();
				}

			});
			closeInventory();
			MessageTemplate
					.inst(PandaPrefixedStringField.applyPrefix(
							getChestInventoryData().getCustomFields().get(MESSAGES_PRICESPECIFYINFO_PATH).toString()))
					.process().sendMessage(e.getWhoClicked());
		} else if (e.getClick() == ClickType.RIGHT && this.protection.isForSale()) {
			this.protection.setPriceAndSave(0);
			updateInventory();
		}
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

	@ItemExecutor("Toggle-boundary-button")
	private void executeToggleBoundaryButton() {
		PlayerData pd = playerDataService.getPlayerData(getPlayer());
		protectionParticlesService.toggleView(getPlayer(), pd.isStaffMode());
		updateInventory();
	}

}
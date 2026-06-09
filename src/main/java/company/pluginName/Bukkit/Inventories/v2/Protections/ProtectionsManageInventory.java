package company.pluginName.Bukkit.Inventories.v2.Protections;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Bukkit.Inventories.Protections.Banneds.ProtectionBannedsInventory;
import company.pluginName.Bukkit.Inventories.Protections.Flags.ProtectionFlagsInventory;
import company.pluginName.Bukkit.Inventories.Protections.Members.ProtectionMembersInventory;
import company.pluginName.Bukkit.Inventories.Protections.Merge.ProtectionMergeInventory;
import company.pluginName.Bukkit.Inventories.Protections.Permissions.ProtectionsPermissionsListInventory;
import company.pluginName.Bukkit.Inventories.Protections.Settings.ProtectionsSettingsListInventory;
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
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemGenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemPregenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.ChestInventoryObject;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.GeneratedItem;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Item;
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Listeners.PandaMessageListener.Callback;
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Services.PandaMessageListenerService;
import darkpanda73.PandaUtils.Utilities.Java.Arrays.ArrayUtilities;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionRemovalInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionChangeDisplayItemRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionHideBlockRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionRenameRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSellRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionShowBlockRequestInput;

@Inventory("protections_manage_v2")
public class ProtectionsManageInventory extends ChestInventoryObject {

	private static final String MESSAGES_TYPENEWNAMEINFO_PATH = "Messages.Type-new-name-info";
	private static final String MESSAGES_PRICESPECIFYINFO_PATH = "Messages.Price-specify-info";

	private static final String PROTECTIONINFO_CHANGEDISPLAYITEMLORELINE = "Change-display-item-lore-line";
	private static final String PROTECTIONINFO_RESETDISPLAYITEMLORELINE = "Reset-display-item-lore-line";

	private static final String TOGGLEBLOCKBUTTON_SHOWBLOCKITEM_PATH = "Show-block-item";

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

		Replacement[] playerReplacements = placeholdersService.getPlayerReplacements(getPlayer());
		Replacement[] protectionReplacements = placeholdersService.getProtectionReplacements(protection);
		Replacement[] protectionBlockReplacements = placeholdersService
				.getProtectionBlockReplacements(protection.getProtectionBlock());

		setReplacements(ArrayUtilities.join(
				new Replacement[playerReplacements.length + protectionReplacements.length
						+ protectionBlockReplacements.length],
				playerReplacements, protectionReplacements, protectionBlockReplacements));
		setTitleReplacements(getReplacements());
	}

	@ItemPregenerator("Toggle-block-button")
	private static void pregenerateToggleBlockButton(Item item) {
		item.getItems().put(Item.DISPLAYITEM_KEY,
				ItemBuilder.inst().fromMap(item.getData(), Item.DISPLAYITEM_KEY).build());
		item.getItems().put(TOGGLEBLOCKBUTTON_SHOWBLOCKITEM_PATH,
				ItemBuilder.inst().fromMap(item.getData(), TOGGLEBLOCKBUTTON_SHOWBLOCKITEM_PATH).build());
	}

	@ItemPregenerator("Toggle-boundary-button")
	private static void pregenerateToggleBoundaryButton(Item item) {
		item.getItems().put(Item.DISPLAYITEM_KEY,
				ItemBuilder.inst().fromMap(item.getData(), Item.DISPLAYITEM_KEY).build());
		item.getItems().put(TOGGLEBOUNDARYBUTTON_HIDEBOUNDARYITEM_PATH,
				ItemBuilder.inst().fromMap(item.getData(), TOGGLEBOUNDARYBUTTON_HIDEBOUNDARYITEM_PATH).build());
	}

	@ItemGenerator("Protection-info")
	private ItemStack generateDisplayItemButton(Item item) {
		ItemBuilder builder = ItemBuilder.inst().fromMap(item.getData(), Item.DISPLAYITEM_KEY);

		if (ProtectionUtilities.canChangeDisplayItem(protection, getPlayer())) {
			List<String> extraLore = new ArrayList<>();

			Object changeLine = item.getData().get(PROTECTIONINFO_CHANGEDISPLAYITEMLORELINE);
			if (changeLine != null) {
				extraLore.add(changeLine.toString());
			}

			if (this.protection.getDisplayItem() != null) {
				Object resetLine = item.getData().get(PROTECTIONINFO_RESETDISPLAYITEMLORELINE);
				if (resetLine != null) {
					extraLore.add(resetLine.toString());
				}
			}

			if (!extraLore.isEmpty()) {
				builder.getLore().add("&0");
				builder.getLore().addAll(extraLore);
			}
		}

		return builder.apply(this.protection.getDisplayItemOrDefault().clone());
	}

	@ItemGenerator("Toggle-block-button")
	private ItemStack generateToggleBlockButton(Item item) {
		return protection.canToggleBlockVisibility(getPlayer())
				? item.getItems().get(protection.getUtils().isProtectionBlockShown() ? Item.DISPLAYITEM_KEY
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
						: Item.DISPLAYITEM_KEY);
	}

	@ItemExecutor("Close-button")
	private void executeCloseButton() {
		goToPreviousInventory();
	}

	@ItemExecutor("Protection-info")
	private void executeChangeDisplayItemButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		if (ProtectionUtilities.canChangeDisplayItem(protection, getPlayer())) {
			try {
				if (e.getCursor() != null && e.getCursor().getType() != Material.AIR) {
					RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
							.protectionChangeDisplayItemRequest(ProtectionChangeDisplayItemRequestInput
									.inst(getPlayer(), protection, e.getCursor()));
					updateInventory();
				} else if (protection.getDisplayItem() != null) {
					RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
							.protectionChangeDisplayItemRequest(
									ProtectionChangeDisplayItemRequestInput.inst(getPlayer(), protection, null));
					updateInventory();
				}
			} catch (RoyaleProtectionBlocksException e1) {
				e1.sendError(getPlayer());
			}
		}
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
		try {
			if (e.isLeftClick()) {
				new ProtectionMergeInventory(getPlayer(), protection).openInventory();
			} else if (e.isRightClick()) {
				new ProtectionSplitInventory(getPlayer(), protection).openInventory();
			}
		} catch (RoyaleProtectionBlocksExceptionImpl e1) {
			e1.sendError(getPlayer());
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

	@ItemExecutor("Permissions-button")
	private void executePermissionsButton() {
		new ProtectionsPermissionsListInventory(getPlayer(), protection).openInventory();
	}

	@ItemExecutor("Settings-button")
	private void executeSettingsButton() {
		new ProtectionsSettingsListInventory(getPlayer(), protection).openInventory();
	}

}
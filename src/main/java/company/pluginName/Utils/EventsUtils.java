package company.pluginName.Utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PlayerInteractionsPckg.PlayerInteractionsServiceImpl;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.ItemStackDataUtilities;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import lombok.AllArgsConstructor;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.ItemType;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.ProtectionBlocks.IProtectionBlock;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionManagementInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionRemovalInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionCreationRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionRemovalRequestInput;

public class EventsUtils {

	@PandaInject
	private static MainPluginClass plugin;

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private static PlayerInteractionsServiceImpl playerInteractionsService;

	@PandaInject
	private static PlayerDataService playerDataService;

	@PandaInject
	private static PlaceholderAPI placeholderApi;

	public static BlockPlaceResult onVanillaBlockPlaceEvent(Player player, Block block, EquipmentSlot hand,
			ItemStack defaultItem) {
		return onBlockPlaceEvent(EventOrigin.VANILLA, player, block, hand, defaultItem);
	}

	public static BlockPlaceResult onOraxenBlockPlaceEvent(Player player, Block block, EquipmentSlot hand,
			ItemStack defaultItem) {
		return onBlockPlaceEvent(EventOrigin.ORAXEN, player, block, hand, defaultItem);
	}

	public static BlockPlaceResult onItemsAdderBlockPlaceEvent(Player player, Block block, EquipmentSlot hand,
			ItemStack defaultItem) {
		return onBlockPlaceEvent(EventOrigin.ITEMS_ADDER, player, block, hand, defaultItem);
	}

	private static BlockPlaceResult onBlockPlaceEvent(EventOrigin eventOrigin, Player player, Block block,
			EquipmentSlot hand, ItemStack defaultItem) {
		ItemStack item = eventOrigin != EventOrigin.VANILLA ? ItemStacksUtils.getItemByHand(player, hand, defaultItem)
				: defaultItem;

		if (item != null) {
			String protectionBlockId = null;

			try {
				protectionBlockId = ItemStackDataUtilities.getPersistentData(item, plugin,
						ProtectionBlock.PROTECTION_BLOCK_ID_KEY, String.class);
			} catch (Exception e) {
				MessageTemplate.inst(PandaPrefixedStringField
						.applyPrefix("An error has ocurred trying to retrieve the Protection Block ID from an item: %s"
								.formatted(e.getMessage())))
						.process().sendMessage(Bukkit.getConsoleSender());
				e.printStackTrace();
			}

			if (protectionBlockId != null && !protectionBlockId.isEmpty()) {
				ProtectionBlock protectionBlock = protectionBlocksService.getProtectionBlockById(protectionBlockId);

				if (protectionBlock == null) {
					return BlockPlaceResult.IGNORE;
				}

				if (player.getGameMode() != GameMode.CREATIVE) {
					if (item.getAmount() > 1) {
						item.setAmount(item.getAmount() - 1);
					} else {
						if (hand == EquipmentSlot.OFF_HAND) {
							ItemStacksUtils.setItemInOffHand(player, null);
						} else {
							ItemStacksUtils.setItemInMainHand(player, null);
						}
					}
				}

				Location playerLocation = player.getLocation();

				TasksUtils.executeOnAsync(() -> {
					try {
						playerInteractionsService.protectionCreationRequest(ProtectionCreationRequestInput.inst(player,
								player.getUniqueId(), protectionBlock, block.getLocation()));
					} catch (RoyaleProtectionBlocksException e2) {
						if (player.isOnline()) {
							e2.sendError(player);
						}

						TasksUtils.execute(() -> {
							ItemStack protectionBlockItem = protectionBlock != null ? protectionBlock.generateItem()
									: null;

							if (protectionBlockItem != null) {
								if (player.isOnline()) {
									if (player.getGameMode() != GameMode.CREATIVE) {
										player.getInventory().addItem(protectionBlockItem)
												.forEach((index, missingItem) -> player.getLocation().getWorld()
														.dropItem(player.getLocation(), missingItem));
									}
								} else {
									playerLocation.getWorld().dropItem(playerLocation, protectionBlockItem);
								}
							}
						});
					}
				});

				return BlockPlaceResult.SUCCESS;

			}

			Protection prot = RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
					.findProtectionBySourceBlock(block);

			if (prot != null) {
				if (!prot.getUtils().isProtectionBlockShown()) {
					IProtectionBlock protectionBlock = prot.getProtectionBlock();
					if (protectionBlock == null || protectionBlock.isSameType(item)) {
						MessageTemplate.inst(Messages.ERROR_PROTECTIONS_SAMEITEMASPROTECTION.applyPrefix()).process()
								.sendMessage(player);
						return BlockPlaceResult.CANCEL;
					}
				}
			}
		}
		return BlockPlaceResult.IGNORE;
	}

	public static BlockBreakResult onVanillaBlockBreakEvent(Player player, Block block) {
		return onBlockBreakEvent(EventOrigin.VANILLA, player, block);
	}

	public static BlockBreakResult onOraxenBlockBreakEvent(Player player, Block block) {
		return onBlockBreakEvent(EventOrigin.ORAXEN, player, block);
	}

	public static BlockBreakResult onItemsAdderBlockBreakEvent(Player player, Block block) {
		return onBlockBreakEvent(EventOrigin.ITEMS_ADDER, player, block);
	}

	private static BlockBreakResult onBlockBreakEvent(EventOrigin eventOrigin, Player player, Block block) {
		Protection protection = RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(block);
		if (protection != null) {
			IProtectionBlock protectionBlock = protection.getProtectionBlock();

			if (protectionBlock != null && protectionBlock.getItemType() != eventOrigin.itemType) {
				return BlockBreakResult.IGNORE;
			}

			ProtectionUtilities.hideBlock(block);

			TasksUtils.executeOnAsync(() -> {
				try {
					playerInteractionsService
							.protectionRemovalRequest(ProtectionRemovalRequestInput.inst(player, protection));
				} catch (RoyaleProtectionBlocksException e) {
					if (e.getType() != RoyaleProtectionBlocksException.Type.PROTECTIONS_DELETE_CANCELLED) {
						e.sendError(player);
					}

					TasksUtils.execute(() -> ProtectionUtilities.showBlock(block, protectionBlock.getItem()));
				}
			});

			return BlockBreakResult.SUCCESS;
		}
		return BlockBreakResult.IGNORE;
	}

	public static BlockInteractResult onVanillaBlockInteractEvent(Player player, IProtection protection) {
		return onBlockInteractEvent(EventOrigin.VANILLA, player, protection);
	}

	public static BlockInteractResult onOraxenBlockInteractEvent(Player player, IProtection protection) {
		return onBlockInteractEvent(EventOrigin.ORAXEN, player, protection);
	}

	public static BlockInteractResult onItemsAdderBlockInteractEvent(Player player, IProtection protection) {
		return onBlockInteractEvent(EventOrigin.ITEMS_ADDER, player, protection);
	}

	private static BlockInteractResult onBlockInteractEvent(EventOrigin eventOrigin, Player player,
			IProtection protection) {
		IProtectionBlock protectionBlock = protection.getProtectionBlock();

		if (protectionBlock != null && protectionBlock.getItemType() != eventOrigin.itemType) {
			return BlockInteractResult.IGNORE;
		}

		if (player.isSneaking() && ProtectionUtilities.canDelete(protection, player)) {
			try {
				playerInteractionsService.openProtectionRemovalInventoryRequest(
						OpenProtectionRemovalInventoryRequestInput.inst(player, protection));
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(player);
			}
		} else if (Settings.SETTINGS_PROTECTION_OPENINVENTORYONINTERACT.getContent()
				&& ProtectionUtilities.canManage(protection, player)) {
			try {
				playerInteractionsService.openProtectionManagementInventoryRequest(
						OpenProtectionManagementInventoryRequestInput.inst(player, protection));
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(player);
			}
		}
		return BlockInteractResult.SUCCESS;
	}

	@AllArgsConstructor
	public static enum EventOrigin {
		VANILLA(ItemType.VANILLA), ORAXEN(ItemType.ORAXEN), ITEMS_ADDER(ItemType.ITEMS_ADDER);

		private ItemType itemType;
	}

	public static enum BlockPlaceResult {
		IGNORE, SUCCESS, CANCEL
	}

	public static enum BlockBreakResult {
		IGNORE, SUCCESS, CANCEL
	}

	public static enum BlockInteractResult {
		IGNORE, SUCCESS, CANCEL
	}

}

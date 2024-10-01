package company.pluginName.Utils;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import company.pluginName.MainPluginClass;
import company.pluginName.API.Services.PlayerInteractionsServiceImpl;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import company.pluginName.Utils.ProtectionBlocksUtils.ItemType;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.ItemStackDataUtilities;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import lombok.AllArgsConstructor;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.CreationCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionCreationAttemptEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionManagementInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionRemovalInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionRemovalRequestInput;

public class EventsUtils {

	@PandaInject
	private static MainPluginClass plugin;

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private static ProtectionsService protectionsService;

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
		// Gets the item depending on the hand, or by default, the one offered by the
		// event. This is used to prevent issues with plugins manipulating the items on
		// this events like ItemsAdder.
		ItemStack item = eventOrigin != EventOrigin.VANILLA ? ItemStacksUtils.getItemByHand(player, hand, defaultItem)
				: defaultItem;

		if (item != null) {
			// Checks if the item is a protection block by getting its metadata field
			// 'ProtectionBlockId'. If the id is null or empty, this means it's not
			// associated to any protection block.
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
				String finalProtectionBlockId = protectionBlockId;

				Debugger.log(MessageType.BLOCK_PLACE_IS_PROTECTION_BLOCK,
						() -> new Object[] { finalProtectionBlockId, player.getName() });

				ProtectionBlock protectionBlock = protectionBlocksService.getProtectionBlockById(protectionBlockId);

				// The item is ignored in case the protection block couldn't be found in the
				// protections service.
				if (protectionBlock != null && protectionBlock.getInformation().getItemType() == eventOrigin.itemType) {

					// The player must have permissions for this block. If there's no permission,
					// the player can use it freely, but if there is, and the player does not have
					// this permission, it's denied and the event is cancelled.
					if (protectionBlock.getInformation().getPermission() == null
							|| player.hasPermission(protectionBlock.getInformation().getPermission())) {

						// The world must be in the allowed list of the protection block if the allowed
						// list is not empty. If it's on the allowed list, the block can be used. If
						// it's not on the list and the list is not empty, then it's denied. If the
						// allowed list is empty, the world mustn't be on the banned worlds list.
						String worldName = block.getLocation().getWorld().getName();
						boolean emptyAllowedWorlds = protectionBlock.getAllowedWorlds().get().isEmpty();
						boolean allowedWorld = protectionBlock.getAllowedWorlds().get().contains(worldName);
						boolean bannedWorld = Settings.SETTINGS_BANNEDWORLDS.getContent().contains(worldName);
						if ((!emptyAllowedWorlds && allowedWorld) || (emptyAllowedWorlds && !bannedWorld)) {

							// If everything is ok, then the plugin will attempt to create a new protection
							// block where the block was placed. If everything is ok, the protection will be
							// created and a confirmation message will be send to the player. If something
							// fails, the event will be cancelled and an error message will be send to the
							// player.
							try {
								// TODO: This should ask to PlayerInteractionsServiceImpl
								Protection protection = new Protection(player.getUniqueId(), block.getLocation(),
										protectionBlock);

								ProtectionCreationAttemptEvent attemptEvent = new ProtectionCreationAttemptEvent(player,
										protection, CreationCause.PLAYER);
								Bukkit.getPluginManager().callEvent(attemptEvent);

								if (attemptEvent.isCancelled()) {
									throw Exceptions.Protections.Save.CANCELLED.generateException();
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

								ItemStack protectionBlockItem = protectionBlock != null
										? protectionBlock.getInformation().generateItem()
										: null;
								Location playerLocation = player.getLocation();

								Consumer<Throwable> onError = (throwable) -> {
									if (!(throwable instanceof RoyaleProtectionBlocksExceptionImpl)) {
										throwable = Exceptions.Protections.Save.UNKNOWN.generateException(throwable);
									}

									if (player.isOnline()) {
										((RoyaleProtectionBlocksExceptionImpl) throwable).sendError(player);
									}

									TasksUtils.execute(() -> {
										if (protectionBlockItem != null) {
											if (player.isOnline()) {
												if (player.getGameMode() != GameMode.CREATIVE) {
													player.getInventory().addItem(protectionBlockItem)
															.forEach((index, missingItem) -> player.getLocation()
																	.getWorld()
																	.dropItem(player.getLocation(), missingItem));
												}
											} else {
												try {
													playerLocation.getWorld().dropItem(playerLocation,
															protectionBlock.getInformation().generateItem());
												} catch (RoyaleProtectionBlocksExceptionImpl e) {
													e.printStackTrace();
												}
											}
										}
									});
								};

								TasksUtils.executeOnAsync(() -> {
									try {
										protection.create(player).subscribe((createdProtection) -> {
											if (Settings.SETTINGS_PROTECTION_SETPLAYERPOSITIONASHOMEONCREATION
													.getContent()) {
												try {
													createdProtection.setHome(playerLocation);
												} catch (Exception e) {
													e.printStackTrace();
												}
											}

											if (player.isOnline()) {
												MessageTemplate.inst(
														Messages.MESSAGE_PROTECTIONS_CREATEDSUCCESSFULLY.applyPrefix())
														.process().sendMessage(player);
											}

											TasksUtils
													.execute(() -> createdProtection.getUtils().showProtectionBlock());
										}, onError);
									} catch (RoyaleProtectionBlocksExceptionImpl e) {
										onError.accept(e);
									}
								});

								return BlockPlaceResult.SUCCESS;
							} catch (RoyaleProtectionBlocksExceptionImpl e1) {
								if (e1.getExceptionType() == Exceptions.Protections.Save.CANCELLED) {
									Debugger.log(MessageType.PROTECTION_CREATION_ATTEMPT_CANCELLED,
											() -> new Object[] { String.valueOf(block.getLocation().getX()),
													String.valueOf(block.getLocation().getY()),
													String.valueOf(block.getLocation().getZ()) });
								} else {
									e1.sendError(player);
								}
								return BlockPlaceResult.CANCEL;
							}
						} else {
							if (!emptyAllowedWorlds && !allowedWorld) {
								MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTALLOWEDWORLD.applyPrefix()).process()
										.sendMessage(player);
							} else {
								MessageTemplate.inst(Messages.ERROR_PROTECTIONS_BANNEDWORLD.applyPrefix()).process()
										.sendMessage(player);
							}
							return BlockPlaceResult.CANCEL;
						}
					} else {
						MessageTemplate.inst(Messages.ERROR_PROTECTIONS_PERMISSIONDENIED.applyPrefix()).process()
								.sendMessage(player);
						return BlockPlaceResult.CANCEL;
					}
				}
			} else {
				Debugger.log(MessageType.BLOCK_PLACE_NOT_PROTECTION_BLOCK, () -> new Object[] { player.getName() });
			}

			// Checks if the place where the block is being set is the location of a
			// protection block. If that's the case, then it'll check if the item is the
			// same as the protection block. Being the case, the user is prevented from
			// doing it as it can cause interferences on the show/hide commands.
			Protection prot = protectionsService.findProtectionBySourceBlock(block);
			if (prot != null) {
				if (!prot.getUtils().isProtectionBlockShown()) {
					ProtectionBlock protectionBlock = prot.getProtectionBlock().getObject();
					if (protectionBlock == null || protectionBlock.getInformation().isSameType(item)) {
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
		Protection protection = protectionsService.findProtectionBySourceBlock(block);
		if (protection != null) {
			ProtectionBlock protectionBlock = protection.getProtectionBlock().getObject();

			if (protectionBlock != null && protectionBlock.getInformation().getItemType() != eventOrigin.itemType) {
				return BlockBreakResult.IGNORE;
			}

			try {
				playerInteractionsService
						.protectionRemovalRequest(ProtectionRemovalRequestInput.inst(player, protection));

				return BlockBreakResult.SUCCESS;
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				if (e.getType() == RoyaleProtectionBlocksException.Type.PROTECTIONS_DELETE_CANCELLED) {
					Debugger.log(MessageType.PROTECTION_REMOVAL_ATTEMPT_CANCELLED,
							() -> new Object[] { protection.getRegionId() });
				} else {
					e.sendError(player);
				}
				return BlockBreakResult.CANCEL;
			}
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
		ProtectionBlock protectionBlock = ((Protection) protection).getProtectionBlock().getObject();

		if (protectionBlock != null && protectionBlock.getInformation().getItemType() != eventOrigin.itemType) {
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

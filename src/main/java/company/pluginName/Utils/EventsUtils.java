package company.pluginName.Utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.MainPluginClass.Debugger.MessageType;
import company.pluginName.Bukkit.Inventories.Protections.ProtectionsManagerInventory;
import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Exceptions.Protection.Delete.ProtectionDeleteException;
import company.pluginName.Exceptions.Protection.Delete.ProtectionDeleteUnknownException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveException;
import company.pluginName.Exceptions.ProtectionBlocks.ProtectionBlocksGenerateItemException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.Utils.ProtectionBlocksUtils.ItemType;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.ItemStackDataUtilities;
import lombok.AllArgsConstructor;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

public class EventsUtils {

	public static BlockPlaceResult onVanillaBlockPlaceEvent(Player player, Block block, EquipmentSlot hand,
			ItemStack defaultItem) {
		return onBlockPlaceEvent(EventOrigin.VANILLA, player, block, hand, defaultItem);
	}

	public static BlockPlaceResult onOraxenBlockPlaceEvent(Player player, Block block, EquipmentSlot hand,
			ItemStack defaultItem) {
		return onBlockPlaceEvent(EventOrigin.ORAXEN, player, block, hand, defaultItem);
	}

	private static BlockPlaceResult onBlockPlaceEvent(EventOrigin eventOrigin, Player player, Block block,
			EquipmentSlot hand, ItemStack defaultItem) {
		// Gets the item depending on the hand, or by default, the one offered by the
		// event. This is used to prevent issues with plugins manipulating the items on
		// this events like ItemsAdder.
		ItemStack item = ItemStacksUtils.getItemByHand(player, hand, defaultItem);

		if (item != null) {
			// Checks if the item is a protection block by getting its metadata field
			// 'ProtectionBlockId'. If the id is null or empty, this means it's not
			// associated to any protection block.
			String protectionBlockId = null;

			try {
				protectionBlockId = ItemStackDataUtilities.getPersistentData(item, MainPluginClass.getPlugin(),
						ProtectionBlock.PROTECTION_BLOCK_ID_KEY, String.class);
			} catch (Exception e) {
				MessageBuilder.createMessage(MessageString
						.applyPrefix("An error has ocurred trying to retrieve the Protection Block ID from an item: %s"
								.formatted(e.getMessage())))
						.sendMessage(Bukkit.getConsoleSender());
				e.printStackTrace();
			}

			if (protectionBlockId != null && !protectionBlockId.isEmpty()) {
				String finalProtectionBlockId = protectionBlockId;

				MainPluginClass.Debugger.log(MessageType.BLOCK_PLACE_IS_PROTECTION_BLOCK,
						() -> new Object[] { finalProtectionBlockId, player.getName() });

				ProtectionBlock protectionBlock = MainPluginClass.getPlugin().getProtectionsModule()
						.getProtectionBlockById(protectionBlockId);

				// The item is ignored in case the protection block couldn't be found in the
				// protections service.
				if (protectionBlock != null
						&& (protectionBlock.getInformation().getItemType() == eventOrigin.itemType)) {

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
						boolean bannedWorld = SettingList.SETTINGS_BANNEDWORLDS.getContent().contains(worldName);
						if ((!emptyAllowedWorlds && allowedWorld) || (emptyAllowedWorlds && !bannedWorld)) {

							// If everything is ok, then the plugin will attempt to create a new protection
							// block where the block was placed. If everything is ok, the protection will be
							// created and a confirmation message will be send to the player. If something
							// fails, the event will be cancelled and an error message will be send to the
							// player.
							try {
								MainPluginClass.Debugger.log(MessageType.PROTECTION_CREATION_ATTEMPT,
										() -> new Object[] { player.getName(), String.valueOf(block.getX()),
												String.valueOf(block.getY()), String.valueOf(block.getZ()) });

								Protection protection = MainPluginClass.getPlugin().getProtectionsModule()
										.createProtection(player, protectionBlock, block.getLocation());

								MessageBuilder
										.createMessage(
												MessageString.MESSAGE_PROTECTIONS_CREATEDSUCCESSFULLY.applyPrefix())
										.sendMessage(player);

								SettingList.SETTINGS_COMMANDSONCREATION.getContent()
										.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
												MainPluginClass.getPlaceholderAPI().isHooked()
														? MainPluginClass.getPlaceholderAPI().applyPlaceholders(command,
																player)
														: command));

								MainPluginClass.Debugger.log(MessageType.PROTECTION_CREATION,
										() -> new Object[] { player.getName(), protection.getDisplayName(),
												String.valueOf(protection.getProtectionBlockLocation().getX()),
												String.valueOf(protection.getProtectionBlockLocation().getY()),
												String.valueOf(protection.getProtectionBlockLocation().getZ()) });

								return BlockPlaceResult.SUCCESS;
							} catch (ProtectionSaveException e1) {
								e1.sendError(player);
								return BlockPlaceResult.CANCEL;
							}
						} else {
							if (!emptyAllowedWorlds && !allowedWorld) {
								MessageBuilder
										.createMessage(MessageString.ERROR_PROTECTIONS_NOTALLOWEDWORLD.applyPrefix())
										.sendMessage(player);
							} else {
								MessageBuilder.createMessage(MessageString.ERROR_PROTECTIONS_BANNEDWORLD.applyPrefix())
										.sendMessage(player);
							}
							return BlockPlaceResult.CANCEL;
						}
					} else {
						MessageBuilder.createMessage(MessageString.ERROR_PROTECTIONS_PERMISSIONDENIED.applyPrefix())
								.sendMessage(player);
						return BlockPlaceResult.CANCEL;
					}
				}
			} else {
				MainPluginClass.Debugger.log(MessageType.BLOCK_PLACE_NOT_PROTECTION_BLOCK,
						() -> new Object[] { player.getName() });
			}

			// Checks if the place where the block is being set is the location of a
			// protection block. If that's the case, then it'll check if the item is the
			// same as the protection block. Being the case, the user is prevented from
			// doing it as it can cause interferences on the show/hide commands.
			Protection prot = MainPluginClass.getPlugin().getProtectionsModule()
					.getProtectionByBlock(block.getLocation());
			if (prot != null) {
				ProtectionBlock protectionBlock = prot.getProtectionBlock().getObject();
				if (protectionBlock == null || protectionBlock.getInformation().isSameType(item)) {

					MessageBuilder.createMessage(MessageString.ERROR_PROTECTIONS_SAMEITEMASPROTECTION.applyPrefix())
							.sendMessage(player);
					return BlockPlaceResult.CANCEL;
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

	private static BlockBreakResult onBlockBreakEvent(EventOrigin eventOrigin, Player player, Block block) {
		Protection protection = MainPluginClass.getPlugin().getProtectionsModule()
				.getProtectionByBlock(block.getLocation());
		if (protection != null) {
			ProtectionBlock protectionBlock = protection.getProtectionBlock().getObject();

			if (protectionBlock != null && protectionBlock.getInformation().getItemType() != eventOrigin.itemType) {
				return BlockBreakResult.IGNORE;
			}

			try {
				ItemStack protectionBlockItem = null;
				try {
					if (protectionBlock != null) {
						protectionBlockItem = protection.getProtectionBlock().getObject().getInformation()
								.generateItem();
					}
				} catch (ProtectionBlocksGenerateItemException e) {
					throw new ProtectionDeleteUnknownException(e);
				}

				MainPluginClass.Debugger.log(MessageType.PROTECTION_REMOVAL_ATTEMPT,
						() -> new Object[] { player.getName(), protection.getDisplayName(),
								String.valueOf(protection.getProtectionBlockLocation().getX()),
								String.valueOf(protection.getProtectionBlockLocation().getY()),
								String.valueOf(protection.getProtectionBlockLocation().getZ()) });

				MainPluginClass.getPlugin().getProtectionsModule().removeProtection(player, protection);

				if (eventOrigin == EventOrigin.VANILLA && protectionBlockItem != null) {
					block.getWorld().dropItem(block.getLocation(), protectionBlockItem);
				}

				MessageBuilder.createMessage(MessageString.MESSAGE_PROTECTIONS_REMOVEDSUCCESSFULLY.applyPrefix())
						.sendMessage(player);

				SettingList.SETTINGS_COMMANDSONREMOVAL.getContent()
						.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
								MainPluginClass.getPlaceholderAPI().isHooked()
										? MainPluginClass.getPlaceholderAPI().applyPlaceholders(command, player)
										: command));

				if (player.getOpenInventory().getType() != InventoryType.CRAFTING) {
					player.closeInventory();
				}

				MainPluginClass.Debugger.log(MessageType.PROTECTION_REMOVAL,
						() -> new Object[] { player.getName(), protection.getDisplayName(),
								String.valueOf(protection.getProtectionBlockLocation().getX()),
								String.valueOf(protection.getProtectionBlockLocation().getY()),
								String.valueOf(protection.getProtectionBlockLocation().getZ()) });

				return BlockBreakResult.SUCCESS;
			} catch (ProtectionDeleteException e1) {
				e1.sendError(player);
				return BlockBreakResult.CANCEL;
			}
		}
		return BlockBreakResult.IGNORE;
	}

	public static BlockInteractResult onVanillaBlockInteractEvent(Player player, Protection protection) {
		return onBlockInteractEvent(EventOrigin.VANILLA, player, protection);
	}

	public static BlockInteractResult onOraxenBlockInteractEvent(Player player, Protection protection) {
		return onBlockInteractEvent(EventOrigin.ORAXEN, player, protection);
	}

	private static BlockInteractResult onBlockInteractEvent(EventOrigin eventOrigin, Player player,
			Protection protection) {
		ProtectionBlock protectionBlock = protection.getProtectionBlock().getObject();

		if (protectionBlock != null && protectionBlock.getInformation().getItemType() != eventOrigin.itemType) {
			return BlockInteractResult.IGNORE;
		}

		if (player.isSneaking() && protection.canDelete(player)) {
			new ConfirmationInventory(player, () -> {
				try {
					ItemStack protectionBlockItem = null;
					try {
						if (protectionBlock != null) {
							protectionBlockItem = protection.getProtectionBlock().getObject().getInformation()
									.generateItem();
						}
					} catch (ProtectionBlocksGenerateItemException e) {
						throw new ProtectionDeleteUnknownException(e);
					}

					if (protection.isProtectionBlockShown()) {
						protection.hideProtectionBlock();
					}

					MainPluginClass.getPlugin().getProtectionsModule().removeProtection(protection);

					if (protectionBlockItem != null) {
						HashMap<Integer, ItemStack> items = player.getInventory().addItem(protectionBlockItem);

						if (items != null && !items.isEmpty()) {
							items.values().forEach(
									item -> player.getLocation().getWorld().dropItem(player.getLocation(), item));
						}
					}

					MessageBuilder.createMessage(MessageString.MESSAGE_PROTECTIONS_REMOVEDSUCCESSFULLY.applyPrefix())
							.sendMessage(player);
				} catch (ProtectionDeleteException e1) {
					e1.sendError(player);
				}
			}).openInventory();

		} else if (protection.canManage(player)) {
			new ProtectionsManagerInventory(player, protection).openInventory();
		}
		return BlockInteractResult.SUCCESS;
	}

	@AllArgsConstructor
	public static enum EventOrigin {
		VANILLA(ItemType.VANILLA), ORAXEN(ItemType.ORAXEN);

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

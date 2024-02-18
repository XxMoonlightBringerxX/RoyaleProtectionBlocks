package company.pluginName.Utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import company.pluginName.MainPluginClass;
import company.pluginName.APIs.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.Bukkit.Inventories.Protections.ProtectionsManageInventory;
import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Utils.ProtectionBlocksUtils.ItemType;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.ItemStackDataUtilities;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import lombok.AllArgsConstructor;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

public class EventsUtils {

	@PandaInject
	private static MainPluginClass plugin;

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private static ProtectionsService protectionsService;

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
		ItemStack item = ItemStacksUtils.getItemByHand(player, hand, defaultItem);

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
								Debugger.log(MessageType.PROTECTION_CREATION_ATTEMPT,
										() -> new Object[] { player.getName(), String.valueOf(block.getX()),
												String.valueOf(block.getY()), String.valueOf(block.getZ()) });

								Protection protection = protectionsService.createProtection(player, protectionBlock,
										block.getLocation());

								MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_CREATEDSUCCESSFULLY.applyPrefix())
										.process().sendMessage(player);

								Settings.SETTINGS_COMMANDSONCREATION.getContent()
										.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
												placeholderApi.getHook().isHooked()
														? placeholderApi.getHook().applyPlaceholders(command, player)
														: command));

								Debugger.log(MessageType.PROTECTION_CREATION,
										() -> new Object[] { player.getName(),
												protection.getDisplayName() != null ? protection.getDisplayName()
														: protection.getRegionId(),
												String.valueOf(protection.getProtectionBlockLocation().getX()),
												String.valueOf(protection.getProtectionBlockLocation().getY()),
												String.valueOf(protection.getProtectionBlockLocation().getZ()) });

								return BlockPlaceResult.SUCCESS;
							} catch (RoyaleProtectionBlocksException e1) {
								e1.sendError(player);
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
			Protection prot = protectionsService.getProtectionByBlock(block.getLocation());
			if (prot != null) {
				ProtectionBlock protectionBlock = prot.getProtectionBlock().getObject();
				if (protectionBlock == null || protectionBlock.getInformation().isSameType(item)) {
					MessageTemplate.inst(Messages.ERROR_PROTECTIONS_SAMEITEMASPROTECTION.applyPrefix()).process()
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

	public static BlockBreakResult onItemsAdderBlockBreakEvent(Player player, Block block) {
		return onBlockBreakEvent(EventOrigin.ITEMS_ADDER, player, block);
	}

	private static BlockBreakResult onBlockBreakEvent(EventOrigin eventOrigin, Player player, Block block) {
		Protection protection = protectionsService.getProtectionByBlock(block.getLocation());
		if (protection != null) {
			ProtectionBlock protectionBlock = protection.getProtectionBlock().getObject();

			if (protectionBlock != null && protectionBlock.getInformation().getItemType() != eventOrigin.itemType) {
				return BlockBreakResult.IGNORE;
			}

			try {
				ItemStack protectionBlockItem = null;

				if (protectionBlock != null) {
					protectionBlockItem = protection.getProtectionBlock().getObject().getInformation().generateItem();
				}

				Debugger.log(MessageType.PROTECTION_REMOVAL_ATTEMPT,
						() -> new Object[] { player.getName(),
								protection.getDisplayName() != null ? protection.getDisplayName()
										: protection.getRegionId(),
								String.valueOf(protection.getProtectionBlockLocation().getX()),
								String.valueOf(protection.getProtectionBlockLocation().getY()),
								String.valueOf(protection.getProtectionBlockLocation().getZ()) });

				if (eventOrigin != EventOrigin.VANILLA) {
					protection.hideProtectionBlock();
				}

				protectionsService.removeProtection(player, protection);

				block.getWorld().dropItem(block.getLocation(), protectionBlockItem);

				MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_REMOVEDSUCCESSFULLY.applyPrefix()).process()
						.sendMessage(player);

				Settings.SETTINGS_COMMANDSONREMOVAL.getContent()
						.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
								placeholderApi.getHook().isHooked()
										? placeholderApi.getHook().applyPlaceholders(command, player)
										: command));

				if (player.getOpenInventory().getType() != InventoryType.CRAFTING) {
					player.closeInventory();
				}

				Debugger.log(MessageType.PROTECTION_REMOVAL,
						() -> new Object[] { player.getName(),
								protection.getDisplayName() != null ? protection.getDisplayName()
										: protection.getRegionId(),
								String.valueOf(protection.getProtectionBlockLocation().getX()),
								String.valueOf(protection.getProtectionBlockLocation().getY()),
								String.valueOf(protection.getProtectionBlockLocation().getZ()) });

				return BlockBreakResult.SUCCESS;
			} catch (RoyaleProtectionBlocksException e1) {
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

	public static BlockInteractResult onItemsAdderBlockInteractEvent(Player player, Protection protection) {
		return onBlockInteractEvent(EventOrigin.ITEMS_ADDER, player, protection);
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

					if (protectionBlock != null) {
						protectionBlockItem = protection.getProtectionBlock().getObject().getInformation()
								.generateItem();
					}

					if (protection.isProtectionBlockShown()) {
						protection.hideProtectionBlock();
					}

					protectionsService.removeProtection(protection);

					if (protectionBlockItem != null) {
						HashMap<Integer, ItemStack> items = player.getInventory().addItem(protectionBlockItem);

						if (items != null && !items.isEmpty()) {
							items.values().forEach(
									item -> player.getLocation().getWorld().dropItem(player.getLocation(), item));
						}
					}

					MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_REMOVEDSUCCESSFULLY.applyPrefix()).process()
							.sendMessage(player);
				} catch (RoyaleProtectionBlocksException e1) {
					e1.sendError(player);
				}
			}).openInventory();

		} else if (Settings.SETTINGS_PROTECTION_OPENINVENTORYONINTERACT.getContent() && protection.canManage(player)) {
			new ProtectionsManageInventory(player, protection).openInventory();
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

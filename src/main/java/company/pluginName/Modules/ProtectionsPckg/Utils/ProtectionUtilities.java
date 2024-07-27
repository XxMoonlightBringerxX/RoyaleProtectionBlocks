package company.pluginName.Modules.ProtectionsPckg.Utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Hooks.ItemsAdderAPI.ItemsAdderAPI;
import company.pluginName.Hooks.ItemsAdderAPI.Hook.ItemsAdderHook;
import company.pluginName.Hooks.OraxenAPI.OraxenAPI;
import company.pluginName.Hooks.OraxenAPI.Hook.OraxenHook;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.Blocks.BlockUtilities;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.SkinUtilities;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.IProtection;

public class ProtectionUtilities {

	@PandaInject
	private static ItemsAdderAPI itemsAdderApi;

	@PandaInject
	private static OraxenAPI oraxenApi;

	public static String generateDefaultName(Location location) {
		return location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_"
				+ location.getBlockZ();
	}

	public static void showBlock(Block block, ItemStack item) {
		ItemsAdderHook.PlaceResult itemsAdderResult = itemsAdderApi.getHook().setBlock(item, block.getLocation());

		if (itemsAdderResult != ItemsAdderHook.PlaceResult.NOT_HOOKED) {
			if (itemsAdderResult == ItemsAdderHook.PlaceResult.PLACED) {
				return;
			}
		}

		OraxenHook.PlaceResult oraxenResult = oraxenApi.getHook().setBlock(item, block.getLocation());

		if (oraxenResult != OraxenHook.PlaceResult.NOT_HOOKED) {
			if (oraxenResult == OraxenHook.PlaceResult.PLACED) {
				return;
			}
		}

		block.setType(item.getType());
		if (item.getType() == Material.PLAYER_HEAD.getMaterial()) {
			BlockUtilities.setSkin(block, SkinUtilities.NMS.getSkinAsBase64(item));
		}
	}

	public static void hideBlock(Block block) {
		ItemsAdderHook.PlaceResult itemsAdderResult = itemsAdderApi.getHook().setBlock(null, block.getLocation());

		if (itemsAdderResult != ItemsAdderHook.PlaceResult.NOT_HOOKED) {
			if (itemsAdderResult == ItemsAdderHook.PlaceResult.PLACED) {
				return;
			}
		}

		OraxenHook.PlaceResult oraxenResult = oraxenApi.getHook().setBlock(null, block.getLocation());

		if (oraxenResult != OraxenHook.PlaceResult.NOT_HOOKED) {
			if (oraxenResult == OraxenHook.PlaceResult.PLACED) {
				return;
			}
		}

		block.setType(Material.AIR.getMaterial());
	}

	public static boolean canManage(IProtection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canDelete(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.DELETE_OTHERS.hasPermission(pl);
	}

	public static boolean canChangeId(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canRename(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canChangeDisplayItem(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canToggleBlock(IProtection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canViewBoundaries(IProtection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canTeleport(IProtection protection, Player pl) {
		return (protection.isOwner(pl.getUniqueId())
				|| protection.isMember(pl.getUniqueId()) && PermissionsService.TELEPORT.hasPermission(pl))
				|| PermissionsService.TELEPORT_OTHERS.hasPermission(pl);
	}

	public static boolean canAddOwner(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.OWNERS_ADD_OTHERS.hasPermission(pl);
	}

	public static boolean canRemoveOwner(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.OWNERS_REMOVE_OTHERS.hasPermission(pl);
	}

	public static boolean canAddMember(IProtection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.MEMBERS_ADD_OTHERS.hasPermission(pl);
	}

	public static boolean canRemoveMember(IProtection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.MEMBERS_REMOVE_OTHERS.hasPermission(pl);
	}

	public static boolean canAddBanned(IProtection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.BANNEDS_ADD_OTHERS.hasPermission(pl);
	}

	public static boolean canRemoveBanned(IProtection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.BANNEDS_REMOVE_OTHERS.hasPermission(pl);
	}

	public static boolean canSeeInformation(IProtection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.INFO_OTHERS.hasPermission(pl);
	}

	public static boolean canKick(IProtection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.KICK_OTHERS.hasPermission(pl);
	}

	public static boolean canFly(IProtection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId());
	}

	public static boolean canSetHome(IProtection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId());
	}

	public static boolean canChangePriority(IProtection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.PRIORITY_OTHERS.hasPermission(pl);
	}

}

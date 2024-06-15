package company.pluginName.Modules.ProtectionsPckg.Utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import company.pluginName.APIs.ItemsAdderAPI.ItemsAdderAPI;
import company.pluginName.APIs.ItemsAdderAPI.Hook.ItemsAdderHook;
import company.pluginName.APIs.OraxenAPI.OraxenAPI;
import company.pluginName.APIs.OraxenAPI.Hook.OraxenHook;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.Blocks.BlockUtilities;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.SkinUtilities;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;

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

	public static boolean canDelete(Protection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.DELETE_OTHERS.hasPermission(pl);
	}

	public static boolean canManage(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canChangeId(Protection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canRename(Protection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canChangeDisplayItem(Protection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canToggleBlock(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canViewBoundaries(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canTeleport(Protection protection, Player pl) {
		return (protection.isOwner(pl.getUniqueId())
				|| protection.isMember(pl.getUniqueId()) && PermissionsService.TELEPORT.hasPermission(pl))
				|| PermissionsService.TELEPORT_OTHERS.hasPermission(pl);
	}

	public static boolean canAddOwner(Protection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.OWNERS_ADD_OTHERS.hasPermission(pl);
	}

	public static boolean canRemoveOwner(Protection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.OWNERS_REMOVE_OTHERS.hasPermission(pl);
	}

	public static boolean canAddMember(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.MEMBERS_ADD_OTHERS.hasPermission(pl);
	}

	public static boolean canRemoveMember(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.MEMBERS_REMOVE_OTHERS.hasPermission(pl);
	}

	public static boolean canAddBanned(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.BANNEDS_ADD_OTHERS.hasPermission(pl);
	}

	public static boolean canRemoveBanned(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.BANNEDS_REMOVE_OTHERS.hasPermission(pl);
	}

	public static boolean canSeeInformation(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || PermissionsService.INFO_OTHERS.hasPermission(pl);
	}

	public static boolean canFly(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId());
	}
}

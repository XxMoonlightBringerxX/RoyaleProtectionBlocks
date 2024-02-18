package company.pluginName.Modules.ProtectionsPckg.Utils;

import org.bukkit.entity.Player;

import company.pluginName.Permissions;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;

public class ProtectionUtilities {

	public static boolean canDelete(Protection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || pl.hasPermission(Permissions.PROTECTION_DELETE_OTHERS);
	}

	public static boolean canManage(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || pl.hasPermission(Permissions.PROTECTION_MANAGE_OTHERS);
	}

	public static boolean canChangeId(Protection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || pl.hasPermission(Permissions.PROTECTION_ID_OTHERS);
	}

	public static boolean canToggleBlock(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || pl.hasPermission(Permissions.PROTECTION_TOGGLEBLOCK_OTHERS);
	}

	public static boolean canViewBoundaries(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || pl.hasPermission(Permissions.PROTECTION_VIEW_OTHERS);
	}

	public static boolean canTeleport(Protection protection, Player pl) {
		return (protection.isOwner(pl.getUniqueId())
				|| protection.isMember(pl.getUniqueId()) && pl.hasPermission(Permissions.PROTECTION_TELEPORT))
				|| pl.hasPermission(Permissions.PROTECTION_TELEPORT_OTHERS);
	}

	public static boolean canAddOwner(Protection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || pl.hasPermission(Permissions.PROTECTION_OWNERS_ADD_OTHERS);
	}

	public static boolean canRemoveOwner(Protection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId())
				|| pl.hasPermission(Permissions.PROTECTION_OWNERS_REMOVE_OTHERS);
	}

	public static boolean canAddMember(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || pl.hasPermission(Permissions.PROTECTION_MEMBERS_ADD_OTHERS);
	}

	public static boolean canRemoveMember(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || pl.hasPermission(Permissions.PROTECTION_MEMBERS_REMOVE_OTHERS);
	}

	public static boolean canAddBanned(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || pl.hasPermission(Permissions.PROTECTION_BANNEDS_ADD_OTHERS);
	}

	public static boolean canRemoveBanned(Protection protection, Player pl) {
		return protection.isOwner(pl.getUniqueId()) || pl.hasPermission(Permissions.PROTECTION_BANNEDS_REMOVE_OTHERS);
	}
}

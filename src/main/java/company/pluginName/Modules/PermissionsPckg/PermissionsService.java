package company.pluginName.Modules.PermissionsPckg;

import java.util.Objects;

import org.bukkit.entity.Player;

import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import darkpanda73.PandaUtils.Services.PandaPermissionsModule.PandaPermissionsService;
import darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects.PandaCustomizablePermission;
import darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects.PandaParametizedPermission;
import darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects.PandaPermission;

public class PermissionsService extends PandaPermissionsService {

	// Normal user permissions
	public static final PandaPermission TELEPORT = new PandaCustomizablePermission("Teleport",
			"protectionblocks.teleport");
	public static final PandaPermission TELEPORT_OTHERS = new PandaCustomizablePermission("Teleport.Others",
			"protectionblocks.teleport.others");
	public static final PandaPermission TELEPORT_BYPASS = new PandaCustomizablePermission("Teleport.Bypass",
			"protectionblocks.teleport.bypass");

	public static final PandaPermission LIST_OTHERS = new PandaCustomizablePermission("List.Others",
			"protectionblocks.list.others");

	public static final PandaPermission DELETE_OTHERS = new PandaCustomizablePermission("Delete.Others",
			"protectionblocks.delete.others");

	public static final PandaPermission MANAGE_OTHERS = new PandaCustomizablePermission("Manage.Others",
			"protectionblocks.manage.others");

	public static final PandaPermission MEMBERS_ADD_OTHERS = new PandaCustomizablePermission("Members.Add.Others",
			"protectionblocks.members.add.others");
	public static final PandaPermission MEMBERS_REMOVE_OTHERS = new PandaCustomizablePermission("Members.Remove.Others",
			"protectionblocks.members.remove.others");

	public static final PandaPermission BANNEDS_ADD_OTHERS = new PandaCustomizablePermission("Banneds.Add.Others",
			"protectionblocks.banneds.add.others");
	public static final PandaPermission BANNEDS_REMOVE_OTHERS = new PandaCustomizablePermission("Banneds.Remove.Others",
			"protectionblocks.banneds.remove.others");
	public static final PandaPermission BANNEDS_BYPASS = new PandaCustomizablePermission("Banneds.Bypass",
			"protectionblocks.banneds.bypass");

	public static final PandaPermission KICK_OTHERS = new PandaCustomizablePermission("Kick.Others",
			"protectionblocks.kick.others");
	public static final PandaPermission KICK_BYPASS = new PandaCustomizablePermission("Kick.Bypass",
			"protectionblocks.kick.bypass");

	public static final PandaPermission OWNERS_ADD_OTHERS = new PandaCustomizablePermission("Owners.Add.Others",
			"protectionblocks.owners.add.others");
	public static final PandaPermission OWNERS_REMOVE_OTHERS = new PandaCustomizablePermission("Owners.Remove.Others",
			"protectionblocks.owners.remove.others");

	public static final PandaPermission INFO_OTHERS = new PandaCustomizablePermission("Info.Others",
			"protectionblocks.info.others");

	public static final PandaPermission FLY_BYPASS = new PandaCustomizablePermission("Fly.Bypass",
			"protectionblocks.fly.bypass");

	public static final PandaPermission OVERLAP_BYPASS = new PandaCustomizablePermission("Overlap.Bypass",
			"protectionblocks.overlap.bypass");

	public static final PandaPermission ECONOMY_BYPASS = new PandaCustomizablePermission("Economy.Bypass",
			"protectionblocks.economy.bypass");

	public static final PandaPermission BLOCKS_CREATE = new PandaCustomizablePermission("Blocks.Create",
			"protectionblocks.blocks.create");
	public static final PandaPermission BLOCKS_EDIT = new PandaCustomizablePermission("Blocks.Edit",
			"protectionblocks.blocks.edit");
	public static final PandaPermission BLOCKS_DELETE = new PandaCustomizablePermission("Blocks.Delete",
			"protectionblocks.blocks.delete");

	public static final PandaParametizedPermission MAX_TEMPLATE = new PandaParametizedPermission("World.Max.Template",
			"protectionblocks.max.{max}");
	public static final PandaPermission MAX_BYPASS = new PandaCustomizablePermission("Max.Bypass",
			"protectionblocks.max.bypass");
	public static final PandaParametizedPermission BLOCK_MAX_TEMPLATE = new PandaParametizedPermission(
			"Blocks.Max.Template", "protectionblocks.{block}.max.{max}");
	public static final PandaParametizedPermission BLOCK_MAX_BYPASS = new PandaParametizedPermission(
			"Blocks.Max.Bypass", "protectionblocks.{block}.max.bypass");

	public static Integer getGeneralMaxCapacity(Player pl) {
		return MAX_TEMPLATE.findPermissions(pl).entrySet().stream()
				.map(entry -> entry.getValue().containsKey("max") ? stringToInt(entry.getValue().get("max")) : null)
				.filter(Objects::nonNull).sorted((int1, int2) -> int2.compareTo(int1)).findFirst().orElse(null);
	}

	public static Integer getPerBlockMaxCapacity(Player pl, ProtectionBlock block) {
		return BLOCK_MAX_TEMPLATE.findPermissions(pl).entrySet().stream()
				.filter(entry -> block.getInformation().getId()
						.equalsIgnoreCase(entry.getValue().getOrDefault("block", null)))
				.map(entry -> entry.getValue().containsKey("max") ? stringToInt(entry.getValue().get("max")) : null)
				.filter(Objects::nonNull).sorted((int1, int2) -> int2.compareTo(int1)).findFirst().orElse(null);
	}

	public static Integer stringToInt(String number) {
		try {
			return Integer.parseInt(number);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}

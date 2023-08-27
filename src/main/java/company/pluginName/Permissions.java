package company.pluginName;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;

public class Permissions {

	public static final String PROTECTION_LIST_OTHERS = "protectionblocks.list.others";
	public static final String PROTECTION_DELETE_OTHERS = "protectionblocks.delete.others";
	public static final String PROTECTION_MANAGE_OTHERS = "protectionblocks.manage.others";
	public static final String PROTECTION_RENAME_OTHERS = "protectionblocks.rename.others";
	public static final String PROTECTION_VIEW_OTHERS = "protectionblocks.view.others";
	public static final String PROTECTION_TOGGLEBLOCK_OTHERS = "protectionblocks.toggleblock.others";
	public static final String PROTECTION_MEMBERS_ADD_OTHERS = "protectionblocks.members.add.others";
	public static final String PROTECTION_MEMBERS_REMOVE_OTHERS = "protectionblocks.members.remove.others";
	public static final String PROTECTION_BANNEDS_ADD_OTHERS = "protectionblocks.banneds.add.others";
	public static final String PROTECTION_BANNEDS_REMOVE_OTHERS = "protectionblocks.banneds.remove.others";
	public static final String PROTECTION_BANNEDS_BYPASS = "protectionblocks.banneds.bypass";
	public static final String PROTECTION_KICK_BYPASS = "protectionblocks.kick.bypass";
	public static final String PROTECTION_OWNERS_ADD_OTHERS = "protectionblocks.owners.add.others";
	public static final String PROTECTION_OWNERS_REMOVE_OTHERS = "protectionblocks.owners.remove.others";
	public static final String PROTECTION_MAX_BYPASS = "protectionblocks.max.bypass";

	public static final String PROTECTION_RELOAD = "protectionblocks.reload";

	public static final String PROTECTION_BLOCKS = "protectionblocks.blocks";
	public static final String PROTECTION_BLOCKS_CREATE = "protectionblocks.blocks.create";
	public static final String PROTECTION_BLOCKS_EDIT = "protectionblocks.blocks.edit";
	public static final String PROTECTION_BLOCKS_DELETE = "protectionblocks.blocks.delete";
	public static final String PROTECTION_BLOCKS_GIVE = "protectionblocks.blocks.give";

	public static final String PROTECTION_FILES = "protectionblocks.files";
	public static final String PROTECTION_FILES_EXPORT = "protectionblocks.files.export";
	public static final String PROTECTION_FILES_IMPORT = "protectionblocks.files.import";

	private static final String PROTECTION_MAX_PREFIX = "protectionblocks.max.";

	public static int getMaxCapacity(Player pl) {
		int i = 0;
		for (PermissionAttachmentInfo perm : pl.getEffectivePermissions()) {
			String p = perm.getPermission().toLowerCase();
			if (perm.getValue() && p.startsWith(PROTECTION_MAX_PREFIX)) {
				try {
					i = Integer.parseInt(p.substring(PROTECTION_MAX_PREFIX.length()));
					break;
				} catch (NumberFormatException e) {
					MessageBuilder.createMessage(MessageString.applyPrefix(
							"There's something wrong with " + pl.getName() + "'s permission: " + perm.getPermission()))
							.sendMessage(Bukkit.getConsoleSender());
					break;
				}
			}
		}
		return i;
	}

}

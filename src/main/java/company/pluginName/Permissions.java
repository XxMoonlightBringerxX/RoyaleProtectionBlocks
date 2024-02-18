package company.pluginName;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;

public class Permissions {

	public static final String PROTECTION_TELEPORT = "protectionblocks.teleport";
	public static final String PROTECTION_TELEPORT_OTHERS = "protectionblocks.teleport.others";
	public static final String PROTECTION_LIST_OTHERS = "protectionblocks.list.others";
	public static final String PROTECTION_DELETE_OTHERS = "protectionblocks.delete.others";
	public static final String PROTECTION_MANAGE_OTHERS = "protectionblocks.manage.others";
	public static final String PROTECTION_ID_OTHERS = "protectionblocks.id.others";
	public static final String PROTECTION_RENAME_OTHERS = "protectionblocks.rename.others";
	public static final String PROTECTION_VIEW_OTHERS = "protectionblocks.view.others";
	public static final String PROTECTION_TOGGLEBLOCK_OTHERS = "protectionblocks.toggleblock.others";
	public static final String PROTECTION_MEMBERS_ADD_OTHERS = "protectionblocks.members.add.others";
	public static final String PROTECTION_MEMBERS_REMOVE_OTHERS = "protectionblocks.members.remove.others";
	public static final String PROTECTION_BANNEDS_ADD_OTHERS = "protectionblocks.banneds.add.others";
	public static final String PROTECTION_BANNEDS_REMOVE_OTHERS = "protectionblocks.banneds.remove.others";
	public static final String PROTECTION_BANNEDS_BYPASS = "protectionblocks.banneds.bypass";
	public static final String PROTECTION_KICK_OTHERS = "protectionblocks.kick.others";
	public static final String PROTECTION_KICK_BYPASS = "protectionblocks.kick.bypass";
	public static final String PROTECTION_OWNERS_ADD_OTHERS = "protectionblocks.owners.add.others";
	public static final String PROTECTION_OWNERS_REMOVE_OTHERS = "protectionblocks.owners.remove.others";
	public static final String PROTECTION_OVERLAP_BYPASS = "protectionblocks.overlap.bypass";
	public static final String PROTECTION_MAX_BYPASS = "protectionblocks.max.bypass";
	public static final String PROTECTION_ECONOMY_BYPASS = "protectionblocks.economy.bypass";
	public static final String PROTECTION_TELEPORT_BYPASS = "protectionblocks.teleport.bypass";

	public static final String PROTECTION_RELOAD = "protectionblocks.reload";
	public static final String PROTECTION_TRANSFER = "protectionblocks.transfer";
	public static final String PROTECTION_PURGE = "protectionblocks.purge";
	public static final String PROTECTION_SPAWN = "protectionblocks.setspawn";
	public static final String PROTECTION_SETSPAWN = "protectionblocks.setspawn";

	public static final String PROTECTION_BLOCKS = "protectionblocks.blocks";
	public static final String PROTECTION_BLOCKS_CREATE = "protectionblocks.blocks.create";
	public static final String PROTECTION_BLOCKS_EDIT = "protectionblocks.blocks.edit";
	public static final String PROTECTION_BLOCKS_DELETE = "protectionblocks.blocks.delete";
	public static final String PROTECTION_BLOCKS_GIVE = "protectionblocks.blocks.give";

	public static final String PROTECTION_FILES = "protectionblocks.files";
	public static final String PROTECTION_FILES_EXPORT = "protectionblocks.files.export";
	public static final String PROTECTION_FILES_IMPORT = "protectionblocks.files.import";

	private static final String PROTECTION_MAX_PREFIX = "protectionblocks.max.";

	private static final String PROTECTION_BLOCK_MAX_PREFIX = "protectionblocks.{block}.max.";

	public static Integer getGeneralMaxCapacity(Player pl) {
		Integer i = null;
		Integer increase = 0;
		for (PermissionAttachmentInfo perm : pl.getEffectivePermissions()) {
			String p = perm.getPermission().toLowerCase();
			if (perm.getValue()) {
				if (p.startsWith(PROTECTION_MAX_PREFIX)) {
					try {
						String fragment = p.substring(PROTECTION_MAX_PREFIX.length());
						if (fragment.length() > 0) {
							if (fragment.charAt(0) == '+' || fragment.charAt(0) == '-') {
								increase += Integer.parseInt(fragment);
							} else if (i == null) {
								i = Integer.parseInt(fragment);
							}
						}
					} catch (NumberFormatException e) {
						MessageTemplate
								.inst(PandaPrefixedStringField.applyPrefix("There's something wrong with "
										+ pl.getName() + "'s permission: " + perm.getPermission()))
								.process().sendMessage(Bukkit.getConsoleSender());
						i = null;
						continue;
					}
				}
			}
		}
		return i != null ? (i + increase) : null;
	}

	public static Integer getPerBlockMaxCapacity(Player pl, ProtectionBlock block) {
		Integer i = null;
		Integer increase = 0;
		String blockPermissionPrefix = block != null
				? PROTECTION_BLOCK_MAX_PREFIX.replace("{block}", block.getInformation().getId())
				: null;
		for (PermissionAttachmentInfo perm : pl.getEffectivePermissions()) {
			String p = perm.getPermission().toLowerCase();
			if (perm.getValue()) {
				if (blockPermissionPrefix != null && p.startsWith(blockPermissionPrefix)) {
					try {
						String fragment = p.substring(blockPermissionPrefix.length());
						if (fragment.length() > 0) {
							if (fragment.charAt(0) == '+' || fragment.charAt(0) == '-') {
								increase += Integer.parseInt(fragment);
							} else if (i == null) {
								i = Integer.parseInt(fragment);
							}
						}
					} catch (NumberFormatException e) {
						MessageTemplate
								.inst(PandaPrefixedStringField.applyPrefix("There's something wrong with "
										+ pl.getName() + "'s permission: " + perm.getPermission()))
								.process().sendMessage(Bukkit.getConsoleSender());
						continue;
					}
				}
			}
		}
		return i != null ? (i + increase) : null;
	}

}

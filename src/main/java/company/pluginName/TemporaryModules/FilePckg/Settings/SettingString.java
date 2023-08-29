package company.pluginName.TemporaryModules.FilePckg.Settings;

import company.pluginName.Permissions;
import company.pluginName.TemporaryModules.FilePckg.FileModuleObjects.FileObjFieldsEnum;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public enum SettingString implements FileObjFieldsEnum<String> {
	SETTINGS_PROTECTION_STARTERBLOCK("Settings.Protection.Starter-block", "Settings.Protection.Stater-block", ""),
	SETTINGS_PROTECTION_SENDTOWORLDONKICK("Settings.Protection.Send-to-world-on-kick", "world"),

	COMMANDS_PROTECTIONBLOCKS_NAME("Commands.Protection-blocks.Name", "protectionblocks"),
	COMMANDS_PROTECTIONBLOCKS_DESCRIPTION("Commands.Protection-blocks.Description", "Open the GUI or execute commands"),
	COMMANDS_PROTECTIONBLOCKS_USAGE("Commands.Protection-blocks.Usage", "[help|subcommand]"),
	COMMANDS_PROTECTIONBLOCKS_PERMISSION("Commands.Protection-blocks.Permission", ""),

	COMMANDS_PROTECTIONBLOCKS_LIST_NAME("Commands.Protection-blocks.List.Name", "list"),
	COMMANDS_PROTECTIONBLOCKS_LIST_DESCRIPTION("Commands.Protection-blocks.List.Description",
			"Open a list with all your protections"),
	COMMANDS_PROTECTIONBLOCKS_LIST_USAGE("Commands.Protection-blocks.List.Usage", ""),
	COMMANDS_PROTECTIONBLOCKS_LIST_PERMISSION("Commands.Protection-blocks.List.Permission", ""),

	COMMANDS_PROTECTIONBLOCKS_ADDMEMBER_NAME("Commands.Protection-blocks.Add-member.Name", "addmember"),
	COMMANDS_PROTECTIONBLOCKS_ADDMEMBER_DESCRIPTION("Commands.Protection-blocks.Add-member.Description",
			"Add a member on your current protection"),
	COMMANDS_PROTECTIONBLOCKS_ADDMEMBER_USAGE("Commands.Protection-blocks.Add-member.Usage", "<username>"),
	COMMANDS_PROTECTIONBLOCKS_ADDMEMBER_PERMISSION("Commands.Protection-blocks.Add-member.Name", ""),

	COMMANDS_PROTECTIONBLOCKS_REMOVEMEMBER_NAME("Commands.Protection-blocks.Remove-member.Name", "removemember"),
	COMMANDS_PROTECTIONBLOCKS_REMOVEMEMBER_DESCRIPTION("Commands.Protection-blocks.Remove-member.Description",
			"Remove a member on your current protection"),
	COMMANDS_PROTECTIONBLOCKS_REMOVEMEMBER_USAGE("Commands.Protection-blocks.Remove-member.Usage", "<username>"),
	COMMANDS_PROTECTIONBLOCKS_REMOVEMEMBER_PERMISSION("Commands.Protection-blocks.Remove-member.Permission", ""),

	COMMANDS_PROTECTIONBLOCKS_ADDOWNER_NAME("Commands.Protection-blocks.Add-owner.Name", "addowner"),
	COMMANDS_PROTECTIONBLOCKS_ADDOWNER_DESCRIPTION("Commands.Protection-blocks.Add-owner.Description",
			"Add an owner on your current protection"),
	COMMANDS_PROTECTIONBLOCKS_ADDOWNER_USAGE("Commands.Protection-blocks.Add-owner.Usage", "<username>"),
	COMMANDS_PROTECTIONBLOCKS_ADDOWNER_PERMISSION("Commands.Protection-blocks.Add-owner.Permission", ""),

	COMMANDS_PROTECTIONBLOCKS_REMOVEOWNER_NAME("Commands.Protection-blocks.Remove-owner.Name", "removeowner"),
	COMMANDS_PROTECTIONBLOCKS_REMOVEOWNER_DESCRIPTION("Commands.Protection-blocks.Remove-owner.Description",
			"Remove an owner on your current protection"),
	COMMANDS_PROTECTIONBLOCKS_REMOVEOWNER_USAGE("Commands.Protection-blocks.Remove-owner.Usage", "<username>"),
	COMMANDS_PROTECTIONBLOCKS_REMOVEOWNER_PERMISSION("Commands.Protection-blocks.Remove-owner.Permission", ""),

	COMMANDS_PROTECTIONBLOCKS_BAN_NAME("Commands.Protection-blocks.Ban.Name", "ban"),
	COMMANDS_PROTECTIONBLOCKS_BAN_DESCRIPTION("Commands.Protection-blocks.Ban.Description",
			"Ban a player from your current protection"),
	COMMANDS_PROTECTIONBLOCKS_BAN_USAGE("Commands.Protection-blocks.Ban.Usage", "<username>"),
	COMMANDS_PROTECTIONBLOCKS_BAN_PERMISSION("Commands.Protection-blocks.Ban.Permission", ""),

	COMMANDS_PROTECTIONBLOCKS_UNBAN_NAME("Commands.Protection-blocks.Unban.Name", "unban"),
	COMMANDS_PROTECTIONBLOCKS_UNBAN_DESCRIPTION("Commands.Protection-blocks.Unban.Description",
			"Unban a player from your current protection"),
	COMMANDS_PROTECTIONBLOCKS_UNBAN_USAGE("Commands.Protection-blocks.Unban.Usage", "<username>"),
	COMMANDS_PROTECTIONBLOCKS_UNBAN_PERMISSION("Commands.Protection-blocks.Unban.Permission", ""),

	COMMANDS_PROTECTIONBLOCKS_KICK_NAME("Commands.Protection-blocks.Kick.Name", "kick"),
	COMMANDS_PROTECTIONBLOCKS_KICK_DESCRIPTION("Commands.Protection-blocks.Kick.Description",
			"Kick a player from your current protection"),
	COMMANDS_PROTECTIONBLOCKS_KICK_USAGE("Commands.Protection-blocks.Kick.Usage", "<username>"),
	COMMANDS_PROTECTIONBLOCKS_KICK_PERMISSION("Commands.Protection-blocks.Kick.Permission", ""),

	COMMANDS_PROTECTIONBLOCKS_RELOAD_NAME("Commands.Protection-blocks.Reload.Name", "reload"),
	COMMANDS_PROTECTIONBLOCKS_RELOAD_DESCRIPTION("Commands.Protection-blocks.Reload.Description", "Reload the plugin"),
	COMMANDS_PROTECTIONBLOCKS_RELOAD_USAGE("Commands.Protection-blocks.Reload.Usage", ""),
	COMMANDS_PROTECTIONBLOCKS_RELOAD_PERMISSION("Commands.Protection-blocks.Reload.Permission",
			Permissions.PROTECTION_RELOAD),

	COMMANDS_PROTECTIONBLOCKS_RENAME_NAME("Commands.Protection-blocks.Rename.Name", "rename"),
	COMMANDS_PROTECTIONBLOCKS_RENAME_DESCRIPTION("Commands.Protection-blocks.Rename.Description",
			"Rename your current protection name"),
	COMMANDS_PROTECTIONBLOCKS_RENAME_USAGE("Commands.Protection-blocks.Rename.Usage", "<new name>"),
	COMMANDS_PROTECTIONBLOCKS_RENAME_PERMISSION("Commands.Protection-blocks.Rename.Permission", ""),

	COMMANDS_PROTECTIONBLOCKS_SETHOME_NAME("Commands.Protection-blocks.Set-home.Name", "sethome"),
	COMMANDS_PROTECTIONBLOCKS_SETHOME_DESCRIPTION("Commands.Protection-blocks.Set-home.Description",
			"Set the home on your current protection"),
	COMMANDS_PROTECTIONBLOCKS_SETHOME_USAGE("Commands.Protection-blocks.Set-home.Usage", ""),
	COMMANDS_PROTECTIONBLOCKS_SETHOME_PERMISSION("Commands.Protection-blocks.Set-home.Permission", ""),

	COMMANDS_PROTECTIONBLOCKS_HIDE_NAME("Commands.Protection-blocks.Hide.Name", "hide"),
	COMMANDS_PROTECTIONBLOCKS_HIDE_DESCRIPTION("Commands.Protection-blocks.Hide.Description",
			"Hide the protection block on your current protection"),
	COMMANDS_PROTECTIONBLOCKS_HIDE_USAGE("Commands.Protection-blocks.Hide.Usage", ""),
	COMMANDS_PROTECTIONBLOCKS_HIDE_PERMISSION("Commands.Protection-blocks.Hide.Permission", ""),

	COMMANDS_PROTECTIONBLOCKS_SHOW_NAME("Commands.Protection-blocks.Show.Name", "show"),
	COMMANDS_PROTECTIONBLOCKS_SHOW_DESCRIPTION("Commands.Protection-blocks.Show.Description",
			"Show the protection block on your current protection"),
	COMMANDS_PROTECTIONBLOCKS_SHOW_USAGE("Commands.Protection-blocks.Show.Usage", ""),
	COMMANDS_PROTECTIONBLOCKS_SHOW_PERMISSION("Commands.Protection-blocks.Show.Permission", ""),

	COMMANDS_PROTECTIONBLOCKS_VIEW_NAME("Commands.Protection-blocks.View.Name", "view"),
	COMMANDS_PROTECTIONBLOCKS_VIEW_DESCRIPTION("Commands.Protection-blocks.View.Description",
			"Show the boundaries of your current protection for a certain time"),
	COMMANDS_PROTECTIONBLOCKS_VIEW_USAGE("Commands.Protection-blocks.View.Usage", ""),
	COMMANDS_PROTECTIONBLOCKS_VIEW_PERMISSION("Commands.Protection-blocks.View.Permission", ""),

	COMMANDS_PROTECTIONBLOCKS_TRANSFER_NAME("Commands.Protection-blocks.Transfer.Name", "transfer"),
	COMMANDS_PROTECTIONBLOCKS_TRANSFER_DESCRIPTION("Commands.Protection-blocks.Transfer.Description",
			"Transfer data from a plugin to RoyaleProtectionBlocks"),
	COMMANDS_PROTECTIONBLOCKS_TRANSFER_USAGE("Commands.Protection-blocks.Transfer.Usage", ""),
	COMMANDS_PROTECTIONBLOCKS_TRANSFER_PERMISSION("Commands.Protection-blocks.Transfer.Permission",
			Permissions.PROTECTION_TRANSFER),

	COMMANDS_PROTECTIONBLOCKS_BLOCKS_NAME("Commands.Protection-blocks.Blocks.Name", "blocks"),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_DESCRIPTION("Commands.Protection-blocks.Blocks.Description",
			"Open a list with all the blocks"),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_USAGE("Commands.Protection-blocks.Blocks.Usage", "[help|subcommand]"),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_PERMISSION("Commands.Protection-blocks.Blocks.Permission",
			Permissions.PROTECTION_BLOCKS),

	COMMANDS_PROTECTIONBLOCKS_BLOCKS_ADD_NAME("Commands.Protection-blocks.Blocks.Add.Name", "add"),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_ADD_DESCRIPTION("Commands.Protection-blocks.Blocks.Add.Description",
			"Create a new block"),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_ADD_USAGE("Commands.Protection-blocks.Blocks.Add.Usage",
			"<id> <x> <y> <z> [permission]"),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_ADD_PERMISSION("Commands.Protection-blocks.Blocks.Add.Permission",
			Permissions.PROTECTION_BLOCKS_CREATE),

	COMMANDS_PROTECTIONBLOCKS_BLOCKS_REMOVE_NAME("Commands.Protection-blocks.Blocks.Remove.Name", "remove"),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_REMOVE_DESCRIPTION("Commands.Protection-blocks.Blocks.Remove.Description",
			"Remove an existing block"),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_REMOVE_USAGE("Commands.Protection-blocks.Blocks.Remove.Usage", "<id>"),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_REMOVE_PERMISSION("Commands.Protection-blocks.Blocks.Remove.Permission",
			Permissions.PROTECTION_BLOCKS_DELETE),

	COMMANDS_PROTECTIONBLOCKS_BLOCKS_GIVE_NAME("Commands.Protection-blocks.Blocks.Give.Name", "give"),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_GIVE_DESCRIPTION("Commands.Protection-blocks.Blocks.Give.Description",
			"Give an existing block to yourself or a player"),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_GIVE_USAGE("Commands.Protection-blocks.Blocks.Give.Usage", "<id> [player]"),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_GIVE_PERMISSION("Commands.Protection-blocks.Blocks.Give.Permission",
			Permissions.PROTECTION_BLOCKS_GIVE),

	COMMANDS_PROTECTIONBLOCKS_FILES_NAME("Commands.Protection-blocks.Files.Name", "files"),
	COMMANDS_PROTECTIONBLOCKS_FILES_DESCRIPTION("Commands.Protection-blocks.Files.Description",
			"Manage the importation and exportation of data through files"),
	COMMANDS_PROTECTIONBLOCKS_FILES_USAGE("Commands.Protection-blocks.Files.Usage", "[help|subcommand]"),
	COMMANDS_PROTECTIONBLOCKS_FILES_PERMISSION("Commands.Protection-blocks.Files.Permission",
			Permissions.PROTECTION_FILES),

	COMMANDS_PROTECTIONBLOCKS_FILES_EXPORT_NAME("Commands.Protection-blocks.Files.Export.Name", "export"),
	COMMANDS_PROTECTIONBLOCKS_FILES_EXPORT_DESCRIPTION("Commands.Protection-blocks.Files.Export.Description",
			"Export all the information of an specific type of data to its respective file"),
	COMMANDS_PROTECTIONBLOCKS_FILES_EXPORT_USAGE("Commands.Protection-blocks.Files.Export.Usage", "<blocks>"),
	COMMANDS_PROTECTIONBLOCKS_FILES_EXPORT_PERMISSION("Commands.Protection-blocks.Files.Export.Permission",
			Permissions.PROTECTION_FILES_EXPORT),

	COMMANDS_PROTECTIONBLOCKS_FILES_IMPORT_NAME("Commands.Protection-blocks.Files.Import.Name", "import"),
	COMMANDS_PROTECTIONBLOCKS_FILES_IMPORT_DESCRIPTION("Commands.Protection-blocks.Files.Import.Description",
			"Import all the information of an specific type of data from its respective file"),
	COMMANDS_PROTECTIONBLOCKS_FILES_IMPORT_USAGE("Commands.Protection-blocks.Files.Import.Usage", "<blocks>"),
	COMMANDS_PROTECTIONBLOCKS_FILES_IMPORT_PERMISSION("Commands.Protection-blocks.Files.Import.Permission",
			Permissions.PROTECTION_FILES_IMPORT),

	COMMANDS_GENERIC_HELP_NAME("Commands.Generic.Help.Name", "help"),
	COMMANDS_GENERIC_HELP_DESCRIPTION("Commands.Generic.Help.Description", "Get all the commands"),
	COMMANDS_GENERIC_HELP_USAGE("Commands.Generic.Help.Usage", "[page]"),
	COMMANDS_GENERIC_HELP_PERMISSION("Commands.Generic.Help.Permission", "");

	private @NonNull @Getter String path;
	private @NonNull @Getter String oldPath;
	private @NonNull @Getter String defaultContent;
	private @Setter String content;

	SettingString(String path, String defaultContent) {
		this(path, path, defaultContent);
	}

	@Override
	public String getContent() {
		return (this.content != null) ? this.content : this.defaultContent;
	}

	@Override
	public String toString() {
		return String.valueOf(getContent());
	}

	public <T extends Enum<T>> T toEnum(Class<T> enumClass) {
		return Enum.valueOf(enumClass, getContent());
	}

	@Override
	public Type getType() {
		return Type.STRING;
	}

	public static enum WorldImportationType {
		ONLINE, OFFLINE, NONE
	}

	public static enum WorldExportationType {
		ONLINE, NONE
	}
}

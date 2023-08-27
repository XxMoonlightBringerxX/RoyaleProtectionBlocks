package company.pluginName.TemporaryModules.FilePckg.Settings;

import java.util.Arrays;
import java.util.List;

import company.pluginName.TemporaryModules.FilePckg.FileModuleObjects.FileObjFieldsEnum;

public enum SettingList implements FileObjFieldsEnum<List<String>> {
	SETTINGS_BANNEDWORLDS("Settings.Banned-worlds", Arrays.asList("World1", "World2", "World3")),
	SETTINGS_EDITABLEFLAGS("Settings.Editable-flags", Arrays.asList(
			"tnt|Can TnT explode?|eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWU0MzUyNjgwZDBiYjI5YjkxMzhhZjc4MzMwMWEzOTFiMzQwOTBjYjQ5NDFkNTJjMDg3Y2E3M2M4MDM2Y2I1MSJ9fX0=",
			"chest-access|Can chests be opened by others?|eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRjMzZjOWNiNTBhNTI3YWE1NTYwN2EwZGY3MTg1YWQyMGFhYmFhOTAzZThkOWFiZmM3ODI2MDcwNTU0MGRlZiJ9fX0=",
			"pvp|Is PvP allowed?|eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc2NTM0MTM1M2MwMjllOWI2NTVmNGY1NzkzMWFlNmFkYzJjN2E3M2M2NTc5NDVkOTQ1YTMwNzY0MWQzNzc4In19fQ==")),
	SETTINGS_COMMANDSONCREATION("Settings.Commands-on-creation", Arrays.asList()),
	SETTINGS_COMMANDSONREMOVAL("Settings.Commands-on-removal", Arrays.asList()),

	COMMANDS_PROTECTIONBLOCKS_ALIASES("Commands.Protection-blocks.Aliases", Arrays.asList("pb")),
	COMMANDS_PROTECTIONBLOCKS_LIST_ALIASES("Commands.Protection-blocks.List.Aliases", Arrays.asList("l")),
	COMMANDS_PROTECTIONBLOCKS_ADDMEMBER_ALIASES("Commands.Protection-blocks.Add-member.Aliases", Arrays.asList("am")),
	COMMANDS_PROTECTIONBLOCKS_REMOVEMEMBER_ALIASES("Commands.Protection-blocks.Remove-member.Aliases",
			Arrays.asList("rm")),
	COMMANDS_PROTECTIONBLOCKS_ADDOWNER_ALIASES("Commands.Protection-blocks.Add-owner.Aliases", Arrays.asList("l")),
	COMMANDS_PROTECTIONBLOCKS_REMOVEOWNER_ALIASES("Commands.Protection-blocks.Remove-owner.Aliases",
			Arrays.asList("l")),
	COMMANDS_PROTECTIONBLOCKS_BAN_ALIASES("Commands.Protection-blocks.Ban.Aliases", Arrays.asList("bn")),
	COMMANDS_PROTECTIONBLOCKS_UNBAN_ALIASES("Commands.Protection-blocks.Unban.Aliases", Arrays.asList("ubn")),
	COMMANDS_PROTECTIONBLOCKS_KICK_ALIASES("Commands.Protection-blocks.Kick.Aliases", Arrays.asList("k")),
	COMMANDS_PROTECTIONBLOCKS_RELOAD_ALIASES("Commands.Protection-blocks.Reload.Aliases", Arrays.asList("rd")),
	COMMANDS_PROTECTIONBLOCKS_RENAME_ALIASES("Commands.Protection-blocks.Rename.Aliases", Arrays.asList("rn")),
	COMMANDS_PROTECTIONBLOCKS_SETHOME_ALIASES("Commands.Protection-blocks.Set-home.Aliases", Arrays.asList("sh")),
	COMMANDS_PROTECTIONBLOCKS_HIDE_ALIASES("Commands.Protection-blocks.Hide.Aliases", Arrays.asList("h")),
	COMMANDS_PROTECTIONBLOCKS_SHOW_ALIASES("Commands.Protection-blocks.Show.Aliases", Arrays.asList("sw")),
	COMMANDS_PROTECTIONBLOCKS_VIEW_ALIASES("Commands.Protection-blocks.View.Aliases", Arrays.asList("v")),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_ALIASES("Commands.Protection-blocks.Blocks.Aliases", Arrays.asList("b")),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_ADD_ALIASES("Commands.Protection-blocks.Blocks.Add.Aliases", Arrays.asList("a")),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_REMOVE_ALIASES("Commands.Protection-blocks.Blocks.Remove.Aliases",
			Arrays.asList("r")),
	COMMANDS_PROTECTIONBLOCKS_BLOCKS_GIVE_ALIASES("Commands.Protection-blocks.Blocks.Give.Aliases", Arrays.asList("g")),
	COMMANDS_PROTECTIONBLOCKS_FILES_ALIASES("Commands.Protection-blocks.Files.Aliases", Arrays.asList("f")),
	COMMANDS_PROTECTIONBLOCKS_FILES_EXPORT_ALIASES("Commands.Protection-blocks.Files.Export.Aliases",
			Arrays.asList("e")),
	COMMANDS_PROTECTIONBLOCKS_FILES_IMPORT_ALIASES("Commands.Protection-blocks.Files.Import.Aliases",
			Arrays.asList("i")),
	COMMANDS_GENERIC_HELP_ALIASES("Commands.Generic.Help.Aliases", Arrays.asList("h"));

	// Methods
	String oldpath, path;
	List<String> content, defaultcontent;

	SettingList(String path, List<String> defaultcontent) {
		this(path, path, defaultcontent);
	}

	SettingList(String path, String oldpath, List<String> defaultcontent) {
		this.path = path;
		this.oldpath = oldpath;
		this.defaultcontent = defaultcontent;
	}

	@Override
	public String toString() {
		return String.valueOf(getContent());
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getOldPath() {
		return oldpath;
	}

	@Override
	public List<String> getDefaultContent() {
		return defaultcontent;
	}

	@Override
	public List<String> getContent() {
		return content != null ? content : defaultcontent;
	}

	@Override
	public void setContent(List<String> content) {
		this.content = content;
	}

	@Override
	public Type getType() {
		return Type.STRING_LIST;
	}

}
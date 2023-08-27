package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Files;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Bukkit.Inventories.ProtectionBlocks.ProtectionBlocksListInventory;
import company.pluginName.Modules.CommandsPckg.Base.HelpSubCommand;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;

public class FilesCommand extends Command {

	public FilesCommand(Command command) {
		super(command, "files", SettingString.COMMANDS_PROTECTIONBLOCKS_FILES_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_FILES_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_FILES_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_FILES_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_FILES_ALIASES.getContent());
		addCommand(new ExportSubCommand(this));
		addCommand(new ImportSubCommand(this));
		sortCommands();
		addCommand(new HelpSubCommand(this), 0);
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean userids) {
		if (!getPermission().isEmpty() && !sender.hasPermission(getPermission())) {
			return false;
		}

		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null && args.length == 0) {
			new ProtectionBlocksListInventory(pl).openInventory();
			return true;
		}
		return super.execute(cmd, sender, args, userids);
	}
}

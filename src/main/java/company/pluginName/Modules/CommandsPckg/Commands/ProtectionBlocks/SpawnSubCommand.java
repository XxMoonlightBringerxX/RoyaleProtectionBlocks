package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;

public class SpawnSubCommand extends SubCommand {

	public SpawnSubCommand(Command command) {
		super(command, "setspawn", SettingString.COMMANDS_PROTECTIONBLOCKS_SPAWN_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_SPAWN_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_SPAWN_DESCRIPTION.toString(), "",
				SettingList.COMMANDS_PROTECTIONBLOCKS_SPAWN_ALIASES.getContent());
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			pl.teleport(MainPluginClass.getPlugin().getProtectionSettingsModule().getSpawn());
		} else {
			MessageBuilder.createMessage(MessageString.ERROR_CONSOLEDENIED.applyPrefix()).sendMessage(sender);
		}
		return true;
	}

}

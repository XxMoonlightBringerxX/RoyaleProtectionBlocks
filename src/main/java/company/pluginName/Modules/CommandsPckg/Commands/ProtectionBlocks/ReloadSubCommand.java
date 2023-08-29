package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.command.CommandSender;

import company.pluginName.MainPluginClass;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;

public class ReloadSubCommand extends SubCommand {

	public ReloadSubCommand(Command command) {
		super(command, "reload", SettingString.COMMANDS_PROTECTIONBLOCKS_RELOAD_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_RELOAD_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_RELOAD_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_RELOAD_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_RELOAD_ALIASES.getContent());
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		MainPluginClass.getPlugin().reloadPlugin();
		MessageBuilder.createMessage(MessageString.MESSAGE_RELOAD.applyPrefix()).sendMessage(sender);
		return true;
	}

}

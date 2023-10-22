package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;

public class SetSpawnSubCommand extends SubCommand {

	public SetSpawnSubCommand(Command command) {
		super(command, "setspawn", SettingString.COMMANDS_PROTECTIONBLOCKS_SETSPAWN_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_SETSPAWN_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_SETSPAWN_DESCRIPTION.toString(), "",
				SettingList.COMMANDS_PROTECTIONBLOCKS_SETSPAWN_ALIASES.getContent());
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			try {
				MainPluginClass.getPlugin().getProtectionSettingsModule().setSpawn(pl.getLocation());

				MessageBuilder.createMessage(MessageString.MESSAGE_SETSPAWNSUCCESSFULLY.applyPrefix()).sendMessage(pl);
			} catch (YamlException | IOException e) {
				MessageBuilder.createMessage(MessageString.ERROR_SETSPAWNERROR.applyPrefix()).sendMessage(pl);
				e.printStackTrace();
			}
		} else {
			MessageBuilder.createMessage(MessageString.ERROR_CONSOLEDENIED.applyPrefix()).sendMessage(sender);
		}
		return true;
	}

}

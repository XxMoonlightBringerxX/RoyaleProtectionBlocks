package company.pluginName.Modules.CommandsPckg.Base;

import company.pluginName.TemporaryModules.FilePckg.Messages.MessageList;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.HelpCommand;

public class HelpSubCommand extends HelpCommand {

	public HelpSubCommand(Command command) {
		super(command, SettingString.COMMANDS_GENERIC_HELP_NAME.toString(),
				SettingString.COMMANDS_GENERIC_HELP_PERMISSION.toString(),
				SettingString.COMMANDS_GENERIC_HELP_DESCRIPTION.toString(),
				SettingString.COMMANDS_GENERIC_HELP_USAGE.toString(),
				SettingList.COMMANDS_GENERIC_HELP_ALIASES.getContent());
		this.setHeader(() -> MessageList.MESSAGE_HELPER_HEADER.getContent())
				.setBody(() -> MessageList.MESSAGE_HELPER_BODY.getContent())
				.setFooter(() -> MessageList.MESSAGE_HELPER_FOOTER.getContent())
				.setUnavailableLeftArrow(() -> MessageString.MESSAGE_HELPER_UNAVAILABLELEFTARROW.getContent())
				.setAvailableLeftArrow(() -> MessageString.MESSAGE_HELPER_AVAILABLELEFTARROW.getContent())
				.setUnavailableRightArrow(() -> MessageString.MESSAGE_HELPER_UNAVAILABLERIGHTARROW.getContent())
				.setAvailableRightArrow(() -> MessageString.MESSAGE_HELPER_AVAILABLERIGHTARROW.getContent());
	}

}
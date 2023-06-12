package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;

public class ViewSubCommand extends SubCommand {

	public ViewSubCommand(Command command) {
		super(command, "view", SettingString.COMMANDS_PROTECTIONBLOCKS_VIEW_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_VIEW_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_VIEW_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_VIEW_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_VIEW_ALIASES.getContent());
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			Protection protection = MainPluginClass.getPlugin().getProtectionsModule()
					.getProtectionByLocation(pl.getLocation());
			if (protection != null) {
				if (protection.isMainOwner(pl.getUniqueId())) {
					protection.toggleProtectionView();
				} else {
					MessageBuilder.createMessage(MessageString.ERROR_PROTECTIONS_NOTMAINOWNER.applyPrefix())
							.sendMessage(sender);
				}
			} else {
				MessageBuilder.createMessage(MessageString.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix())
						.sendMessage(sender);
			}
		} else {
			MessageBuilder.createMessage(MessageString.ERROR_CONSOLEDENIED.applyPrefix()).sendMessage(sender);
		}
		return true;
	}

}

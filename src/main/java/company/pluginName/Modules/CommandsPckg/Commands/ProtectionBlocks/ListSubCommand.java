package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Bukkit.Inventories.Protections.ProtectionsListInventory;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;

public class ListSubCommand extends SubCommand {

	public ListSubCommand(Command command) {
		super(command, "list", SettingString.COMMANDS_PROTECTIONBLOCKS_LIST_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_LIST_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_LIST_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_LIST_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_LIST_ALIASES.getContent());
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			new ProtectionsListInventory(pl).openInventory();
		} else {
			MessageBuilder.createMessage(MessageString.applyPrefix(MessageString.ERROR_CONSOLEDENIED))
					.sendMessage(sender);
		}
		return true;
	}
}

package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Permissions;
import company.pluginName.Bukkit.Inventories.Protections.ProtectionsListInventory;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import company.pluginName.Utils.OfflinePlayerUtils;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;

public class ListSubCommand extends SubCommand {

	public ListSubCommand(Command command) {
		super(command, "list", SettingString.COMMANDS_PROTECTIONBLOCKS_LIST_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_LIST_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_LIST_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_LIST_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_LIST_ALIASES.getContent());
	}

	@Override
	public List<String> tabComplete(Command cmd, CommandSender sender, String[] args) {
		if (args.length == 1) {
			return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
		}
		return EMPTY_LIST;
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (args.length > 1 && pl.hasPermission(Permissions.PROTECTION_LIST_OTHERS)) {
				OfflinePlayer owner = OfflinePlayerUtils.getOfflinePlayer(args[1]);
				if (owner != null) {
					new ProtectionsListInventory(pl, owner).openInventory();
				} else {
					MessageBuilder.createMessage(MessageString.ERROR_PLAYERNOTFOUND.applyPrefix()).sendMessage(sender);
				}
			} else {
				new ProtectionsListInventory(pl).openInventory();
			}
		} else {
			MessageBuilder.createMessage(MessageString.applyPrefix(MessageString.ERROR_CONSOLEDENIED))
					.sendMessage(sender);
		}
		return true;
	}
}

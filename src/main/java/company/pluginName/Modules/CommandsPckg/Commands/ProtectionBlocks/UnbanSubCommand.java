package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.ProtectionBanneds.Delete.ProtectionBannedsDeleteException;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Utils.OfflinePlayerUtils;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;

public class UnbanSubCommand extends SubCommand {

	public UnbanSubCommand(Command command) {
		super(command, "unban", SettingString.COMMANDS_PROTECTIONBLOCKS_UNBAN_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_UNBAN_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_UNBAN_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_UNBAN_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_UNBAN_ALIASES.getContent());
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
			if (args.length > 1) {
				Protection protection = MainPluginClass.getPlugin().getProtectionsModule()
						.getProtectionByLocation(pl.getLocation());
				if (protection != null) {
					OfflinePlayer banned = OfflinePlayerUtils.getOfflinePlayer(args[1]);
					if (banned != null) {
						try {
							protection.getBanneds().remove(pl, banned.getUniqueId());

							MessageBuilder
									.createMessage(
											MessageString.MESSAGE_PROTECTIONS_BANNEDS_REMOVEDSUCCESSFULLY.applyPrefix())
									.sendMessage(sender);
						} catch (ProtectionBannedsDeleteException e) {
							e.sendError(pl);
						}
					} else {
						MessageBuilder.createMessage(MessageString.ERROR_PLAYERNOTFOUND.applyPrefix())
								.sendMessage(sender);
					}
				} else {
					MessageBuilder.createMessage(MessageString.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix())
							.sendMessage(sender);
				}
			} else {
				MessageBuilder.createMessage(MessageString.applyPrefix(getUsage())).sendMessage(pl);
			}
		} else {
			MessageBuilder.createMessage(MessageString.ERROR_CONSOLEDENIED.applyPrefix()).sendMessage(sender);
		}
		return true;
	}
}

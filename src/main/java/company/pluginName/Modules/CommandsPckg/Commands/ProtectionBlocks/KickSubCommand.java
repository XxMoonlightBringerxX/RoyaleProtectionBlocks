package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Permissions;
import company.pluginName.Exceptions.Protection.ProtectionKickDeniedException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;

public class KickSubCommand extends SubCommand {

	public KickSubCommand(Command command) {
		super(command, "kick", SettingString.COMMANDS_PROTECTIONBLOCKS_KICK_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_KICK_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_KICK_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_KICK_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_KICK_ALIASES.getContent());
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
					Player kicked = Bukkit.getPlayer(args[1]);
					if (kicked != null) {
						if (!kicked.hasPermission(Permissions.PROTECTION_KICK_BYPASS)) {
							try {
								if (protection.getActions().kickPlayer(pl, kicked)) {
									MessageBuilder.createMessage(MessageString.MESSAGE_PROTECTIONS_KICKED.applyPrefix())
											.sendMessage(kicked);
									MessageBuilder
											.createMessage(MessageString.MESSAGE_PROTECTIONS_PLAYERKICKED.applyPrefix())
											.sendMessage(sender);
								} else {
									MessageBuilder.createMessage(
											MessageString.MESSAGE_PROTECTIONS_PLAYERNOTINPROTECTION.applyPrefix())
											.sendMessage(sender);
								}
							} catch (ProtectionKickDeniedException e) {
								e.sendError(pl);
							}
						} else {
							MessageBuilder
									.createMessage(MessageString.MESSAGE_PROTECTIONS_PLAYERWITHKICKBYPASS.applyPrefix())
									.sendMessage(sender);
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

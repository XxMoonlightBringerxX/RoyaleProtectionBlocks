package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.ProtectionMembers.Delete.ProtectionMembersDeleteException;
import company.pluginName.Exceptions.ProtectionOwners.Delete.ProtectionOwnersDeleteException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;

public class LeaveSubCommand extends SubCommand {

	public LeaveSubCommand(Command command) {
		super(command, "leave", SettingString.COMMANDS_PROTECTIONBLOCKS_LEAVE_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_LEAVE_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_LEAVE_DESCRIPTION.toString(), "",
				SettingList.COMMANDS_PROTECTIONBLOCKS_LEAVE_ALIASES.getContent());
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			Protection protection = MainPluginClass.getPlugin().getProtectionsModule()
					.getProtectionByLocation(pl.getLocation());
			if (protection != null) {
				try {
					boolean left = false;

					if (protection.getOwners().list().contains(pl.getUniqueId())) {
						protection.getOwners().remove(pl.getUniqueId());
						left = true;
					}

					if (protection.getMembers().list().contains(pl.getUniqueId())) {
						protection.getMembers().remove(pl.getUniqueId());
						left = true;
					}

					if (left) {
						MessageBuilder.createMessage(MessageString.MESSAGE_PROTECTIONS_LEFTSUCCESSFULLY.applyPrefix())
								.sendMessage(sender);
					} else {
						MessageBuilder.createMessage(MessageString.ERROR_PROTECTIONS_NOTMEMBER.applyPrefix())
								.sendMessage(sender);
					}
					protection.getOwners().remove(pl.getUniqueId());
					protection.getMembers().remove(pl.getUniqueId());
				} catch (ProtectionMembersDeleteException | ProtectionOwnersDeleteException e) {
					e.sendError(pl);
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

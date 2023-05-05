package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveException;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;

public class RenameSubCommand extends SubCommand {

	public RenameSubCommand(Command command) {
		super(command, "rename", SettingString.COMMANDS_PROTECTIONBLOCKS_RENAME_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_RENAME_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_RENAME_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_RENAME_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_RENAME_ALIASES.getContent());
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (args.length > 1) {
				Protection protection = MainPluginClass.getPlugin().getProtectionsModule()
						.getProtectionByLocation(pl.getLocation());
				if (protection != null) {
					if (protection.isMainOwner(pl.getUniqueId())) {
						try {
							protection.setDisplayName(Arrays.stream(Arrays.copyOfRange(args, 1, args.length))
									.collect(Collectors.joining(" ")));
							MessageBuilder
									.createMessage(MessageString.MESSAGE_PROTECTIONS_RENAMEDSUCCESSFULLY.applyPrefix())
									.sendMessage(sender);
						} catch (ProtectionSaveException e) {
							e.sendError(pl);
						}
					} else {
						MessageBuilder.createMessage(MessageString.ERROR_PROTECTIONS_NOTMAINOWNER.applyPrefix())
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

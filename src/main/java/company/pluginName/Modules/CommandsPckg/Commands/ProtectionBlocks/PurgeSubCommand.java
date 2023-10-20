package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.PurgeConfiguration;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import darkpanda73.PandaUtils.PandaColors.Objects.TextReplacement;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;

public class PurgeSubCommand extends SubCommand {

	public PurgeSubCommand(Command command) {
		super(command, "purge", SettingString.COMMANDS_PROTECTIONBLOCKS_PURGE_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_PURGE_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_PURGE_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_PURGE_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_PURGE_ALIASES.getContent());
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		if (args.length > 1) {
			PurgeConfiguration purgeConfiguration = new PurgeConfiguration();
			for (int i = 1; i < args.length; i++) {
				switch (args[i].toLowerCase()) {
				case "confirm":
					Bukkit.getScheduler().runTaskAsynchronously(MainPluginClass.getPlugin(), () -> {
						MessageBuilder.createMessage(MessageString.MESSAGE_PURGE_START.applyPrefix()).sendMessage(sender);

						List<Protection> protectionsToPurge = MainPluginClass.getPlugin().getProtectionsRemoverModule()
								.retrieveProtectionsToPurge(purgeConfiguration);

						Bukkit.getScheduler().runTask(MainPluginClass.getPlugin(), () -> {
							List<Protection> removedProtections = MainPluginClass.getPlugin().getProtectionsRemoverModule()
									.purgeProtections(protectionsToPurge);

							MessageBuilder
									.createMessage(TextInput.inst().text(MessageString.MESSAGE_PURGE_END.applyPrefix())
											.replacements(new TextReplacement("{amount}", () -> String.valueOf(removedProtections.size()))))
									.sendMessage(sender);

							if (protectionsToPurge.size() != 0) {
								MessageBuilder
										.createMessage(TextInput.inst().text(MessageString.MESSAGE_PURGE_ERROR.applyPrefix()).replacements(
												new TextReplacement("{amount}", () -> String.valueOf(protectionsToPurge.size()))))
										.sendMessage(sender);
							}
						});
					});
					return true;
				case "--config":
					purgeConfiguration.copy(MainPluginClass.getPlugin().getProtectionsRemoverModule().getConfiguredPurgeConfiguration());
					break;
				case "--days":
				case "--hours":
				case "--minutes":
					if (args.length > i + 1) {
						try {
							Integer number = Integer.parseInt(args[++i]);

							if (number < 0) {
								MessageBuilder.createMessage(MessageString.ERROR_NUMBERBELOWZERO.applyPrefix()).sendMessage(sender);
								return true;
							}

							if (args[i].toLowerCase().equals("--days")) {
								purgeConfiguration.setDays(number);
							} else if (args[i].toLowerCase().equals("--hours")) {
								purgeConfiguration.setHours(number);
							} else {
								purgeConfiguration.setMinutes(number);
							}
						} catch (NumberFormatException e) {
							MessageBuilder.createMessage(MessageString.ERROR_INVALIDNUMBER.applyPrefix()).sendMessage(sender);
							return true;
						}
					} else {
						MessageBuilder.createMessage(MessageString.ERROR_PURGE_NOVALUEFORPARAMETER.applyPrefix()).sendMessage(sender);
					}
					break;
				default:
					MessageBuilder.createMessage(MessageString.ERROR_PURGE_INVALIDPARAMETER.applyPrefix()).sendMessage(sender);
					return true;
				}
			}

			Bukkit.getScheduler().runTaskAsynchronously(MainPluginClass.getPlugin(), () -> {
				MessageBuilder.createMessage(MessageString.MESSAGE_PURGE_SEARCH.applyPrefix()).sendMessage(sender);

				List<Protection> protectionsToPurge = MainPluginClass.getPlugin().getProtectionsRemoverModule()
						.retrieveProtectionsToPurge(purgeConfiguration);
				MessageBuilder
						.createMessage(TextInput.inst().text(MessageString.MESSAGE_PURGE_WARNING.applyPrefix())
								.replacements(new TextReplacement("{amount}", () -> String.valueOf(protectionsToPurge.size())),
										new TextReplacement("{command}", () -> String.format("/%s %s confirm", getCommandPath(),
												Arrays.stream(Arrays.copyOfRange(args, 1, args.length)).collect(Collectors.joining(" "))))))
						.sendMessage(sender);
			});
		} else {
			MessageBuilder.createMessage(MessageString.applyPrefix(getUsage())).sendMessage(sender);
		}
		return true;
	}

}

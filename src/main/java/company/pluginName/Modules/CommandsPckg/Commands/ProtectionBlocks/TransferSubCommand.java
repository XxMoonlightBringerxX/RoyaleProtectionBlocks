package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveException;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import darkpanda73.PandaUtils.PandaColors.Objects.TextReplacement;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;

public class TransferSubCommand extends SubCommand {

	private static final List<String> PROTECTION_PLUGINS = Arrays.asList("ProtectionStones");

	public TransferSubCommand(Command command) {
		super(command, "list", SettingString.COMMANDS_PROTECTIONBLOCKS_TRANSFER_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_TRANSFER_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_TRANSFER_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_TRANSFER_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_TRANSFER_ALIASES.getContent());
	}

	@Override
	public List<String> tabComplete(Command cmd, CommandSender sender, String[] args) {
		if (args.length == 1) {
			return PROTECTION_PLUGINS;
		}
		return EMPTY_LIST;
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		if (args.length > 1) {
			switch (args[1].toLowerCase()) {
			case "protectionstones":
				if (MainPluginClass.getProtectionStonesAPI().isHooked()) {
					if (args.length > 2 && args[2].equalsIgnoreCase("confirm")) {
						MessageBuilder.createMessage("Starting transfer process.").sendMessage(sender);

						MainPluginClass.getProtectionStonesAPI().getProtectionBlocks().forEach(block -> {
							try {
								block.save();
							} catch (ProtectionBlocksSaveException e) {
								e.sendError(Bukkit.getConsoleSender());
							}
						});

						MessageBuilder
								.createMessage("Finished transfer process. Attempting to disable protectionstones.")
								.sendMessage(sender);

						Plugin plugin = Bukkit.getPluginManager().getPlugin("ProtectionStones");

						Bukkit.getPluginManager().disablePlugin(plugin);

						MessageBuilder
								.createMessage("Disabled protectionstones. Attempting to remove protectionstones.")
								.sendMessage(sender);

						try {
							Files.delete(new java.io.File(
									plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath())
									.toPath());
							MessageBuilder.createMessage("Protectionstones removed. Shutting down the server.")
									.sendMessage(sender);
						} catch (IOException e) {
							MessageBuilder.createMessage(
									"Protectionstones couldn't be removed. Please remove the file manually. Shutting down the server.")
									.sendMessage(sender);
							e.printStackTrace();
						}

						Bukkit.shutdown();
					} else {
						MessageBuilder
								.createMessage(TextInput.inst()
										.text(MessageString.MESSAGE_TRANSFER_WARNING.applyPrefix())
										.replacements(new TextReplacement("{command}",
												() -> String.format("%s %s confirm", getCommandPath(), args[1]))))
								.sendMessage(sender);
					}
				} else {
					MessageBuilder.createMessage(
							"ProtectionStones is not loaded. Please load ProtectionStones to transfer its data.")
							.sendMessage(sender);
				}
				break;
			default:
				MessageBuilder
						.createMessage(TextInput.inst().text(MessageString.MESSAGE_TRANSFER_AVAILABLELIST.applyPrefix())
								.replacements(new TextReplacement("{list}",
										() -> PROTECTION_PLUGINS.stream().collect(Collectors.joining(", ")))))
						.sendMessage(sender);
				break;
			}
		} else {
			MessageBuilder.createMessage(MessageString.applyPrefix(getUsage())).sendMessage(sender);
		}
		return true;
	}
}

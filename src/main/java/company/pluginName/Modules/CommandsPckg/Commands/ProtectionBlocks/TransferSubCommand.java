package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import company.pluginName.MainPluginClass;
import company.pluginName.APIs.ProtectionStonesAPI.TransferResult;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageList;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import darkpanda73.PandaUtils.PandaColors.Objects.TextReplacement;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;

public class TransferSubCommand extends SubCommand {

	private static final List<String> PROTECTION_PLUGINS = Arrays.asList("ProtectionStones");
	private static final List<String> SECOND_ARG = Arrays.asList("confirm");

	public TransferSubCommand(Command command) {
		super(command, "transfer", SettingString.COMMANDS_PROTECTIONBLOCKS_TRANSFER_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_TRANSFER_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_TRANSFER_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_TRANSFER_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_TRANSFER_ALIASES.getContent());
	}

	@Override
	public List<String> tabComplete(Command cmd, CommandSender sender, String[] args) {
		if (args.length == 1) {
			return PROTECTION_PLUGINS;
		} else if (args.length == 2) {
			return SECOND_ARG;
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
						long start = System.currentTimeMillis();

						MessageBuilder.createMessage(MessageString.MESSAGE_TRANSFER_START.applyPrefix()).sendMessage(sender);

						TransferResult<ProtectionBlock> protectionBlocksResult = MainPluginClass.getProtectionStonesAPI()
								.transferProtectionBlocks();
						TransferResult<Protection> protectionsResult = MainPluginClass.getProtectionStonesAPI().transferProtections();

						MessageBuilder
								.createMessage(TextInput.inst().text(MessageList.MESSAGE_TRANSFER_RESULT.toArray()).replacements(
										new TextReplacement("{transferred_protection_blocks}",
												() -> String.valueOf(protectionBlocksResult.getSuccessList().size())),
										new TextReplacement("{failed_protection_blocks}",
												() -> String.valueOf(protectionBlocksResult.getExceptionsList().size())),
										new TextReplacement(
												"{transferred_protections}",
												() -> String.valueOf(protectionsResult.getSuccessList().size())),
										new TextReplacement("{failed_protections}",
												() -> String.valueOf(protectionsResult.getExceptionsList().size())),
										new TextReplacement("{errors_in_console}",
												() -> String.valueOf(protectionBlocksResult.isErrorsInConsole()
														|| protectionsResult.isErrorsInConsole())),
										new TextReplacement("{required_time}", () -> String.valueOf(System.currentTimeMillis() - start))))
								.sendMessage(sender);

						MessageBuilder.createMessage(MessageString.MESSAGE_TRANSFER_END.applyPrefix()).sendMessage(sender);
					} else {
						MessageBuilder
								.createMessage(TextInput.inst().text(MessageString.MESSAGE_TRANSFER_WARNING.applyPrefix()).replacements(
										new TextReplacement("{command}", () -> String.format("/%s %s confirm", getCommandPath(), args[1]))))
								.sendMessage(sender);
					}
				} else {
					MessageBuilder.createMessage(MessageString.MESSAGE_TRANSFER_PROTECTIONSTONESNOTLOADED.applyPrefix())
							.sendMessage(sender);
				}
				break;
			default:
				MessageBuilder
						.createMessage(TextInput.inst().text(MessageString.MESSAGE_TRANSFER_AVAILABLELIST.applyPrefix()).replacements(
								new TextReplacement("{list}", () -> PROTECTION_PLUGINS.stream().collect(Collectors.joining(", ")))))
						.sendMessage(sender);
				break;
			}
		} else {
			MessageBuilder.createMessage(MessageString.applyPrefix(getUsage())).sendMessage(sender);
		}
		return true;
	}
}

package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import company.pluginName.APIs.ProtectionStonesAPI.ProtectionStonesAPI;
import company.pluginName.APIs.ProtectionStonesAPI.Hook.ProtectionStonesHook.TransferResult;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "transfer",
		pathName = "Transfer",
		defaultName = "transfer",
		defaultDescription = "Transfer data from a plugin to RoyaleProtectionBlocks",
		defaultAliases = "t",
		defaultUsage = "[plugin to transfer] <confirm>",
		defaultPermission = "protectionblocks.transfer")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class TransferSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionStonesAPI protectionStonesApi;

	private static final List<String> PROTECTION_PLUGINS = Arrays.asList("ProtectionStones");
	private static final List<String> SECOND_ARG = Arrays.asList("confirm");

	public TransferSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public List<String> tabComplete(PandaCommand cmd, CommandSender sender, String[] args) {
		if (args.length == 1) {
			return PROTECTION_PLUGINS;
		} else if (args.length == 2) {
			return SECOND_ARG;
		}
		return EMPTY_LIST;
	}

	@Override
	public CommandResponse executeCommandProcess(PandaCommand cmd, CommandSender sender, String[] args,
			boolean useids) {
		if (args.length > 1) {
			switch (args[1].toLowerCase()) {
			case "protectionstones":
				if (protectionStonesApi.getHook().isHooked()) {
					if (args.length > 2 && args[2].equalsIgnoreCase("confirm")) {
						long start = System.currentTimeMillis();

						MessageTemplate.inst(Messages.MESSAGE_TRANSFER_START.applyPrefix()).process()
								.sendMessage(sender);

						TransferResult<ProtectionBlock> protectionBlocksResult = protectionStonesApi.getHook()
								.transferProtectionBlocks();
						TransferResult<Protection> protectionsResult = protectionStonesApi.getHook()
								.transferProtections();

						MessageTemplate.inst(Messages.MESSAGE_TRANSFER_RESULT.toArray()).setReplacements(
								new Replacement("{transferred_protection_blocks}",
										() -> String.valueOf(protectionBlocksResult.getSuccessList().size())),
								new Replacement("{failed_protection_blocks}",
										() -> String.valueOf(protectionBlocksResult.getExceptionsList().size())),
								new Replacement("{transferred_protections}",
										() -> String.valueOf(protectionsResult.getSuccessList().size())),
								new Replacement("{failed_protections}",
										() -> String.valueOf(protectionsResult.getExceptionsList().size())),
								new Replacement("{errors_in_console}",
										() -> String.valueOf(protectionBlocksResult.isErrorsInConsole()
												|| protectionsResult.isErrorsInConsole())),
								new Replacement("{required_time}",
										() -> String.valueOf(System.currentTimeMillis() - start)))
								.process().sendMessage(sender);

						MessageTemplate.inst(Messages.MESSAGE_TRANSFER_END.applyPrefix()).process().sendMessage(sender);
					} else {
						MessageTemplate.inst(Messages.MESSAGE_TRANSFER_WARNING.applyPrefix())
								.setReplacements(new Replacement("{command}",
										() -> String.format("/%s %s confirm", getCommandPath(), args[1])))
								.process().sendMessage(sender);
					}
				} else {
					MessageTemplate.inst(Messages.MESSAGE_TRANSFER_PROTECTIONSTONESNOTLOADED.applyPrefix()).process()
							.sendMessage(sender);
				}
				break;
			default:
				MessageTemplate.inst(Messages.MESSAGE_TRANSFER_AVAILABLELIST.applyPrefix())
						.setReplacements(new Replacement("{list}",
								() -> PROTECTION_PLUGINS.stream().collect(Collectors.joining(", "))))
						.process().sendMessage(sender);
				break;
			}
		} else {
			MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process().sendMessage(sender);
		}
		return new TrueResponse();
	}
}

package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Hooks.ProtectionStonesAPI.ProtectionStonesAPI;
import company.pluginName.Hooks.ProtectionStonesAPI.Hook.ProtectionStonesHook.TransferResult;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPurgePckg.ProtectionsPurgeService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;

@PandaSubCommandAnnotation(parentCommand = AdminCommand.class)
@PandaCommandAnnotation(
		id = "transfer",
		pathName = "Transfer",
		defaultName = "transfer",
		defaultDescription = "Transfer data from a plugin to RoyaleProtectionBlocks",
		defaultAliases = "t",
		defaultUsage = "[plugin to transfer] <confirm>",
		defaultPermission = "protectionblocks.admin.transfer")
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

	@PandaInject
	private static ProtectionsPurgeService protectionsPurgeService;

	private static final List<String> PROTECTION_PLUGINS = Arrays.asList("ProtectionStones");
	private static final List<String> SECOND_ARG = Arrays.asList("confirm");

	public TransferSubCommand() throws InstantiationException {
		super();
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		if (argIndex == 0) {
			return PROTECTION_PLUGINS;
		} else if (argIndex == 1) {
			return SECOND_ARG;
		}
		return super.generateAutocompleteList(sender, argIndex);
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		return CommandResponse.queuedAsync(() -> {
			if (parameters.getParameters().size() > 0) {
				switch (parameters.getParameters().get(0).toLowerCase()) {
				case "protectionstones":
					if (protectionStonesApi.getHook().isHooked()) {
						if (parameters.getParameters().size() > 1
								&& parameters.getParameters().get(1).equalsIgnoreCase("confirm")) {
							long start = System.currentTimeMillis();

							MessageTemplate.inst(Messages.MESSAGE_TRANSFER_START.applyPrefix()).process()
									.sendMessage(sender);

							try {
								boolean isAutoPurgeRunning = protectionsPurgeService.isRunning();

								if (isAutoPurgeRunning) {
									MessageTemplate
											.inst(PandaPrefixedStringField
													.applyPrefix("&7Disabling purge system temporary."))
											.process().sendMessage(Bukkit.getConsoleSender());
									protectionsPurgeService.disableAutoPurge();
								}

								TransferResult<ProtectionBlock> protectionBlocksResult = protectionStonesApi.getHook()
										.transferProtectionBlocks();

								TransferResult<Protection> protectionsResult = protectionStonesApi.getHook()
										.transferProtections();

								if (isAutoPurgeRunning) {
									MessageTemplate
											.inst(PandaPrefixedStringField.applyPrefix("&7Enabling purge system."))
											.process().sendMessage(Bukkit.getConsoleSender());
									protectionsPurgeService.enableAutoPurge();
								}

								MessageTemplate.inst(Messages.MESSAGE_TRANSFER_RESULT.toArray())
										.setReplacements(
												new Replacement("{transferred_protection_blocks}",
														() -> String
																.valueOf(protectionBlocksResult.getSuccessAmount())),
												new Replacement("{failed_protection_blocks}",
														() -> String.valueOf(protectionBlocksResult.getFailedAmount())),
												new Replacement("{transferred_protections}",
														() -> String.valueOf(protectionsResult.getSuccessAmount())),
												new Replacement("{failed_protections}",
														() -> String.valueOf(protectionsResult.getFailedAmount())),
												new Replacement("{errors_in_console}",
														() -> String.valueOf(protectionBlocksResult.isErrorsInConsole()
																|| protectionsResult.isErrorsInConsole())),
												new Replacement("{required_time}",
														() -> String.valueOf(System.currentTimeMillis() - start)))
										.process().sendMessage(sender);

								MessageTemplate.inst(Messages.MESSAGE_TRANSFER_END.applyPrefix()).process()
										.sendMessage(sender);
							} catch (Exception e) {
								MessageTemplate.inst(Messages.ERROR_ERROR.applyPrefix()).process().sendMessage(sender);
								e.printStackTrace();
							}
						} else {
							MessageTemplate.inst(Messages.MESSAGE_TRANSFER_WARNING.applyPrefix())
									.setReplacements(
											new Replacement("{command}",
													() -> String.format("/%s %s confirm", getCommandPath(),
															parameters.getParameters().get(0))))
									.process().sendMessage(sender);
						}
					} else {
						MessageTemplate.inst(Messages.MESSAGE_TRANSFER_PROTECTIONSTONESNOTLOADED.applyPrefix())
								.process().sendMessage(sender);
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
				MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process()
						.sendMessage(sender);
			}
		});
	}
}

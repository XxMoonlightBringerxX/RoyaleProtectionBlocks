package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPurgePckg.ProtectionsPurgeService;
import company.pluginName.Modules.ProtectionsPurgePckg.Objects.PurgeConfiguration;
import company.pluginName.Utils.DiscordUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;

@PandaSubCommandAnnotation(parentCommand = AdminCommand.class)
@PandaCommandAnnotation(
		id = "purge",
		pathName = "Purge",
		defaultName = "purge",
		defaultDescription = "Delete protections older than the specified time, based on the last connection of the owner.",
		defaultUsage = "[--days <amount of days>] [--hours <amount of hours>] [--minutes <amount of minutes>] [--worlds <world1;world2;...>] [--show-ignored-players] [--export-only] [confirm]",
		defaultAliases = "p",
		defaultPermission = "protectionblocks.admin.purge")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class PurgeSubCommand extends PandaSubCommand {

	private static final List<String> ARGS = Arrays.asList("--export-only", "--show-ignored-players", "--wolds",
			"--days", "--hours", "--minutes", "confirm");

	@PandaInject
	private static ProtectionsPurgeService protectionsPurgeService;

	public PurgeSubCommand() throws InstantiationException {
		super();
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		return ARGS;
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		if (parameters.getParameters().size() > 1) {
			PurgeConfiguration purgeConfiguration = new PurgeConfiguration();
			for (int i = 0; i < parameters.getParameters().size(); i++) {
				switch (parameters.getParameters().get(i).toLowerCase()) {
				case "--export-only":
					return CommandResponse.queuedAsync(() -> {
						MessageTemplate.inst(Messages.MESSAGE_PURGE_SEARCH.applyPrefix()).process().sendMessage(sender);

						List<Protection> protectionsToPurge = protectionsPurgeService
								.retrieveProtectionsToPurge(purgeConfiguration);

						try {
							File file = protectionsPurgeService.exportProtections(purgeConfiguration,
									protectionsToPurge);

							MessageTemplate.inst(Messages.MESSAGE_PURGE_EXPORTEND.applyPrefix())
									.setReplacements(
											new Replacement("{amount}",
													() -> String.valueOf(protectionsToPurge.size())),
											new Replacement("{file}", () -> file.getName()))
									.process().sendMessage(sender);
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
				case "confirm":
					return CommandResponse.queuedAsync(() -> {
						MessageTemplate.inst(Messages.MESSAGE_PURGE_START.applyPrefix()).process().sendMessage(sender);

						List<Protection> protectionsToPurge = protectionsPurgeService
								.retrieveProtectionsToPurge(purgeConfiguration);

						List<Protection> removedProtections = protectionsPurgeService
								.purgeProtections(protectionsToPurge, RemovalCause.MANUAL_PURGE);

						DiscordUtilities.sendPurgeSummaryMessage(sender instanceof Player ? (Player) sender : null,
								purgeConfiguration, removedProtections);

						MessageTemplate.inst(Messages.MESSAGE_PURGE_END.applyPrefix())
								.setReplacements(
										new Replacement("{amount}", () -> String.valueOf(removedProtections.size())))
								.process().sendMessage(sender);

						if (protectionsToPurge.size() != 0) {
							MessageTemplate.inst(Messages.MESSAGE_PURGE_ERROR.applyPrefix())
									.setReplacements(new Replacement("{amount}",
											() -> String.valueOf(protectionsToPurge.size())))
									.process().sendMessage(sender);
						}
					});
				case "--worlds":
					if (parameters.getParameters().size() > i + 1) {
						String worlds = parameters.getParameters().get(++i);

						purgeConfiguration.getWorldsNames().addAll(Arrays.asList(worlds.split(";")));
					} else {
						MessageTemplate.inst(Messages.ERROR_PURGE_NOVALUEFORPARAMETER.applyPrefix()).process()
								.sendMessage(sender);
						return new TrueResponse();
					}
					break;
				case "--show-ignored-players":
					purgeConfiguration.setShowIgnoredPlayers(true);
					break;
				case "--days":
				case "--hours":
				case "--minutes":
					if (parameters.getParameters().size() > i + 1) {
						try {
							Integer number = Integer.parseInt(parameters.getParameters().get(i + 1));

							if (number < 0) {
								MessageTemplate.inst(Messages.ERROR_NUMBERBELOWZERO.applyPrefix()).process()
										.sendMessage(sender);
								return new TrueResponse();
							}

							if (parameters.getParameters().get(i).toLowerCase().equals("--days")) {
								purgeConfiguration.setDays(number);
							} else if (parameters.getParameters().get(i).toLowerCase().equals("--hours")) {
								purgeConfiguration.setHours(number);
							} else {
								purgeConfiguration.setMinutes(number);
							}

							i++;
						} catch (NumberFormatException e) {
							MessageTemplate.inst(Messages.ERROR_INVALIDNUMBER.applyPrefix()).process()
									.sendMessage(sender);
							return new TrueResponse();
						}
					} else {
						MessageTemplate.inst(Messages.ERROR_PURGE_NOVALUEFORPARAMETER.applyPrefix()).process()
								.sendMessage(sender);
						return new TrueResponse();
					}
					break;
				default:
					MessageTemplate.inst(Messages.ERROR_PURGE_INVALIDPARAMETER.applyPrefix()).process()
							.sendMessage(sender);
					return new TrueResponse();
				}
			}

			return CommandResponse.queuedAsync(() -> {
				MessageTemplate.inst(Messages.MESSAGE_PURGE_SEARCH.applyPrefix()).process().sendMessage(sender);

				List<Protection> protectionsToPurge = protectionsPurgeService
						.retrieveProtectionsToPurge(purgeConfiguration);
				MessageTemplate.inst(Messages.MESSAGE_PURGE_WARNING.applyPrefix())
						.setReplacements(new Replacement("{amount}", () -> String.valueOf(protectionsToPurge.size())),
								new Replacement("{command}",
										() -> String.format("/%s %s confirm", getCommandPath(),
												parameters.getParameters().stream().collect(Collectors.joining(" ")))))
						.process().sendMessage(sender);
			});
		} else {
			MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process().sendMessage(sender);
		}
		return new TrueResponse();
	}

}

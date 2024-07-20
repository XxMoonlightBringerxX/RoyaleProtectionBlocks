package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Hooks.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionFlagsPckg.ProtectionFlagsService;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import lombok.Getter;

@PandaSubCommandAnnotation(parentCommand = AdminCommand.class)
@PandaCommandAnnotation(
		id = "resetflags",
		pathName = "Reset-flags",
		defaultName = "resetflags",
		defaultDescription = "Reset the flags values to the default ones",
		defaultAliases = "rf",
		defaultPermission = "protectionblocks.admin.resetflags",
		defaultUsage = "[--all] [--flags (flag-name1;flag-name2;...)]"
)
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true
)
public class ResetFlagsSubCommand extends PandaSubCommand {

	@RegisteredPandaField("lang")
	private static final PandaPrefixedStringField MESSAGE_RESETFALGS_FLAGSRESETTODEFAULTSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Reset-flags.Flags-set-to-default-successfully", "&aThe flags were reset to default successfully.");

	@RegisteredPandaField("lang")
	private static final PandaPrefixedStringField ERROR_RESETFLAGS_NOFLAGSFOUND = new PandaPrefixedStringField(
			"Error.Reset-flags.No-flags-found", "&cNo flags could be found!");

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static ProtectionSettingsService protectionSettingsService;

	@PandaInject
	private static ProtectionFlagsService protectionFlagsService;

	@PandaInject
	private static PlaceholderAPI placeholderApi;

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	private static final String ALL_KEY = "--all";
	private static final String FLAGS_KEY = "--flags";

	private @Getter List<String> registeredKeyOnlyParameters = Arrays.asList(ALL_KEY);
	private @Getter List<String> registeredKeyValueParameters = Arrays.asList(FLAGS_KEY);

	public ResetFlagsSubCommand() throws InstantiationException {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		return CommandResponse.queuedAsync(() -> {
			boolean allRegions = parameters.getKeyOnlyParameters().stream().filter(key -> key.equalsIgnoreCase(ALL_KEY))
					.findFirst().isPresent();
			Protection currentProtection = null;

			if (!allRegions) {
				if (sender instanceof Player) {
					currentProtection = protectionsService.findProtectionByLocation(((Player) sender).getLocation());

					if (currentProtection == null) {
						MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
								.sendMessage(sender);
						return;
					}
				} else {
					MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
					return;
				}
			}

			Set<String> flagsToReset = parameters.getKeyValueParameters().stream()
					.filter(pair -> pair.getFirst().equalsIgnoreCase(FLAGS_KEY))
					.map(pair -> (Set<String>) (pair.getSecond() != null
							? Arrays.stream(pair.getSecond().toLowerCase().split(";")).filter(flagId -> {
								try {
									return worldGuardApi.getHook().getInternalWorldGuard().getAllFlags().stream()
											.anyMatch(flag -> flag.getName().equalsIgnoreCase(flagId));
								} catch (Exception e) {
									return false;
								}
							}).collect(Collectors.toSet())
							: Collections.emptySet()))
					.findFirst().orElse(null);

			if (flagsToReset != null && flagsToReset.isEmpty()) {
				MessageTemplate.inst(ERROR_RESETFLAGS_NOFLAGSFOUND.applyPrefix()).process().sendMessage(sender);
				return;
			}

			(currentProtection != null ? Arrays.asList(currentProtection)
					: protectionsService.getProtectionByRegion().values()).forEach(protection -> {
						if (flagsToReset != null) {
							protection.getWorldGuardFlags().resetFlags(flagsToReset);
						} else {
							protection.getWorldGuardFlags().resetFlags();
						}
					});

			MessageTemplate.inst(MESSAGE_RESETFALGS_FLAGSRESETTODEFAULTSUCCESSFULLY.applyPrefix()).process()
					.sendMessage(sender);
		});
	}

}

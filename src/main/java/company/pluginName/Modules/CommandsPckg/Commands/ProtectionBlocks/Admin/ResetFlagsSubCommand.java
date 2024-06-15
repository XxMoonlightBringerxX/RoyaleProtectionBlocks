package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.APIs.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService.DefaultFlag;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
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
	private static PlaceholderAPI placeholderApi;

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

			if (!allRegions && sender instanceof Player) {
				currentProtection = protectionsService.findProtectionByLocation(((Player) sender).getLocation());

				if (currentProtection == null) {
					MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
							.sendMessage(sender);
					return;
				}
			}

			List<String> flagsToModify = parameters.getKeyValueParameters().stream()
					.filter(pair -> pair.getFirst().equalsIgnoreCase(FLAGS_KEY))
					.map(pair -> (List<String>) (pair.getSecond() != null
							? Arrays.asList(pair.getSecond().toLowerCase().split(";"))
							: Collections.emptyList()))
					.findFirst().orElse(null);

			List<DefaultFlag> defaultFlags = protectionSettingsService.getDefaultFlags().stream().filter(
					flag -> flagsToModify == null || flagsToModify.contains(flag.getFlag().getName().toLowerCase()))
					.collect(Collectors.toList());

			if (defaultFlags.isEmpty()) {
				MessageTemplate.inst(ERROR_RESETFLAGS_NOFLAGSFOUND.applyPrefix()).process().sendMessage(sender);
				return;
			}

			(currentProtection != null ? Arrays.asList(currentProtection)
					: protectionsService.getProtectionByRegion().values()).forEach(protection -> {
						OfflinePlayer owner = OfflinePlayerUtilities.getOfflinePlayer(protection.getOwnerUuid());

						HashMap<com.sk89q.worldguard.protection.flags.Flag<?>, Object> flags = new HashMap<>();
						protectionSettingsService.getDefaultFlags().forEach(defaultFlag -> {
							if (placeholderApi.getHook().isHooked()) {
								if (defaultFlag.getValue() instanceof String) {
									flags.put(defaultFlag.getFlag(),
											MessageTemplate.inst(placeholderApi.getHook()
													.applyPlaceholders((String) defaultFlag.getValue(), owner)
													.replaceAll("\\\\%", "%")).process().toString());
								} else if (defaultFlag.getValue() instanceof Set) {
									Set<String> set = new HashSet<>();
									((Set<String>) defaultFlag.getValue())
											.forEach(string -> set.add(MessageTemplate.inst(placeholderApi.getHook()
													.applyPlaceholders(string, owner).replaceAll("\\\\%", "%"))
													.process().toString()));
									flags.put(defaultFlag.getFlag(), set);
								} else {
									flags.put(defaultFlag.getFlag(), defaultFlag.getValue());
								}
							} else {
								flags.put(defaultFlag.getFlag(), defaultFlag.getValue());
							}

							if (defaultFlag.getRegionGroup() != null
									&& defaultFlag.getFlag().getRegionGroupFlag() != null && defaultFlag.getFlag()
											.getRegionGroupFlag().getDefault() != defaultFlag.getRegionGroup()) {
								flags.put(defaultFlag.getFlag().getRegionGroupFlag(), defaultFlag.getRegionGroup());
							}
						});

						protection.getProtectedRegion().setFlags(flags);
					});

			MessageTemplate.inst(MESSAGE_RESETFALGS_FLAGSRESETTODEFAULTSUCCESSFULLY.applyPrefix()).process()
					.sendMessage(sender);
		});
	}

}

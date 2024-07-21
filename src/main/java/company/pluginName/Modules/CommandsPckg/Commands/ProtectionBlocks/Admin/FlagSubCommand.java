package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegionGroup;

import company.pluginName.Hooks.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionFlagsPckg.ProtectionFlagsService;
import company.pluginName.Modules.ProtectionFlagsPckg.Utils.ProtectionFlagUtilities;
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
		id = "flag",
		pathName = "Flag",
		defaultName = "flag",
		defaultDescription = "Replace the flag value of a protection or all the protections. Specify no value to clear the flag from the protections.",
		defaultAliases = "f",
		defaultPermission = "protectionblocks.admin.flag",
		defaultUsage = "<flag name> [flag value] [--all] [--group <all|members|owners|non_members|non_owners>]"
)
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true
)
public class FlagSubCommand extends PandaSubCommand {

	@RegisteredPandaField("lang")
	private static final PandaPrefixedStringField MESSAGE_FLAGS_FLAGSETSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Flags.Flag-set-successfully", "&aThe flag value has been replaced successfully.");

	@RegisteredPandaField("lang")
	private static final PandaPrefixedStringField MESSAGE_FLAGS_FLAGCLEAREDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Flags.Flag-cleared-successfully", "&aThe flag value has been cleared successfully.");

	@RegisteredPandaField("lang")
	private static final PandaPrefixedStringField ERROR_FLAGS_NOTFOUND = new PandaPrefixedStringField(
			"Error.Flags.Not-found", "&cNo flag could be found with the specified name!");

	@RegisteredPandaField("lang")
	private static final PandaPrefixedStringField ERROR_FLAGS_INVALIDGROUP = new PandaPrefixedStringField(
			"Error.Flags.Invalid-group", "&cThe specified group is not a valid group!");

	@RegisteredPandaField("lang")
	private static final PandaPrefixedStringField ERROR_FLAGS_INVALIDVALUE = new PandaPrefixedStringField(
			"Error.Flags.Invalid-Value", "&cThe specified value is not a valid value for this flag!");

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
	private static final String GROUP_KEY = "--group";

	private @Getter List<String> registeredKeyOnlyParameters = Arrays.asList(ALL_KEY);
	private @Getter List<String> registeredKeyValueParameters = Arrays.asList(GROUP_KEY);

	public FlagSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		return CommandResponse.queuedAsync(() -> {
			if (!parameters.getUnprocessedParameters().isEmpty()) {
				boolean allRegions = parameters.getKeyOnlyParameters().stream()
						.filter(key -> key.equalsIgnoreCase(ALL_KEY)).findFirst().isPresent();
				Protection currentProtection = null;

				if (!allRegions) {
					if (sender instanceof Player) {
						currentProtection = protectionsService
								.findProtectionParentByLocation(((Player) sender).getLocation());

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

				try {
					Flag<?> flag = worldGuardApi.getHook().getInternalWorldGuard().getAllFlags().stream()
							.filter(wgFlag -> wgFlag.getName().equals(parameters.getUnprocessedParameters().get(0)))
							.findFirst().orElse(null);

					if (flag == null) {
						MessageTemplate.inst(ERROR_FLAGS_NOTFOUND.applyPrefix()).process().sendMessage(sender);
						return;
					}

					if (parameters.getUnprocessedParameters().size() > 1) {
						String groupValue = parameters.getKeyValueParameters().stream()
								.filter(pair -> pair.getFirst().equalsIgnoreCase(GROUP_KEY))
								.map(pair -> pair.getSecond()).findFirst().orElse(null);
						final RegionGroup group;

						try {
							group = groupValue != null ? RegionGroup.valueOf(groupValue.toUpperCase()) : null;
						} catch (IllegalArgumentException e) {
							MessageTemplate.inst(ERROR_FLAGS_INVALIDGROUP.applyPrefix()).process().sendMessage(sender);
							return;
						}

						Object value;

						try {
							value = ProtectionFlagUtilities
									.stringToValue(
											flag, Arrays
													.stream(Arrays.copyOfRange(
															parameters.getUnprocessedParameters()
																	.toArray(new String[parameters
																			.getUnprocessedParameters().size()]),
															1, parameters.getUnprocessedParameters().size()))
													.collect(Collectors.joining(" ")));
						} catch (Exception e) {
							MessageTemplate.inst(ERROR_FLAGS_INVALIDVALUE.applyPrefix()).process().sendMessage(sender);
							return;
						}

						(currentProtection != null ? Arrays.asList(currentProtection)
								: protectionsService.getProtectionByRegion().values())
								.forEach(protection -> ProtectionFlagUtilities.setValue(protection.getProtectedRegion(),
										flag, value, group));

						MessageTemplate.inst(MESSAGE_FLAGS_FLAGSETSUCCESSFULLY.applyPrefix()).process()
								.sendMessage(sender);
					} else {
						(currentProtection != null ? Arrays.asList(currentProtection)
								: protectionsService.getProtectionByRegion().values())
								.forEach(protection -> ProtectionFlagUtilities.setValue(protection.getProtectedRegion(),
										flag, null));

						MessageTemplate.inst(MESSAGE_FLAGS_FLAGCLEAREDSUCCESSFULLY.applyPrefix()).process()
								.sendMessage(sender);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process()
						.sendMessage(sender);
			}
		});
	}

}

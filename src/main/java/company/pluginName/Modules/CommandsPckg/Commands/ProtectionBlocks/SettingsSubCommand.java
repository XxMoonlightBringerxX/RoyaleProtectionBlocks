package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
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
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.SettingGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.AbstractSetting;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.BooleanSetting;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSwitchSettingRequestInput;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "settings",
		pathName = "Settings",
		defaultName = "settings",
		defaultDescription = "Edit the settings of your protection",
		defaultUsage = "[<setting id> <group> <value> ]",
		defaultAliases = "st")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class SettingsSubCommand extends PandaSubCommand {

	private static final List<String> SETTING_GROUP_VALUES = Arrays.stream(SettingGroup.values())
			.map(group -> group.name().toLowerCase()).collect(Collectors.toList());

	@PandaInject
	private static ProtectionSettingsService protectionSettingsService;

	public SettingsSubCommand() throws InstantiationException {
		super();
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		if (argIndex == 0) {
			return protectionSettingsService.getSettingIds();
		} else if (argIndex == 1) {
			return SETTING_GROUP_VALUES;
		}
		return super.generateAutocompleteList(sender, argIndex);
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (parameters.getParameters().size() > 2) {
				Protection protection = RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
						.findProtectionParentByLocation(pl.getLocation());
				if (protection != null) {
					try {
						AbstractSetting<?> setting = protectionSettingsService
								.getSetting(parameters.getParameters().get(0));
						if (setting != null) {
							try {
								SettingGroup group = SettingGroup
										.valueOf(parameters.getParameters().get(1).toUpperCase());

								requestSwitch(pl, protection, setting, group,
										parameters.getParameters().subList(2, parameters.getParameters().size())
												.stream().collect(Collectors.joining("")));

								MessageTemplate
										.inst(Messages.MESSAGE_PROTECTIONS_SETTINGS_SWITCHEDSUCCESSFULLY.applyPrefix())
										.setReplacements(new Replacement("{setting_name}", () -> setting.getName()),
												new Replacement("{group_name}", () -> group.name().toLowerCase()),
												new Replacement("{value}", () -> {
													try {
														return protection.getSettingValueAsString(setting, group);
													} catch (RoyaleProtectionBlocksException e) {
														return "???";
													}
												}))
										.process().sendMessage(pl);
							} catch (IllegalArgumentException e) {
								throw Exceptions.Protections.Settings.INVALIDVALUE.generateException()
										.setReplacements(new Replacement("{options}",
												() -> SETTING_GROUP_VALUES.stream().collect(Collectors.joining(", "))));
							}
						} else {
							throw Exceptions.Protections.Settings.NOTFOUND.generateException();
						}
					} catch (RoyaleProtectionBlocksException e) {
						e.sendError(pl);
					}
				} else {
					MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
							.sendMessage(sender);
				}
			} else {
				MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process().sendMessage(pl);
			}
		} else {
			MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}

	private <T extends Serializable> void requestSwitch(Player pl, Protection protection, AbstractSetting<T> setting,
			SettingGroup group, String value) throws RoyaleProtectionBlocksException {
		if (setting instanceof BooleanSetting) {
			RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
					.protectionSwitchSettingRequest(ProtectionSwitchSettingRequestInput.inst(pl, protection, setting,
							group, setting.parseStringToValue(value)));
		} else {
			throw Exceptions.Protections.Settings.NOTFOUND.generateException();
		}
	}

}

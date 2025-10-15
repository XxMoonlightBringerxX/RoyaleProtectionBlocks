package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionPermissionsPckg.ProtectionPermissionsService;
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
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.PermissionGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Permissions.PermissionInterface;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSwitchPermissionRequestInput;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "permissions",
		pathName = "Permissions",
		defaultName = "permissions",
		defaultDescription = "Edit the permissions of your protection",
		defaultUsage = "[<permission id> <group> <value> ]",
		defaultAliases = "pe")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class PermissionsSubCommand extends PandaSubCommand {

	private static final List<String> PERMISSION_GROUP_VALUES = Arrays.stream(PermissionGroup.values())
			.map(group -> group.name().toLowerCase()).collect(Collectors.toList());

	private static final List<String> PERMISSION_VALUES = Arrays.asList("default", "true", "false");

	@PandaInject
	private static ProtectionPermissionsService protectionPermissionsService;

	public PermissionsSubCommand() throws InstantiationException {
		super();
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		if (argIndex == 0) {
			return protectionPermissionsService.getPermissions().stream()
					.filter(perm -> perm.isEnabled() && perm.isEditable()).map(PermissionInterface::getId)
					.collect(Collectors.toList());
		} else if (argIndex == 1) {
			return PERMISSION_GROUP_VALUES;
		} else if (argIndex == 2) {
			return PERMISSION_VALUES;
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
						PermissionInterface permission = protectionPermissionsService
								.getPermission(parameters.getParameters().get(0));
						if (permission != null && permission.isEnabled()) {
							try {
								PermissionGroup group = PermissionGroup
										.valueOf(parameters.getParameters().get(1).toUpperCase());

								Boolean value = parameters.getParameters().get(2).toLowerCase().equals("default") ? null
										: Boolean.valueOf(parameters.getParameters().get(2));

								RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
										.protectionSwitchPermissionRequest(ProtectionSwitchPermissionRequestInput
												.inst(pl, protection, permission, group, value));

								Boolean currentValue = protection.getPermissionValue(permission, group);

								MessageTemplate
										.inst(Messages.MESSAGE_PROTECTIONS_PERMISSIONS_SWITCHEDSUCCESSFULLY
												.applyPrefix())
										.setReplacements(new Replacement("{permission_name}",
												() -> permission.getDisplayName() != null ? permission.getDisplayName()
														: permission.getId()),
												new Replacement("{group_name}", () -> group.name().toLowerCase()),
												new Replacement("{value}",
														() -> currentValue != null
																? (Boolean.TRUE.equals(currentValue)
																		? Messages.MESSAGE_GENERAL_TRUE.getContent()
																		: Messages.MESSAGE_GENERAL_FALSE.getContent())
																: Messages.MESSAGE_GENERAL_NULL.getContent()))
										.process().sendMessage(pl);
							} catch (IllegalArgumentException e) {
								throw Exceptions.Protections.Settings.INVALIDVALUE.generateException()
										.setReplacements(new Replacement("{options}", () -> PERMISSION_GROUP_VALUES
												.stream().collect(Collectors.joining(", "))));
							}
						} else {
							throw Exceptions.Protections.Permissions.NOTFOUND.generateException();
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

}

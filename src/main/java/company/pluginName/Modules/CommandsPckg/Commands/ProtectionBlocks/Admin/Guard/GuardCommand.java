package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin.Guard;

import org.bukkit.command.CommandSender;

import company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin.AdminCommand;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;

@PandaSubCommandAnnotation(parentCommand = AdminCommand.class)
@PandaCommandAnnotation(
		id = "guard",
		pathName = "Guard",
		defaultName = "guard",
		defaultDescription = "Protect players from the auto-purge or manual purges to prevent their protections from being removed.",
		defaultAliases = "g",
		defaultUsage = "['player'|'protection']",
		defaultPermission = "protectionblocks.admin.guard",
		helpCommand = false)
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true)
public class GuardCommand extends PandaCommand {

	@PandaInject
	private static ProtectionSettingsService protectionSettingService;

	public GuardCommand() throws InstantiationException {
		super();
	}

	@Override
	protected CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process().sendMessage(sender);
		return CommandResponse.trueResponse();
	}

}

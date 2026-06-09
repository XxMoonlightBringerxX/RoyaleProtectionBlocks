package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import org.bukkit.command.CommandSender;

import company.pluginName.Modules.FilePckg.Messages;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;

@PandaSubCommandAnnotation(parentCommand = AdminCommand.class)
@PandaCommandAnnotation(
		id = "reload",
		pathName = "Reload",
		defaultName = "reload",
		defaultDescription = "Reload the plugin",
		defaultAliases = "r",
		defaultPermission = "protectionblocks.admin.reload"
)
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = false
)
public class ReloadSubCommand extends PandaSubCommand {

	@PandaInject
	private static PandaPluginClass plugin;

	public ReloadSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		plugin.reloadPlugin();
		MessageTemplate.inst(Messages.MESSAGE_RELOAD.applyPrefix()).process().sendMessage(sender);
		return new TrueResponse();
	}

}

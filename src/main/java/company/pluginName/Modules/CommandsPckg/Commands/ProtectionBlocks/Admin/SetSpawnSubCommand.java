package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Permissions;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;

@PandaSubCommandAnnotation(parentCommand = AdminCommand.class)
@PandaCommandAnnotation(
		id = "setspawn",
		pathName = "Set-spawn",
		defaultName = "setspawn",
		defaultDescription = "Specify the spawn configured for the plugin. This one is mostly used to send players after being kicked or banned from a protection",
		defaultAliases = "ss",
		defaultPermission = Permissions.PROTECTION_ADMIN_SETSPAWN)
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true)
public class SetSpawnSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionSettingsService protectionSettingService;

	public SetSpawnSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(PandaCommand cmd, CommandSender sender, String[] args,
			boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			try {
				protectionSettingService.setSpawn(pl.getLocation());

				MessageTemplate.inst(Messages.MESSAGE_SETSPAWNSUCCESSFULLY.applyPrefix()).process().sendMessage(pl);
			} catch (YamlException | IOException e) {
				MessageTemplate.inst(Messages.ERROR_SETSPAWNERROR.applyPrefix()).process().sendMessage(pl);
				e.printStackTrace();
			}
		} else {
			MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}

}

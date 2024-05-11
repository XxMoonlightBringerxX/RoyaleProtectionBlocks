package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import company.pluginName.Permissions;
import company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.ProtectionBlocksCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "admin",
		pathName = "Admin",
		defaultName = "admin",
		defaultDescription = "Commands used to manage staff commands",
		defaultUsage = "[help|subcommand]",
		defaultPermission = Permissions.PROTECTION_ADMIN,
		defaultAliases = "a")
@PandaCommandAnnotation.Customizable(aliases = true, description = true, name = true, permission = true, usage = true)
public class AdminCommand extends PandaCommand {

	public AdminCommand() throws InstantiationException {
		super();
	}

}

package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Files;

import company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.ProtectionBlocksCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "files",
		pathName = "Files",
		defaultName = "files",
		defaultDescription = "Manage the importation and exportation of data through files",
		defaultUsage = "[help|subcommand]",
		defaultPermission = "protectionblocks.files",
		defaultAliases = "f")
@PandaCommandAnnotation.Customizable(aliases = true, description = true, name = true, permission = true, usage = true)
public class FilesCommand extends PandaCommand {

	public FilesCommand() throws InstantiationException {
		super();
	}

}

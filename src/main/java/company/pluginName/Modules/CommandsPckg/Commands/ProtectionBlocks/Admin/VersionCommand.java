package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;

@PandaSubCommandAnnotation(parentCommand = AdminCommand.class)
public class VersionCommand
		extends darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Defaults.VersionCommand {

	public VersionCommand() throws InstantiationException {
		super();
	}

}

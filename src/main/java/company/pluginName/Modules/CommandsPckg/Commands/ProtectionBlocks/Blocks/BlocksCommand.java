package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Blocks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Bukkit.Inventories.ProtectionBlocks.ProtectionBlocksListInventory;
import company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.ProtectionBlocksCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.FalseResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "blocks",
		pathName = "Blocks",
		defaultName = "blocks",
		defaultDescription = "Open a list with all the blocks",
		defaultUsage = "[help|subcommand]",
		defaultPermission = "protectionblocks.blocks",
		defaultAliases = "b"
)
@PandaCommandAnnotation.Customizable(aliases = true, description = true, name = true, permission = true, usage = true)
public class BlocksCommand extends PandaCommand {

	public BlocksCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;

		if (pl != null && parameters.getParameters().size() == 0) {
			new ProtectionBlocksListInventory(pl).openInventory();
			return new TrueResponse();
		}

		return new FalseResponse();
	}
}

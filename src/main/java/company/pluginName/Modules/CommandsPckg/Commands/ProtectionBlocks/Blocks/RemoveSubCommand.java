package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Blocks;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;

@PandaSubCommandAnnotation(parentCommand = BlocksCommand.class)
@PandaCommandAnnotation(
		id = "remove",
		pathName = "Remove",
		defaultName = "remove",
		defaultDescription = "Remove an existing block",
		defaultUsage = "<id>",
		defaultPermission = "protectionblocks.blocks.remove",
		defaultAliases = "r")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class RemoveSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	public RemoveSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public List<String> tabComplete(PandaCommand cmd, CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			return protectionBlocksService.getProtectionBlocks().keySet().stream().collect(Collectors.toList());
		default:
			return EMPTY_LIST;
		}
	}

	@Override
	public CommandResponse executeCommandProcess(PandaCommand cmd, CommandSender sender, String[] args,
			boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (args.length > 1) {
				ProtectionBlock block = protectionBlocksService.getProtectionBlockById(args[1]);
				if (block != null) {
					try {
						block.delete(pl);
						MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_BLOCKS_REMOVEDSUCCESSFULLY.applyPrefix())
								.process().sendMessage(pl);
					} catch (RoyaleProtectionBlocksException e) {
						e.sendError(pl);
					}
				} else {
					MessageTemplate.inst(Messages.ERROR_PROTECTIONS_BLOCKS_NOTFOUND.applyPrefix()).process()
							.sendMessage(pl);
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

package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Blocks;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;

@PandaSubCommandAnnotation(parentCommand = BlocksCommand.class)
@PandaCommandAnnotation(
		id = "give",
		pathName = "Give",
		defaultName = "give",
		defaultDescription = "Give an existing block to yourself or a player",
		defaultUsage = "<id> [player]",
		defaultPermission = "protectionblocks.blocks.give",
		defaultAliases = "g"
)
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true
)
public class GiveSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	public GiveSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public List<String> tabComplete(PandaCommand cmd, CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			return protectionBlocksService.getProtectionBlocks().keySet().stream().collect(Collectors.toList());
		case 2:
			return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
		default:
			return Collections.emptyList();
		}
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (parameters.getParameters().size() > 0) {
			ProtectionBlock block = protectionBlocksService.getProtectionBlockById(parameters.getParameters().get(0));
			if (block != null) {
				Player toGive = pl;

				if (parameters.getParameters().size() > 1) {
					toGive = Bukkit.getPlayer(parameters.getParameters().get(1));
				}

				if (toGive == null) {
					if (pl == null) {
						MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
					} else {
						MessageTemplate.inst(Messages.ERROR_PLAYERNOTFOUND.applyPrefix()).process().sendMessage(sender);
					}
					return new TrueResponse();
				}

				try {
					toGive.getInventory().addItem(block.getInformation().generateItem());
				} catch (RoyaleProtectionBlocksExceptionImpl e) {
					e.sendError(sender);
				}
			} else {
				MessageTemplate.inst(Messages.ERROR_PROTECTIONS_BLOCKS_NOTFOUND.applyPrefix()).process()
						.sendMessage(sender);
			}
		} else {
			MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process().sendMessage(sender);
		}
		return new TrueResponse();
	}
}

package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
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
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionKickRequestInput;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "kick",
		pathName = "Kick",
		defaultName = "kick",
		defaultDescription = "Kick a player from your current protection",
		defaultUsage = "<username>",
		defaultAliases = "k"
)
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true
)
public class KickSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	public KickSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public List<String> tabComplete(PandaCommand cmd, CommandSender sender, String[] args) {
		if (args.length == 1) {
			return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (parameters.getParameters().size() > 0) {
				Protection protection = protectionsService.findProtectionParentByLocation(pl.getLocation());
				if (protection != null) {
					Player kicked = Bukkit.getPlayer(parameters.getParameters().get(0));
					if (kicked != null) {
						try {
							if (playerInteractionsService
									.protectionKickRequest(ProtectionKickRequestInput.inst(pl, protection, kicked))) {
								MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_KICKED.applyPrefix()).process()
										.sendMessage(kicked);
								MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_PLAYERKICKED.applyPrefix()).process()
										.sendMessage(sender);
							} else {
								MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_PLAYERNOTINPROTECTION.applyPrefix())
										.process().sendMessage(sender);
							}
						} catch (RoyaleProtectionBlocksException e) {
							e.sendError(pl);
						}
					} else {
						MessageTemplate.inst(Messages.ERROR_PLAYERNOTFOUND.applyPrefix()).process().sendMessage(sender);
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

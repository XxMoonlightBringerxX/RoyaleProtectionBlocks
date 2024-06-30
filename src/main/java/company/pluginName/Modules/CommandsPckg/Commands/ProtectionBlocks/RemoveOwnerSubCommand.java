package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Owners.ProtectionOwnerRemoveRequestInput;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "removeowner",
		pathName = "Remove-owner",
		defaultName = "removeowner",
		defaultDescription = "Remove an owner on your current protection",
		defaultUsage = "<username>",
		defaultAliases = "ro"
)
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true
)
public class RemoveOwnerSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	public RemoveOwnerSubCommand() throws InstantiationException {
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
		return CommandResponse.queuedAsync(() -> {
			Player pl = sender instanceof Player ? (Player) sender : null;
			if (pl != null) {
				if (parameters.getParameters().size() > 0) {
					Protection protection = protectionsService.findProtectionByLocation(pl.getLocation());
					if (protection != null) {
						OfflinePlayer owner = OfflinePlayerUtilities
								.getOfflinePlayer(parameters.getParameters().get(0));
						if (owner != null) {
							try {
								playerInteractionsService.protectionOwnerRemoveRequest(
										ProtectionOwnerRemoveRequestInput.inst(pl, protection, owner.getUniqueId()));

								MessageTemplate
										.inst(Messages.MESSAGE_PROTECTIONS_OWNERS_REMOVEDSUCCESSFULLY.applyPrefix())
										.process().sendMessage(sender);
							} catch (RoyaleProtectionBlocksException e) {
								e.sendError(pl);
							}
						} else {
							MessageTemplate.inst(Messages.ERROR_PLAYERNOTFOUND.applyPrefix()).process()
									.sendMessage(sender);
						}
					} else {
						MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
								.sendMessage(sender);
					}
				} else {
					MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process()
							.sendMessage(pl);
				}
			} else {
				MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
			}
		});
	}
}

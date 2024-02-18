package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "unban",
		pathName = "Unban",
		defaultName = "unban",
		defaultDescription = "Unban a player from your current protection",
		defaultAliases = "ubn",
		defaultUsage = "<username>")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class UnbanSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionsService protectionsService;

	public UnbanSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public List<String> tabComplete(PandaCommand cmd, CommandSender sender, String[] args) {
		if (args.length == 1) {
			return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
		}
		return EMPTY_LIST;
	}

	@Override
	public CommandResponse executeCommandProcess(PandaCommand cmd, CommandSender sender, String[] args,
			boolean useids) {
		return CommandResponse.queuedAsync(() -> {
			Player pl = sender instanceof Player ? (Player) sender : null;
			if (pl != null) {
				if (args.length > 1) {
					Protection protection = protectionsService.getProtectionByLocation(pl.getLocation());
					if (protection != null) {
						OfflinePlayer banned = OfflinePlayerUtilities.getOfflinePlayer(args[1]);
						if (banned != null) {
							try {
								protection.getBanneds().remove(pl, banned.getUniqueId());

								MessageTemplate
										.inst(Messages.MESSAGE_PROTECTIONS_BANNEDS_REMOVEDSUCCESSFULLY.applyPrefix())
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

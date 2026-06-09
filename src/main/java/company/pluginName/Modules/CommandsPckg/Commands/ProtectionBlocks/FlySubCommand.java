package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Player.PlayerToggleFlightAttemptEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Player.PlayerToggleFlightEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "fly",
		pathName = "Fly",
		defaultName = "fly",
		defaultDescription = "Allows to fly around your protections",
		defaultAliases = "f",
		defaultPermission = "protectionblocks.fly")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true)
public class FlySubCommand extends PandaSubCommand {

	@PandaInject
	private static PlayerDataService playerDataService;

	public FlySubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			PlayerData playerData = playerDataService.getPlayerData(pl);
			if (!playerData.getCurrentProtections().isEmpty()) {
				try {
					if (PermissionsService.FLY_BYPASS.hasPermission(pl)) {
						toggleFlight(pl);
					} else {
						if (PermissionsService.FLY.hasPermission(pl)) {
							if (playerData.getCurrentProtections().stream()
									.anyMatch(protection -> protection.canFly(pl))) {
								toggleFlight(pl);
							} else {
								throw Exceptions.Protections.Flight.PERMISSIONDENIEDPROTECTION.generateException();
							}
						} else {
							throw Exceptions.Protections.Flight.PERMISSIONDENIED.generateException();
						}
					}
				} catch (RoyaleProtectionBlocksException e) {
					e.sendError(sender);
				}
			} else {
				MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
						.sendMessage(sender);
			}
		} else {
			MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}

	private void toggleFlight(Player pl) {
		PlayerToggleFlightAttemptEvent attemptEvent = new PlayerToggleFlightAttemptEvent(pl, !pl.getAllowFlight());

		Bukkit.getPluginManager().callEvent(attemptEvent);

		if (!attemptEvent.isCancelled()) {
			pl.setAllowFlight(!pl.getAllowFlight());
			MessageTemplate.inst((pl.getAllowFlight() ? Messages.MESSAGE_PROTECTIONS_FLIGHT_ENABLED
					: Messages.MESSAGE_PROTECTIONS_FLIGHT_DISABLED).applyPrefix()).process().sendMessage(pl);

			PlayerToggleFlightEvent event = new PlayerToggleFlightEvent(pl, pl.getAllowFlight());

			Bukkit.getPluginManager().callEvent(event);
		}
	}

}

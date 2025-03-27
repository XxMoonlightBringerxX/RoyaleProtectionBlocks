package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
				if (pl.getAllowFlight() || PermissionsService.FLY_BYPASS.hasPermission(pl)
						|| (PermissionsService.FLY.hasPermission(pl) && playerData.getCurrentProtections().stream()
								.anyMatch(protection -> protection.canFly(pl)))) {
					pl.setAllowFlight(!pl.getAllowFlight());
					MessageTemplate
							.inst((pl.getAllowFlight() ? Messages.MESSAGE_PROTECTIONS_FLIGHT_ENABLED
									: Messages.MESSAGE_PROTECTIONS_FLIGHT_DISABLED).applyPrefix())
							.process().sendMessage(sender);
				} else {
					MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTOWNERANYPROTECTION.applyPrefix()).process()
							.sendMessage(sender);
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
}

package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Bukkit.Events.Listeners.PlayerAutoFlightListener;
import company.pluginName.Bukkit.Events.Listeners.PlayerFlightListener;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "autoflight",
		pathName = "Auto-flight",
		defaultName = "autoflight",
		defaultDescription = "Enable auto-flight, which grants flight ability on allowed protections",
		defaultAliases = "af")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class AutoFlightSubCommand extends PandaSubCommand {

	@PandaInject
	private static PlayerDataService playerDataService;

	public AutoFlightSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public boolean precondition() {
		return PlayerFlightListener.SETTINGS_PROTECTION_FLIGHT_ENABLEFLIGHTCONTROL.isTrue()
				&& PlayerAutoFlightListener.SETTINGS_PROTECTION_FLIGHT_ALLOWAUTOFLIGHT.isTrue();
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		if (argIndex == 0) {
			return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
		}
		return super.generateAutocompleteList(sender, argIndex);
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		return CommandResponse.queuedAsync(() -> {
			Player pl = sender instanceof Player ? (Player) sender : null;
			if (pl != null) {
				PlayerData playerData = playerDataService.getPlayerData(pl);
				playerData.setAutoFlightAndSave(!playerData.isAutoFlight());

				if (playerData.isAutoFlight()) {
					MessageTemplate.inst(Messages.MESSAGE_PLAYERDATA_AUTOFLIGHT_ENABLEDSUCCESSFULLY.applyPrefix())
							.process().sendMessage(sender);
				} else {
					MessageTemplate.inst(Messages.MESSAGE_PLAYERDATA_AUTOFLIGHT_DISABLEDSUCCESSFULLY.applyPrefix())
							.process().sendMessage(sender);
				}
			} else {
				MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
			}
		});
	}
}

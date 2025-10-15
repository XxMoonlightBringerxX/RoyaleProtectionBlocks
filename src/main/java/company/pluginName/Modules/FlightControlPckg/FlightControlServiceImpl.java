package company.pluginName.Modules.FlightControlPckg;

import java.util.Collection;

import org.bukkit.entity.Player;

import company.pluginName.Bukkit.Events.Listeners.PlayerFlightListener;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.FlightControl.FlightControlService;

@PandaService
public class FlightControlServiceImpl implements FlightControlService {

	@PandaInject
	private PlayerFlightListener playerFlightListener;

	@PandaInject
	private PlayerDataService playerDataService;

	public void checkFlightAvailability(Player player) {
		playerFlightListener.checkFlightAvailability(player,
				playerDataService.getPlayerData(player).getCurrentProtections());
	}

	public void checkFlightAvailability(Collection<? extends Player> players) {
		players.forEach(player -> checkFlightAvailability(player));
	}

}

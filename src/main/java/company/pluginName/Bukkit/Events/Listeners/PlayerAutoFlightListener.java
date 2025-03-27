package company.pluginName.Bukkit.Events.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Player.PlayerEnterExitProtectionEvent;

@PandaListener
public class PlayerAutoFlightListener implements Listener {

	@RegisteredPandaField("config")
	public static PandaBooleanField SETTINGS_PROTECTION_FLIGHT_ALLOWAUTOFLIGHT = new PandaBooleanField(
			"Settings.Protection.Flight.Allow-auto-flight", true);

	@PandaInject
	private PlayerDataService playerDataService;

	@EventHandler
	public void onPlayerEnterExitProtection(PlayerEnterExitProtectionEvent e) {
		if (!e.getPlayer().isFlying() && PlayerFlightListener.SETTINGS_PROTECTION_FLIGHT_ENABLEFLIGHTCONTROL.isTrue()
				&& SETTINGS_PROTECTION_FLIGHT_ALLOWAUTOFLIGHT.isTrue() && !e.getEnteredProtections().isEmpty()) {
			PlayerData playerData = playerDataService.getPlayerData(e.getPlayer());
			if (playerData.isAutoFlight() && (PermissionsService.FLY_BYPASS.hasPermission(e.getPlayer())
					|| (PermissionsService.FLY.hasPermission(e.getPlayer())
							&& e.getEnteredProtections().stream().anyMatch(prot -> prot.canFly(e.getPlayer()))))) {
				e.getPlayer().setAllowFlight(true);
				e.getPlayer().setVelocity(
						new Vector(e.getPlayer().getVelocity().getX(), 0.2D, e.getPlayer().getVelocity().getZ()));
				e.getPlayer().setFlying(true);
			}
		}
	}

}

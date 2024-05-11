package company.pluginName.Bukkit.Events.PlayerEnterExitRegion;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import royale.RoyaleProtectionBlocks.Plugin.InternalAPI.Events.Player.PlayerEnterExitProtectionEvent;

@PandaListener
public class PlayerEnterExitRegionTrigger implements Listener {

	@PandaInject
	private PlayerDataService playerDataService;

	@PandaInject
	private ProtectionsService protectionsService;

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent e) {
		processMovement(e, e.getPlayer(), e.getTo());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		processMovement(e, e.getPlayer(), e.getTo());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		processMovement(e, e.getPlayer(), e.getPlayer().getLocation());
	}

	public void processMovement(Event moveEvent, Player player, Location toLocation) {
		PlayerData playerData = (PlayerData) playerDataService.getPlayerData(player.getUniqueId(), false);

		if (playerData != null) {
			List<Protection> newProtections = this.protectionsService.findProtectionsByLocation(toLocation);

			PlayerEnterExitProtectionEvent event = new PlayerEnterExitProtectionEvent(moveEvent, player,
					playerData.getCurrentProtections(), newProtections);

			if (event.getEnteredProtections().size() > 0 || event.getExitedProtections().size() > 0) {

				Bukkit.getPluginManager().callEvent(event);

				if (event.isCancelled()) {
					if (event instanceof Cancellable) {
						((Cancellable) moveEvent).setCancelled(true);
					}
				} else {
					playerData.setCurrentProtections(newProtections);
				}
			}
		}
	}

}

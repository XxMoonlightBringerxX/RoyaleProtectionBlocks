package company.pluginName.Bukkit.Events.PlayerEnterExitRegion;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
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
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Player.PlayerEnterExitProtectionEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionCreationEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionMergeEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionRemovalEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionSplitEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.Protections.Objects.CachedQuery;

@PandaListener
public class PlayerEnterExitRegionTrigger implements Listener {

	@PandaInject
	private PlayerDataService playerDataService;

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent e) {
		Block fromBlock = e.getFrom().getBlock();
		Block toBlock = e.getTo().getBlock();

		if (fromBlock.getX() != toBlock.getX() || fromBlock.getY() != toBlock.getY()
				|| fromBlock.getZ() != toBlock.getZ()) {
			processMovement(e, e.getPlayer(), e.getTo(), true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		Block fromBlock = e.getFrom().getBlock();
		Block toBlock = e.getTo().getBlock();

		if (fromBlock.getX() != toBlock.getX() || fromBlock.getY() != toBlock.getY()
				|| fromBlock.getZ() != toBlock.getZ()) {
			processMovement(e, e.getPlayer(), e.getTo(), true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		processMovement(e, e.getPlayer(), e.getPlayer().getLocation(), true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionCreation(ProtectionCreationEvent e) {
		Bukkit.getOnlinePlayers().stream()
				.filter(pl -> e.getProtection().isInsideAny(SimpleLocation.of(pl.getLocation()), true))
				.forEach(pl -> processMovement(e, pl, pl.getLocation(), false));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionRemoval(ProtectionRemovalEvent e) {
		Bukkit.getOnlinePlayers().stream()
				.filter(pl -> e.getProtection().isInsideAny(SimpleLocation.of(pl.getLocation()), true))
				.forEach(pl -> processMovement(e, pl, pl.getLocation(), false));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionMerge(ProtectionMergeEvent e) {
		Bukkit.getOnlinePlayers().stream()
				.filter(pl -> e.getParentProtection().isInsideAny(SimpleLocation.of(pl.getLocation()), true))
				.forEach(pl -> processMovement(e, pl, pl.getLocation(), false));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionSplit(ProtectionSplitEvent e) {
		Bukkit.getOnlinePlayers().stream()
				.filter(pl -> e.getProtection().isInsideAny(SimpleLocation.of(pl.getLocation()), true)
						|| e.getOldParentProtection().isInsideAny(SimpleLocation.of(pl.getLocation()), true))
				.forEach(pl -> processMovement(e, pl, pl.getLocation(), false));
	}

	public void processMovement(Event originEvent, Player player, Location toLocation, boolean cancellable) {
		PlayerData playerData = (PlayerData) playerDataService.getPlayerData(player.getUniqueId(), false);

		if (playerData != null) {
			SimpleLocation simpleToLocation = SimpleLocation.of(toLocation);
			CachedQuery<?> cachedQuery = playerData.getLastCachedQuery();

			if (cachedQuery == null || cachedQuery.isExpired()
					|| !cachedQuery.getSimpleLocationArea().isInside(simpleToLocation)) {
				cachedQuery = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService().query(simpleToLocation,
						true);
			}

			List<IProtection> newProtections = cachedQuery.getProtections().stream()
					.map(prot -> (Protection) prot.getParentProtection()).distinct()
					.filter((prot) -> !prot.isDeleted() && prot.isInsideAny(simpleToLocation, true))
					.sorted((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority()))
					.collect(Collectors.toList());

			PlayerEnterExitProtectionEvent event = new PlayerEnterExitProtectionEvent(originEvent, player,
					playerData.getCurrentProtections(), newProtections, cancellable);

			if (event.getEnteredProtections().size() > 0 || event.getExitedProtections().size() > 0) {
				Bukkit.getPluginManager().callEvent(event);

				if (event.isCancellable() && event.isCancelled()) {
					if (event instanceof Cancellable) {
						((Cancellable) originEvent).setCancelled(true);
					}
				} else {
					playerData.setCurrentProtections(newProtections);

					if (playerData.getLastCachedQuery() != cachedQuery) {
						playerData.setLastCachedQuery(cachedQuery);
					}
				}
			} else {
				if (playerData.getLastCachedQuery() != cachedQuery) {
					playerData.setLastCachedQuery(cachedQuery);
				}
			}
		}
	}

}

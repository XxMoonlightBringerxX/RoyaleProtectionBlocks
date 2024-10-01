package company.pluginName.Bukkit.Events.PlayerEnterExitRegion;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaDoubleField;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Player.PlayerEnterExitProtectionEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionCreationEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionMergeEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionRemovalEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionSplitEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation.SimpleLocationArea;

@PandaListener
public class PlayerEnterExitRegionTrigger implements Listener {

	@RegisteredPandaField("config")
	public static final PandaDoubleField SETTINGS_PROTECTION_RADIUSFORSEARCHCACHE = new PandaDoubleField(
			"Settings.Protection.Radius-for-search-cache", 50D);

	@PandaInject
	private PlayerDataService playerDataService;

	@PandaInject
	private ProtectionsService protectionsService;

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent e) {
		processMovement(e, e.getPlayer(), e.getTo(), true, false);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		processMovement(e, e.getPlayer(), e.getTo(), true, false);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		processMovement(e, e.getPlayer(), e.getPlayer().getLocation(), true, true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionCreation(ProtectionCreationEvent e) {
		Bukkit.getOnlinePlayers().stream().filter(pl -> {
			PlayerData playerData = (PlayerData) playerDataService.getPlayerData(pl, false);
			return playerData != null && e.getProtection().isInsideAny(playerData.getCachedArea(), true);
		}).forEach(pl -> processMovement(e, pl, pl.getLocation(), false, true));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionRemoval(ProtectionRemovalEvent e) {
		Bukkit.getOnlinePlayers().stream().filter(pl -> {
			PlayerData playerData = (PlayerData) playerDataService.getPlayerData(pl, false);
			return playerData != null && e.getProtection().isInsideAny(playerData.getCachedArea(), true);
		}).forEach(pl -> processMovement(e, pl, pl.getLocation(), false, true));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionMerge(ProtectionMergeEvent e) {
		Bukkit.getOnlinePlayers().stream().filter(pl -> {
			PlayerData playerData = (PlayerData) playerDataService.getPlayerData(pl, false);
			return playerData != null && e.getParentProtection().isInsideAny(playerData.getCachedArea(), true);
		}).forEach(pl -> processMovement(e, pl, pl.getLocation(), false, true));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionSplit(ProtectionSplitEvent e) {
		Bukkit.getOnlinePlayers().stream().filter(pl -> {
			PlayerData playerData = (PlayerData) playerDataService.getPlayerData(pl, false);
			return playerData != null && (e.getProtection().isInsideAny(playerData.getCachedArea(), true)
					|| e.getOldParentProtection().isInsideAny(playerData.getCachedArea(), true));
		}).forEach(pl -> processMovement(e, pl, pl.getLocation(), false, true));
	}

	public void processMovement(Event originEvent, Player player, Location toLocation, boolean cancellable,
			boolean refreshCache) {
		PlayerData playerData = (PlayerData) playerDataService.getPlayerData(player.getUniqueId(), false);

		if (playerData != null) {
			SimpleLocationArea cachedArea = playerData.getCachedArea();
			List<Protection> cachedProtections = playerData.getCachedProtections();

			if (refreshCache || cachedArea == null || !cachedArea.isInside(SimpleLocation.of(toLocation))) {
				double amount = SETTINGS_PROTECTION_RADIUSFORSEARCHCACHE.getContent();
				cachedArea = SimpleLocationArea.of(toLocation.clone().subtract(amount, amount, amount),
						toLocation.clone().add(amount, amount, amount));
				cachedProtections = this.protectionsService.findProtectionParentsByArea(cachedArea)
						.collect(Collectors.toList());
			}

			SimpleLocation toSimpleLocation = SimpleLocation.of(toLocation);
			List<Protection> newProtections = StreamSupport
					.stream(cachedProtections.spliterator(),
							ProtectionsService.SETTINGS_PROTECTION_ALLOWMULTITHREADSEARCHING.isTrue())
					.filter((prot) -> !prot.isDeleted() && prot.getParentProtection() == prot
							&& prot.getUtils().isInsideAny(toSimpleLocation, true))
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

					if (cachedArea != playerData.getCachedArea()) {
						playerData.setCachedArea(cachedArea);
					}

					if (playerData.getCachedProtections() != cachedProtections) {
						playerData.setCachedProtections(cachedProtections);
					}
				}
			} else {
				if (cachedArea != playerData.getCachedArea()) {
					playerData.setCachedArea(cachedArea);
				}

				if (playerData.getCachedProtections() != cachedProtections) {
					playerData.setCachedProtections(cachedProtections);
				}
			}
		}
	}

}

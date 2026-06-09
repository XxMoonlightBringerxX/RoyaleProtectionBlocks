package company.pluginName.Bukkit.Events.Listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaIntegerField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Player.PlayerEnterExitProtectionEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

@PandaListener
public class PlayerFlightListener implements Listener {

	@RegisteredPandaField("config")
	public static PandaBooleanField SETTINGS_PROTECTION_FLIGHT_ENABLEFLIGHTCONTROL = new PandaBooleanField(
			"Settings.Protection.Flight.Enable-flight-control", true);

	@RegisteredPandaField("config")
	public static PandaIntegerField SETTINGS_PROTECTION_FLIGHT_UNAUTHORIZEDAREAREMOVEAFTERSECONDS = new PandaIntegerField(
			"Settings.Protection.Flight.Unauthorized-area-remove-after-seconds", 5);

	@RegisteredPandaField("config")
	public static PandaStringListField SETTINGS_PROTECTION_FLIGHT_EXCLUDEWORLDS = new PandaStringListField(
			"Settings.Protection.Flight.Exclude-worlds", Arrays.asList("world1", "world2", "world3"));

	@RegisteredPandaField("lang")
	public static PandaStringField MESSAGE_PROTECTION_FLIGHT_REMOVEINCOUNTDOWN = new PandaStringField(
			"Message.Protection.Flight.Remove-in-countdown",
			"&cUnauthorized flight zone. Removing flight in: &e{seconds}");
	@RegisteredPandaField("lang")
	public static PandaStringField MESSAGE_PROTECTION_FLIGHT_REMOVEINDONE = new PandaStringField(
			"Message.Protection.Flight.Remove-in-done", "&cUnauthorized flight zone. Removing flight in: &eNow");

	private HashMap<UUID, BukkitTask> flightRemovalTasks = new HashMap<>();
	private List<UUID> fallDamagePrevention = new ArrayList<>();

	@EventHandler
	public void onPlayerEnterExitProtection(PlayerEnterExitProtectionEvent e) {
		checkFlightAvailability(e.getPlayer(), e.getCurrentProtections());
	}

	@EventHandler
	public void onPlayerGameModeSwitch(PlayerGameModeChangeEvent e) {
		if (e.getNewGameMode() == GameMode.CREATIVE || e.getNewGameMode() == GameMode.SPECTATOR) {
			cancelTask(e.getPlayer().getUniqueId());
		}
	}

	private Runnable getFlightRemovalRunnable(UUID uuid) {
		AtomicInteger atomicInteger = new AtomicInteger(
				SETTINGS_PROTECTION_FLIGHT_UNAUTHORIZEDAREAREMOVEAFTERSECONDS.getContent() + 1);
		return () -> {
			Player player = Bukkit.getPlayer(uuid);

			if (player != null && player.getAllowFlight()) {
				if (atomicInteger.decrementAndGet() == 0) {
					player.setAllowFlight(false);
					fallDamagePrevention.add(player.getUniqueId());
					cancelTask(uuid);

					MessageTemplate.inst(MESSAGE_PROTECTION_FLIGHT_REMOVEINDONE.getContent())
							.setReplacements(new Replacement("{seconds}", () -> String.valueOf(atomicInteger.get())))
							.process().sendActionBar(player);
				} else {
					MessageTemplate.inst(MESSAGE_PROTECTION_FLIGHT_REMOVEINCOUNTDOWN.getContent())
							.setReplacements(new Replacement("{seconds}", () -> String.valueOf(atomicInteger.get())))
							.process().sendActionBar(player);
				}
			} else {
				cancelTask(uuid);
			}
		};
	}

	private void cancelTask(UUID uuid) {
		BukkitTask task = flightRemovalTasks.remove(uuid);

		if (task != null) {
			task.cancel();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent e) {
		fallDamagePrevention.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerStopFalling(PlayerMoveEvent e) {
		if (e.getTo().getY() - e.getFrom().getY() >= 0D) {
			fallDamagePrevention.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerFallDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && e.getCause() == DamageCause.FALL
				&& fallDamagePrevention.remove(e.getEntity().getUniqueId())) {
			e.setCancelled(true);
		}
	}

	public void checkFlightAvailability(Player player, List<? extends IProtection> currentProtections) {
		if (!Boolean.TRUE.equals(SETTINGS_PROTECTION_FLIGHT_ENABLEFLIGHTCONTROL.getContent())) {
			return;
		}

		if (PermissionsService.FLY_BYPASS.hasPermission(player)) {
			return;
		}

		if (!player.getAllowFlight()) {
			return;
		}

		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
			return;
		}

		if (SETTINGS_PROTECTION_FLIGHT_EXCLUDEWORLDS.getContent().contains(player.getLocation().getWorld().getName())) {
			return;
		}

		if (!flightRemovalTasks.containsKey(player.getUniqueId())) {
			if (currentProtections.stream().noneMatch(protection -> protection.canFly(player))) {
				if (player.isFlying()) {
					flightRemovalTasks.put(player.getUniqueId(),
							TasksUtils.executeWithTimer(getFlightRemovalRunnable(player.getUniqueId()), 0, 20));
				} else {
					player.setAllowFlight(false);
				}
			}
		} else if (currentProtections.stream().anyMatch(protection -> protection.canFly(player))) {
			cancelTask(player.getUniqueId());
		}
	}

}

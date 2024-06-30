package company.pluginName.Bukkit.Events.Listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.scheduler.BukkitTask;

import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaIntegerField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Player.PlayerEnterExitProtectionEvent;

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

	@PandaInject
	private PlayerDataService playerDataService;

	private HashMap<UUID, BukkitTask> flightRemovalTasks = new HashMap<>();

	@EventHandler
	public void onPlayerEnterExitProtection(PlayerEnterExitProtectionEvent e) {
		if (!Boolean.TRUE.equals(SETTINGS_PROTECTION_FLIGHT_ENABLEFLIGHTCONTROL.getContent())) {
			return;
		}

		if (!e.getPlayer().getAllowFlight()) {
			return;
		}

		if (e.getPlayer().getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
			return;
		}

		if (SETTINGS_PROTECTION_FLIGHT_EXCLUDEWORLDS.getContent()
				.contains(e.getPlayer().getLocation().getWorld().getName())) {
			return;
		}

		if (!flightRemovalTasks.containsKey(e.getPlayer().getUniqueId())) {
			if (e.getCurrentProtections().isEmpty() || e.getCurrentProtections().stream()
					.noneMatch(protection -> ProtectionUtilities.canFly((Protection) protection, e.getPlayer()))) {
				flightRemovalTasks.put(e.getPlayer().getUniqueId(),
						TasksUtils.executeWithTimer(getFlightRemovalRunnable(e.getPlayer().getUniqueId()), 0, 20));
			}
		} else {
			if (!e.getCurrentProtections().isEmpty() && e.getCurrentProtections().stream()
					.anyMatch(protection -> ProtectionUtilities.canFly((Protection) protection, e.getPlayer()))) {
				cancelTask(e.getPlayer().getUniqueId());
			}
		}
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

}

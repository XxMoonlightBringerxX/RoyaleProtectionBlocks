package company.pluginName.Bukkit.Events.PlayerEnterExitRegion;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import royale.RoyaleProtectionBlocks.Plugin.InternalAPI.Events.Player.PlayerEnterExitProtectionEvent;

@PandaListener
public class PlayerEnterExitRegionBanListener implements Listener {

	private static final long MESSAGE_THRESHOLD = 1000 * 2;

	@PandaInject
	private ProtectionSettingsService protectionSettingsService;

	private HashMap<UUID, Long> lastMessages = new HashMap<>();
	private BukkitTask task;

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerEnterExitRegion(PlayerEnterExitProtectionEvent e) {
		if (!PermissionsService.BANNEDS_BYPASS.hasPermission(e.getPlayer()) && e.getEnteredProtections().stream()
				.anyMatch(protection -> ((Protection) protection).isBanned(e.getPlayer().getUniqueId()))) {
			e.setCancelled(true);

			if (e.getCause() instanceof PlayerJoinEvent) {
				e.getPlayer().teleport(this.protectionSettingsService.getSpawn());
			}

			if (!lastMessages.containsKey(e.getPlayer().getUniqueId()) || lastMessages.get(e.getPlayer().getUniqueId())
					+ MESSAGE_THRESHOLD <= System.currentTimeMillis()) {
				MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_BANNED.applyPrefix()).process()
						.sendMessage(Bukkit.getPlayer(e.getPlayer().getUniqueId()));
				lastMessages.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());

				runCleanTask();
			}
		}
	}

	public synchronized void runCleanTask() {
		if (this.task != null) {
			this.task = TasksUtils.executeOnAsyncWithTimer(() -> {
				lastMessages.entrySet()
						.removeIf(entry -> entry.getValue() + MESSAGE_THRESHOLD <= System.currentTimeMillis());
				if (lastMessages.isEmpty()) {
					this.task.cancel();
					this.task = null;
				}
			}, MESSAGE_THRESHOLD * 5, MESSAGE_THRESHOLD * 5);
		}
	}

}

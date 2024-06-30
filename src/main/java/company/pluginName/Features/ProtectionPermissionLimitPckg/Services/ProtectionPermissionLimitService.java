package company.pluginName.Features.ProtectionPermissionLimitPckg.Services;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import company.pluginName.Features.ProtectionPermissionLimitPckg.Listeners.ProtectionPermissionLimitListener;
import company.pluginName.Features.ProtectionPermissionLimitPckg.Utils.ProtectionPermissionLimitUtilities;
import company.pluginName.Features.ProtectionPermissionLimitPckg.Utils.ProtectionPermissionLimitUtilities.Summary;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Utils.TimeUtils;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;

@PandaService
public class ProtectionPermissionLimitService {

	@PandaInject
	private ProtectionPermissionLimitListener permissionsListener;

	@PandaInject
	private ProtectionsService protectionsService;

	private BukkitTask task;

	@LoadMethod
	private void load() {
		startTask();
	}

	@UnloadMethod
	private void unload() {
		stopTask();
	}

	@ReloadMethod
	private void reload() {
		stopTask();
		startTask();
	}

	public void startTask() {
		if (ProtectionPermissionLimitUtilities.SETTINGS_PROTECTION_BLOCKPROTECTIONSIFEXCEEDINGLIMITS.isTrue()) {
			long seconds = TimeUtils.stringToSeconds(
					ProtectionPermissionLimitUtilities.SETTINGS_PROTECTION_CHECKIFEXCEEDINGLIMITSEVERY.getContent());

			if (seconds > 0) {
				task = TasksUtils.executeOnAsyncWithTimer(() -> {
					Bukkit.getOnlinePlayers().forEach(player -> {
						Summary summary = ProtectionPermissionLimitUtilities.checkCapacity(player);

						ProtectionPermissionLimitUtilities.sendSummaryMessage(player, summary);
					});
				}, 0, seconds * 20);
			}
		}
	}

	public void stopTask() {
		if (task != null) {
			task.cancel();
		}
		task = null;
	}

}

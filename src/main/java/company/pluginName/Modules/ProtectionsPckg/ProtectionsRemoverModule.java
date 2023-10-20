package company.pluginName.Modules.ProtectionsPckg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitTask;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.Protection.Delete.ProtectionDeleteException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.PurgeConfiguration;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import company.pluginName.Utils.OfflinePlayerUtils;
import company.pluginName.Utils.TimeUtils;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import lombok.Getter;
import relampagorojo93.LibsCollection.SpigotPlugin.LoadOn;
import relampagorojo93.LibsCollection.SpigotPlugin.PluginModule;

public class ProtectionsRemoverModule implements PluginModule {

	private static final long DAYS_IN_MILLIS = 86400000;
	private static final long HOURS_IN_MILLIS = 3600000;
	private static final long MINUTES_IN_MILLIS = 60000;

	private BukkitTask autoRemovalTask;
	private @Getter PurgeConfiguration configuredPurgeConfiguration;

	@Override
	public LoadOn loadOn() {
		return LoadOn.ENABLE;
	}

	@Override
	public boolean optional() {
		return true;
	}

	@Override
	public boolean allowReload() {
		return true;
	}

	@Override
	public boolean load() {
		try {
			if (!SettingString.SETTINGS_PROTECTION_AUTOPURGE_EXECUTEEVERY.getContent().isEmpty()) {
				long millis = TimeUtils.stringToSeconds(SettingString.SETTINGS_PROTECTION_AUTOPURGE_PURGEOLDERTHAN.getContent());
				long executeEvery = TimeUtils.stringToSeconds(SettingString.SETTINGS_PROTECTION_AUTOPURGE_EXECUTEEVERY.getContent());

				if (executeEvery > 0) {
					MessageBuilder.createMessage(MessageString.applyPrefix(String.format(
							"Set auto purge to execute every %d seconds to remove regions older than %d seconds", executeEvery, millis)))
							.sendMessage(Bukkit.getConsoleSender());

					configuredPurgeConfiguration = new PurgeConfiguration().setMillis(millis * 1000);

					autoRemovalTask = Bukkit.getScheduler().runTaskTimerAsynchronously(MainPluginClass.getPlugin(), () -> {
						List<Protection> protectionsToPurge = retrieveProtectionsToPurge(configuredPurgeConfiguration);
						Bukkit.getScheduler().runTask(MainPluginClass.getPlugin(), () -> purgeProtections(protectionsToPurge));
					}, 0, executeEvery * 20);
				}
			}
		} catch (NumberFormatException e) {
			MessageBuilder.createMessage(MessageString.applyPrefix(String.format("Unable to create purge timer: %s", e.getMessage())))
					.sendMessage(Bukkit.getConsoleSender());
		}
		return true;
	}

	@Override
	public boolean unload() {
		if (autoRemovalTask != null) {
			this.autoRemovalTask.cancel();
		}
		return true;
	}

	public List<Protection> retrieveProtectionsToPurge(PurgeConfiguration configuration) {
		long olderThan = System.currentTimeMillis() - (configuration.getDays() * DAYS_IN_MILLIS)
				- (configuration.getHours() * HOURS_IN_MILLIS) - (configuration.getMinutes() * MINUTES_IN_MILLIS)
				- configuration.getMillis();
		List<Protection> protections = new ArrayList<>();

		switch (configuration.getBasedOn()) {
		case PLAYER_LAST_TIME:
			MainPluginClass.getPlugin().getProtectionsModule().getProtectionsByOwner().forEach((owner, ownerProtections) -> {
				OfflinePlayer pl = OfflinePlayerUtils.getOfflinePlayer(owner);
				if (pl != null) {
					if (pl.getLastPlayed() < olderThan) {
						protections.addAll(ownerProtections);
					}
				} else {
					protections.addAll(ownerProtections);
				}
			});
			break;
		case REGION_CREATED_DAYE:
			MainPluginClass.getPlugin().getProtectionsModule().getProtectionsByWorld().forEach((world, ownerProtections) -> {
				ownerProtections.forEach(protection -> {
					if (protection.getCreatedDate() < olderThan) {
						protections.add(protection);
					}
				});
			});
			break;
		}

		return protections;
	}

	public List<Protection> purgeProtections(List<Protection> protectionsToPurge) {
		MessageBuilder
				.createMessage(MessageString.applyPrefix(
						String.format("&7[System] Starting purge process with &e%d &7protection(s)...", protectionsToPurge.size())))
				.sendMessage(Bukkit.getConsoleSender());

		List<Protection> purgedProtections = new ArrayList<>();

		if (protectionsToPurge.size() != 0) {
			Iterator<Protection> iterator = protectionsToPurge.iterator();

			while (iterator.hasNext()) {
				Protection protection = iterator.next();

				if (protection.isProtectionBlockShown()) {
					protection.hideProtectionBlock();
				}

				try {
					MainPluginClass.getPlugin().getProtectionsModule().removeProtection(protection);
					purgedProtections.add(protection);
					iterator.remove();
				} catch (ProtectionDeleteException e) {
					e.printStackTrace();
				}
			}

			MessageBuilder.createMessage(Arrays.asList(MessageString.PREFIX.toString(),
					MessageString.applyPrefix(String.format("&a[System] Purged protections: %d", purgedProtections.size())),
					MessageString.applyPrefix(String.format("&c[System] Failed protections: %d", protectionsToPurge.size())),
					MessageString.PREFIX.toString())).sendMessage(Bukkit.getConsoleSender());
		} else {
			MessageBuilder
					.createMessage(MessageString.applyPrefix(
							String.format("&c[System] Cancelled purge process as no protections were found", protectionsToPurge.size())))
					.sendMessage(Bukkit.getConsoleSender());
		}

		return purgedProtections;
	}

}
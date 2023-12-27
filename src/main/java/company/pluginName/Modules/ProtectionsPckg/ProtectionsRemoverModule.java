package company.pluginName.Modules.ProtectionsPckg;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import relampagorojo93.LibsCollection.JSONLib.JSONArray;
import relampagorojo93.LibsCollection.JSONLib.JSONObject;
import relampagorojo93.LibsCollection.SpigotPlugin.LoadOn;
import relampagorojo93.LibsCollection.SpigotPlugin.PluginModule;

public class ProtectionsRemoverModule implements PluginModule {

	private static final DateFormat FILE_DATE_FORMAT = new SimpleDateFormat("'/purge.'yyyyMMddHHmmss'.json'");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
				long millis = TimeUtils
						.stringToSeconds(SettingString.SETTINGS_PROTECTION_AUTOPURGE_PURGEOLDERTHAN.getContent());
				long executeEvery = TimeUtils
						.stringToSeconds(SettingString.SETTINGS_PROTECTION_AUTOPURGE_EXECUTEEVERY.getContent());

				if (executeEvery > 0) {
					MessageBuilder.createMessage(MessageString.applyPrefix(String.format(
							"Set auto purge to execute every %d seconds to remove regions older than %d seconds",
							executeEvery, millis))).sendMessage(Bukkit.getConsoleSender());

					configuredPurgeConfiguration = new PurgeConfiguration().setMillis(millis * 1000);

					autoRemovalTask = Bukkit.getScheduler().runTaskTimerAsynchronously(MainPluginClass.getPlugin(),
							() -> {
								List<Protection> protectionsToPurge = retrieveProtectionsToPurge(
										configuredPurgeConfiguration);
								Bukkit.getScheduler().runTask(MainPluginClass.getPlugin(),
										() -> purgeProtections(protectionsToPurge));
							}, 0, executeEvery * 20);
				}
			}
		} catch (NumberFormatException e) {
			MessageBuilder
					.createMessage(MessageString
							.applyPrefix(String.format("Unable to create purge timer: %s", e.getMessage())))
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
		long currentTime = System.currentTimeMillis();
		long olderThan = currentTime - (configuration.getDays() * DAYS_IN_MILLIS)
				- (configuration.getHours() * HOURS_IN_MILLIS) - (configuration.getMinutes() * MINUTES_IN_MILLIS)
				- configuration.getMillis();
		List<Protection> protections = new ArrayList<>();

		switch (configuration.getBasedOn()) {
		case PLAYER_LAST_TIME:
			MainPluginClass.getPlugin().getProtectionsModule().getProtectionsByOwner()
					.forEach((owner, ownerProtections) -> {
						OfflinePlayer pl = OfflinePlayerUtils.getOfflinePlayer(owner);
						if (pl != null) {
							if ((pl.isOnline() ? currentTime : pl.getLastPlayed()) <= olderThan) {
								protections.addAll(ownerProtections);
							}
						} else {
							protections.addAll(ownerProtections);
						}
					});
			break;
		case REGION_CREATED_DATE:
			MainPluginClass.getPlugin().getProtectionsModule().getProtectionsByWorld()
					.forEach((world, ownerProtections) -> {
						ownerProtections.forEach(protection -> {
							if (protection.getCreatedDate() <= olderThan) {
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
				.createMessage(MessageString.applyPrefix(String.format(
						"&7[System] Starting purge process with &e%d &7protection(s)...", protectionsToPurge.size())))
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
					MessageString
							.applyPrefix(String.format("&a[System] Purged protections: %d", purgedProtections.size())),
					MessageString
							.applyPrefix(String.format("&c[System] Failed protections: %d", protectionsToPurge.size())),
					MessageString.PREFIX.toString())).sendMessage(Bukkit.getConsoleSender());
		} else {
			MessageBuilder.createMessage(MessageString.applyPrefix(String.format(
					"&c[System] Cancelled purge process as no protections were found", protectionsToPurge.size())))
					.sendMessage(Bukkit.getConsoleSender());
		}

		return purgedProtections;
	}

	public File exportProtections(PurgeConfiguration configuration, List<Protection> protectionsToPurge)
			throws Exception {
		File file = new File(MainPluginClass.getPlugin().getFileModule().PLUGIN_FOLDER.getFolder().getPath()
				+ FILE_DATE_FORMAT.format(new Date()));

		MessageBuilder
				.createMessage(MessageString.applyPrefix(String
						.format("&7[System] Exporting JSON with &e%d &7protection(s)...", protectionsToPurge.size())))
				.sendMessage(Bukkit.getConsoleSender());

		JSONObject json = new JSONObject();

		long currentTime = System.currentTimeMillis();
		long olderThan = currentTime - (configuration.getDays() * DAYS_IN_MILLIS)
				- (configuration.getHours() * HOURS_IN_MILLIS) - (configuration.getMinutes() * MINUTES_IN_MILLIS)
				- configuration.getMillis();
		json.addObject("beforeDate", DATE_FORMAT.format(new Date(olderThan)));

		json.addObject("foundProtections", protectionsToPurge.size());
		json.addObject("totalProtections", MainPluginClass.getPlugin().getProtectionsModule().getProtectionsByOwner()
				.values().stream().map(list -> list.size()).collect(Collectors.reducing(0, (a, b) -> a + b)));

		JSONObject protectionsObject = new JSONObject();

		HashMap<UUID, List<Protection>> protectionsPerOwner = new HashMap<>();
		protectionsToPurge.forEach(protection -> protectionsPerOwner
				.computeIfAbsent(protection.getOwnerUuid(), (uuid) -> new ArrayList<>()).add(protection));

		MainPluginClass.getPlugin().getProtectionsModule().getProtectionsByOwner().keySet().forEach(owner -> {
			OfflinePlayer pl = OfflinePlayerUtils.getOfflinePlayer(owner);
			if (pl != null) {
				List<Protection> protections = protectionsPerOwner.getOrDefault(owner, Collections.emptyList());
				if (!protections.isEmpty() || configuration.isShowIgnoredPlayers()) {
					JSONObject data = new JSONObject();
					data.addObject("ownerName", pl.getName());
					data.addObject("ownerLastTime",
							pl.isOnline() ? "Online" : DATE_FORMAT.format(new Date(pl.getLastPlayed())));

					JSONArray protectionsFromOwner = new JSONArray();

					protections.forEach(protection -> protectionsFromOwner.addObject(protection.getRegionId()));

					data.addObject("protectionsTopurge", protectionsFromOwner);

					protectionsObject.addObject(owner.toString(), data);
				}
			}
		});

		json.addObject("protections", protectionsObject);

		json.save(file);

		return file;
	}

}
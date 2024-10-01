package company.pluginName.Modules.ProtectionsPurgePckg;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitTask;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.FilePckg.FilesService;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPurgePckg.Objects.AutoPurgeLog;
import company.pluginName.Modules.ProtectionsPurgePckg.Objects.PurgeConfiguration;
import company.pluginName.Modules.SQLPckg.SQLService;
import company.pluginName.Utils.DiscordUtilities;
import company.pluginName.Utils.TimeUtils;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import lombok.Getter;
import relampagorojo93.LibsCollection.JSONLib.JSONArray;
import relampagorojo93.LibsCollection.JSONLib.JSONObject;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.IProtection;

@PandaService
public class ProtectionsPurgeService {

	@PandaInject
	private MainPluginClass plugin;

	@PandaInject
	private ProtectionsService protectionsService;

	@PandaInject
	private FilesService filesService;

	@PandaInject
	private SQLService sqlService;

	private static final DateFormat FILE_DATE_FORMAT = new SimpleDateFormat("'/purge.'yyyyMMddHHmmss'.json'");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final long DAYS_IN_MILLIS = 86400000;
	private static final long HOURS_IN_MILLIS = 3600000;
	private static final long MINUTES_IN_MILLIS = 60000;

	private BukkitTask autoRemovalTask;
	private @Getter PurgeConfiguration configuredPurgeConfiguration;

	private AutoPurgeLog lastLog;

	@LoadMethod
	public void load() throws ReportError {
		try {
			if (!Settings.SETTINGS_PROTECTION_AUTOPURGE_EXECUTEEVERY.getContent().isEmpty()) {
				long olderThan = TimeUtils
						.stringToSeconds(Settings.SETTINGS_PROTECTION_AUTOPURGE_PURGEOLDERTHAN.getContent());
				long executeEvery = TimeUtils
						.stringToSeconds(Settings.SETTINGS_PROTECTION_AUTOPURGE_EXECUTEEVERY.getContent());

				if (executeEvery > 0) {
					plugin.sendDebug(getClass(), String.format(
							"Set auto purge to execute every %d seconds to remove regions older than %d seconds",
							executeEvery, olderThan));

					configuredPurgeConfiguration = new PurgeConfiguration().setMillis(olderThan * 1000);
					lastLog = sqlService.getLastAutoPurgeLog();

					if (lastLog == null) {
						plugin.sendDebug(getClass(), String.format(
								"There's no last auto-purge log. Creating a new one with the current millis. Next auto-purge will be executed once the last log is older than '%d' seconds.",
								executeEvery));
						lastLog = new AutoPurgeLog(System.currentTimeMillis(),
								this.configuredPurgeConfiguration.getMillis(), 0);
						sqlService.saveAutoPurgeLog(lastLog);
					}

					autoRemovalTask = TasksUtils.executeOnAsyncWithTimer(() -> {
						lastLog = new AutoPurgeLog(System.currentTimeMillis(),
								this.configuredPurgeConfiguration.getMillis(), 0);
						List<Protection> protectionsToPurge = retrieveProtectionsToPurge(configuredPurgeConfiguration);
						TasksUtils.execute(() -> {
							FutureTask<List<Protection>> purgedProtections = purgeProtections(protectionsToPurge);
							TasksUtils.executeOnAsync(() -> {
								try {
									DiscordUtilities.sendPurgeSummaryMessage(null, configuredPurgeConfiguration,
											purgedProtections.get());
									lastLog = new AutoPurgeLog(System.currentTimeMillis(),
											this.configuredPurgeConfiguration.getMillis(),
											purgedProtections.get().size());
									sqlService.saveAutoPurgeLog(lastLog);
								} catch (InterruptedException | ExecutionException e) {
									e.printStackTrace();
								}
							});
						});

					}, (long) (Math.max(
							((executeEvery * 1000) - (System.currentTimeMillis() - lastLog.getExecutionMillis())), 0)
							/ 50), (long) (executeEvery * 20));
				}
			}
		} catch (NumberFormatException e) {
			throw new ReportResult.ReportError(PandaPrefixedStringField
					.applyPrefix(String.format("Unable to create purge timer: %s", e.getMessage())), false, e);
		}
	}

	@UnloadMethod
	public void unload() {
		if (autoRemovalTask != null) {
			this.autoRemovalTask.cancel();
		}
		lastLog = null;
	}

	@ReloadMethod
	private void reload() throws ReportError {
		unload();
		load();
	}

	public boolean isRunning() {
		return this.configuredPurgeConfiguration != null;
	}

	public long calculateExpiresIn(IProtection protection) {
		if (this.configuredPurgeConfiguration != null) {
			long currentTime = System.currentTimeMillis();
			long olderThan = currentTime - (this.configuredPurgeConfiguration.getDays() * DAYS_IN_MILLIS)
					- (this.configuredPurgeConfiguration.getHours() * HOURS_IN_MILLIS)
					- (this.configuredPurgeConfiguration.getMinutes() * MINUTES_IN_MILLIS)
					- this.configuredPurgeConfiguration.getMillis();

			switch (this.configuredPurgeConfiguration.getBasedOn()) {
			case PLAYER_LAST_TIME:
				return Math.max(protection.getOwnerLastPlayed() - olderThan, 0);
			case REGION_CREATED_DATE:
				return Math.max(protection.getCreatedDate() - olderThan, 0);
			}
		}
		return Long.MAX_VALUE;
	}

	public List<Protection> retrieveProtectionsToPurge(PurgeConfiguration configuration) {
		long currentTime = System.currentTimeMillis();
		long olderThan = currentTime - (configuration.getDays() * DAYS_IN_MILLIS)
				- (configuration.getHours() * HOURS_IN_MILLIS) - (configuration.getMinutes() * MINUTES_IN_MILLIS)
				- configuration.getMillis();
		List<Protection> protections = new ArrayList<>();

		switch (configuration.getBasedOn()) {
		case PLAYER_LAST_TIME:
			protectionsService.getProtectionByRegion().values().stream()
					.filter(protection -> protection.getOwnerLastPlayed() <= olderThan).forEach(protections::add);
			break;
		case REGION_CREATED_DATE:
			protectionsService.getProtectionByRegion().values().stream()
					.filter(protection -> protection.getCreatedDate() <= olderThan).forEach(protections::add);
			break;
		}

		return protections;
	}

	public FutureTask<List<Protection>> purgeProtections(List<Protection> protectionsToPurge) {
		MessageTemplate
				.inst(PandaPrefixedStringField.applyPrefix(String.format(
						"&7[System] Starting purge process with &e%d &7protection(s)...", protectionsToPurge.size())))
				.process().sendMessage(Bukkit.getConsoleSender());

		FutureTask<List<Protection>> task;

		if (protectionsToPurge.size() != 0) {
			task = new FutureTask<>(() -> {
				List<Protection> purgedProtections = new ArrayList<>();
				Iterator<Protection> iterator = protectionsToPurge.iterator();

				while (iterator.hasNext()) {
					try {
						Protection protection = iterator.next();

						if (protection.getUtils().isProtectionBlockShown()) {
							protection.getUtils().hideProtectionBlock();
						}

						if (protection.getBoundaries().isProtectionViewActive()) {
							protection.getBoundaries().toggleProtectionView();
						}

						protection.delete(RemovalCause.PURGE).subscribe();

						purgedProtections.add(protection);
						iterator.remove();
					} catch (RoyaleProtectionBlocksExceptionImpl e) {
						e.printStackTrace();
					}
				}

				MessageTemplate.inst(Arrays.asList(MainPluginClass.PREFIX.getContent(),
						PandaPrefixedStringField.applyPrefix(
								String.format("&a[System] Purged protections: %d", purgedProtections.size())),
						PandaPrefixedStringField.applyPrefix(
								String.format("&c[System] Failed protections: %d", protectionsToPurge.size())),
						MainPluginClass.PREFIX.getContent())).process().sendMessage(Bukkit.getConsoleSender());

				return purgedProtections;
			});

			TasksUtils.execute(() -> {
				protectionsToPurge.forEach(protection -> {
					if (protection.getUtils().isProtectionBlockShown()) {
						protection.getUtils().hideProtectionBlock();
					}
				});

				TasksUtils.executeOnAsync(task::run);
			});
		} else {
			task = new FutureTask<>(() -> Collections.emptyList());

			task.run();

			MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(String.format(
					"&c[System] Cancelled purge process as no protections were found", protectionsToPurge.size())))
					.process().sendMessage(Bukkit.getConsoleSender());
		}

		return task;
	}

	public File exportProtections(PurgeConfiguration configuration, List<Protection> protectionsToPurge)
			throws Exception {
		File file = new File(filesService.getPluginFolder().getFile().getPath() + FILE_DATE_FORMAT.format(new Date()));

		MessageTemplate
				.inst(PandaPrefixedStringField.applyPrefix(String
						.format("&7[System] Exporting JSON with &e%d &7protection(s)...", protectionsToPurge.size())))
				.process().sendMessage(Bukkit.getConsoleSender());

		JSONObject json = new JSONObject();

		long currentTime = System.currentTimeMillis();
		long olderThan = currentTime - (configuration.getDays() * DAYS_IN_MILLIS)
				- (configuration.getHours() * HOURS_IN_MILLIS) - (configuration.getMinutes() * MINUTES_IN_MILLIS)
				- configuration.getMillis();
		json.addObject("beforeDate", DATE_FORMAT.format(new Date(olderThan)));

		json.addObject("foundProtections", protectionsToPurge.size());
		json.addObject("totalProtections", protectionsService.getProtectionsByOwner().values().stream()
				.map(list -> list.size()).collect(Collectors.reducing(0, (a, b) -> a + b)));

		JSONObject protectionsObject = new JSONObject();

		HashMap<UUID, List<Protection>> protectionsPerOwner = new HashMap<>();
		protectionsToPurge.forEach(protection -> protectionsPerOwner
				.computeIfAbsent(protection.getOwnerUuid(), (uuid) -> new ArrayList<>()).add(protection));

		protectionsService.getProtectionsByOwner().keySet().forEach(owner -> {
			OfflinePlayer pl = OfflinePlayerUtilities.getOfflinePlayer(owner);
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
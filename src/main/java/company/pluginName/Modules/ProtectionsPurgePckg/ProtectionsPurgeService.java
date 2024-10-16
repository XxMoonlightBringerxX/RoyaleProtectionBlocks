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

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.FilePckg.FilesService;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPurgePckg.Objects.PurgeConfiguration;
import company.pluginName.Modules.ProtectionsPurgePckg.Objects.PurgeConfiguration.BasedOn;
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
import darkpanda73.PandaUtils.Utilities.Java.SleepingLoop;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import lombok.Getter;
import relampagorojo93.LibsCollection.JSONLib.JSONArray;
import relampagorojo93.LibsCollection.JSONLib.JSONObject;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionRemovalAttemptEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;

@PandaService(priority = 1000)
public class ProtectionsPurgeService {

	@PandaInject
	private MainPluginClass plugin;

	@PandaInject
	private PlayerInteractionsService playerInteractionsService;

	@PandaInject
	private ProtectionsService protectionsService;

	@PandaInject
	private FilesService filesService;

	@PandaInject
	private SQLService sqlService;

	private static final DateFormat FILE_DATE_FORMAT = new SimpleDateFormat("'/purge.'yyyyMMddHHmmss'.json'");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private @Getter PurgeConfiguration configuredPurgeConfiguration;
	private SleepingLoop sleepingLoop;

	@LoadMethod
	public void load() throws ReportError {
		try {
			if (!Settings.SETTINGS_PROTECTION_AUTOPURGE_PURGEOLDERTHAN.getContent().isEmpty()) {
				long olderThan = TimeUtils
						.stringToSeconds(Settings.SETTINGS_PROTECTION_AUTOPURGE_PURGEOLDERTHAN.getContent());

				if (olderThan > 0) {
					plugin.sendDebug(getClass(),
							String.format("Set auto purge to remove regions older than %d seconds", olderThan));
					configuredPurgeConfiguration = new PurgeConfiguration().setMillis(olderThan * 1000);

					sleepingLoop = new SleepingLoop(() -> {
						List<Protection> protectionsToPurge = retrieveProtectionsToPurge(configuredPurgeConfiguration);

						if (protectionsToPurge.size() > 0) {
							try {
								FutureTask<List<Protection>> purgeTask = purgeProtections(protectionsToPurge,
										RemovalCause.AUTO_PURGE);

								List<Protection> removedProtections = purgeTask.get();

								DiscordUtilities.sendPurgeSummaryMessage(null, this.configuredPurgeConfiguration,
										removedProtections);
							} catch (InterruptedException | ExecutionException e) {
							}
						}
					}, () -> {
						Thread.sleep(5000);

						Protection protectionFound = this.protectionsService.getProtectionByRegion().values().stream()
								.filter(prot -> !prot.isDeleted() && !prot.isCreationInProgress()
										&& !prot.isRemovalInProgress())
								.sorted((prot1,
										prot2) -> configuredPurgeConfiguration.getBasedOn() == BasedOn.PLAYER_LAST_TIME
												? Long.compare(prot1.getOwnerLastPlayed(), prot2.getOwnerLastPlayed())
												: Long.compare(prot1.getCreatedDate(), prot2.getCreatedDate()))
								.findFirst().orElse(null);

						if (protectionFound != null) {
							return configuredPurgeConfiguration.getRemainingTime(protectionFound);
						}
						return Long.MAX_VALUE;
					});

					this.sleepingLoop.start();
				}
			}
		} catch (NumberFormatException e) {
			throw new ReportResult.ReportError(PandaPrefixedStringField
					.applyPrefix(String.format("Unable to create purge timer: %s", e.getMessage())), false, e);
		}
	}

	@UnloadMethod
	public void unload() {
		if (sleepingLoop != null) {
			this.sleepingLoop.interrupt();
		}
	}

	@ReloadMethod
	private void reload() throws ReportError {
		unload();
		load();
	}

	public void awakePurgeTask() {
		if (this.sleepingLoop != null) {
			this.sleepingLoop.awake();
		}
	}

	public boolean isRunning() {
		return this.configuredPurgeConfiguration != null;
	}

	public long calculateExpiresIn(IProtection protection) {
		if (this.configuredPurgeConfiguration != null) {
			return this.configuredPurgeConfiguration.getRemainingTime(protection);
		}
		return Long.MAX_VALUE;
	}

	public List<Protection> retrieveProtectionsToPurge(PurgeConfiguration configuration) {
		return configuration.getRemainingTime(protectionsService.getProtectionByRegion().values())
				.filter(pair -> pair.getSecond() <= 0).map(Pair::getFirst).collect(Collectors.toList());
	}

	public FutureTask<List<Protection>> purgeProtections(List<Protection> protectionsToPurge,
			RemovalCause removalCause) {
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

						ProtectionRemovalAttemptEvent attemptEvent = new ProtectionRemovalAttemptEvent(null, protection,
								removalCause);
						Bukkit.getPluginManager().callEvent(attemptEvent);

						if (protection.getUtils().isProtectionBlockShown()) {
							protection.getUtils().hideProtectionBlock();
						}

						if (protection.getBoundaries().isProtectionViewActive()) {
							protection.getBoundaries().toggleProtectionView();
						}

						protection.delete(removalCause).subscribe();

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
		long olderThan = currentTime - (configuration.getDays() * PurgeConfiguration.DAYS_IN_MILLIS)
				- (configuration.getHours() * PurgeConfiguration.HOURS_IN_MILLIS)
				- (configuration.getMinutes() * PurgeConfiguration.MINUTES_IN_MILLIS) - configuration.getMillis();
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
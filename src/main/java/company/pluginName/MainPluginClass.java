package company.pluginName;

import company.pluginName.Modules.BStatsPckg.Objects.ProvidedProtectionsCustomChart;
import darkpanda73.PandaUtils.PandaBStats.Annotations.EnableBStats;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.EnableMessagesListener;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListeners;
import darkpanda73.PandaUtils.PandaUpdateChecker.Annotations.EnableUpdateChecker;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.EnableCommandsService;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Annotations.EnableDiscordWebhookService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.EnableInventoriesService;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.EnablePlayerHeadCacheService;
import darkpanda73.PandaUtils.Services.PandaPlayerDataModule.Listeners.PandaPlayerDataEvents;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Events.InventoryEvents;

@PandaListeners({ InventoryEvents.class, PandaPlayerDataEvents.class })
@EnableCommandsService
@EnableMessagesListener
@EnableInventoriesService
@EnablePlayerHeadCacheService
@EnableUpdateChecker("109118")
@EnableBStats(value = 21346, customCharts = ProvidedProtectionsCustomChart.class)
@EnableDiscordWebhookService
public class MainPluginClass extends PandaPluginClass {

	@RegisteredPandaField("lang")
	public static final PandaStringField PREFIX = new PandaStringField("Prefix",
			"&d&lRoyale&fProtectionBlocks >>&r&7 ");

	@Override
	public String getPrefix() {
		return PREFIX.toString();
	}

}

package company.pluginName;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListeners;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.EnableCommandsService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.EnableInventoriesService;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.EnablePlayerHeadCacheService;
import darkpanda73.PandaUtils.Services.PandaLoggingModule.Annotations.EnableLoggingService;
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Annotations.EnableMessageListenerService;
import darkpanda73.PandaUtils.Services.PandaPlayerDataModule.Listeners.PandaPlayerDataEvents;

@PandaListeners({ PandaPlayerDataEvents.class })
@EnableCommandsService
@EnableMessageListenerService
@EnableInventoriesService
@EnablePlayerHeadCacheService
//@EnableUpdateChecker("109118")
//@EnableBStats(value = 21346, customCharts = ProvidedProtectionsCustomChart.class)
@EnableLoggingService
public class MainPluginClass extends PandaPluginClass {

	@RegisteredPandaField("lang")
	public static final PandaStringField PREFIX = new PandaStringField("Prefix",
			"&d&lRoyale&fProtectionBlocks &5FREE &f>>&r&7 ");

	@Override
	public String getPrefix() {
		return PREFIX.toString();
	}

}

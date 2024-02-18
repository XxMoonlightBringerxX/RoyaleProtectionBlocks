package company.pluginName;

import company.pluginName.APIs.WorldGuard.WorldGuardAPI;
import company.pluginName.Bukkit.Inventories.Protections.Flags.Objects.Flag;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.EnableMessagesListener;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListeners;
import darkpanda73.PandaUtils.PandaUpdateChecker.Annotations.EnableUpdateChecker;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.EnableCommandsService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.EnableInventoriesService;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.EnablePlayerHeadCacheService;
import darkpanda73.PandaUtils.Services.PandaPlayerDataModule.Listeners.PandaPlayerDataEvents;
import lombok.Getter;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Events.InventoryEvents;

@PandaListeners({ InventoryEvents.class, PandaPlayerDataEvents.class })
@EnableCommandsService
@EnableMessagesListener
@EnableInventoriesService
@EnablePlayerHeadCacheService
@EnableUpdateChecker
public class MainPluginClass extends PandaPluginClass {

	@RegisteredPandaField("lang")
	public static final PandaStringField PREFIX = new PandaStringField("Prefix",
			"&d&lRoyale&fProtectionBlocks >>&r&7 ");

	@PandaInject
	public static WorldGuardAPI worldGuardApi;

	private boolean worldGuardHooked = false;

	private @Getter String spigotResourceId = "109118";

	@Override
	public String getPrefix() {
		return PREFIX.toString();
	}

	@Override
	public void onAfterEnablePlugin() {
		if (worldGuardApi.getHook() != null && worldGuardApi.getHook().isHooked()) {
			try {
				worldGuardApi.getHook().registerHandlers();
				worldGuardHooked = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Flag.initFlags();
		super.onAfterEnablePlugin();
	}

	@Override
	public void onAfterReloadPlugin() {
		if (!worldGuardHooked && worldGuardApi.getHook() != null && worldGuardApi.getHook().isHooked()) {
			try {
				worldGuardApi.getHook().registerHandlers();
				worldGuardHooked = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Flag.initFlags();
		super.onAfterReloadPlugin();
	}

}

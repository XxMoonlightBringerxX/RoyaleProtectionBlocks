package company.pluginName.Modules.ProtectionSettingsPckg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.FilePckg.FilesService;
import company.pluginName.Modules.ProtectionSettingsPckg.Objects.Settings.GreetingFarewellMessagesSetting;
import company.pluginName.Modules.ProtectionSettingsPckg.Objects.Settings.TNTExplosionsSetting;
import company.pluginName.Modules.ProtectionSettingsPckg.Objects.Settings.Templates.SettingImpl;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.PandaCommandsService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.SettingInterface;

@PandaService(loadOn = LoadStep.ENABLE)
public class ProtectionSettingsService {

	public static final TNTExplosionsSetting TNT_EXPLOSIONS_SETTING = new TNTExplosionsSetting();
	public static final GreetingFarewellMessagesSetting GREETING_FAREWELL_MESSAGES_SETTING = new GreetingFarewellMessagesSetting();

	@PandaInject
	private MainPluginClass plugin;

	@PandaInject
	private FilesService filesService;

	@PandaInject
	private PandaCommandsService commandsService;

	private PandaYamlFile protectionSettingsFile;
	private Map<String, SettingInterface<?>> registeredSettings = new HashMap<>();
	private boolean loaded = false;

	public ProtectionSettingsService() {
		registerSetting(TNT_EXPLOSIONS_SETTING);
		registerSetting(GREETING_FAREWELL_MESSAGES_SETTING);
	}

	@PostInjectMethod
	private void postInject() throws IOException, YamlException {
		protectionSettingsFile = new PandaYamlFile("protectionsettings",
				filesService.getFolder("plugin").getFile().getPath() + "/Protection-Settings.yml",
//				new PandaYaml(plugin.getResource(plugin.getClass().getPackage().getName().replaceAll("\\.", "/")
//						+ "/Resources/Protection-Settings.yml"))
				null);
		filesService.addFile(protectionSettingsFile);
	}

	@LoadMethod
	private void load() throws FileNotFoundException, YamlException, IOException {
		this.registeredSettings.values().forEach(setting -> protectionSettingsFile.registerFieldsFromObject(setting));

		this.protectionSettingsFile.refreshFields();

		this.updateItems();

		this.loaded = true;
	}

	@ReloadMethod
	private void reload() {
		this.updateItems();
	}

	private void updateItems() {
		try {
			PandaYaml yaml = new PandaYaml(protectionSettingsFile.getFile());

			registeredSettings.values().forEach(setting -> {
				if (setting instanceof SettingImpl<?>) {
					if (!yaml.getRoot().has("Settings." + setting.getId() + ".Display-item")) {
						ItemBuilder.inst().fromItem(((SettingImpl<?>) setting).getDefaultDisplayItem()).toMap()
								.forEach((key, value) -> {
									yaml.getRoot().set("Settings." + setting.getId() + ".Display-item." + key, value);
								});
					}

					((SettingImpl<?>) setting).setDisplayItem(ItemBuilder.inst()
							.fromMap(yaml.getRoot().getSection("Settings." + setting.getId() + ".Display-item").toMap())
							.build());
				}
			});

			yaml.saveYAML(protectionSettingsFile.getFile());
		} catch (YamlException | IOException e) {
			e.printStackTrace();
		}
	}

	public void registerSetting(SettingInterface<?> setting) {
		if (!loaded) {
			this.registeredSettings.put(setting.getId(), setting);
		} else {
			throw new UnsupportedOperationException("You can't register settings when the plugin is already loaded");
		}
	}

	public List<String> getSettingIds() {
		return new ArrayList<>(this.registeredSettings.keySet());
	}

	public List<SettingInterface<?>> getSettings() {
		return new ArrayList<>(this.registeredSettings.values());
	}

	public SettingInterface<?> getSetting(String id) {
		return this.registeredSettings.get(id);
	}

}

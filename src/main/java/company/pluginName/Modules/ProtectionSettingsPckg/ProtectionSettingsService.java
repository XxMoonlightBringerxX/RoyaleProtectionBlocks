package company.pluginName.Modules.ProtectionSettingsPckg;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.FilePckg.FilesService;
import company.pluginName.Modules.ProtectionSettingsPckg.Objects.FlySetting;
import company.pluginName.Modules.ProtectionSettingsPckg.Objects.TeleportSetting;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.PandaCommandsService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.AbstractSetting;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.BooleanSetting;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.DoubleSetting;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.StringListSetting;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.StringSetting;

@PandaService(loadOn = LoadStep.ENABLE, priority = 998)
public class ProtectionSettingsService {

	public static final FlySetting FLY_SETTING = new FlySetting();
	public static final TeleportSetting TELEPORT_SETTING = new TeleportSetting();

	@PandaInject
	private MainPluginClass plugin;

	@PandaInject
	private FilesService filesService;

	@PandaInject
	private PandaCommandsService commandsService;

	private PandaYamlFile protectionSettingsFile;
	private Map<String, AbstractSetting<?>> registeredSettings = new HashMap<>();

	public ProtectionSettingsService() {
		registerSetting(FLY_SETTING);
		registerSetting(TELEPORT_SETTING);
	}

	@LoadMethod
	private void load() {
		try {
			PandaYaml yaml = new PandaYaml(protectionSettingsFile.getFile());

			registeredSettings.values().forEach(setting -> readSection(setting, yaml.getRoot()));

			yaml.saveYAML(protectionSettingsFile.getFile());
		} catch (YamlException | IOException e) {
			e.printStackTrace();
		}
	}

	@PostInjectMethod
	private void postInject() {
		try {
			protectionSettingsFile = new PandaYamlFile("protectionsettings",
					filesService.getFolder("plugin").getFile().getPath() + "/ProtectionSettings.yml",
					new PandaYaml(plugin.getResource(plugin.getClass().getPackage().getName().replaceAll("\\.", "/")
							+ "/Resources/ProtectionSettings.yml")));
			filesService.addFile(protectionSettingsFile);
		} catch (IOException | YamlException e) {
			e.printStackTrace();
		}
	}

	public void registerSetting(AbstractSetting<?> setting) {
		this.registeredSettings.put(setting.getId(), setting);
	}

	public List<String> getSettingIds() {
		return new ArrayList<>(this.registeredSettings.keySet());
	}

	public List<AbstractSetting<?>> getSettings() {
		return new ArrayList<>(this.registeredSettings.values());
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> AbstractSetting<T> getSetting(String id) {
		try {
			return (AbstractSetting<T>) this.registeredSettings.get(id);
		} catch (ClassCastException e) {
			return null;
		}
	}

	private void readSection(AbstractSetting<?> setting, YamlSection root) {
		if (!root.has("Settings." + setting.getId() + ".Display-item")) {
			ItemBuilder.inst().fromItem(setting.getDefaultDisplayItem()).toMap().forEach((key, value) -> {
				root.set("Settings." + setting.getId() + ".Display-item." + key, value);
			});
		}

		setting.setDisplayItem(ItemBuilder.inst()
				.fromMap(root.getSectionOrDefault("Settings." + setting.getId() + ".Display-item").toMap()).build());

		if (!root.has("Settings." + setting.getId() + ".Name")) {
			root.set("Settings." + setting.getId() + ".Name", setting.getDefaultName());
		}

		setting.setName(root.getData("Settings." + setting.getId() + ".Name").getString());

		if (!root.has("Settings." + setting.getId() + ".Editable")) {
			root.set("Settings." + setting.getId() + ".Editable", true);
		}

		setting.setEditable(root.getData("Settings." + setting.getId() + ".Editable").getBoolean());

		if (!root.has("Settings." + setting.getId() + ".Permission")) {
			root.set("Settings." + setting.getId() + ".Permission", null);
		}

		setting.setPermission(root.getData("Settings." + setting.getId() + ".Permission").getString());

		if (!root.has("Settings." + setting.getId() + ".Cost")) {
			root.set("Settings." + setting.getId() + ".Cost", null);
		}

		setting.setCost(root.getData("Settings." + setting.getId() + ".Cost").getDouble(0D));

		if (!root.has("Settings." + setting.getId() + ".Defaults.Non-members")) {
			root.set("Settings." + setting.getId() + ".Defaults.Non-members", setting.getNonMembersDefaultValue());
		}

		if (!root.has("Settings." + setting.getId() + ".Defaults.Members")) {
			root.set("Settings." + setting.getId() + ".Defaults.Members", setting.getMembersDefaultValue());
		}

		if (!root.has("Settings." + setting.getId() + ".Defaults.Owners")) {
			root.set("Settings." + setting.getId() + ".Defaults.Owners", setting.getOwnersDefaultValue());
		}

		readDefaults(setting, root.getSection("Settings." + setting.getId() + ".Defaults"));
	}

	private void readDefaults(AbstractSetting<?> setting, YamlSection defaultsSection) {
		if (setting instanceof BooleanSetting) {
			((BooleanSetting) setting).setNonMembersValue(defaultsSection.getData("Non-members").getBoolean());
			((BooleanSetting) setting).setMembersValue(defaultsSection.getData("Members").getBoolean());
			((BooleanSetting) setting).setOwnersValue(defaultsSection.getData("Owners").getBoolean());
		} else if (setting instanceof DoubleSetting) {
			((DoubleSetting) setting).setNonMembersValue(defaultsSection.getData("Non-members").getDouble());
			((DoubleSetting) setting).setMembersValue(defaultsSection.getData("Members").getDouble());
			((DoubleSetting) setting).setOwnersValue(defaultsSection.getData("Owners").getDouble());
		} else if (setting instanceof StringListSetting) {
			((StringListSetting) setting)
					.setNonMembersValue(new ArrayList<>(defaultsSection.getData("Non-members").getStringList()));
			((StringListSetting) setting)
					.setMembersValue(new ArrayList<>(defaultsSection.getData("Members").getStringList()));
			((StringListSetting) setting)
					.setOwnersValue(new ArrayList<>(defaultsSection.getData("Owners").getStringList()));
		} else if (setting instanceof StringSetting) {
			((StringSetting) setting).setNonMembersValue(defaultsSection.getData("Non-members").getString());
			((StringSetting) setting).setMembersValue(defaultsSection.getData("Members").getString());
			((StringSetting) setting).setOwnersValue(defaultsSection.getData("Owners").getString());
		}
	}

}

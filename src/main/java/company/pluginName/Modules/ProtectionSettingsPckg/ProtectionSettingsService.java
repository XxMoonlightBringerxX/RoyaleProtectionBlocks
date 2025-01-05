package company.pluginName.Modules.ProtectionSettingsPckg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import company.pluginName.Modules.ProtectionSettingsPckg.Objects.AbstractSetting;
import company.pluginName.Modules.ProtectionSettingsPckg.Objects.Defaults.BuildSetting;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;

@PandaService(loadOn = LoadStep.LOAD)
public class ProtectionSettingsService {

	private Map<String, AbstractSetting<?>> registeredSettings = new HashMap<>();

	@LoadMethod
	private void load() {
		registerSetting(new BuildSetting());
	}

	public void registerSetting(AbstractSetting<?> setting) {
		this.registeredSettings.put(setting.getId(), setting);
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> AbstractSetting<T> getSetting(String id) {
		try {
			return (AbstractSetting<T>) this.registeredSettings.get(id);
		} catch (ClassCastException e) {
			return null;
		}
	}

}

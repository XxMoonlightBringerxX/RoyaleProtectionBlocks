package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionSettingsPckg.Objects.AbstractSetting;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtectionSettings {

	@PandaInject
	private static ProtectionSettingsService protectionSettingsService;

	private Protection protection;

	private Map<String, Object> settings = new HashMap<>();

	public ProtectionSettings(Protection protection) {
		this.protection = protection;
	}

	public void resetSettings() {
		this.settings.clear();
	}

	public <T extends Serializable> void setValue(AbstractSetting<T> setting, T value)
			throws RoyaleProtectionBlocksExceptionImpl {
		if (protectionSettingsService.getSetting(setting.getId()) != setting) {
			// TODO replace with a custom exception for this things
			throw Exceptions.Protections.UNKNOWN.generateException();
		}

		this.settings.put(setting.getId(), value);
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getValue(AbstractSetting<T> setting) throws RoyaleProtectionBlocksExceptionImpl {
		if (protectionSettingsService.getSetting(setting.getId()) != setting) {
			// TODO replace with a custom exception for this things
			throw Exceptions.Protections.UNKNOWN.generateException();
		}

		return (T) this.settings.get(setting.getId());
	}

}

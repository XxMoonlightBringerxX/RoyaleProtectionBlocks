package company.pluginName.Modules.ProtectionsPckg.Objects.Components.Settings;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.SQLPckg.SQLService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import lombok.AllArgsConstructor;
import lombok.Data;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.PermissionGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.SettingInterface;

@Data
@AllArgsConstructor
public class ProtectionSettingManager {

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_SETTINGS_TRUEVALUENAME = new PandaPrefixedStringField(
			"Inventory.Protection.Settings.True-value-name", "&aTrue");

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_SETTINGS_FALSEVALUENAME = new PandaPrefixedStringField(
			"Inventory.Protection.Settings.False-value-name", "&cFalse");

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_SETTINGS_STRINGVALUENAME = new PandaPrefixedStringField(
			"Inventory.Protection.Settings.String-value-name", "&b{text}");

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_SETTINGS_NOTDEFINEDNAME = new PandaPrefixedStringField(
			"Inventory.Protection.Settings.Not-defined-name", "&7Not defined");

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_SETTINGS_NUMERICVALUENAME = new PandaPrefixedStringField(
			"Inventory.Protection.Settings.Numeric-value-name", "&b{number}");

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_SETTINGS_UNKNOWNVALUENAME = new PandaPrefixedStringField(
			"Inventory.Protection.Settings.Unknown-value-name", "&7???");

	@PandaInject
	private static ProtectionSettingsService protectionSettingsService;

	@PandaInject
	private static SQLService sqlService;

	@PandaInject
	private static PlayerDataService playerDataService;

	private Protection protection;

	private Map<PermissionGroup, Map<String, Serializable>> settings = new HashMap<>();

	public ProtectionSettingManager(Protection protection) {
		this.protection = protection;
	}

	public void resetSettings() {
		this.settings.clear();
	}

	public <T extends Serializable> void setUnparsedValue(SettingInterface<T> setting, PermissionGroup group,
			String value) throws RoyaleProtectionBlocksExceptionImpl {
		T object;

		try {
			object = setting.parseStringToValue(value);
		} catch (Throwable e) {
			throw Exceptions.Protections.Settings.INVALIDVALUE.generateException();
		}

		setValue(setting, group, object);
	}

	public <T extends Serializable> void setValue(SettingInterface<T> setting, PermissionGroup group, T value)
			throws RoyaleProtectionBlocksExceptionImpl {
		this.settings.computeIfAbsent(group, (g) -> new HashMap<>()).put(setting.getId(), value);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtectionSetting(protection.getProtectionId(), setting.getId(), group, value);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public <T extends Serializable> T getValue(SettingInterface<T> setting, Player player) {
		return getValue(setting, protection.getGroup(player));
	}

	public <T extends Serializable> T getValue(SettingInterface<T> setting, UUID playerUuid) {
		return getValue(setting, protection.getGroup(playerUuid));
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getValue(SettingInterface<T> setting, PermissionGroup group) {
		try {
			return (T) this.settings.getOrDefault(group, Collections.emptyMap()).getOrDefault(setting.getId(),
					setting.getValue(group));
		} catch (ClassCastException e) {
			if (this.settings.containsKey(group)) {
				this.settings.get(group).remove(setting.getId());
			}
			return setting.getValue(group);
		}
	}

	public String getValueAsString(SettingInterface<?> setting, Player player)
			throws RoyaleProtectionBlocksExceptionImpl {
		return getValueAsString(setting, protection.getGroup(player));
	}

	public String getValueAsString(SettingInterface<?> setting, PermissionGroup group)
			throws RoyaleProtectionBlocksExceptionImpl {
		Serializable value = getValue(setting, group);

		if (value == null) {
			return MESSAGE_PROTECTIONS_SETTINGS_NOTDEFINEDNAME.toString();
		}

		if (value instanceof Boolean) {
			return (Boolean.TRUE.equals(value) ? MESSAGE_PROTECTIONS_SETTINGS_TRUEVALUENAME
					: MESSAGE_PROTECTIONS_SETTINGS_FALSEVALUENAME).toString();
		} else if (value instanceof String) {
			String text = (String) value;
			return MessageTemplate.inst(MESSAGE_PROTECTIONS_SETTINGS_STRINGVALUENAME.toString())
					.setReplacements(new Replacement("{text}", () -> text != null ? text : "---")).process().toString();
		} else if (value instanceof Number) {
			String text = value.toString();
			return MessageTemplate.inst(MESSAGE_PROTECTIONS_SETTINGS_NUMERICVALUENAME.toString())
					.setReplacements(new Replacement("{number}", () -> text != null ? text : "---")).process()
					.toString();
		}

		return MESSAGE_PROTECTIONS_SETTINGS_UNKNOWNVALUENAME.toString();
	}

	public String getValueAsStringSafely(SettingInterface<?> setting, Player player) {
		try {
			return getValueAsString(setting, player);
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			return MESSAGE_PROTECTIONS_SETTINGS_UNKNOWNVALUENAME.toString();
		}
	}

	public String getValueAsStringSafely(SettingInterface<?> setting, PermissionGroup group) {
		try {
			return getValueAsString(setting, group);
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			return MESSAGE_PROTECTIONS_SETTINGS_UNKNOWNVALUENAME.toString();
		}
	}

}

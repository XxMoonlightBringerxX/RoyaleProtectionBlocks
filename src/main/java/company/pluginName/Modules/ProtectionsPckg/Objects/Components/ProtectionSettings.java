package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
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
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.SettingGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.AbstractSetting;

@Data
@AllArgsConstructor
public class ProtectionSettings {

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

	private Protection protection;

	private Map<String, Serializable> nonMembersSettings = new HashMap<>();
	private Map<String, Serializable> membersSettings = new HashMap<>();
	private Map<String, Serializable> ownersSettings = new HashMap<>();

	public ProtectionSettings(Protection protection) {
		this.protection = protection;
	}

	public void resetSettings() {
		this.nonMembersSettings.clear();
		this.membersSettings.clear();
		this.ownersSettings.clear();
	}

	public void setUnparsedValue(AbstractSetting<?> setting, SettingGroup group, String value)
			throws RoyaleProtectionBlocksExceptionImpl {
		Serializable object;

		try {
			object = setting.parseStringToValue(value);
		} catch (Throwable e) {
			throw Exceptions.Protections.Settings.INVALIDVALUE.generateException();
		}

		switch (group) {
		case OWNERS:
			ownersSettings.put(setting.getId(), object);
			break;
		case MEMBERS:
			membersSettings.put(setting.getId(), object);
			break;
		case NON_MEMBERS:
			nonMembersSettings.put(setting.getId(), object);
			break;
		}

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtectionSetting(protection.getProtectionId(), setting.getId(), group, object);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public <T extends Serializable> void setValue(AbstractSetting<T> setting, SettingGroup group, T value)
			throws RoyaleProtectionBlocksExceptionImpl {
		switch (group) {
		case OWNERS:
			ownersSettings.put(setting.getId(), value);
			break;
		case MEMBERS:
			membersSettings.put(setting.getId(), value);
			break;
		case NON_MEMBERS:
			nonMembersSettings.put(setting.getId(), value);
			break;
		}

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtectionSetting(protection.getProtectionId(), setting.getId(), group, value);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public <T extends Serializable> T getValue(AbstractSetting<T> setting, Player player)
			throws RoyaleProtectionBlocksExceptionImpl {
		if (protection.isOwner(player.getUniqueId()) || protection.isMainOwner(player.getUniqueId())) {
			return getValue(setting, SettingGroup.OWNERS);
		} else if (protection.isMember(player.getUniqueId())) {
			return getValue(setting, SettingGroup.MEMBERS);
		} else {
			return getValue(setting, SettingGroup.NON_MEMBERS);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getValue(AbstractSetting<T> setting, SettingGroup group)
			throws RoyaleProtectionBlocksExceptionImpl {
		try {
			switch (group) {
			case OWNERS:
				return (T) ownersSettings.getOrDefault(setting.getId(), setting.getOwnersValue());
			case MEMBERS:
				return (T) membersSettings.getOrDefault(setting.getId(), setting.getMembersValue());
			case NON_MEMBERS:
				return (T) nonMembersSettings.getOrDefault(setting.getId(), setting.getNonMembersValue());
			}
		} catch (ClassCastException e) {
			switch (group) {
			case OWNERS:
				ownersSettings.remove(setting.getId());
				return setting.getOwnersValue();
			case MEMBERS:
				membersSettings.remove(setting.getId());
				return setting.getMembersValue();
			case NON_MEMBERS:
				nonMembersSettings.remove(setting.getId());
				return setting.getNonMembersValue();
			}
		}
		return setting.getNonMembersValue();
	}

	public String getValueAsString(AbstractSetting<?> setting, Player player)
			throws RoyaleProtectionBlocksExceptionImpl {
		if (protection.isOwner(player.getUniqueId()) || protection.isMainOwner(player.getUniqueId())) {
			return getValueAsString(setting, SettingGroup.OWNERS);
		} else if (protection.isMember(player.getUniqueId())) {
			return getValueAsString(setting, SettingGroup.MEMBERS);
		} else {
			return getValueAsString(setting, SettingGroup.NON_MEMBERS);
		}
	}

	public String getValueAsString(AbstractSetting<?> setting, SettingGroup group)
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

}

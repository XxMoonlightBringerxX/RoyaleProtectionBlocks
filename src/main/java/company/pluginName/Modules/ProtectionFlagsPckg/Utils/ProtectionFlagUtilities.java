package company.pluginName.Modules.ProtectionFlagsPckg.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.Enums.EconomyService;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.ProtectionFlagsPckg.Objects.ProtectionFlag;
import company.pluginName.Utils.EconomyUtils;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment.ColorMode;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;

public class ProtectionFlagUtilities {

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_FLAGS_ALLOWVALUENAME = new PandaPrefixedStringField(
			"Message.Protections.Flags.Allow-value-name", "Inventory.Protection.Flags.Allow-value-name", "&aAllow");

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_FLAGS_DENYVALUENAME = new PandaPrefixedStringField(
			"Message.Protections.Flags.Deny-value-name", "Inventory.Protection.Flags.Deny-value-name", "&cDeny");

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_FLAGS_STRINGVALUENAME = new PandaPrefixedStringField(
			"Message.Protections.Flags.String-value-name", "Inventory.Protection.Flags.String-value-name", "&b{text}");

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_FLAGS_NOTDEFINEDNAME = new PandaPrefixedStringField(
			"Message.Protections.Flags.Not-defined-name", "Inventory.Protection.Flags.Not-defined-name",
			"&7Not defined");

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_FLAGS_NUMERICVALUENAME = new PandaPrefixedStringField(
			"Inventory.Protection.Flags.Numeric-value-name", "&b{number}");

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_FLAGS_UNKNOWNVALUENAME = new PandaPrefixedStringField(
			"Inventory.Protection.Flags.Unknown-value-name", "&7???");

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	public static void setValue(ProtectedRegion protectedRegion, Flag<?> flag, Object value)
			throws ClassCastException, IllegalStateException {
		setValue(protectedRegion, flag, value, null);
	}

	@SuppressWarnings("unchecked")
	public static void setValue(ProtectedRegion protectedRegion, Flag<?> flag, Object value, RegionGroup regionGroup)
			throws ClassCastException, IllegalStateException {
		if (value != null) {
			if (isDoubleFlag(flag)) {
				if (!(value instanceof Double)) {
					throw new IllegalArgumentException("Value is not compatible with the flag type");
				}
				protectedRegion.setFlag((DoubleFlag) flag, (Double) value);
			} else if (isIntegerFlag(flag)) {
				if (!(value instanceof Integer)) {
					throw new IllegalArgumentException("Value is not compatible with the flag type");
				}
				protectedRegion.setFlag((IntegerFlag) flag, (Integer) value);
			} else if (isStateFlag(flag)) {
				if (!(value instanceof State)) {
					throw new IllegalArgumentException("Value is not compatible with the flag type");
				}
				protectedRegion.setFlag((StateFlag) flag, (State) value);
			} else if (isStringFlag(flag)) {
				if (!(value instanceof String)) {
					throw new IllegalArgumentException("Value is not compatible with the flag type");
				}
				protectedRegion.setFlag((StringFlag) flag, (String) value);
			} else if (isStringSetFlag(flag)) {
				if (!(value instanceof Set)) {
					throw new IllegalArgumentException("Value is not compatible with the flag type");
				}
				protectedRegion.setFlag((SetFlag<String>) flag, (Set<String>) value);
			} else {
				throw new IllegalStateException("Value is not a supported type");
			}

			if (flag.getRegionGroupFlag() != null && regionGroup != null) {
				protectedRegion.setFlag(flag.getRegionGroupFlag(), regionGroup);
			}
		} else {
			protectedRegion.setFlag(flag, null);
			protectedRegion.setFlag(flag.getRegionGroupFlag(), null);
		}
	}

	public static Object getValue(ProtectedRegion protectedRegion, Flag<?> flag) throws ClassCastException {
		Object value = protectedRegion.getFlag(flag);
		return value != null ? value : flag.getDefault();
	}

	public static boolean isDoubleFlag(Flag<?> flag) {
		return flag instanceof DoubleFlag;
	}

	public static boolean isIntegerFlag(Flag<?> flag) {
		return flag instanceof IntegerFlag;
	}

	public static boolean isStateFlag(Flag<?> flag) {
		return flag instanceof StateFlag;
	}

	public static boolean isStringFlag(Flag<?> flag) {
		return flag instanceof StringFlag;
	}

	public static boolean isStringSetFlag(Flag<?> flag) {
		return flag instanceof SetFlag;
	}

	public static Object stringToValue(Flag<?> flag, String value)
			throws NumberFormatException, IllegalArgumentException, IllegalStateException {
		if (isDoubleFlag(flag)) {
			return Double.parseDouble(value);
		} else if (isIntegerFlag(flag)) {
			return Integer.parseInt(value);
		} else if (isStateFlag(flag)) {
			return State.valueOf(value.toUpperCase());
		} else if (isStringFlag(flag)) {
			return value;
		} else if (isStringSetFlag(flag)) {
			return Arrays.stream(value.split(";;")).collect(Collectors.toSet());
		} else {
			throw new IllegalStateException("Value is not a supported type");
		}
	}

	public static void configToFlags(File configFile, File flagsFile) {
		try {
			PandaYaml configYaml = new PandaYaml(configFile);
			PandaYaml flagsYaml = new PandaYaml(flagsFile);

			YamlSection flagsSection = configYaml.getRoot().getSection("Settings.Protection.Flags");
			YamlSection defaultFlagsSection = configYaml.getRoot().getSection("Settings.Protection.Default-flags");
			if (defaultFlagsSection != null || flagsSection != null) {
				flagsYaml.getRoot().getNamedChilds().forEach(child -> flagsYaml.getRoot().remove(child.getName()));

				if (flagsSection != null) {
					flagsSection.getNamedChilds().forEach(flagChild -> {
						if (flagChild.isYamlSection()) {
							flagsYaml.getRoot().set(String.format("Settings.Flags.%s.Editable", flagChild.getName()),
									true);

							ItemBuilder.inst().fromMap(flagChild.asYamlSection().toMap()).toMap()
									.forEach((key, val) -> flagsYaml.getRoot().set(String.format(
											"Settings.Flags.%s.Display-item.%s", flagChild.getName(), key), val));

							YamlData<?> groupData = flagChild.asYamlSection().getData("Group");
							if (groupData != null) {
								flagsYaml.getRoot().set(
										String.format("Settings.Flags.%s.Default-group", flagChild.getName()),
										groupData.getString());
							}
						}
					});
				}

				if (defaultFlagsSection != null) {
					defaultFlagsSection.getNamedChilds().forEach(flagChild -> {
						if (flagChild.isYamlSection()) {
							if (!flagsYaml.getRoot()
									.has(String.format("Settings.Flags.%s.Editable", flagChild.getName()))) {
								flagsYaml.getRoot()
										.set(String.format("Settings.Flags.%s.Editable", flagChild.getName()), false);
							}

							flagsYaml.getRoot().set(
									String.format("Settings.Flags.%s.Default-value", flagChild.getName()),
									flagChild.asYamlSection().getDataOrDefault("Value", YamlData.empty()).getString());

							YamlData<?> groupData = flagChild.asYamlSection().getData("Group");
							if (groupData != null) {
								flagsYaml.getRoot().set(
										String.format("Settings.Flags.%s.Default-group", flagChild.getName()),
										groupData.getString());
							}
						}
					});
				}

				if (flagsSection != null) {
					configYaml.getRoot().remove(flagsSection.toPath());
				}

				if (defaultFlagsSection != null) {
					configYaml.getRoot().remove(defaultFlagsSection.toPath());
				}

				configYaml.saveYAML(configFile);
				flagsYaml.saveYAML(flagsFile);
			}
		} catch (YamlException | IOException e) {
			e.printStackTrace();
		}
	}

	public static ProtectionFlag sectionToFlag(YamlSection section)
			throws NullPointerException, IllegalArgumentException {
		RegionGroup defaultGroup = getRegionGroup(section);
		Double costPerChange = getCostPerChange(section);
		EconomyService economyService = getEconomyService(section);

		ProtectionFlag protectionFlag = new ProtectionFlag(section.getName());
		YamlData<?> value = section.getDataOrDefault("Default-value", YamlData.empty());

		if (isDoubleFlag(protectionFlag.getWorldGuardFlag())) {
			protectionFlag.setDefaultValue(value.getDouble());
		} else if (isIntegerFlag(protectionFlag.getWorldGuardFlag())) {
			protectionFlag.setDefaultValue(value.getInteger());
		} else if (isStateFlag(protectionFlag.getWorldGuardFlag())) {
			try {
				protectionFlag.setDefaultValue(
						value.getString() != null ? State.valueOf(value.getString().toUpperCase()) : null);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(String.format("Value '%s' is not a valid group", value.getString()));
			}
		} else if (isStringFlag(protectionFlag.getWorldGuardFlag())) {
			protectionFlag.setDefaultValue(value.getString());
		} else if (isStringSetFlag(protectionFlag.getWorldGuardFlag())) {
			if (value.getStringList() != null) {
				protectionFlag.setDefaultValue(value.getStringList().stream().collect(Collectors.toSet()));
			} else if (value.getString() != null) {
				Set<String> set = new HashSet<>();
				set.add(value.getString());
				protectionFlag.setDefaultValue(set);
			}
		} else {
			throw new NullPointerException(
					String.format("Unable to find compatible flag type for '%s'", section.getName()));
		}

		protectionFlag.setEditable(section.getDataOrDefault("Editable", Boolean.FALSE).getBoolean());
		protectionFlag.setHidden(section.getDataOrDefault("Hidden", Boolean.FALSE).getBoolean());
		protectionFlag.setHideIfNoValue(section.getDataOrDefault("Hide-if-no-value", false).getBoolean());
		protectionFlag.setHideIfNoPermission(section.getDataOrDefault("Hide-if-no-permission", false).getBoolean());
		protectionFlag.setPermission(section.getDataOrDefault("Permission", YamlData.empty()).getString());

		if (section.has("Display-item")) {
			protectionFlag.setDisplayItem(
					ItemBuilder.inst().fromMap(section.toMap(), "Display-item").setColorMode(ColorMode.IGNORE).build());
		}

		if (costPerChange != null) {
			protectionFlag.setCostPerChange(costPerChange);
		}

		if (defaultGroup != null) {
			protectionFlag.setDefaultGroup(defaultGroup);
		}

		if (economyService != null) {
			protectionFlag.setEconomyService(economyService);
		}

		return protectionFlag;
	}

	public static String valueToString(Object value) {
		if (value == null) {
			return MESSAGE_PROTECTIONS_FLAGS_NOTDEFINEDNAME.toString();
		}

		if (value instanceof State) {
			return ((State) value == State.ALLOW ? MESSAGE_PROTECTIONS_FLAGS_ALLOWVALUENAME
					: MESSAGE_PROTECTIONS_FLAGS_DENYVALUENAME).toString();
		} else if (value instanceof String) {
			String text = (String) value;
			return MessageTemplate.inst(MESSAGE_PROTECTIONS_FLAGS_STRINGVALUENAME.toString())
					.setReplacements(new Replacement("{text}", () -> text != null ? text : "---")).process().toString();
		} else if (value instanceof Number) {
			String text = value.toString();
			return MessageTemplate.inst(MESSAGE_PROTECTIONS_FLAGS_NUMERICVALUENAME.toString())
					.setReplacements(new Replacement("{number}", () -> text != null ? text : "---")).process()
					.toString();
		}

		return MESSAGE_PROTECTIONS_FLAGS_UNKNOWNVALUENAME.toString();
	}

	private static RegionGroup getRegionGroup(YamlSection section) {
		String defaultGroupValue = section.getDataOrDefault("Default-group", YamlData.empty()).getString();
		RegionGroup defaultGroup = null;

		if (defaultGroupValue != null) {
			try {
				defaultGroup = RegionGroup.valueOf(defaultGroupValue.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(String.format("Value '%s' is not a valid group", defaultGroupValue));
			}
		}

		return defaultGroup;
	}

	private static Double getCostPerChange(YamlSection section) {
		Double costPerChange = section.getDataOrDefault("Cost-per-change", YamlData.empty()).getDouble();

		if (costPerChange != null && !EconomyUtils.isVaultHooked() && !EconomyUtils.isTokenManagerHooked()) {
			throw new NullPointerException("No compatible economy plugin is currently attached");
		}

		return costPerChange;
	}

	private static EconomyService getEconomyService(YamlSection section) {
		String economyServiceValue = section.getDataOrDefault("Economy-service", YamlData.empty()).getString();
		EconomyService economyService = null;

		if (economyServiceValue != null) {
			try {
				economyService = EconomyService.valueOf(economyServiceValue.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
						String.format("Value '%s' is not a valid economy service", economyServiceValue));
			}
		}

		if (economyService != null) {
			switch (economyService) {
			case VAULT:
				if (!EconomyUtils.isVaultHooked()) {
					throw new NullPointerException("Vault is currently not hooked");
				}
				break;
			case TOKENMANAGER:
				if (!EconomyUtils.isTokenManagerHooked()) {
					throw new NullPointerException("TokenManager is currently not hooked");
				}
				break;
			}
		}

		return economyService;
	}

}

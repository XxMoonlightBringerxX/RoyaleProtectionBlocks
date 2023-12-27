package company.pluginName.Bukkit.Inventories.Protections.Flags.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.MainPluginClass;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import darkpanda73.PandaUtils.PandaColors.Objects.TextReplacement;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackUtilities;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlObject;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class Flag<T> {

	public static List<Flag<?>> FLAGS;

	public static void initFlags() {
		HashMap<String, FlagItemData> itemDatas = new HashMap<>();

		try {
			PandaYaml yaml = new PandaYaml(MainPluginClass.getPlugin().getFileModule().CONFIG_FILE.getFile());

			YamlSection section = yaml.getRoot().getSection("Settings.Flags");
			if (section != null) {
				section.getChilds().stream().filter(YamlObject::isYamlSection).map(YamlObject::asYamlSection)
						.forEach(childSection -> {
							try {
								YamlData<?> group = childSection.getData("Group");
								itemDatas.put(childSection.getName(), new FlagItemData(
										ItemStackUtilities.mapToItem(childSection.toMap()),
										group != null ? RegionGroup.valueOf(group.getString().toUpperCase()) : null));
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Flag<?>> list = new ArrayList<>();
		try {
			List<com.sk89q.worldguard.protection.flags.Flag<?>> worldGuardFlags = MainPluginClass.getWorldGuardAPI()
					.getInternalWorldGuard().getAllFlags();

			list = itemDatas.keySet().stream()
					.map(flag -> worldGuardFlags.stream()
							.filter(worldGuardFlag -> worldGuardFlag.getName().equals(flag)).findFirst().orElse(null))
					.filter(Objects::nonNull).map(wFlag -> new Flag<>(wFlag, itemDatas.get(wFlag.getName())))
					.filter(flag -> flag != null).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		FLAGS = Collections.unmodifiableList(list);
	}

	private com.sk89q.worldguard.protection.flags.Flag<T> worldGuardFlag;
	private FlagItemData itemData;

	public void setFlagValue(ProtectedRegion protectedRegion, T value) {
		Flag.setFlagValue(worldGuardFlag, protectedRegion,
				(itemData.getRegionGroup() != null ? itemData.getRegionGroup()
						: worldGuardFlag.getRegionGroupFlag().getDefault()),
				value);
	}

	public T getFlagValue(ProtectedRegion protectedRegion) {
		return Flag.getFlagValue(worldGuardFlag, protectedRegion);
	}

	public String getFlagValueAsString(ProtectedRegion protectedRegion) {
		return Flag.getFlagValueAsString(worldGuardFlag, protectedRegion);
	}

	@Data
	@Setter(lombok.AccessLevel.NONE)
	@AllArgsConstructor
	public static class FlagItemData {

		private ItemStack item;
		private RegionGroup regionGroup;

	}

	public static <T> void setFlagValue(com.sk89q.worldguard.protection.flags.Flag<T> flag,
			ProtectedRegion protectedRegion, RegionGroup regionGroup, T value) {
		protectedRegion.setFlag(flag, value);
		protectedRegion.setFlag(flag.getRegionGroupFlag(), regionGroup);
	}

	public static <T> T getFlagValue(com.sk89q.worldguard.protection.flags.Flag<T> flag,
			ProtectedRegion protectedRegion) {
		T value = protectedRegion.getFlag(flag);
		return value != null ? value : flag.getDefault();
	}

	public static String getFlagValueAsString(com.sk89q.worldguard.protection.flags.Flag<?> flag,
			ProtectedRegion protectedRegion) {
		Object value = getFlagValue(flag, protectedRegion);
		if (value == null) {
			return MessageString.INVENTORY_PROTECTION_FLAGS_NOTDEFINEDNAME.toString();
		}

		if (flag instanceof StateFlag) {
			return ((State) value == State.ALLOW ? MessageString.INVENTORY_PROTECTION_FLAGS_ALLOWVALUENAME
					: MessageString.INVENTORY_PROTECTION_FLAGS_DENYVALUENAME).toString();
		} else if (flag instanceof StringFlag) {
			String text = (String) value;
			return MessageBuilder
					.createMessage(
							TextInput.inst().text(MessageString.INVENTORY_PROTECTION_FLAGS_STRINGVALUENAME.toString())
									.replacements(new TextReplacement("{text}", () -> text != null ? text : "---")))
					.toString();
		}

		return "&7???";
	}

}

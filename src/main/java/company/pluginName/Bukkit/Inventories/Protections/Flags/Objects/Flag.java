package company.pluginName.Bukkit.Inventories.Protections.Flags.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.MainPluginClass;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackUtilities;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlObject;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
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
				section.getChilds().stream().filter(YamlObject::isYamlSection).map(YamlObject::asYamlSection).forEach(childSection -> {
					try {
						YamlData<?> group = childSection.getData("Group");
						itemDatas.put(childSection.getName(), new FlagItemData(ItemStackUtilities.mapToItem(childSection.toMap()),
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
			List<com.sk89q.worldguard.protection.flags.Flag<?>> worldGuardFlags = MainPluginClass.getWorldGuardAPI().getInternalWorldGuard()
					.getAllFlags();

			list = itemDatas.keySet().stream().map(flag -> worldGuardFlags.stream()
					.filter(worldGuardFlag -> worldGuardFlag.getName().equals(flag)).findFirst().orElse(null)).filter(Objects::nonNull)
					.map(wFlag -> {
						FlagItemData data = itemDatas.get(wFlag.getName());
						if (wFlag instanceof StringFlag) {
							StringFlag stringFlag = (StringFlag) wFlag;
							return new Flag<String>(stringFlag.getName(), String.class, setFlagValue(stringFlag, data.getRegionGroup()),
									getFlagValue(stringFlag), stringFlag.getDefault(), stringFlag.getRegionGroupFlag().getDefault(), data);
						} else if (wFlag instanceof StateFlag) {
							StateFlag stateFlag = (StateFlag) wFlag;
							return new Flag<State>(stateFlag.getName(), State.class, setFlagValue(stateFlag, data.getRegionGroup()),
									getFlagValue(stateFlag), stateFlag.getDefault(), stateFlag.getRegionGroupFlag().getDefault(),
									itemDatas.get(stateFlag.getName()));
						}
						return null;
					}).filter(flag -> flag != null).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		FLAGS = Collections.unmodifiableList(list);
	}

	private static <T> BiConsumer<ProtectedRegion, T> setFlagValue(com.sk89q.worldguard.protection.flags.Flag<T> flag,
			RegionGroup regionGroup) {
		return (protectedRegion, value) -> {
			protectedRegion.setFlag(flag, value);
			protectedRegion.setFlag(flag.getRegionGroupFlag(), regionGroup);
		};
	}

	private static <T> Function<ProtectedRegion, T> getFlagValue(com.sk89q.worldguard.protection.flags.Flag<T> flag) {
		return (protectedRegion) -> {
			return protectedRegion.getFlag(flag);
		};
	}

	private String name;
	private Class<T> type;
	private @Getter(lombok.AccessLevel.NONE) @Setter(lombok.AccessLevel.NONE) BiConsumer<ProtectedRegion, T> setter;
	private @Getter(lombok.AccessLevel.NONE) @Setter(lombok.AccessLevel.NONE) Function<ProtectedRegion, T> getter;
	private T defaultValue;
	private RegionGroup defaultRegion;
	private FlagItemData itemData;

	public void setFlagValue(ProtectedRegion protectedRegion, T value) {
		this.setter.accept(protectedRegion, value);
	}

	public T getFlagValue(ProtectedRegion protectedRegion) {
		T value = this.getter.apply(protectedRegion);
		return value != null ? value : getDefaultValue();
	}

	@Data
	@Setter(lombok.AccessLevel.NONE)
	@AllArgsConstructor
	public static class FlagItemData {

		private ItemStack item;
		private RegionGroup regionGroup;

	}

}

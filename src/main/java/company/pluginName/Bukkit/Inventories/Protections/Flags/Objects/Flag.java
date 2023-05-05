package company.pluginName.Bukkit.Inventories.Protections.Flags.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
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

		SettingList.SETTINGS_EDITABLEFLAGS.getContent().forEach(config -> {
			String[] split = config.split("\\|");
			if (split.length == 3) {
				itemDatas.put(split[0], new FlagItemData(split[1], split[2]));
			}
		});

		List<Flag<?>> list = new ArrayList<>();
		try {
			list = MainPluginClass.getWorldGuardAPI().getInternalWorldGuard().getAllFlags().stream()
					.filter(flag -> itemDatas.containsKey(flag.getName())).map(wFlag -> {
						if (wFlag instanceof StringFlag) {
							StringFlag stringFlag = (StringFlag) wFlag;
							return new Flag<String>(stringFlag.getName(), String.class, setFlagValue(stringFlag),
									getFlagValue(stringFlag), stringFlag.getDefault(),
									itemDatas.get(stringFlag.getName()));
						} else if (wFlag instanceof StateFlag) {
							StateFlag stateFlag = (StateFlag) wFlag;
							return new Flag<State>(stateFlag.getName(), State.class, setFlagValue(stateFlag),
									getFlagValue(stateFlag), stateFlag.getDefault(),
									itemDatas.get(stateFlag.getName()));
						}
						return null;
					}).filter(flag -> flag != null).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		FLAGS = Collections.unmodifiableList(list);
	}

	private static <T> BiConsumer<ProtectedRegion, T> setFlagValue(com.sk89q.worldguard.protection.flags.Flag<T> flag) {
		return (protectedRegion, value) -> {
			protectedRegion.setFlag(flag, value);
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

		private String name;
		private String skin;

	}

}

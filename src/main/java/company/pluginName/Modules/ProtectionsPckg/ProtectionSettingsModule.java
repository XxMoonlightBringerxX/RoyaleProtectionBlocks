package company.pluginName.Modules.ProtectionsPckg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;

import com.sk89q.worldguard.protection.flags.CommandStringFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.StringFlag;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import lombok.Getter;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotPlugin.LoadOn;
import relampagorojo93.LibsCollection.SpigotPlugin.PluginModule;
import relampagorojo93.LibsCollection.YAMLLib.YAMLFile;
import relampagorojo93.LibsCollection.YAMLLib.Objects.Section;

public class ProtectionSettingsModule implements PluginModule {

	private @Getter HashMap<Flag<?>, Object> defaultFlags = new HashMap<>();

	@Override
	public boolean load() {
		loadDefaultFlags();
		return true;
	}

	@Override
	public boolean unload() {
		this.defaultFlags.clear();
		return true;
	}

	@Override
	public LoadOn loadOn() {
		return LoadOn.BEFORE_ENABLE;
	}

	@Override
	public boolean optional() {
		return false;
	}

	@Override
	public boolean allowReload() {
		return true;
	}

	private void loadDefaultFlags() {
		try {
			List<Flag<?>> flags = MainPluginClass.getWorldGuardAPI().getInternalWorldGuard().getAllFlags();

			YAMLFile config = new YAMLFile(MainPluginClass.getPlugin().getFileModule().CONFIG_FILE.getFile());

			config.getSection("Settings.Protection.Default-flags", new Section()).getChilds().forEach(child -> {
				if (child.isSection()) {
					Section section = child.asSection();

					Optional<Flag<?>> foundFlagOpt = flags.stream()
							.filter(flag -> flag.getName().equals(section.getName())).findFirst();

					if (foundFlagOpt.isPresent()) {
						Flag<?> foundFlag = foundFlagOpt.get();

						if (foundFlag instanceof StringFlag || foundFlag instanceof CommandStringFlag) {
							MessageBuilder.createMessage(MessageString.applyPrefix(String.format(
									"Registering default flag '%s&7' with value '%s&7' as StringFlag|CommandStringFlag",
									foundFlag.getName(), child.getString()))).sendMessage(Bukkit.getConsoleSender());
							defaultFlags.put(foundFlag, child.getString());
						} else if (foundFlag instanceof StateFlag) {
							try {
								MessageBuilder
										.createMessage(MessageString.applyPrefix(String.format(
												"Registering default flag '%s&7' with value '%s&7' as StateFlag",
												foundFlag.getName(), child.getString())))
										.sendMessage(Bukkit.getConsoleSender());
								defaultFlags.put(foundFlag, State.valueOf(child.getString().toUpperCase()));
							} catch (IllegalArgumentException e) {
								MessageBuilder.createMessage(MessageString.applyPrefix(String.format(
										"Issue trying to load default flag '%s&7'. The specified value is not a valid State (ALLOW, DENY)",
										section.getName())));
							}
						} else if (foundFlag instanceof SetFlag) {
							MessageBuilder
									.createMessage(MessageString.applyPrefix(String.format(
											"Registering default flag '%s&7' with value '%s&7' as SetFlag",
											foundFlag.getName(), child.getString())))
									.sendMessage(Bukkit.getConsoleSender());

							Set<String> set = new HashSet<>();
							if (child.getStringList() == null) {
								set.add(child.getString());
							} else {
								child.getStringList().forEach(set::add);
							}

							defaultFlags.put(foundFlag, set);
						} else {
							MessageBuilder
									.createMessage(MessageString.applyPrefix(String
											.format("Not able to register default flag '%s&7'", foundFlag.getName())))
									.sendMessage(Bukkit.getConsoleSender());
						}
					}
				}
			});
			config.reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

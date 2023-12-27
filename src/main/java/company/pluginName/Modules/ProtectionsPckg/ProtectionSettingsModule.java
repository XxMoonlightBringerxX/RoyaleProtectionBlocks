package company.pluginName.Modules.ProtectionsPckg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldguard.protection.flags.CommandStringFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.StringFlag;

import company.pluginName.MainPluginClass;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import relampagorojo93.LibsCollection.SpigotPlugin.LoadOn;
import relampagorojo93.LibsCollection.SpigotPlugin.PluginModule;

public class ProtectionSettingsModule implements PluginModule {

	private static final String SPAWN_OLD_FIELD = "Settings.Protection.Send-to-world-on-kick";

	@Getter
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class DefaultFlag {

		private @NonNull Flag<?> flag;
		private @NonNull Object value;
		private RegionGroup regionGroup = RegionGroup.ALL;

	}

	private @Getter List<DefaultFlag> defaultFlags = new ArrayList<>();
	private @Getter Location spawn = null;

	@Override
	public boolean load() {
		loadDefaultFlags();
		loadSpawn();
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

			PandaYaml config = new PandaYaml(MainPluginClass.getPlugin().getFileModule().CONFIG_FILE.getFile());

			AtomicBoolean modified = new AtomicBoolean(false);

			config.getRoot().getSectionOrDefault("Settings.Protection.Default-flags").getNamedChilds()
					.forEach(child -> {
						Optional<Flag<?>> foundFlagOpt = flags.stream()
								.filter(flag -> flag.getName().equals(child.getName())).findFirst();

						if (child.isYamlData()) {
							String path = child.toPath();
							Object value = child.asYamlData().getData();

							config.getRoot().set(String.format("%s.%s", path, "Value"), value);
							config.getRoot().set(String.format("%s.%s", path, "Group"),
									foundFlagOpt.isPresent()
											? foundFlagOpt.get().getRegionGroupFlag().getDefault().name()
											: RegionGroup.ALL.name());

							modified.set(true);
						}
					});

			if (modified.get()) {
				config.saveYAML(MainPluginClass.getPlugin().getFileModule().CONFIG_FILE.getFile());
			}

			config.getRoot().getSectionOrDefault("Settings.Protection.Default-flags").getNamedChilds()
					.forEach(child -> {
						if (child.isYamlSection()) {
							YamlSection section = child.asYamlSection();

							YamlData<?> value = section.getData("Value");
							YamlData<?> group = section.getData("Group");

							if (value == null) {
								return;
							}

							Optional<Flag<?>> foundFlagOpt = flags.stream()
									.filter(flag -> flag.getName().equals(section.getName())).findFirst();

							if (foundFlagOpt.isPresent()) {
								Flag<?> foundFlag = foundFlagOpt.get();
								RegionGroup regionGroup;

								try {
									String regionGroupString = group != null ? group.getString() : null;
									regionGroup = group != null
											? (regionGroupString != null && !regionGroupString.isEmpty()
													? RegionGroup.valueOf(regionGroupString)
													: null)
											: foundFlag.getRegionGroupFlag().getDefault();
								} catch (Exception e) {
									regionGroup = foundFlag.getRegionGroupFlag().getDefault();
								}

								if (foundFlag instanceof StringFlag || foundFlag instanceof CommandStringFlag) {
									MessageBuilder.createMessage(MessageString.applyPrefix(String.format(
											"Registering default flag '%s&7' with value '%s&7' as StringFlag|CommandStringFlag",
											foundFlag.getName(), value.getString())))
											.sendMessage(Bukkit.getConsoleSender());
									defaultFlags.add(new DefaultFlag(foundFlag, value.getString(), regionGroup));
								} else if (foundFlag instanceof StateFlag) {
									try {
										MessageBuilder.createMessage(MessageString.applyPrefix(String.format(
												"Registering default flag '%s&7' with value '%s&7' as StateFlag",
												foundFlag.getName(), value.getString())))
												.sendMessage(Bukkit.getConsoleSender());
										defaultFlags.add(new DefaultFlag(foundFlag,
												State.valueOf(value.getString().toUpperCase()), regionGroup));
									} catch (IllegalArgumentException e) {
										MessageBuilder.createMessage(MessageString.applyPrefix(String.format(
												"Issue trying to load default flag '%s&7'. The specified value is not a valid State (ALLOW, DENY)",
												section.getName())));
									}
								} else if (foundFlag instanceof SetFlag) {
									MessageBuilder
											.createMessage(MessageString.applyPrefix(String.format(
													"Registering default flag '%s&7' with value '%s&7' as SetFlag",
													foundFlag.getName(), value.getString())))
											.sendMessage(Bukkit.getConsoleSender());

									Set<String> set = new HashSet<>();
									if (value.getStringList() == null) {
										set.add(value.getString());
									} else {
										value.getStringList().forEach(set::add);
									}

									defaultFlags.add(new DefaultFlag(foundFlag, set, regionGroup));
								} else {
									MessageBuilder
											.createMessage(MessageString.applyPrefix(String.format(
													"Not able to register default flag '%s&7'", foundFlag.getName())))
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

	private void loadSpawn() {
		try {
			this.spawn = null;

			PandaYaml yaml = new PandaYaml(MainPluginClass.getPlugin().getFileModule().CONFIG_FILE.getFile());
			YamlSection section = yaml.getRoot().getSection("Settings.Spawn");
			if (section != null) {
				YamlData<?> worldName = section.getDataOrDefault("World", null);
				if (worldName != null) {
					World world = Bukkit.getWorld(worldName.getString());
					if (world != null) {
						double x = section.getDataOrDefault("X", 0d).getDouble();
						double y = section.getDataOrDefault("Y", 0d).getDouble();
						double z = section.getDataOrDefault("Z", 0d).getDouble();
						float yaw = section.getDataOrDefault("Yaw", 0f).getFloat();
						float pitch = section.getDataOrDefault("Pitch", 0f).getFloat();

						this.spawn = new Location(world, x, y, z, yaw, pitch);
					}
				}
			}

			if (this.spawn == null) {
				World world = null;

				YamlData<?> worldNameOldSetting = yaml.getRoot().getData(SPAWN_OLD_FIELD);
				if (worldNameOldSetting != null && worldNameOldSetting.getString("").isEmpty()) {
					world = Bukkit.getWorld(worldNameOldSetting.getString(""));
				}

				if (world == null) {
					MessageBuilder.createMessage(MessageString.applyPrefix(
							"No spawn setting could be found available in the current configuration. Using the first world spawn as the plugin spawn. Use '/pb setspawn' in-game to set a new spawn for the plugin which will be used as location on kicks or bans in protections."))
							.sendMessage(Bukkit.getConsoleSender());
					world = Bukkit.getWorlds().get(0);
				}

				this.spawn = world.getSpawnLocation();
			}
		} catch (YamlException | IOException e) {
			MessageBuilder.createMessage(MessageString.applyPrefix("Not able to to load plugin spawn"))
					.sendMessage(Bukkit.getConsoleSender());
			e.printStackTrace();
		}
	}

	public void setSpawn(@NonNull Location loc) throws FileNotFoundException, YamlException, IOException {
		PandaYaml yaml = new PandaYaml(MainPluginClass.getPlugin().getFileModule().CONFIG_FILE.getFile());
		yaml.getRoot().set("Settings.Spawn.World", loc.getWorld().getName());
		yaml.getRoot().set("Settings.Spawn.X", loc.getX());
		yaml.getRoot().set("Settings.Spawn.Y", loc.getY());
		yaml.getRoot().set("Settings.Spawn.Z", loc.getZ());
		yaml.getRoot().set("Settings.Spawn.Yaw", loc.getYaw());
		yaml.getRoot().set("Settings.Spawn.Pitch", loc.getPitch());
		yaml.saveYAML(MainPluginClass.getPlugin().getFileModule().CONFIG_FILE.getFile());

		this.spawn = loc;
	}

}

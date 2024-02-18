package company.pluginName.Modules.ProtectionSettingsPckg;

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

import company.pluginName.APIs.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.FilePckg.FilesService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@PandaService
public class ProtectionSettingsService {

	@PandaInject
	private FilesService filesService;

	@PandaInject
	private WorldGuardAPI worldGuardApi;

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

	@LoadMethod
	private void load() {
		loadDefaultFlags();
		loadSpawn();
	}

	@UnloadMethod
	private void unload() {
		this.defaultFlags.clear();
	}

	@ReloadMethod
	private void reload() {
		unload();
		load();
	}

	private void loadDefaultFlags() {
		try {
			List<Flag<?>> flags = worldGuardApi.getHook().getInternalWorldGuard().getAllFlags();

			PandaYaml config = new PandaYaml(filesService.getConfigFile().getFile());

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
				config.saveYAML(filesService.getConfigFile().getFile());
			}

			config.getRoot().getSectionOrDefault("Settings.Protection.Default-flags").getNamedChilds()
					.forEach(child -> {
						if (child.isYamlSection()) {
							YamlSection section = child.asYamlSection();

							YamlData<?> value = section.getData("Value");
							YamlData<?> group = section.getData("Group");

							if (value == null) {
								MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(String.format(
										"&cUnable to register default flat '%s': Value is missing.", child.getName())))
										.process().sendMessage(Bukkit.getConsoleSender());
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
									MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(String.format(
											"Registering default flag '%s&7' with value '%s&7' as StringFlag|CommandStringFlag",
											foundFlag.getName(), value.getString()))).process()
											.sendMessage(Bukkit.getConsoleSender());
									defaultFlags.add(new DefaultFlag(foundFlag, value.getString(), regionGroup));
								} else if (foundFlag instanceof StateFlag) {
									try {
										MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(String.format(
												"Registering default flag '%s&7' with value '%s&7' as StateFlag",
												foundFlag.getName(), value.getString()))).process()
												.sendMessage(Bukkit.getConsoleSender());
										defaultFlags.add(new DefaultFlag(foundFlag,
												State.valueOf(value.getString().toUpperCase()), regionGroup));
									} catch (IllegalArgumentException e) {
										MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(String.format(
												"&cUnable to register default flat '%s': The specified value is not a valid State (ALLOW, DENY)",
												section.getName()))).process().sendMessage(Bukkit.getConsoleSender());
									}
								} else if (foundFlag instanceof SetFlag) {
									MessageTemplate
											.inst(PandaPrefixedStringField.applyPrefix(String.format(
													"Registering default flag '%s&7' with value '%s&7' as SetFlag",
													foundFlag.getName(), value.getString())))
											.process().sendMessage(Bukkit.getConsoleSender());

									Set<String> set = new HashSet<>();
									if (value.getStringList() == null) {
										set.add(value.getString());
									} else {
										value.getStringList().forEach(set::add);
									}

									defaultFlags.add(new DefaultFlag(foundFlag, set, regionGroup));
								} else {
									MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(String.format(
											"&cUnable to register default flat '%s': Flag is currently not supported.",
											child.getName()))).process().sendMessage(Bukkit.getConsoleSender());
								}
							} else {
								MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(String.format(
										"&cUnable to register default flat '%s': Flag could not be found in WorldGuard.",
										child.getName()))).process().sendMessage(Bukkit.getConsoleSender());
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

			PandaYaml yaml = new PandaYaml(filesService.getConfigFile().getFile());
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
				MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(
						"No spawn setting could be found available in the current configuration. Using the first world spawn as the plugin spawn. Use '/pb setspawn' in-game to set a new spawn for the plugin which will be used as location on kicks or bans in protections."))
						.process().sendMessage(Bukkit.getConsoleSender());

				this.spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
			}
		} catch (YamlException | IOException e) {
			MessageTemplate.inst(PandaPrefixedStringField.applyPrefix("Not able to to load plugin spawn")).process()
					.sendMessage(Bukkit.getConsoleSender());
			e.printStackTrace();
		}
	}

	public void setSpawn(@NonNull Location loc) throws FileNotFoundException, YamlException, IOException {
		PandaYaml yaml = new PandaYaml(filesService.getConfigFile().getFile());
		yaml.getRoot().set("Settings.Spawn.World", loc.getWorld().getName());
		yaml.getRoot().set("Settings.Spawn.X", loc.getX());
		yaml.getRoot().set("Settings.Spawn.Y", loc.getY());
		yaml.getRoot().set("Settings.Spawn.Z", loc.getZ());
		yaml.getRoot().set("Settings.Spawn.Yaw", loc.getYaw());
		yaml.getRoot().set("Settings.Spawn.Pitch", loc.getPitch());
		yaml.saveYAML(filesService.getConfigFile().getFile());

		this.spawn = loc;
	}

}

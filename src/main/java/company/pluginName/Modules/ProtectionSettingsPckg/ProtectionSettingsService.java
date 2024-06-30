package company.pluginName.Modules.ProtectionSettingsPckg;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
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
import lombok.Getter;
import lombok.NonNull;

@PandaService
public class ProtectionSettingsService {

	@PandaInject
	private FilesService filesService;

	@PandaInject
	private WorldGuardAPI worldGuardApi;

	private @Getter Location spawn = null;

	@LoadMethod
	private void load() {
		loadSpawn();
	}

	@UnloadMethod
	private void unload() {
	}

	@ReloadMethod
	private void reload() {
		unload();
		load();
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

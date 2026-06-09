package company.pluginName.Modules.SettingsPckg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import company.pluginName.MainPluginClass;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin.SetSpawnSubCommand;
import company.pluginName.Modules.FilePckg.FilesService;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Utils.EconomyUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.PandaCommandsService;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.Enums.EconomyService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import lombok.Getter;
import lombok.NonNull;

@PandaService
public class SettingsService {

	@PandaInject
	private FilesService filesService;

	@PandaInject
	private WorldGuardAPI worldGuardApi;

	@PandaInject
	private PandaCommandsService commandsService;

	private @Getter Location spawn = null;
	private @Getter EconomyService protectionTeleportEconomyService;
	private @Getter SimpleDateFormat dateFormat;

	@LoadMethod
	private void load() {
		loadSpawn();
		loadProtectionTeleportEconomy();
		loadDateFormat();
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
						"No spawn setting could be found available in the current configuration. Using the first world spawn as the plugin spawn. Use '{command}' in-game to set a new spawn for the plugin which will be used as location on kicks or bans in protections."))
						.setReplacements(new Replacement("{command}",
								() -> commandsService.getSubCommandByClass(SetSpawnSubCommand.class).getCommandUsage()))
						.process().sendMessage(Bukkit.getConsoleSender());

				this.spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
			}
		} catch (YamlException | IOException e) {
			MessageTemplate.inst(PandaPrefixedStringField.applyPrefix("Not able to to load plugin spawn")).process()
					.sendMessage(Bukkit.getConsoleSender());
			e.printStackTrace();
		}
	}

	private void loadProtectionTeleportEconomy() {
		try {
			protectionTeleportEconomyService = Settings.SETTINGS_PROTECTION_TELEPORTECONOMY.hasContent()
					? EconomyService.valueOf(Settings.SETTINGS_PROTECTION_TELEPORTECONOMY.getContent().toUpperCase())
					: null;

			if (protectionTeleportEconomyService != null
					&& !EconomyUtilities.isEconomyEnabled(protectionTeleportEconomyService)) {
				MainPluginClass.getSimpleLogger().sendWarning(String.format(
						"The defined economy for the protections teleport could not be found installed on the server (%s). In order to add cost to teleports, you must install the economy plugin or switch the defined economy plugin for one of your current economy plugins.",
						protectionTeleportEconomyService.name()));
				protectionTeleportEconomyService = null;
			}
		} catch (IllegalArgumentException e) {
			MainPluginClass.getSimpleLogger()
					.sendError(String.format("Invalid protections teleport economy service (%s)",
							Settings.SETTINGS_PROTECTION_TELEPORTECONOMY.getContent().toUpperCase()));
		}
	}

	private void loadDateFormat() {
		try {
			this.dateFormat = new SimpleDateFormat(Settings.SETTINGS_GENERAL_DATEFORMAT.getContent());
		} catch (Exception e) {
			MainPluginClass.getSimpleLogger().sendError(String.format("Invalid date format specified (%s)",
					Settings.SETTINGS_GENERAL_DATEFORMAT.getContent()));
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

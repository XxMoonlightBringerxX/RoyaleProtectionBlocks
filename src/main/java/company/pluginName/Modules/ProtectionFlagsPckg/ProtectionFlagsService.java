package company.pluginName.Modules.ProtectionFlagsPckg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.FilePckg.FilesService;
import company.pluginName.Modules.ProtectionFlagsPckg.Objects.ProtectionFlag;
import company.pluginName.Modules.ProtectionFlagsPckg.Utils.ProtectionFlagUtilities;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;

@PandaService
public class ProtectionFlagsService {

	@PandaInject
	private MainPluginClass plugin;

	@PandaInject
	private FilesService filesService;

	private HashMap<String, ProtectionFlag> flags = new HashMap<>();

	@LoadMethod
	private void load() {
		try {
			File configFile = filesService.getConfigFile().getFile();
			File flagsFile = filesService.getFlagsFile().getFile();

			ProtectionFlagUtilities.configToFlags(configFile, flagsFile);

			PandaYaml yaml = new PandaYaml(flagsFile);

			YamlSection section = yaml.getRoot().getSection("Settings.Flags");

			if (section != null) {
				section.getNamedChilds().forEach(namedObject -> {
					if (namedObject.isYamlSection()) {
						try {
							ProtectionFlag protectionFlag = ProtectionFlagUtilities
									.sectionToFlag(namedObject.asYamlSection());

							flags.put(namedObject.getName().toLowerCase(), protectionFlag);

							plugin.sendDebug(getClass(), String.format(
									"&aLoaded flag with ID '%s' successfully: &8[&eEditable: &7%s&e, Value: &7%s&e, Group: &7%s&8]",
									namedObject.getName().toLowerCase(),
									(protectionFlag.isEditable() ? "True" : "False"), protectionFlag.getDefaultValue(),
									(protectionFlag.getDefaultGroup() != null ? protectionFlag.getDefaultGroup().name()
											: "null")));
						} catch (Exception e) {
							plugin.sendError(getClass(), String.format("Unable to load flag configuration '%s':  %s",
									namedObject.getName(), e.getMessage()));
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@UnloadMethod
	private void unload() {
		this.flags.clear();
	}

	@ReloadMethod
	private void reload() {
		this.unload();
		this.load();
	}

	public List<ProtectionFlag> getFlags() {
		return new ArrayList<>(this.flags.values());
	}

	public ProtectionFlag getFlag(String id) {
		return flags.get(id.toLowerCase());
	}

}

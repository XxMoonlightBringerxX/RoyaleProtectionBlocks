package company.pluginName.Modules.FilePckg.Files;

import java.io.IOException;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Defaults.PandaConfigFile;

public class ConfigFile extends PandaConfigFile {

	public ConfigFile(PandaFolder parentFolder, PandaPluginClass plugin) throws IOException, YamlException {
		super(parentFolder, plugin);
	}

	@Override
	protected PandaYaml updateFileVersion(PandaYaml yaml) {
		PandaYaml curYaml = super.updateFileVersion(yaml);

		if (curYaml != this.getDefaultYaml()) {
			YamlData<?> purgeExecuteEverySeconds = yaml.getRoot()
					.getData("Settings.Protection.Auto-purge.Execute-every");
			if (purgeExecuteEverySeconds != null) {
				if (purgeExecuteEverySeconds.getString("").isEmpty()) {
					yaml.getRoot().set("Settings.Protection.Auto-purge.Purge-older-than", "");
				}
				yaml.getRoot().remove(purgeExecuteEverySeconds.toPath());
			}
		}

		return curYaml;
	}

}

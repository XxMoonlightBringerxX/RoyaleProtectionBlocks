package company.pluginName.Modules.FilePckg.Files;

import java.io.IOException;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;

public class FlagsFile extends PandaYamlFile {

	public FlagsFile(PandaFolder parentFolder, PandaPluginClass plugin) throws IOException, YamlException {
		super("flags", parentFolder.getFile().getPath() + "/Flags.yml", new PandaYaml(plugin.getResource(
				plugin.getClass().getPackage().getName().replaceAll("\\.", "/") + "/Resources/Flags.yml")));
	}

	@Override
	protected PandaYaml updateFileVersion(PandaYaml yaml) {
		if (this.getDefaultYaml() == null) {
			return yaml;
		}

		try {
			int currentVersion = yaml.getRoot().getDataOrDefault("Version", 0).getInteger();
			int defaultVersion = this.getDefaultYaml().getRoot().getDataOrDefault("Version", 0).getInteger();

			if (currentVersion < defaultVersion) {
				PandaYaml defaultYaml = this.getDefaultYaml().cloneYaml();

				defaultYaml.getRoot().remove("Settings.Flags");

				for (YamlData<?> currentSection : yaml.getRoot().getAllNamedChilds()) {
					if (!currentSection.toPath().equals("Version")) {
						defaultYaml.getRoot().set(currentSection.toPath(), currentSection);
					}
				}

				return defaultYaml;
			}
		} catch (Exception e) {
			System.out.println(
					String.format("Unable to update file '%s' with new default file content:", getFile().getPath()));
			e.printStackTrace();
		}
		return yaml;
	}

}

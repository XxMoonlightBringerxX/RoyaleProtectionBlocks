package company.pluginName.Modules.FilePckg;

import java.io.IOException;

import company.pluginName.MainPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Defaults.PandaConfigFile;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Defaults.PandaLangFile;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Defaults.PandaPluginFolder;
import lombok.Getter;

public class FilesService extends PandaFilesService {

	@PandaInject
	private MainPluginClass plugin;

	private @Getter PandaFolder pluginFolder;
	private @Getter PandaYamlFile langFile;
	private @Getter PandaYamlFile configFile;
	private @Getter PandaYamlFile flagsFile;

	@PostInjectMethod
	private void postInject() throws ReportError {
		addFolder(pluginFolder = new PandaPluginFolder(plugin));

		try {
			addFile(configFile = new PandaConfigFile(pluginFolder, plugin));
			addFile(langFile = new PandaLangFile(pluginFolder, plugin));
			addFile(flagsFile = new PandaYamlFile("lang", pluginFolder.getFile().getPath() + "/Flags.yml",
					new PandaYaml(plugin.getResource(plugin.getClass().getPackage().getName().replaceAll("\\.", "/")
							+ "/Resources/Flags.yml"))));
		} catch (IOException | YamlException e) {
			throw new ReportResult.ReportError("Unable to initialize files.", true, e);
		}
	}

}

package company.pluginName.Modules.FilePckg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.FilePckg.Files.ConfigFile;
import company.pluginName.Modules.FilePckg.Files.FlagsFile;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;
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
	private @Getter PandaYamlFile blocksFile;

	@PostInjectMethod
	private void postInject() throws ReportError {
		addFolder(pluginFolder = new PandaPluginFolder(plugin));

		try {
			addFile(configFile = new ConfigFile(pluginFolder, plugin));
			addFile(langFile = new PandaLangFile(pluginFolder, plugin));
			addFile(flagsFile = new FlagsFile(pluginFolder, plugin));
			addFile(blocksFile = new PandaYamlFile("blocks", pluginFolder.getFile().getPath() + "/blocks.yml",
					new PandaYaml(plugin.getResource(plugin.getClass().getPackage().getName().replaceAll("\\.", "/")
							+ "/Resources/Blocks.yml"))));
		} catch (IOException | YamlException e) {
			throw new ReportResult.ReportError("Unable to initialize files.", true, e);
		}
	}

	@LoadMethod
	protected void load(ReportResult reportResult) throws Throwable {
		if (this.getFile("commands").getFile().exists()) {
			File file = this.getFile("commands").getFile();
			PandaYaml yaml = new PandaYaml(file);

			if (!yaml.getRoot().has("Commands.Protection-blocks.Subcommands.Store")
					&& yaml.getRoot().has("Commands.Protection-blocks.Subcommands.Buy")) {
				yaml.getRoot().getSection("Commands.Protection-blocks.Subcommands.Buy").toMap()
						.forEach((key, value) -> {
							yaml.getRoot().set("Commands.Protection-blocks.Subcommands.Store." + key, value);
						});
				yaml.getRoot().set("Commands.Protection-blocks.Subcommands.Store.Name", "store");
				yaml.getRoot().set("Commands.Protection-blocks.Subcommands.Store.Aliases", new ArrayList<>());
				yaml.getRoot().remove("Commands.Protection-blocks.Subcommands.Buy");
				yaml.saveYAML(file);
			}
		}

		super.load(reportResult);
	}

}

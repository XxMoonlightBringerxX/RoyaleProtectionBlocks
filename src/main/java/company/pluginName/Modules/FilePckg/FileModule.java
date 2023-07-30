package company.pluginName.Modules.FilePckg;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.FilePckg.Messages.MessageList;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.FilePckg.Settings.SettingBoolean;
import company.pluginName.Modules.FilePckg.Settings.SettingInt;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import relampagorojo93.LibsCollection.SpigotPlugin.Defaults.FileModuleObjects.FileObj;
import relampagorojo93.LibsCollection.SpigotPlugin.Defaults.FileModuleObjects.FolderObj;
import relampagorojo93.LibsCollection.YAMLLib.YAMLFile;

public class FileModule extends relampagorojo93.LibsCollection.SpigotPlugin.Defaults.FileModule {

	public FolderObj PLUGIN_FOLDER;
	public FileObj LANG_FILE;
	public FileObj CONFIG_FILE;

	@Override
	public boolean load() {
		if (this.folders.length == 0 && this.files.length == 0) {
			try {
				this.setFolders(PLUGIN_FOLDER = new FolderObj("plugins/" + MainPluginClass.getPlugin().getName()));
				this.setFiles(
						CONFIG_FILE = new FileObj(PLUGIN_FOLDER.getFolder().getPath() + "/Config.yml",
								new YAMLFile(MainPluginClass.getPlugin()
										.getResource(MainPluginClass.getPlugin().getClass().getPackage().getName()
												.replaceAll("\\.", "/") + "/Resources/Config.yml")),
								SettingString.values(), SettingBoolean.values(), SettingInt.values(),
								SettingList.values()),
						LANG_FILE = new FileObj(PLUGIN_FOLDER.getFolder().getPath() + "/Lang.yml",
								new YAMLFile(MainPluginClass.getPlugin()
										.getResource(MainPluginClass.getPlugin().getClass().getPackage().getName()
												.replaceAll("\\.", "/") + "/Resources/Lang.yml")),
								MessageString.values(), MessageList.values()));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		return super.load();
	}

}

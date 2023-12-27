package company.pluginName.TemporaryModules.FilePckg;

import java.util.Arrays;

import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.TemporaryModules.FilePckg.FileModuleObjects.FileObj;
import company.pluginName.TemporaryModules.FilePckg.FileModuleObjects.FolderObj;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageList;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingBoolean;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingInt;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackUtilities;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.SkinUtilities;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import relampagorojo93.LibsCollection.SpigotPlugin.LoadOn;
import relampagorojo93.LibsCollection.SpigotPlugin.PluginModule;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

public class FileModule implements PluginModule {

	public FolderObj PLUGIN_FOLDER;
	public FileObj LANG_FILE;
	public FileObj CONFIG_FILE;
	public FileObj BLOCKS_FILE;

	public FolderObj[] folders = new FolderObj[0];
	public FileObj[] files = new FileObj[0];

	public void setFolders(FolderObj... folders) {
		this.folders = folders;
	}

	public void setFiles(FileObj... files) {
		this.files = files;
	}

	@Override
	public boolean load() {
		if (this.folders.length == 0 && this.files.length == 0) {
			try {
				this.setFolders(PLUGIN_FOLDER = new FolderObj("plugins/" + MainPluginClass.getPlugin().getName()));
				this.setFiles(CONFIG_FILE = new FileObj(PLUGIN_FOLDER.getFolder().getPath() + "/Config.yml",
						new PandaYaml(MainPluginClass.getPlugin()
								.getResource(MainPluginClass.getPlugin().getClass().getPackage().getName()
										.replaceAll("\\.", "/") + "/Resources/Config.yml")),
						SettingString.values(), SettingBoolean.values(), SettingInt.values(), SettingList.values()) {

					@Override
					protected PandaYaml fileVersionCheck(PandaYaml oldyaml, PandaYaml newyaml) {
						PandaYaml yaml = super.fileVersionCheck(oldyaml, newyaml);
						if (yaml != oldyaml) {
							YamlData<?> editableFlags = yaml.getRoot().getData("Settings.Editable-flags");
							if (editableFlags != null) {
								yaml.getRoot().remove("Settings.Flags");
								editableFlags.getStringList().forEach(flag -> {
									String[] split = flag.split("\\|");
									if (split.length == 3) {
										ItemStack item = ItemStacksUtils.createItemStack(
												SkinUtilities.NMS.setSkinSafe(Material.PLAYER_HEAD.getItemStack(),
														split[2]),
												String.format("&e%s", split[1]),
												Arrays.asList("&0", "&7Value: &8[&7{value}&8]"));

										ItemStackUtilities.itemToMap(item).forEach((key, value) -> yaml.getRoot()
												.set(String.format("Settings.Flags.%s.%s", split[0], key), value));
									}
								});
								yaml.getRoot().remove("Settings.Editable-flags");
							}
						}
						return yaml;
					}

				}, LANG_FILE = new FileObj(PLUGIN_FOLDER.getFolder().getPath() + "/Lang.yml",
						new PandaYaml(MainPluginClass.getPlugin()
								.getResource(MainPluginClass.getPlugin().getClass().getPackage().getName()
										.replaceAll("\\.", "/") + "/Resources/Lang.yml")),
						MessageString.values(), MessageList.values()),
						BLOCKS_FILE = new FileObj(
								PLUGIN_FOLDER.getFolder()
										.getPath() + "/Blocks.yml",
								new PandaYaml(MainPluginClass.getPlugin()
										.getResource(MainPluginClass.getPlugin().getClass().getPackage().getName()
												.replaceAll("\\.", "/") + "/Resources/Blocks.yml"))));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		for (FolderObj folder : folders) {
			if (!folder.createFolder()) {
				return false;
			}
		}

		for (FileObj file : files) {
			if (!file.createFile()) {
				return false;
			}
		}

		return true;

	}

	@Override
	public boolean unload() {
		return true;
	}

	@Override
	public LoadOn loadOn() {
		return LoadOn.BEFORE_LOAD;
	}

	@Override
	public boolean optional() {
		return false;
	}

	@Override
	public boolean allowReload() {
		return true;
	}
}

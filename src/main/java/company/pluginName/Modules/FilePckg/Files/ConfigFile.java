package company.pluginName.Modules.FilePckg.Files;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackUtilities;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.SkinUtilities;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Defaults.PandaConfigFile;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

public class ConfigFile extends PandaConfigFile {

	public ConfigFile(PandaFolder parentFolder, PandaPluginClass plugin) throws IOException, YamlException {
		super(parentFolder, plugin);
	}

	@Override
	protected PandaYaml updateFileVersion(PandaYaml yaml) {
		PandaYaml curYaml = super.updateFileVersion(yaml);

		if (curYaml != this.getDefaultYaml()) {
			// TODO: Remove on 0.1.5 Alpha
			YamlSection flags = yaml.getRoot().getSection("Settings.Flags");

			if (flags != null) {
				flags.toMap()
						.forEach((k, v) -> yaml.getRoot().set(String.format("Settings.Protection.Flags.%s", k), v));
				yaml.getRoot().remove("Settings.Flags");
			}

			YamlData<?> editableFlags = yaml.getRoot().getData("Settings.Editable-flags");
			if (editableFlags != null) {
				yaml.getRoot().remove("Settings.Protection.Flags");
				editableFlags.getStringList().forEach(flag -> {
					String[] split = flag.split("\\|");
					if (split.length == 3) {
						ItemStack item = ItemStacksUtils.createItemStack(
								SkinUtilities.NMS.setSkinSafe(Material.PLAYER_HEAD.getItemStack(), split[2]),
								String.format("&e%s", split[1]), Arrays.asList("&0", "&7Value: &8[&7{value}&8]"));

						ItemStackUtilities.itemToMap(item).forEach((key, value) -> yaml.getRoot()
								.set(String.format("Settings.Flags.%s.%s", split[0], key), value));
					}
				});
				yaml.getRoot().remove("Settings.Editable-flags");
			}
		}

		return curYaml;
	}

}

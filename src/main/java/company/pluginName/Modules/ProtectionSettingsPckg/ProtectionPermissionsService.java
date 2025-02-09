package company.pluginName.Modules.ProtectionSettingsPckg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.FilePckg.FilesService;
import company.pluginName.Modules.ProtectionSettingsPckg.Objects.Permissions.AbstractPermissionImpl;
import company.pluginName.Modules.ProtectionSettingsPckg.Objects.Permissions.FlyPermission;
import company.pluginName.Modules.ProtectionSettingsPckg.Objects.Permissions.TeleportPermission;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.PandaCommandsService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Permissions.AbstractPermission;

@PandaService(loadOn = LoadStep.ENABLE)
public class ProtectionPermissionsService {

	public static final FlyPermission FLY_PERMISSION = new FlyPermission();
	public static final TeleportPermission TELEPORT_PERMISSION = new TeleportPermission();

	@PandaInject
	private MainPluginClass plugin;

	@PandaInject
	private FilesService filesService;

	@PandaInject
	private PandaCommandsService commandsService;

	private PandaYamlFile protectionSettingsFile;
	private Map<String, AbstractPermission> registeredPermissions = new HashMap<>();
	private boolean loaded = false;

	public ProtectionPermissionsService() {
		registerPermission(FLY_PERMISSION);
		registerPermission(TELEPORT_PERMISSION);
	}

	@PostInjectMethod
	private void postInject() throws IOException, YamlException {
		protectionSettingsFile = new PandaYamlFile("protectionpermissions",
				filesService.getFolder("plugin").getFile().getPath() + "/Protection-Permissions.yml",
				new PandaYaml(plugin.getResource(plugin.getClass().getPackage().getName().replaceAll("\\.", "/")
						+ "/Resources/Protection-Permissions.yml")));
		filesService.addFile(protectionSettingsFile);
	}

	@LoadMethod
	private void load() throws FileNotFoundException, YamlException, IOException {
		this.registeredPermissions.values()
				.forEach(permission -> protectionSettingsFile.registerFieldsFromObject(permission));

		this.protectionSettingsFile.refreshFields();

		this.updateItems();

		this.loaded = true;
	}

	@ReloadMethod
	private void reload() {
		this.updateItems();
	}

	private void updateItems() {
		try {
			PandaYaml yaml = new PandaYaml(protectionSettingsFile.getFile());

			registeredPermissions.values().forEach(permission -> {
				if (permission instanceof AbstractPermissionImpl) {
					if (!yaml.getRoot().has("Permissions." + permission.getId() + ".Display-item")) {
						ItemBuilder.inst().fromItem(((AbstractPermissionImpl) permission).getDefaultDisplayItem())
								.toMap().forEach((key, value) -> {
									yaml.getRoot().set("Permissions." + permission.getId() + ".Display-item." + key,
											value);
								});
					}

					((AbstractPermissionImpl) permission).setDisplayItem(ItemBuilder
							.inst().fromMap(yaml.getRoot()
									.getSection("Permissions." + permission.getId() + ".Display-item").toMap())
							.build());
				}
			});

			yaml.saveYAML(protectionSettingsFile.getFile());
		} catch (YamlException | IOException e) {
			e.printStackTrace();
		}
	}

	public void registerPermission(AbstractPermission permission) {
		if (!loaded) {
			this.registeredPermissions.put(permission.getId(), permission);
		} else {
			throw new UnsupportedOperationException(
					"You can't reqgister permissions when the plugin is already loaded");
		}
	}

	public List<String> getPermissionIds() {
		return new ArrayList<>(this.registeredPermissions.keySet());
	}

	public List<AbstractPermission> getPermission() {
		return new ArrayList<>(this.registeredPermissions.values());
	}

	public AbstractPermission getPermission(String id) {
		return this.registeredPermissions.get(id);
	}

}

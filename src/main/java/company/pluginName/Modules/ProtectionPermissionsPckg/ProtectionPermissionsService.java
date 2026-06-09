package company.pluginName.Modules.ProtectionPermissionsPckg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.FilePckg.FilesService;
import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.BreakPermission;
import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.FlyPermission;
import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.InteractPermission;
import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.InventoryAccessPermission;
import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.PlacePermission;
import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.PvPPermission;
import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.RideVehiclesPermission;
import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.TeleportPermission;
import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.ToggleBlockVisibilityPermission;
import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.Templates.PermissionImpl;
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
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Permissions.PermissionInterface;

@PandaService(loadOn = LoadStep.ENABLE)
public class ProtectionPermissionsService {

	public static final FlyPermission FLY_PERMISSION = new FlyPermission();
	public static final TeleportPermission TELEPORT_PERMISSION = new TeleportPermission();
	public static final ToggleBlockVisibilityPermission TOGGLEBLOCKVISIBILITY_PERMISSION = new ToggleBlockVisibilityPermission();
	public static final RideVehiclesPermission RIDEVEHICLES_PERMISSION = new RideVehiclesPermission();
	public static final BreakPermission BREAK_PERMISSION = new BreakPermission();
	public static final PlacePermission PLACE_PERMISSION = new PlacePermission();
	public static final InteractPermission INTERACT_PERMISSION = new InteractPermission();
	public static final InventoryAccessPermission INVENTORYACCESS_PERMISSION = new InventoryAccessPermission();
	public static final PvPPermission PVP_PERMISSION = new PvPPermission();

	@PandaInject
	private MainPluginClass plugin;

	@PandaInject
	private FilesService filesService;

	@PandaInject
	private PandaCommandsService commandsService;

	private PandaYamlFile protectionSettingsFile;
	private Map<String, PermissionInterface> registeredPermissions = new HashMap<>();
	private boolean loaded = false;

	public ProtectionPermissionsService() {
		registerPermission(FLY_PERMISSION);
		registerPermission(TELEPORT_PERMISSION);
		registerPermission(TOGGLEBLOCKVISIBILITY_PERMISSION);
		registerPermission(RIDEVEHICLES_PERMISSION);
		registerPermission(BREAK_PERMISSION);
		registerPermission(PLACE_PERMISSION);
		registerPermission(INTERACT_PERMISSION);
		registerPermission(INVENTORYACCESS_PERMISSION);
		registerPermission(PVP_PERMISSION);
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
				if (permission instanceof PermissionImpl) {
					if (!yaml.getRoot().has("Permissions." + permission.getId() + ".Display-item")) {
						ItemBuilder.inst().fromItem(((PermissionImpl) permission).getDefaultDisplayItem())
								.toMap().forEach((key, value) -> {
									yaml.getRoot().set("Permissions." + permission.getId() + ".Display-item." + key,
											value);
								});
					}

					((PermissionImpl) permission).setDisplayItem(ItemBuilder
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

	public void registerPermission(PermissionInterface permission) {
		if (!loaded) {
			this.registeredPermissions.put(permission.getId(), permission);
		} else {
			throw new UnsupportedOperationException("You can't register permissions when the plugin is already loaded");
		}
	}

	public List<String> getPermissionIds() {
		return new ArrayList<>(this.registeredPermissions.keySet());
	}

	public List<PermissionInterface> getPermissions() {
		return new ArrayList<>(this.registeredPermissions.values());
	}

	public PermissionInterface getPermission(String id) {
		return this.registeredPermissions.get(id);
	}

}

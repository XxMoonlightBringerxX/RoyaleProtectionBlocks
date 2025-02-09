package company.pluginName.Modules.ProtectionSettingsPckg.Objects.Permissions;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.SettingGroup;

public class TeleportPermission extends AbstractPermissionImpl {

	public TeleportPermission() {
		super("teleport", true, null, null, "Teleport To Home", null, true, true, ItemBuilder.inst().setMaterial(Material.ENDER_PEARL)
				.setName("&eCan players teleport to your protection?").setLore(Arrays
						.asList("&7Define if players can teleport", "&7to your protection through the menu."))
				.build());
	}

}

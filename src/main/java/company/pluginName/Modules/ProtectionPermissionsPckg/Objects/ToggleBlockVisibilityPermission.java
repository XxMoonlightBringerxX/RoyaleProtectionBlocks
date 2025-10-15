package company.pluginName.Modules.ProtectionPermissionsPckg.Objects;

import java.util.Arrays;

import org.bukkit.Material;

import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.Templates.PermissionImpl;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;

public class ToggleBlockVisibilityPermission extends PermissionImpl {

	public ToggleBlockVisibilityPermission() {
		super("toggle-block-visibility", true, null, null, "Block Visibility", false, false, false, true, true, true,
				ItemBuilder.inst().setMaterial(Material.GRASS_BLOCK)
						.setName("&eCan players toggle the block visibility on your protection?")
						.setLore(Arrays.asList("&7Define if players can toggle", "&7the protection block visibility on",
								"&7your protection with the &e/pb [show/hide]", "&7command or through the menu."))
						.build());
	}

	@Override
	public Boolean getStaffValue() {
		return true;
	}

	@Override
	public Boolean getMainOwnerValue() {
		return true;
	}

}

package company.pluginName.Modules.ProtectionPermissionsPckg.Objects;

import java.util.Arrays;

import org.bukkit.Material;

import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.Templates.PermissionImpl;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;

public class TeleportPermission extends PermissionImpl {

	public TeleportPermission() {
		super("teleport", true, null, null, "Teleport To Home", false, false, true, true, true, true,
				ItemBuilder.inst().setMaterial(Material.ENDER_PEARL)
						.setName("&eCan players teleport to your protection?").setLore(Arrays
								.asList("&7Define if players can teleport", "&7to your protection through the menu."))
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

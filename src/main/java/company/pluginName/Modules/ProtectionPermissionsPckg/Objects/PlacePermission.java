package company.pluginName.Modules.ProtectionPermissionsPckg.Objects;

import java.util.Arrays;

import org.bukkit.Material;

import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.Templates.PermissionImpl;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;

public class PlacePermission extends PermissionImpl {

	public PlacePermission() {
		super("place", true, null, null, "Place blocks", false, true, true, true, true, true,
				ItemBuilder.inst().setMaterial(Material.STONE).setName("&eCan players place blocks on your protection?")
						.setLore(Arrays.asList("&7Define if players can place", "&7blocks on your protection."))
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

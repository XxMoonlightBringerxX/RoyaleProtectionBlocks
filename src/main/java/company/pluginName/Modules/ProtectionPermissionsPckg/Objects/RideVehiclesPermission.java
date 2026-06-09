package company.pluginName.Modules.ProtectionPermissionsPckg.Objects;

import java.util.Arrays;

import org.bukkit.Material;

import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.Templates.PermissionImpl;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;

public class RideVehiclesPermission extends PermissionImpl {

	public RideVehiclesPermission() {
		super("ride-vehicles", true, null, null, "Ride vehicles", false, true, true, true, true, true, ItemBuilder
				.inst().setMaterial(Material.LEATHER_HORSE_ARMOR)
				.setName("&eCan players ride vehicles on your protection?")
				.setLore(Arrays.asList("&7Define if players can ride", "&7vehicles on your protection",
						"&7or not. People will be able", "&7to leave a vehicle, but not", "&7to get inside it again."))
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

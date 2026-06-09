package company.pluginName.Modules.ProtectionPermissionsPckg.Objects;

import java.util.Arrays;

import org.bukkit.Material;

import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.Templates.PermissionImpl;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;

public class FlyPermission extends PermissionImpl {

	public FlyPermission() {
		super("fly", true, null, null, "Fly Ability", false, true, false, true, true, true, ItemBuilder.inst()
				.setMaterial(Material.FEATHER).setName("&eCan players fly on your protection?").setLore(Arrays
						.asList("&7Define if players can fly", "&7on your protection with the", "&e/pb fly &7command."))
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

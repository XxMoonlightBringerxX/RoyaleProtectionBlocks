package company.pluginName.Modules.ProtectionPermissionsPckg.Objects;

import java.util.Arrays;

import org.bukkit.Material;

import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.Templates.PermissionImpl;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;

public class BreakPermission extends PermissionImpl {

	public BreakPermission() {
		super("break", true, null, null, "Break blocks", false, true, true, true, true, true, ItemBuilder.inst()
				.setMaterial(Material.DIAMOND_PICKAXE).setName("&eCan players break blocks on your protection?")
				.setLore(Arrays.asList("&7Define if players can break", "&7blocks on your protection.")).build());
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

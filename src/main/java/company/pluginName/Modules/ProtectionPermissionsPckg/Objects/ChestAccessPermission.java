package company.pluginName.Modules.ProtectionPermissionsPckg.Objects;

import java.util.Arrays;

import org.bukkit.Material;

import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;

public class ChestAccessPermission extends AbstractPermissionImpl {

	public ChestAccessPermission() {
		super("chestaccess", true, null, null, "Chest Access", false, true, true, true, true, true,
				ItemBuilder.inst().setMaterial(Material.CHEST).setName("&eCan players open chests?")
						.setLore(Arrays.asList("&7Define if players can open", "&7chests on your protection."))
						.build());
	}

}

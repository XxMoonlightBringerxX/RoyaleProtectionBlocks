package company.pluginName.Modules.ProtectionSettingsPckg.Objects.Permissions;

import java.util.Arrays;

import org.bukkit.Material;

import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;

public class FlyPermission extends AbstractPermissionImpl {

	public FlyPermission() {
		super("fly", true, null, null, "Fly Ability", false, false, true, ItemBuilder.inst()
				.setMaterial(Material.FEATHER).setName("&eCan players fly on your protection?").setLore(Arrays
						.asList("&7Define if players can fly", "&7on your protection with the", "&e/pb fly &7command."))
				.build());
	}

}

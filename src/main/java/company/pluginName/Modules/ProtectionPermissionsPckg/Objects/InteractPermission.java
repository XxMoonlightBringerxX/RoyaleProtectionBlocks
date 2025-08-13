package company.pluginName.Modules.ProtectionPermissionsPckg.Objects;

import java.util.Arrays;

import org.bukkit.Material;

import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;

public class InteractPermission extends AbstractPermissionImpl {

	public InteractPermission() {
		super("interact", true, null, null, "Interact", false, true, true, true, true, true,
				ItemBuilder.inst().setMaterial(Material.LEVER).setName("&eCan players interact?")
						.setLore(Arrays.asList("&7Define if players can interact", "&7with any object or block",
								"&7on your protection. This will", "&7block any action possible."))
						.build());
	}

}

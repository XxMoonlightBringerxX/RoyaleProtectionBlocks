package company.pluginName.Modules.ProtectionPermissionsPckg.Objects;

import java.util.Arrays;

import org.bukkit.Material;

import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.Templates.PermissionImpl;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.PermissionGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

public class PvPPermission extends PermissionImpl {

	public PvPPermission() {
		super("pvp", true, null, null, "PvP", true, true, true, true, true, true,
				ItemBuilder.inst().setMaterial(Material.DIAMOND_SWORD).setName("&eCan players attack each other?")
						.setLore(Arrays.asList("&7Define if players can attack", "&7each other. Both must be allowed",
								"&7to attack in order to fight."))
						.build());
	}

	@Override
	public Boolean getValue(IProtection protection, PermissionGroup group) {
		if (group == PermissionGroup.STAFF) {
			group = PermissionGroup.NON_MEMBERS;
		} else if (group == PermissionGroup.MAIN_OWNER) {
			group = PermissionGroup.OWNERS;
		}

		Boolean value = protection.getPermissionValue(this, group);

		if (value == null) {
			return getValue(group);
		}

		return value;
	}

}

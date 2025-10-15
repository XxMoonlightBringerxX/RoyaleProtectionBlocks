package company.pluginName.Modules.ProtectionSettingsPckg.Objects.Settings;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

import company.pluginName.Modules.ProtectionSettingsPckg.Objects.Settings.Templates.BooleanSettingImpl;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.PermissionGroup;

public class TNTExplosionsSetting extends BooleanSettingImpl {

	public TNTExplosionsSetting() {
		super("tnt-explosions", true, null, null, "TNT explosions allowed", true, null, null, null,
				ItemBuilder.inst().setMaterial(Material.TNT).setName("&eCan TNT explode on your protection? {value}")
						.setLore(Arrays.asList("&7Define if TNT can explode",
								"&7to your protection. This only prevents", "&7blocks from being destroyed."))
						.build());
	}

	@Override
	public List<PermissionGroup> getManagedGroups() {
		return Arrays.asList(PermissionGroup.GENERIC);
	}

}

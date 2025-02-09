package company.pluginName.Modules.ProtectionSettingsPckg.Objects.Settings;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.SettingGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.BooleanSetting;

public class TeleportSetting extends BooleanSetting {

	public TeleportSetting() {
		super("teleport", "Teleport To Home",
				ItemBuilder.inst().setMaterial(Material.ENDER_PEARL)
						.setName("&eCan players teleport to your protection?").setLore(Arrays
								.asList("&7Define if players can teleport", "&7to your protection through the menu."))
						.build());

		this.nonMembersDefaultValue = false;
		this.membersDefaultValue = true;
		this.ownersDefaultValue = true;
	}

	@Override
	public List<SettingGroup> getManagedGroups() {
		return Arrays.asList(SettingGroup.MEMBERS, SettingGroup.OWNERS);
	}

}

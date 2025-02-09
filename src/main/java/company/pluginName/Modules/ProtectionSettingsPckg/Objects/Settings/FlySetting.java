package company.pluginName.Modules.ProtectionSettingsPckg.Objects.Settings;

import java.util.Arrays;

import org.bukkit.Material;

import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.BooleanSetting;

public class FlySetting extends BooleanSetting {

	public FlySetting() {
		super("fly", "Fly Ability", ItemBuilder
				.inst().setMaterial(Material.FEATHER).setName("&eCan players fly on your protection?").setLore(Arrays
						.asList("&7Define if players can fly", "&7on your protection with the", "&e/pb fly &7command."))
				.build());

		this.nonMembersDefaultValue = false;
		this.membersDefaultValue = false;
		this.ownersDefaultValue = true;
	}

}

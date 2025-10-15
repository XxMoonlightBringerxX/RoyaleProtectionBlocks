package company.pluginName.Modules.ProtectionSettingsPckg.Objects.Settings.Templates;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.PermissionGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.StringListSettingInterface;

public class StringListSettingImpl extends SettingImpl<ArrayList<String>> implements StringListSettingInterface {

	@SuppressWarnings("unused")
	private PandaStringListField genericValueField;
	@SuppressWarnings("unused")
	private PandaStringListField nonMembersValueField;
	@SuppressWarnings("unused")
	private PandaStringListField membersValueField;
	@SuppressWarnings("unused")
	private PandaStringListField ownersValueField;

	public StringListSettingImpl(String id, boolean editable, String permission, Double cost, String displayName,
			ArrayList<String> genericValue, ArrayList<String> nonMembersValue, ArrayList<String> membersValue,
			ArrayList<String> ownersValue, ItemStack displayItem) {
		super(id, editable, permission, cost, displayName, genericValue, nonMembersValue, membersValue, ownersValue,
				displayItem);

		this.genericValueField = getManagedGroups().contains(PermissionGroup.GENERIC)
				? new PandaStringListField("Settings." + id + ".Value", genericValue)
				: null;
		this.nonMembersValueField = getManagedGroups().contains(PermissionGroup.NON_MEMBERS)
				? new PandaStringListField("Settings." + id + ".Non-members-value", nonMembersValue)
				: null;
		this.membersValueField = getManagedGroups().contains(PermissionGroup.MEMBERS)
				? new PandaStringListField("Settings." + id + ".Members-value", membersValue)
				: null;
		this.ownersValueField = getManagedGroups().contains(PermissionGroup.OWNERS)
				? new PandaStringListField("Settings." + id + ".Owners-value", ownersValue)
				: null;
	}

}

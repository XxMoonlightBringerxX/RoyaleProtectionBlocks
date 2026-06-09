package company.pluginName.Modules.ProtectionSettingsPckg.Objects.Settings.Templates;

import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.PermissionGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.StringSettingInterface;

public class StringSettingImpl extends SettingImpl<String> implements StringSettingInterface {

	@SuppressWarnings("unused")
	private PandaStringField genericValueField;
	@SuppressWarnings("unused")
	private PandaStringField nonMembersValueField;
	@SuppressWarnings("unused")
	private PandaStringField membersValueField;
	@SuppressWarnings("unused")
	private PandaStringField ownersValueField;

	public StringSettingImpl(String id, boolean editable, String permission, Double cost, String displayName,
			String genericValue, String nonMembersValue, String membersValue, String ownersValue,
			ItemStack displayItem) {
		super(id, editable, permission, cost, displayName, genericValue, nonMembersValue, membersValue, ownersValue,
				displayItem);

		this.genericValueField = getManagedGroups().contains(PermissionGroup.GENERIC)
				? new PandaStringField("Settings." + id + ".Value", genericValue)
				: null;
		this.nonMembersValueField = getManagedGroups().contains(PermissionGroup.NON_MEMBERS)
				? new PandaStringField("Settings." + id + ".Non-members-value", nonMembersValue)
				: null;
		this.membersValueField = getManagedGroups().contains(PermissionGroup.MEMBERS)
				? new PandaStringField("Settings." + id + ".Members-value", membersValue)
				: null;
		this.ownersValueField = getManagedGroups().contains(PermissionGroup.OWNERS)
				? new PandaStringField("Settings." + id + ".Owners-value", ownersValue)
				: null;
	}

}

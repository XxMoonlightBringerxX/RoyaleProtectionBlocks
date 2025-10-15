package company.pluginName.Modules.ProtectionSettingsPckg.Objects.Settings.Templates;

import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.PermissionGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.BooleanSettingInterface;

public class BooleanSettingImpl extends SettingImpl<Boolean> implements BooleanSettingInterface {

	@SuppressWarnings("unused")
	private PandaBooleanField genericValueField;
	@SuppressWarnings("unused")
	private PandaBooleanField nonMembersValueField;
	@SuppressWarnings("unused")
	private PandaBooleanField membersValueField;
	@SuppressWarnings("unused")
	private PandaBooleanField ownersValueField;

	public BooleanSettingImpl(String id, boolean editable, String permission, Double cost, String displayName,
			Boolean genericValue, Boolean nonMembersValue, Boolean membersValue, Boolean ownersValue,
			ItemStack displayItem) {
		super(id, editable, permission, cost, displayName, genericValue, nonMembersValue, membersValue, ownersValue,
				displayItem);

		this.genericValueField = getManagedGroups().contains(PermissionGroup.GENERIC)
				? new PandaBooleanField("Settings." + id + ".Value", genericValue)
				: null;
		this.nonMembersValueField = getManagedGroups().contains(PermissionGroup.NON_MEMBERS)
				? new PandaBooleanField("Settings." + id + ".Non-members-value", nonMembersValue)
				: null;
		this.membersValueField = getManagedGroups().contains(PermissionGroup.MEMBERS)
				? new PandaBooleanField("Settings." + id + ".Members-value", membersValue)
				: null;
		this.ownersValueField = getManagedGroups().contains(PermissionGroup.OWNERS)
				? new PandaBooleanField("Settings." + id + ".Owners-value", ownersValue)
				: null;
	}

}

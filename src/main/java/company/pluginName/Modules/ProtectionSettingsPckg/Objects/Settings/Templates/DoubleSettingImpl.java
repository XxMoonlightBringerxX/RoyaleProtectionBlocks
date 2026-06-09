package company.pluginName.Modules.ProtectionSettingsPckg.Objects.Settings.Templates;

import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaDoubleField;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.PermissionGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.DoubleSettingInterface;

public class DoubleSettingImpl extends SettingImpl<Double> implements DoubleSettingInterface {

	@SuppressWarnings("unused")
	private PandaDoubleField genericValueField;
	@SuppressWarnings("unused")
	private PandaDoubleField nonMembersValueField;
	@SuppressWarnings("unused")
	private PandaDoubleField membersValueField;
	@SuppressWarnings("unused")
	private PandaDoubleField ownersValueField;

	public DoubleSettingImpl(String id, boolean editable, String permission, Double cost, String displayName,
			Double genericValue, Double nonMembersValue, Double membersValue, Double ownersValue,
			ItemStack displayItem) {
		super(id, editable, permission, cost, displayName, genericValue, nonMembersValue, membersValue, ownersValue,
				displayItem);

		this.genericValueField = getManagedGroups().contains(PermissionGroup.GENERIC)
				? new PandaDoubleField("Settings." + id + ".Value", genericValue)
				: null;
		this.nonMembersValueField = getManagedGroups().contains(PermissionGroup.NON_MEMBERS)
				? new PandaDoubleField("Settings." + id + ".Non-members-value", nonMembersValue)
				: null;
		this.membersValueField = getManagedGroups().contains(PermissionGroup.MEMBERS)
				? new PandaDoubleField("Settings." + id + ".Members-value", membersValue)
				: null;
		this.ownersValueField = getManagedGroups().contains(PermissionGroup.OWNERS)
				? new PandaDoubleField("Settings." + id + ".Owners-value", ownersValue)
				: null;
	}

}

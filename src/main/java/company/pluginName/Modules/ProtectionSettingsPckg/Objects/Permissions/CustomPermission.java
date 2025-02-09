package company.pluginName.Modules.ProtectionSettingsPckg.Objects.Permissions;

import org.bukkit.inventory.ItemStack;

public class CustomPermission extends AbstractPermissionImpl {

	public CustomPermission(String id, boolean editable, String permission, Double cost, String displayName,
			Boolean nonMembersValue, Boolean membersValue, Boolean ownersValue, ItemStack displayItem) {
		super(id, editable, permission, cost, displayName, nonMembersValue, membersValue, ownersValue, displayItem);
	}

}

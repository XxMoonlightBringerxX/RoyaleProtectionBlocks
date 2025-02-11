package company.pluginName.Modules.ProtectionPermissionsPckg.Objects;

import org.bukkit.inventory.ItemStack;

public class CustomPermission extends AbstractPermissionImpl {

	public CustomPermission(String id, boolean editable, String permission, Double cost, String displayName,
			boolean nonMembersValue, boolean nonMembersValueEditable, boolean membersValue,
			boolean membersValueEditable, boolean ownersValue, boolean ownersValueEditable, ItemStack displayItem) {
		super(id, editable, permission, cost, displayName, nonMembersValue, nonMembersValueEditable, membersValue,
				membersValueEditable, ownersValue, ownersValueEditable, displayItem);
	}

}

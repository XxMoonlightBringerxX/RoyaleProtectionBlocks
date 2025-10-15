package company.pluginName.Modules.ProtectionPermissionsPckg.Objects.Templates;

import org.bukkit.inventory.ItemStack;

public class CustomPermissionImpl extends PermissionImpl {

	public CustomPermissionImpl(String id, boolean editable, String permission, Double cost, String displayName,
			boolean nonMembersValue, boolean nonMembersValueEditable, boolean membersValue,
			boolean membersValueEditable, boolean ownersValue, boolean ownersValueEditable, ItemStack displayItem) {
		super(id, editable, permission, cost, displayName, nonMembersValue, nonMembersValueEditable, membersValue,
				membersValueEditable, ownersValue, ownersValueEditable, displayItem);
	}

}

package company.pluginName.Modules.ProtectionPermissionsPckg.Objects;

import java.util.Arrays;

import org.bukkit.Material;

import company.pluginName.Modules.ProtectionPermissionsPckg.Objects.Templates.PermissionImpl;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;

public class InventoryAccessPermission extends PermissionImpl {

	private PandaBooleanField preventHoppersIfNonMembersAccessDeniedField = new PandaBooleanField(
			"Permissions.chestaccess.Prevent-hoppers-if-non-members-access-denied", false);
	private PandaBooleanField dropHoppersIfNonMembersAccessDeniedField = new PandaBooleanField(
			"Permissions.chestaccess.Drop-hoppers-if-non-members-access-denied", false);

	public InventoryAccessPermission() {
		super("chestaccess", true, null, null, "Inventory Access", false, true, true, true, true, true,
				ItemBuilder.inst().setMaterial(Material.CHEST).setName("&eCan players open inventories?")
						.setLore(Arrays.asList("&7Define if players can open", "&7chests or other blocks with",
								"&7inventories on your protection."))
						.build());
	}

	@Override
	public Boolean getStaffValue() {
		return true;
	}

	@Override
	public Boolean getMainOwnerValue() {
		return true;
	}

	public boolean isPreventHoppersIfNonMembersAccessDenied() {
		return preventHoppersIfNonMembersAccessDeniedField.isTrue();
	}

	public boolean isDropHoppersIfNonMembersAccessDenied() {
		return dropHoppersIfNonMembersAccessDeniedField.isTrue();
	}

}

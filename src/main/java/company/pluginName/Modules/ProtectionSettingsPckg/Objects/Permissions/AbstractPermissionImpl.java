package company.pluginName.Modules.ProtectionSettingsPckg.Objects.Permissions;

import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaDoubleField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import lombok.Getter;
import lombok.Setter;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Permissions.AbstractPermission;

public class AbstractPermissionImpl extends AbstractPermission {

	// Data retrieved from the configuration
	private PandaBooleanField editableField;
	private PandaStringField permissionField;
	private PandaDoubleField costField;
	private PandaStringField displayNameField;
	private PandaBooleanField nonMembersValueField;
	private PandaBooleanField membersValueField;
	private PandaBooleanField ownersValueField;

	private @Getter ItemStack defaultDisplayItem;
	private @Getter @Setter ItemStack displayItem;

	public AbstractPermissionImpl(String id, boolean editable, String permission, Double cost, String displayName,
			Boolean nonMembersValue, Boolean membersValue, Boolean ownersValue, ItemStack displayItem) {
		super(id);

		this.editableField = new PandaBooleanField("Permissions." + id + ".Editable", editable);
		this.permissionField = new PandaStringField("Permissions." + id + ".Permission", permission);
		this.costField = new PandaDoubleField("Permissions." + id + ".Cost", cost);
		this.displayNameField = new PandaStringField("Permissions." + id + ".Display-name", displayName);
		this.nonMembersValueField = nonMembersValue != null
				? new PandaBooleanField("Permissions." + id + ".Non-members-value", nonMembersValue)
				: null;
		this.membersValueField = membersValue != null
				? new PandaBooleanField("Permissions." + id + ".Members-value", membersValue)
				: null;
		this.ownersValueField = ownersValue != null
				? new PandaBooleanField("Permissions." + id + ".Owners-value", ownersValue)
				: null;

		this.defaultDisplayItem = displayItem;
	}

	@Override
	public boolean isEditable() {
		return editableField.isTrue();
	}

	@Override
	public String getPermission() {
		return permissionField.getContent();
	}

	@Override
	public Double getCost() {
		return costField.getContent();
	}

	@Override
	public String getDisplayName() {
		return displayNameField.getContent();
	}

	@Override
	public Boolean getNonMembersValue() {
		return nonMembersValueField.getContent();
	}

	@Override
	public Boolean getMembersValue() {
		return membersValueField.getContent();
	}

	@Override
	public Boolean getOwnersValue() {
		return ownersValueField.getContent();
	}

}

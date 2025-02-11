package company.pluginName.Modules.ProtectionPermissionsPckg.Objects;

import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaDoubleField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import lombok.Getter;
import lombok.Setter;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Permissions.AbstractPermission;

public class AbstractPermissionImpl extends AbstractPermission {

	private boolean defaultEditable;
	private String defaultPermission;
	private Double defaultCost;
	private String defaultDisplayName;
	private Boolean defaultNonMembersValue;
	private Boolean defaultMembersValue;
	private Boolean defaultOwnersValue;
	private @Getter ItemStack defaultDisplayItem;

	// Data retrieved from the configuration
	private PandaBooleanField editableField;
	private PandaStringField permissionField;
	private PandaDoubleField costField;
	private PandaStringField displayNameField;
	private PandaBooleanField nonMembersValueField;
	private PandaBooleanField nonMembersValueEditableField;
	private PandaBooleanField membersValueField;
	private PandaBooleanField membersValueEditableField;
	private PandaBooleanField ownersValueField;
	private PandaBooleanField ownersValueEditableField;

	private @Getter @Setter ItemStack displayItem;

	public AbstractPermissionImpl(String id, boolean editable, String permission, Double cost, String displayName,
			boolean nonMembersValue, boolean nonMembersValueEditable, boolean membersValue,
			boolean membersValueEditable, boolean ownersValue, boolean ownersValueEditable, ItemStack displayItem) {
		super(id);

		this.defaultEditable = editable;
		this.defaultPermission = permission;
		this.defaultCost = cost;
		this.defaultDisplayName = displayName;
		this.defaultNonMembersValue = nonMembersValue;
		this.defaultMembersValue = membersValue;
		this.defaultOwnersValue = ownersValue;
		this.defaultDisplayItem = displayItem;

		this.editableField = new PandaBooleanField("Permissions." + id + ".Editable", editable);
		this.permissionField = new PandaStringField("Permissions." + id + ".Permission", permission);
		this.costField = new PandaDoubleField("Permissions." + id + ".Cost", cost);
		this.displayNameField = new PandaStringField("Permissions." + id + ".Display-name", displayName);
		this.nonMembersValueField = nonMembersValueEditable
				? new PandaBooleanField("Permissions." + id + ".Non-members-value", nonMembersValue)
				: null;
		this.nonMembersValueEditableField = nonMembersValueEditable
				? new PandaBooleanField("Permissions." + id + ".Non-members-value-editable", true)
				: null;
		this.membersValueField = membersValueEditable
				? new PandaBooleanField("Permissions." + id + ".Members-value", membersValue)
				: null;
		this.membersValueEditableField = membersValueEditable
				? new PandaBooleanField("Permissions." + id + ".Members-value-editable", true)
				: null;
		this.ownersValueField = ownersValueEditable
				? new PandaBooleanField("Permissions." + id + ".Owners-value", ownersValue)
				: null;
		this.ownersValueEditableField = ownersValueEditable
				? new PandaBooleanField("Permissions." + id + ".Owners-value-editable", true)
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
		return nonMembersValueField != null ? nonMembersValueField.getContent() : defaultNonMembersValue;
	}

	@Override
	public Boolean getMembersValue() {
		return membersValueField != null ? membersValueField.getContent() : defaultMembersValue;
	}

	@Override
	public Boolean getOwnersValue() {
		return ownersValueField != null ? ownersValueField.getContent() : defaultOwnersValue;
	}

	@Override
	public boolean isNonMembersValueEditable() {
		return nonMembersValueEditableField != null ? nonMembersValueEditableField.isTrue() : false;
	}

	@Override
	public boolean isMembersValueEditable() {
		return membersValueEditableField != null ? membersValueEditableField.isTrue() : false;
	}

	@Override
	public boolean isOwnersValueEditable() {
		return ownersValueEditableField != null ? ownersValueEditableField.isTrue() : false;
	}

}

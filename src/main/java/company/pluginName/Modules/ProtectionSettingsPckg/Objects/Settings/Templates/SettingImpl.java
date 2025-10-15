package company.pluginName.Modules.ProtectionSettingsPckg.Objects.Settings.Templates;

import java.io.Serializable;

import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaDoubleField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import lombok.Getter;
import lombok.Setter;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.SettingInterface;

public abstract class SettingImpl<T extends Serializable> implements SettingInterface<T> {

	private String id;

	protected T defaultGenericValue;
	protected T defaultNonMembersValue;
	protected T defaultMembersValue;
	protected T defaultOwnersValue;
	protected @Getter ItemStack defaultDisplayItem;

	// Data retrieved from the configuration
	private PandaBooleanField enabledField;
	private PandaBooleanField editableField;
	private PandaStringField permissionField;
	private PandaDoubleField costField;
	private PandaStringField displayNameField;

	private @Getter @Setter ItemStack displayItem;

	public SettingImpl(String id, boolean editable, String permission, Double cost, String displayName, T genericValue,
			T nonMembersValue, T membersValue, T ownersValue, ItemStack displayItem) {
		this.id = id;

		this.defaultGenericValue = genericValue;
		this.defaultNonMembersValue = nonMembersValue;
		this.defaultMembersValue = membersValue;
		this.defaultOwnersValue = ownersValue;
		this.defaultDisplayItem = displayItem;

		this.enabledField = new PandaBooleanField("Settings." + id + ".Enabled", true);
		this.editableField = new PandaBooleanField("Settings." + id + ".Editable", editable);
		this.permissionField = new PandaStringField("Settings." + id + ".Permission", permission);
		this.costField = new PandaDoubleField("Settings." + id + ".Cost", cost);
		this.displayNameField = new PandaStringField("Settings." + id + ".Display-name", displayName);

		this.defaultDisplayItem = displayItem;
	}

	@Override
	public String getId() {
		return this.id;
	}

	public boolean isEnabled() {
		return enabledField.isTrue();
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

	public T getGenericValue() {
		return defaultGenericValue;
	}

	@Override
	public T getNonMembersValue() {
		return defaultNonMembersValue;
	}

	@Override
	public T getMembersValue() {
		return defaultMembersValue;
	}

	@Override
	public T getOwnersValue() {
		return defaultOwnersValue;
	}

}

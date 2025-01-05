package company.pluginName.Modules.ProtectionSettingsPckg.Objects;

import java.io.Serializable;

import org.bukkit.inventory.ItemStack;

import lombok.Data;

@Data
public abstract class AbstractSetting<T extends Serializable> {

	private String id;

	private boolean editable = true;
	private ItemStack defaultDisplayItem;
	private T nonMembersDefaultValue;
	private T membersDefaultValue;
	private T ownersDefaultValue;

	// TODO: Check for default values for internal purpose and others which are the
	// ones retrieved from the configuration file, which should be differentiated
	// somewhere.

	public AbstractSetting(String id, ItemStack defaultDisplayItem) {
		this.id = id;
		this.defaultDisplayItem = defaultDisplayItem;
	}

}

package company.pluginName.Modules.ProtectionSettingsPckg.Objects;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

public class StringListSetting extends AbstractSetting<ArrayList<String>> {

	public StringListSetting(String id, ItemStack displayItem) {
		super(id, displayItem);
	}

}

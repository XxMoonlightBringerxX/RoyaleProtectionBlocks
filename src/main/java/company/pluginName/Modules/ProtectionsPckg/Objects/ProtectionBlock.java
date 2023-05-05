package company.pluginName.Modules.ProtectionsPckg.Objects;

import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.ProtectionBlocks.Delete.ProtectionBlocksDeleteSQLException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveSQLException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class ProtectionBlock {

	private String id;
	private ItemStack item;
	private int blocksX;
	private int blocksY;
	private int blocksZ;
	private String permission;

	public ItemStack generateItem() {
		return ItemStacksUtils.setData(item.clone(), "ProtectionBlockId", id);
	}

	public void save() throws ProtectionBlocksSaveSQLException {
		MainPluginClass.getPlugin().getSqlModule().saveProtectionBlock(this);
	}

	public void delete() throws ProtectionBlocksDeleteSQLException {
		MainPluginClass.getPlugin().getSqlModule().deleteProtectionBlock(this);
	}

}

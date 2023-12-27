package company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionBlocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.ProtectionBlocks.ProtectionBlocksGenerateItemException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveItemNotAllowedException;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.Utils.ProtectionBlocksUtils;
import company.pluginName.Utils.ProtectionBlocksUtils.ItemType;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.ItemStackDataUtilities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProtectionBlockInformation {

	private String id;
	private @Setter(lombok.AccessLevel.NONE) ItemStack item;
	private int blocksX;
	private int blocksY;
	private int blocksZ;
	private String permission;
	private Double price;

	public void setItem(ItemStack item) throws ProtectionBlocksSaveException {
		if (item != null) {
			if (item.getType() == Material.AIR
					|| (!item.getType().isBlock() && ProtectionBlocksUtils.getItemType(item) == ItemType.VANILLA)) {
				throw new ProtectionBlocksSaveItemNotAllowedException();
			}

			item.setAmount(1);
			this.item = item;
		} else {
			this.item = null;
		}
	}

	public ItemStack generateItem() throws ProtectionBlocksGenerateItemException {
		try {
			return ItemStackDataUtilities.setPersistentData(item.clone(), MainPluginClass.getPlugin(),
					ProtectionBlock.PROTECTION_BLOCK_ID_KEY, id);
		} catch (Exception e) {
			throw new ProtectionBlocksGenerateItemException(e);
		}
	}

	public boolean isForSale() {
		return this.price != null && this.price > 0D;
	}

	public ItemType getItemType() {
		return ProtectionBlocksUtils.getItemType(item);
	}

	public boolean isSameType(ItemStack item) {
		return ProtectionBlocksUtils.isSameType(this.item, item);
	}

	public boolean isSameType(Block block) {
		return ProtectionBlocksUtils.isSameType(this.item, block);
	}

	public void copy(ProtectionBlockInformation information) {
		this.item = information.item != null ? information.item.clone() : null;
		this.blocksX = information.blocksX;
		this.blocksY = information.blocksY;
		this.blocksZ = information.blocksZ;
		this.permission = information.permission;
		this.price = information.price;
	}

}

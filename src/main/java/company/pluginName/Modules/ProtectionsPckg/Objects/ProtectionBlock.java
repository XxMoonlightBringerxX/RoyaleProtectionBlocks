package company.pluginName.Modules.ProtectionsPckg.Objects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.Permissions;
import company.pluginName.Exceptions.ProtectionBlocks.Delete.ProtectionBlocksDeleteDeniedException;
import company.pluginName.Exceptions.ProtectionBlocks.Delete.ProtectionBlocksDeleteException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveDeniedException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveIdInUseException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveIdNullException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveItemNotAllowedException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveItemNullException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Setter(lombok.AccessLevel.NONE)
public class ProtectionBlock {

	private @Setter String id;
	private ItemStack item;
	private @Setter int blocksX;
	private @Setter int blocksY;
	private @Setter int blocksZ;
	private @Setter String permission;

	public ProtectionBlock(ProtectionBlock protectionBlock) {
		this.id = protectionBlock.id;
		this.copy(protectionBlock);
	}

	public void setItem(ItemStack item) throws ProtectionBlocksSaveException {
		if (item != null) {
			if (item.getType() == Material.AIR || (!item.getType().isBlock() && item.getType() != Material.PAPER)) {
				throw new ProtectionBlocksSaveItemNotAllowedException();
			}

			item.setAmount(1);
			this.item = item;
		} else {
			this.item = null;
		}
	}

	public ItemStack generateItem() {
		return ItemStacksUtils.setData(item.clone(), "ProtectionBlockId", id);
	}

	public void save() throws ProtectionBlocksSaveException {
		this.save(null);
	}

	public void save(Player player) throws ProtectionBlocksSaveException {
		if (player != null) {
			if (!player.hasPermission(Permissions.PROTECTION_BLOCKS_CREATE)) {
				throw new ProtectionBlocksSaveDeniedException();
			}
		}

		if (id == null) {
			throw new ProtectionBlocksSaveIdNullException();
		}

		ProtectionBlock registeredProtectionBlock = MainPluginClass.getPlugin().getProtectionsModule()
				.getProtectionBlockById(id.toLowerCase());

		if (registeredProtectionBlock != null && registeredProtectionBlock != this) {
			throw new ProtectionBlocksSaveIdInUseException();
		}

		if (item == null) {
			throw new ProtectionBlocksSaveItemNullException();
		}

		MainPluginClass.getPlugin().getSqlModule().saveProtectionBlock(this);
		MainPluginClass.getPlugin().getProtectionsModule().registerProtectionBlock(this);
	}

	public void delete() throws ProtectionBlocksDeleteException {
		this.delete(null);
	}

	public void delete(Player pl) throws ProtectionBlocksDeleteException {
		if (pl != null) {
			if (!pl.hasPermission(Permissions.PROTECTION_BLOCKS_DELETE)) {
				throw new ProtectionBlocksDeleteDeniedException();
			}
		}

		MainPluginClass.getPlugin().getSqlModule().deleteProtectionBlock(this);
		MainPluginClass.getPlugin().getProtectionsModule().unregisterProtectionBlock(this);
	}

	public void copy(ProtectionBlock protectionBlock) {
		this.item = protectionBlock.item;
		this.blocksX = protectionBlock.blocksX;
		this.blocksY = protectionBlock.blocksY;
		this.blocksZ = protectionBlock.blocksZ;
		this.permission = protectionBlock.permission;
	}

}

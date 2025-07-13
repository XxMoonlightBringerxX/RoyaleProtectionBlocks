package company.pluginName.Hooks.NexoAPI.Hook;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;

public class NexoHook extends PandaAbstractHook {

	@Override
	public void load() throws Throwable {
		try {
			Class.forName("com.nexomc.nexo.api.NexoItems");

			this.hooked = true;
		} catch (Exception e) {
			if (e instanceof ClassNotFoundException) {
				throw new ClassNotFoundException("Unable to find Nexo data");
			}
			throw e;
		}
	}

	@Override
	public void unload() throws Throwable {
	}

	public CheckingResult isCustomBlock(ItemStack item) {
		if (isHooked()) {
			return NexoItems.idFromItem(item) != null ? CheckingResult.IS_CUSTOM_ITEM : CheckingResult.NOT_CUSTOM_ITEM;
		}
		return CheckingResult.NOT_HOOKED;
	}

	public ComparativeResult isSame(Block block, ItemStack protectionBlock) {
		if (isHooked()) {
			String itemId = NexoItems.idFromItem(protectionBlock);

			if (itemId != null) {
				CustomBlockMechanic customBlock = NexoBlocks.customBlockMechanic(block);
				if (customBlock != null && customBlock.getItemID().equals(itemId)) {
					return ComparativeResult.SAME;
				}
				return ComparativeResult.NOT_CUSTOM_BLOCK;
			}
			return ComparativeResult.NOT_CUSTOM_ITEM;
		}
		return ComparativeResult.NOT_HOOKED;
	}

	public ComparativeResult isSame(ItemStack item, ItemStack protectionBlock) {
		if (isHooked()) {
			String itemId = NexoItems.idFromItem(item);

			if (itemId != null) {
				String oraxenItemId = NexoItems.idFromItem(protectionBlock);
				if (oraxenItemId != null && oraxenItemId.equals(itemId)) {
					return ComparativeResult.SAME;
				}
				return ComparativeResult.NOT_CUSTOM_BLOCK;
			}
			return ComparativeResult.NOT_CUSTOM_ITEM;
		}
		return ComparativeResult.NOT_HOOKED;
	}

	public PlaceResult setBlock(ItemStack item, Location loc) {
		if (isHooked()) {
			if (item != null) {
				String itemId = NexoItems.idFromItem(item);

				if (itemId != null) {
					NexoBlocks.place(itemId, loc);
					return PlaceResult.PLACED;
				}
				return PlaceResult.NOT_CUSTOM_ITEM;
			} else {
				if (NexoBlocks.remove(loc, null)) {
					return PlaceResult.PLACED;
				}
				return PlaceResult.NOT_CUSTOM_BLOCK;
			}
		}
		return PlaceResult.NOT_HOOKED;
	}

	public static enum CheckingResult {
		NOT_HOOKED, NOT_CUSTOM_ITEM, IS_CUSTOM_ITEM
	}

	public static enum ComparativeResult {
		NOT_HOOKED, NOT_CUSTOM_ITEM, NOT_CUSTOM_BLOCK, SAME
	}

	public static enum PlaceResult {
		NOT_HOOKED, NOT_CUSTOM_ITEM, NOT_CUSTOM_BLOCK, PLACED
	}

}

package company.pluginName.APIs.ItemsAdderAPI.Hook;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import dev.lone.itemsadder.api.CustomBlock;

public class ItemsAdderHook extends PandaAbstractHook {

	public void load() throws Throwable {
		Class.forName("dev.lone.itemsadder.api.CustomBlock");

		this.hooked = true;
	}

	public void unload() throws Throwable {
	}

	public CheckingResult isCustomBlock(ItemStack item) {
		if (isHooked()) {
			return CustomBlock.byItemStack(item) != null ? CheckingResult.IS_CUSTOM_ITEM
					: CheckingResult.NOT_CUSTOM_ITEM;
		}
		return CheckingResult.NOT_HOOKED;
	}

	public ComparativeResult isSame(ItemStack item, ItemStack protectionBlock) {
		if (isHooked()) {
			CustomBlock customStack = CustomBlock.byItemStack(protectionBlock);

			if (customStack != null) {
				CustomBlock customBlock = CustomBlock.byItemStack(item);
				if (customBlock != null && customBlock.getId().equals(customStack.getId())) {
					return ComparativeResult.SAME;
				}
				return ComparativeResult.NOT_CUSTOM_BLOCK;
			}
			return ComparativeResult.NOT_CUSTOM_ITEM;
		}
		return ComparativeResult.NOT_HOOKED;
	}

	public ComparativeResult isSame(Block block, ItemStack protectionBlock) {
		if (isHooked()) {
			CustomBlock customStack = CustomBlock.byItemStack(protectionBlock);

			if (customStack != null) {
				CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
				if (customBlock != null && customBlock.getId().equals(customStack.getId())) {
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
				CustomBlock customStack = CustomBlock.byItemStack(item);

				if (customStack != null) {
					customStack.place(loc);
					return PlaceResult.PLACED;
				}
				return PlaceResult.NOT_CUSTOM_ITEM;
			} else {
				CustomBlock customBlock = CustomBlock.byAlreadyPlaced(loc.getBlock());

				if (customBlock != null) {
					customBlock.remove();
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

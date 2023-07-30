package company.pluginName.APIs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import dev.lone.itemsadder.api.CustomBlock;
import relampagorojo93.LibsCollection.Utils.Bukkit.APIs.AbstractAPI;

public class ItemsAdderAPI extends AbstractAPI {

	public ItemsAdderAPI() {
		super(MainPluginClass.getPlugin());
		try {
			MessageBuilder.createMessage(getPrefix().concat("<ItemsAdder> Finding ItemsAdder."))
					.sendMessage(Bukkit.getConsoleSender());

			Class.forName("dev.lone.itemsadder.api.CustomBlock");

			this.hooked = true;

			MessageBuilder.createMessage(getPrefix().concat("<ItemsAdder> Done!."))
					.sendMessage(Bukkit.getConsoleSender());
		} catch (Exception e) {
			MessageBuilder
					.createMessage(getPrefix().concat("<ItemsAdder> ItemsAdder could not be loaded. ")
							.concat(isOptional() ? "Ignoring its implementation." : ""))
					.sendMessage(Bukkit.getConsoleSender());
			if (!isOptional()) {
				e.printStackTrace();
			}
		}
	}

	public BlockCheckingResult isCustomBlock(ItemStack item) {
		if (isHooked()) {
			return CustomBlock.byItemStack(item) != null ? BlockCheckingResult.IS_CUSTOM_ITEM
					: BlockCheckingResult.NOT_CUSTOM_ITEM;
		}
		return BlockCheckingResult.NOT_HOOKED;
	}

	public BlockComparativeResult isSame(Block block, ItemStack item) {
		if (isHooked()) {
			CustomBlock customStack = CustomBlock.byItemStack(item);

			if (customStack != null) {
				CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
				if (customBlock != null && customBlock.getId().equals(customStack.getId())) {
					return BlockComparativeResult.SAME;
				}
				return BlockComparativeResult.NOT_CUSTOM_BLOCK;
			}
			return BlockComparativeResult.NOT_CUSTOM_ITEM;
		}
		return BlockComparativeResult.NOT_HOOKED;
	}

	public PlaceBlockResult setBlock(ItemStack item, Location loc) {
		if (isHooked()) {
			if (item != null) {
				CustomBlock customStack = CustomBlock.byItemStack(item);

				if (customStack != null) {
					customStack.place(loc);
					return PlaceBlockResult.PLACED;
				}
				return PlaceBlockResult.NOT_CUSTOM_ITEM;
			} else {
				CustomBlock customBlock = CustomBlock.byAlreadyPlaced(loc.getBlock());

				if (customBlock != null) {
					customBlock.remove();
					return PlaceBlockResult.PLACED;
				}
				return PlaceBlockResult.NOT_CUSTOM_BLOCK;
			}
		}
		return PlaceBlockResult.NOT_HOOKED;
	}

	@Override
	public String getPrefix() {
		return MainPluginClass.getPlugin().getPrefix();
	}

	@Override
	public boolean isOptional() {
		return true;
	}

	public static enum BlockCheckingResult {
		NOT_HOOKED, NOT_CUSTOM_ITEM, IS_CUSTOM_ITEM
	}

	public static enum BlockComparativeResult {
		NOT_HOOKED, NOT_CUSTOM_ITEM, NOT_CUSTOM_BLOCK, SAME
	}

	public static enum PlaceBlockResult {
		NOT_HOOKED, NOT_CUSTOM_ITEM, NOT_CUSTOM_BLOCK, PLACED
	}

}

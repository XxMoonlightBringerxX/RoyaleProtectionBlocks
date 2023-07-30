package company.pluginName.APIs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.mechanics.Mechanic;
import relampagorojo93.LibsCollection.Utils.Bukkit.APIs.AbstractAPI;

public class OraxenAPI extends AbstractAPI {

	public OraxenAPI() {
		super(MainPluginClass.getPlugin());
		try {
			MessageBuilder.createMessage(getPrefix().concat("<Oraxen> Finding Oraxen."))
					.sendMessage(Bukkit.getConsoleSender());

			Class.forName("io.th0rgal.oraxen.api.OraxenItems");

			this.hooked = true;

			MessageBuilder.createMessage(getPrefix().concat("<Oraxen> Done!.")).sendMessage(Bukkit.getConsoleSender());
		} catch (Exception e) {
			MessageBuilder
					.createMessage(getPrefix().concat("<Oraxen> Oraxen could not be loaded. ")
							.concat(isOptional() ? "Ignoring its implementation." : ""))
					.sendMessage(Bukkit.getConsoleSender());
			if (!isOptional()) {
				e.printStackTrace();
			}
		}
	}

	public BlockCheckingResult isCustomBlock(ItemStack item) {
		if (isHooked()) {
			return OraxenItems.getIdByItem(item) != null ? BlockCheckingResult.IS_CUSTOM_ITEM
					: BlockCheckingResult.NOT_CUSTOM_ITEM;
		}
		return BlockCheckingResult.NOT_HOOKED;
	}

	public BlockComparativeResult isSame(Block block, ItemStack item) {
		if (isHooked()) {
			String itemId = OraxenItems.getIdByItem(item);

			if (itemId != null) {
				Mechanic customBlock = OraxenBlocks.getOraxenBlock(block.getLocation());
				if (customBlock != null && customBlock.getItemID().equals(itemId)) {
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
				String itemId = OraxenItems.getIdByItem(item);

				if (itemId != null) {
					OraxenBlocks.place(itemId, loc);
					return PlaceBlockResult.PLACED;
				}
				return PlaceBlockResult.NOT_CUSTOM_ITEM;
			} else {
				if (OraxenBlocks.remove(loc, null)) {
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

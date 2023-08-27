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
					.createMessage(getPrefix().concat("<Oraxen> ItemsAdder could not be loaded. ")
							.concat(isOptional() ? "Ignoring its implementation." : ""))
					.sendMessage(Bukkit.getConsoleSender());
			if (!isOptional()) {
				e.printStackTrace();
			}
		}
	}

	public CheckingResult isCustomBlock(ItemStack item) {
		if (isHooked()) {
			return OraxenItems.getIdByItem(item) != null ? CheckingResult.IS_CUSTOM_ITEM
					: CheckingResult.NOT_CUSTOM_ITEM;
		}
		return CheckingResult.NOT_HOOKED;
	}

	public ComparativeResult isSame(Block block, ItemStack protectionBlock) {
		if (isHooked()) {
			String itemId = OraxenItems.getIdByItem(protectionBlock);

			if (itemId != null) {
				Mechanic customBlock = OraxenBlocks.getOraxenBlock(block.getLocation());
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
			String itemId = OraxenItems.getIdByItem(item);

			if (itemId != null) {
				String oraxenItemId = OraxenItems.getIdByItem(protectionBlock);
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
				String itemId = OraxenItems.getIdByItem(item);

				if (itemId != null) {
					OraxenBlocks.place(itemId, loc);
					return PlaceResult.PLACED;
				}
				return PlaceResult.NOT_CUSTOM_ITEM;
			} else {
				if (OraxenBlocks.remove(loc, null)) {
					return PlaceResult.PLACED;
				}
				return PlaceResult.NOT_CUSTOM_BLOCK;
			}
		}
		return PlaceResult.NOT_HOOKED;
	}

	@Override
	public String getPrefix() {
		return MainPluginClass.getPlugin().getPrefix();
	}

	@Override
	public boolean isOptional() {
		return true;
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

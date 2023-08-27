package company.pluginName.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import company.pluginName.MainPluginClass;
import company.pluginName.APIs.ItemsAdderAPI;
import company.pluginName.APIs.OraxenAPI;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionBlocks.ProtectionBlockAllowedWorlds;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionBlocks.ProtectionBlockInformation;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import lombok.Data;
import lombok.Getter;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

@Data
@Getter(lombok.AccessLevel.NONE)
public class ProtectionBlocksUtils {

	private static final String BLOCKSX_SECTION = "Blocks-x";
	private static final String BLOCKSY_SECTION = "Blocks-y";
	private static final String BLOCKSZ_SECTION = "Blocks-z";
	private static final String PERMISSION_SECTION = "Permission";
	private static final String ITEM_TYPE_SECTION = "Item.Type";
	private static final String ITEM_NAME_SECTION = "Item.Name";
	private static final String ITEM_LORE_SECTION = "Item.Lore";
	private static final String ITEM_SKIN_SECTION = "Item.Skin";
	private static final String ITEM_CUSTOMMODELDATA_SECTION = "Item.Custom-model-data";
	private static final String ALLOWEDWORLDS_SECTION = "Allowed-worlds";

	public static enum ItemType {
		VANILLA, ORAXEN, ITEMS_ADDER;
	}

	public static ItemType getItemType(ItemStack item) {
		if (MainPluginClass.getItemsAdderAPI().isCustomBlock(item) == ItemsAdderAPI.CheckingResult.IS_CUSTOM_ITEM) {
			return ItemType.ITEMS_ADDER;
		} else if (MainPluginClass.getOraxenAPI().isCustomBlock(item) == OraxenAPI.CheckingResult.IS_CUSTOM_ITEM) {
			return ItemType.ORAXEN;
		} else {
			return ItemType.VANILLA;
		}
	}

	public static boolean isSameType(ItemStack protectionBlock, ItemStack item) {
		switch (getItemType(protectionBlock)) {
		case ITEMS_ADDER:
			return MainPluginClass.getItemsAdderAPI().isSame(item,
					protectionBlock) == ItemsAdderAPI.ComparativeResult.SAME;
		case ORAXEN:
			return MainPluginClass.getOraxenAPI().isSame(item, protectionBlock) == OraxenAPI.ComparativeResult.SAME;
		case VANILLA:
			if (item.getType() == protectionBlock.getType()) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSameType(ItemStack protectionBlock, Block block) {
		switch (getItemType(protectionBlock)) {
		case ITEMS_ADDER:
			return MainPluginClass.getItemsAdderAPI().isSame(block,
					protectionBlock) == ItemsAdderAPI.ComparativeResult.SAME;
		case ORAXEN:
			return MainPluginClass.getOraxenAPI().isSame(block, protectionBlock) == OraxenAPI.ComparativeResult.SAME;
		case VANILLA:
			if (block.getType() == protectionBlock.getType()) {
				return true;
			}

			if (block.getType() == Material.PLAYER_WALL_HEAD.getMaterial()
					&& protectionBlock.getType() == Material.PLAYER_HEAD.getMaterial()) {
				return true;
			}
		}
		return false;
	}

	public static Map<String, Object> protectionBlockToMap(ProtectionBlock protectionBlock) {
		HashMap<String, Object> values = new LinkedHashMap<>();

		Material material = Material.getByBukkitMaterial(protectionBlock.getInformation().getItem().getType());
		ItemMeta im = protectionBlock.getInformation().getItem().getItemMeta();
		String skin = material == Material.PLAYER_HEAD
				? ItemStacksUtils.getSkin(protectionBlock.getInformation().getItem())
				: null;

		values.put(BLOCKSX_SECTION, protectionBlock.getInformation().getBlocksX());
		values.put(BLOCKSY_SECTION, protectionBlock.getInformation().getBlocksY());
		values.put(BLOCKSZ_SECTION, protectionBlock.getInformation().getBlocksZ());
		values.put(PERMISSION_SECTION, protectionBlock.getInformation().getPermission());
		values.put(ITEM_TYPE_SECTION, material != null ? material.name() : null);
		values.put(ITEM_NAME_SECTION, im.hasDisplayName() ? im.getDisplayName() : null);
		values.put(ITEM_LORE_SECTION, im.hasLore() ? im.getLore() : null);
		values.put(ITEM_SKIN_SECTION, skin);
		values.put(ALLOWEDWORLDS_SECTION, new ArrayList<>(protectionBlock.getAllowedWorlds().get()));

		return values;
	}

	@SuppressWarnings("unchecked")
	public static ProtectionBlock mapToProtectionBlock(String id, Map<String, Object> map) throws Exception {
		for (String section : new String[] { BLOCKSX_SECTION, BLOCKSY_SECTION, BLOCKSZ_SECTION, ITEM_TYPE_SECTION }) {
			if (!map.containsKey(section)) {
				throw new Exception(
						"The section '%s' must be present on the protection block configuration.".formatted(section));
			}
		}

		int blocksX;
		int blocksY;
		int blocksZ;
		String permission;
		Material material;
		String name;
		String skin;
		List<String> lore;
		List<String> allowedWorlds;

		try {
			blocksX = (int) Long.parseLong(map.get(BLOCKSX_SECTION).toString());
		} catch (NumberFormatException e) {
			throw new Exception("The value '%s' is currently not a number.".formatted(map.get(BLOCKSX_SECTION)));
		}

		try {
			blocksY = (int) Long.parseLong(map.get(BLOCKSY_SECTION).toString());
		} catch (NumberFormatException e) {
			throw new Exception("The value '%s' is currently not a number.".formatted(map.get(BLOCKSY_SECTION)));
		}

		try {
			blocksZ = (int) Long.parseLong(map.get(BLOCKSZ_SECTION).toString());
		} catch (NumberFormatException e) {
			throw new Exception("The value '%s' is currently not a number.".formatted(map.get(BLOCKSZ_SECTION)));
		}

		try {
			permission = map.containsKey(PERMISSION_SECTION) ? (String) map.get(PERMISSION_SECTION) : null;
		} catch (ClassCastException e) {
			throw new Exception("The value '%s' is currently not a string.".formatted(map.get(PERMISSION_SECTION)));
		}

		try {
			material = Material.valueOf((String) map.get(ITEM_TYPE_SECTION));
		} catch (IllegalArgumentException e) {
			throw new Exception(
					"The value '%s' is not a valid type of material.".formatted(map.get(ITEM_TYPE_SECTION)));
		}

		try {
			name = map.containsKey(ITEM_NAME_SECTION) ? (String) map.get(ITEM_NAME_SECTION) : null;
		} catch (ClassCastException e) {
			throw new Exception("The value '%s' is currently not a string.".formatted(map.get(ITEM_NAME_SECTION)));
		}

		try {
			skin = map.containsKey(ITEM_SKIN_SECTION) ? (String) map.get(ITEM_SKIN_SECTION) : null;
		} catch (ClassCastException e) {
			throw new Exception("The value '%s' is currently not a string.".formatted(map.get(ITEM_SKIN_SECTION)));
		}

		try {
			lore = map.containsKey(ITEM_LORE_SECTION) ? (List<String>) map.get(ITEM_LORE_SECTION) : null;
		} catch (ClassCastException e) {
			throw new Exception("The value '%s' is currently not a string list.".formatted(map.get(ITEM_LORE_SECTION)));
		}

		try {
			allowedWorlds = map.containsKey(ALLOWEDWORLDS_SECTION) ? (List<String>) map.get(ALLOWEDWORLDS_SECTION)
					: null;
		} catch (ClassCastException e) {
			throw new Exception(
					"The value '%s' is currently not a string list.".formatted(map.get(ALLOWEDWORLDS_SECTION)));
		}

		ProtectionBlock protectionBlock = MainPluginClass.getPlugin().getProtectionsModule().getProtectionBlockById(id);

		if (protectionBlock != null) {
			protectionBlock = new ProtectionBlock(protectionBlock);

			protectionBlock.getInformation().setBlocksX(blocksX);
			protectionBlock.getInformation().setBlocksY(blocksY);
			protectionBlock.getInformation().setBlocksZ(blocksZ);

			if (map.containsKey(PERMISSION_SECTION)) {
				protectionBlock.getInformation().setPermission(permission);
			}

			if (map.containsKey(ALLOWEDWORLDS_SECTION)) {
				protectionBlock.getAllowedWorlds().clear();
				allowedWorlds.forEach(protectionBlock.getAllowedWorlds()::add);
			}

			ItemStack item = protectionBlock.getInformation().getItem().clone();

			if (map.containsKey(ITEM_SKIN_SECTION) && item.getType() == Material.PLAYER_HEAD.getMaterial()) {
				item = ItemStacksUtils.setSkin(item, skin);
			}

			ItemMeta im = item.getItemMeta();

			if (map.containsKey(ITEM_NAME_SECTION) && name != null) {
				im.setDisplayName(MessageBuilder.createMessage(name).toString());
			}

			if (map.containsKey(ITEM_LORE_SECTION) && lore != null) {
				im.setLore(MessageBuilder.createMessage(lore).getStrings());
			}

			item.setItemMeta(im);

			protectionBlock.getInformation().setItem(item);

			return protectionBlock;
		} else {
			ItemStack item = ItemStacksUtils.createItemStack(material,
					name != null ? MessageBuilder.createMessage(name).toString() : null,
					lore != null ? MessageBuilder.createMessage(lore).getStrings() : null);

			if (skin != null && !skin.isEmpty() && item.getType() == Material.PLAYER_HEAD.getMaterial()) {
				item = ItemStacksUtils.setSkin(item, skin);
			}

			return new ProtectionBlock(new ProtectionBlockInformation(id, item, blocksX, blocksY, blocksZ, permission),
					allowedWorlds != null ? new ProtectionBlockAllowedWorlds(new HashSet<>(allowedWorlds))
							: new ProtectionBlockAllowedWorlds());
		}
	}

}

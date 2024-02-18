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

import company.pluginName.APIs.ItemsAdderAPI.ItemsAdderAPI;
import company.pluginName.APIs.ItemsAdderAPI.Hook.ItemsAdderHook;
import company.pluginName.APIs.OraxenAPI.OraxenAPI;
import company.pluginName.APIs.OraxenAPI.Hook.OraxenHook;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Components.ProtectionBlockAllowedWorlds;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Components.ProtectionBlockInformation;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.SkinUtilities;
import lombok.Data;
import lombok.Getter;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

@Data
@Getter(lombok.AccessLevel.NONE)
public class ProtectionBlocksUtils {

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private static ItemsAdderAPI itemsAdderApi;

	@PandaInject
	private static OraxenAPI oraxenApi;

	private static final String BLOCKSX_SECTION = "Blocks-x";
	private static final String BLOCKSY_SECTION = "Blocks-y";
	private static final String BLOCKSZ_SECTION = "Blocks-z";
	private static final String PERMISSION_SECTION = "Permission";
	private static final String PRICE_SECTION = "Price";
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
		if (itemsAdderApi.getHook().isCustomBlock(item) == ItemsAdderHook.CheckingResult.IS_CUSTOM_ITEM) {
			return ItemType.ITEMS_ADDER;
		} else if (oraxenApi.getHook().isCustomBlock(item) == OraxenHook.CheckingResult.IS_CUSTOM_ITEM) {
			return ItemType.ORAXEN;
		} else {
			return ItemType.VANILLA;
		}
	}

	public static boolean isSameType(ItemStack protectionBlock, ItemStack item) {
		switch (getItemType(protectionBlock)) {
		case ITEMS_ADDER:
			return itemsAdderApi.getHook().isSame(item, protectionBlock) == ItemsAdderHook.ComparativeResult.SAME;
		case ORAXEN:
			return oraxenApi.getHook().isSame(item, protectionBlock) == OraxenHook.ComparativeResult.SAME;
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
			return itemsAdderApi.getHook().isSame(block, protectionBlock) == ItemsAdderHook.ComparativeResult.SAME;
		case ORAXEN:
			return oraxenApi.getHook().isSame(block, protectionBlock) == OraxenHook.ComparativeResult.SAME;
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
				? SkinUtilities.NMS.getSkinAsBase64(protectionBlock.getInformation().getItem())
				: null;

		values.put(BLOCKSX_SECTION, protectionBlock.getInformation().getBlocksX());
		values.put(BLOCKSY_SECTION, protectionBlock.getInformation().getBlocksY());
		values.put(BLOCKSZ_SECTION, protectionBlock.getInformation().getBlocksZ());
		values.put(PERMISSION_SECTION, protectionBlock.getInformation().getPermission());
		values.put(PRICE_SECTION, protectionBlock.getInformation().getPrice());
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
		Double price;
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
			permission = map.containsKey(PERMISSION_SECTION) && map.get(PERMISSION_SECTION) != null
					? (String) map.get(PERMISSION_SECTION)
					: null;
		} catch (ClassCastException e) {
			throw new Exception("The value '%s' is currently not a string.".formatted(map.get(PERMISSION_SECTION)));
		}

		try {
			price = map.containsKey(PRICE_SECTION) && map.get(PRICE_SECTION) != null
					? Double.parseDouble(map.get(PRICE_SECTION).toString())
					: null;
		} catch (ClassCastException e) {
			throw new Exception("The value '%s' is currently not a decimal.".formatted(map.get(PRICE_SECTION)));
		}

		try {
			material = Material.valueOf((String) map.get(ITEM_TYPE_SECTION));
		} catch (IllegalArgumentException e) {
			throw new Exception(
					"The value '%s' is not a valid type of material.".formatted(map.get(ITEM_TYPE_SECTION)));
		}

		try {
			name = map.containsKey(ITEM_NAME_SECTION) && map.get(ITEM_NAME_SECTION) != null
					? (String) map.get(ITEM_NAME_SECTION)
					: null;
		} catch (ClassCastException e) {
			throw new Exception("The value '%s' is currently not a string.".formatted(map.get(ITEM_NAME_SECTION)));
		}

		try {
			skin = map.containsKey(ITEM_SKIN_SECTION) && map.get(ITEM_SKIN_SECTION) != null
					? (String) map.get(ITEM_SKIN_SECTION)
					: null;
		} catch (ClassCastException e) {
			throw new Exception("The value '%s' is currently not a string.".formatted(map.get(ITEM_SKIN_SECTION)));
		}

		try {
			lore = map.containsKey(ITEM_LORE_SECTION) && map.get(ITEM_LORE_SECTION) != null
					? (List<String>) map.get(ITEM_LORE_SECTION)
					: null;
		} catch (ClassCastException e) {
			throw new Exception("The value '%s' is currently not a string list.".formatted(map.get(ITEM_LORE_SECTION)));
		}

		try {
			allowedWorlds = map.containsKey(ALLOWEDWORLDS_SECTION) && map.get(ALLOWEDWORLDS_SECTION) != null
					? (List<String>) map.get(ALLOWEDWORLDS_SECTION)
					: null;
		} catch (ClassCastException e) {
			throw new Exception(
					"The value '%s' is currently not a string list.".formatted(map.get(ALLOWEDWORLDS_SECTION)));
		}

		ProtectionBlock protectionBlock = protectionBlocksService.getProtectionBlockById(id);

		if (protectionBlock != null) {
			protectionBlock = new ProtectionBlock(protectionBlock);

			protectionBlock.getInformation().setBlocksX(blocksX);
			protectionBlock.getInformation().setBlocksY(blocksY);
			protectionBlock.getInformation().setBlocksZ(blocksZ);

			if (map.containsKey(PERMISSION_SECTION)) {
				protectionBlock.getInformation().setPermission(permission);
			}

			if (map.containsKey(PRICE_SECTION)) {
				if (price != null && price <= 0D) {
					protectionBlock.getInformation().setPrice(null);
				} else {
					protectionBlock.getInformation().setPrice(price);
				}
			}

			if (map.containsKey(ALLOWEDWORLDS_SECTION)) {
				protectionBlock.getAllowedWorlds().clear();
				allowedWorlds.forEach(protectionBlock.getAllowedWorlds()::add);
			}

			ItemStack item = protectionBlock.getInformation().getItem().clone();

			if (map.containsKey(ITEM_SKIN_SECTION) && item.getType() == Material.PLAYER_HEAD.getMaterial()) {
				item = SkinUtilities.NMS.setSkinSafe(item, skin);
			}

			ItemMeta im = item.getItemMeta();

			if (map.containsKey(ITEM_NAME_SECTION) && name != null) {
				im.setDisplayName(MessageTemplate.inst(name).toString());
			}

			if (map.containsKey(ITEM_LORE_SECTION) && lore != null) {
				im.setLore(MessageTemplate.inst(lore).getStrings());
			}

			item.setItemMeta(im);

			protectionBlock.getInformation().setItem(item);

			return protectionBlock;
		} else {
			ItemStack item = ItemStacksUtils.createItemStack(material,
					name != null ? MessageTemplate.inst(name).toString() : null,
					lore != null ? MessageTemplate.inst(lore).getStrings() : null);

			if (skin != null && !skin.isEmpty() && item.getType() == Material.PLAYER_HEAD.getMaterial()) {
				item = SkinUtilities.NMS.setSkinSafe(item, skin);
			}

			return new ProtectionBlock(
					new ProtectionBlockInformation(id, item, blocksX, blocksY, blocksZ, permission, price),
					allowedWorlds != null ? new ProtectionBlockAllowedWorlds(new HashSet<>(allowedWorlds))
							: new ProtectionBlockAllowedWorlds());
		}
	}

}

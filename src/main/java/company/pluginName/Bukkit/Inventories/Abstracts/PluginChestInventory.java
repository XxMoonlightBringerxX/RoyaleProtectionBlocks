package company.pluginName.Bukkit.Inventories.Abstracts;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.ProtectionBlocks.ProtectionBlockManagerInventory;
import company.pluginName.Bukkit.Inventories.ProtectionBlocks.BannedWorlds.ProtectionBlockAllowedWorldsInventory;
import company.pluginName.Bukkit.Inventories.Protections.ProtectionsListInventory;
import company.pluginName.Bukkit.Inventories.Protections.Banneds.ProtectionBannedsInventory;
import company.pluginName.Bukkit.Inventories.Protections.Members.ProtectionMembersInventory;
import company.pluginName.Bukkit.Inventories.Protections.Owners.ProtectionOwnersInventory;
import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Bukkit.Inventories.Shared.SearchPlayersInventory;
import company.pluginName.Bukkit.Inventories.Shared.SearchWorldsInventory;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.SkinUtilities;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.ChestInventory;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Item;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

public abstract class PluginChestInventory extends ChestInventory {

	public static final String LEFT_ARROW_SKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==";
	public static final String RIGHT_ARROW_SKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19";
	public static final String CONFIRM_SKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=";
	public static final String CANCEL_SKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==";
	public static final String CLOSE_SKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTljZGI5YWYzOGNmNDFkYWE1M2JjOGNkYTc2NjVjNTA5NjMyZDE0ZTY3OGYwZjE5ZjI2M2Y0NmU1NDFkOGEzMCJ9fX0=";
	public static final String SEARCH_SKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmMzNWU4Njg0YzdmNzc2YmVmZWRjNDMxOWQwODE0OGM1NGJlYTM5MzIxZTFiZDVkZWY3YTU1Yjg5ZmRhYTA5OSJ9fX0=";
	public static final String PLUS_SKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMGZjNmRjZjczOWMxMWZlY2U0M2NkZDE4NGRlYTc5MWNmNzU3YmY3YmQ5MTUzNmZkYmM5NmZhNDdhY2ZiIn19fQ==";
	public static final String X_LETTER_SKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzkxZDZlZGE4M2VkMmMyNGRjZGNjYjFlMzNkZjM2OTRlZWUzOTdhNTcwMTIyNTViZmM1NmEzYzI0NGJjYzQ3NCJ9fX0=";
	public static final String Y_LETTER_SKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODlmZjhjNzQ0OTUwNzI5ZjU4Y2I0ZTY2ZGM2OGVhZjYyZDAxMDZmOGE1MzE1MjkxMzNiZWQxZDU1ZTMifX19";
	public static final String Z_LETTER_SKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzA1ZjE4ZDQxNmY2OGU5YmQxOWQ1NWRmOWZhNzQyZWRmYmYxYTUyNWM4ZTI5ZjY1OWFlODMzYWYyMTdkNTM1In19fQ==";
	public static final String UNKNOWN_SKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcwNWZkOTRhMGM0MzE5MjdmYjRlNjM5YjBmY2ZiNDk3MTdlNDEyMjg1YTAyYjQzOWUwMTEyZGEyMmIyZTJlYyJ9fX0=";
	public static final String TRASH_SKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDQxZjRlZGJjNjhjOTA2MTM1NTI0MmJkNzNlZmZjOTI5OWEzMjUyYjlmMTFlODJiNWYxZWM3YjNiNmFjMCJ9fX0=";
	public static final String WORLD_SKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0=";

	protected static ItemStack LEFT_ARROW_ITEM;
	protected static ItemStack RIGHT_ARROW_ITEM;
	protected static ItemStack CLOSE_ITEM;
	protected static ItemStack CONFIRM_ITEM;
	protected static ItemStack CANCEL_ITEM;
	protected static Item GRAY_STAINED_GLASS_PANE;

	public static void initItems() {
		LEFT_ARROW_ITEM = ItemStacksUtils.createItemStack(
				SkinUtilities.NMS.setSkinSafe(Material.PLAYER_HEAD.getItemStack(), LEFT_ARROW_SKIN),
				MessageBuilder.createMessage(MessageString.INVENTORY_GENERAL_PREVIOUSPAGENAME.toString()).toString());

		RIGHT_ARROW_ITEM = ItemStacksUtils.createItemStack(
				SkinUtilities.NMS.setSkinSafe(Material.PLAYER_HEAD.getItemStack(), RIGHT_ARROW_SKIN),
				MessageBuilder.createMessage(MessageString.INVENTORY_GENERAL_NEXTPAGENAME.toString()).toString());

		CLOSE_ITEM = ItemStacksUtils.createItemStack(
				SkinUtilities.NMS.setSkinSafe(Material.PLAYER_HEAD.getItemStack(), CLOSE_SKIN),
				MessageBuilder.createMessage(MessageString.INVENTORY_GENERAL_CLOSENAME.toString()).toString());

		CONFIRM_ITEM = ItemStacksUtils.createItemStack(Material.LIME_STAINED_GLASS_PANE,
				MessageBuilder.createMessage(MessageString.INVENTORY_GENERAL_CONFIRMNAME.toString()).toString());

		CANCEL_ITEM = ItemStacksUtils.createItemStack(Material.RED_STAINED_GLASS_PANE,
				MessageBuilder.createMessage(MessageString.INVENTORY_GENERAL_CANCELNAME.toString()).toString());

		GRAY_STAINED_GLASS_PANE = new Item(ItemStacksUtils.createItemStack(Material.GRAY_STAINED_GLASS_PANE,
				MessageBuilder.createMessage("&0").toString()));

		ProtectionsListInventory.initItems();
		ProtectionMembersInventory.initItems();
		ProtectionOwnersInventory.initItems();
		ProtectionBannedsInventory.initItems();
		ConfirmationInventory.initItems();
		SearchPlayersInventory.initItems();
		SearchWorldsInventory.initItems();
		ProtectionBlockManagerInventory.initItems();
		ProtectionBlockAllowedWorldsInventory.initItems();
	}

	public PluginChestInventory(Player player) {
		super(player);
	}

	public PluginChestInventory(Player player, ItemStack[] background) {
		super(player);
		setBackground(background);
	}

	public PluginChestInventory(Player player, String name, int size) {
		super(player);
		setName(name);
		setSize(size);
	}

	public PluginChestInventory(Player player, String name, int size, ItemStack[] background) {
		this(player, name, size);
		setBackground(background);
	}

	public void openInventory() {
		this.openInventory(MainPluginClass.getPlugin());
	}

	public void updateInventory() {
		this.updateInventory(MainPluginClass.getPlugin());
	}

	public void closeInventory() {
		this.closeInventory(MainPluginClass.getPlugin());
	}

	public void goToPreviousHolder() {
		this.goToPreviousHolder(MainPluginClass.getPlugin());
	}

	@Override
	public void onClick(InventoryClickEvent e) {
		if (e.getClick() != ClickType.DOUBLE_CLICK) {
			super.onClick(e);
		}
	}
}

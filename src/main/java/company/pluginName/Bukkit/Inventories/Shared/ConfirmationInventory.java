package company.pluginName.Bukkit.Inventories.Shared;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.SkinUtilities;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

public class ConfirmationInventory extends PluginChestInventory {

	private static ItemStack YES_ITEM;
	private static ItemStack NO_ITEM;

	public static void initItems() {
		YES_ITEM = ItemStacksUtils.createItemStack(
				SkinUtilities.NMS.setSkinSafe(Material.PLAYER_HEAD.getItemStack(), CONFIRM_SKIN),
				MessageBuilder.createMessage(MessageString.INVENTORY_CONFIRM_YESNAME.toString()).toString());
		NO_ITEM = ItemStacksUtils.createItemStack(
				SkinUtilities.NMS.setSkinSafe(Material.PLAYER_HEAD.getItemStack(), CANCEL_SKIN),
				MessageBuilder.createMessage(MessageString.INVENTORY_CONFIRM_NONAME.toString()).toString());
	}

	public ConfirmationInventory(Player player, Action action) {
		super(player);

		setName(MessageBuilder.createMessage(MessageString.INVENTORY_CONFIRM_TITLE.toString()).toString());
		setSize(9);

		setSlot(3, new Button(YES_ITEM) {
			@Override
			public void onClick(InventoryClickEvent e) {
				if (action != null) {
					action.execute();
				}
				goToPreviousHolder();
			}
		});

		setSlot(5, new Button(NO_ITEM) {
			@Override
			public void onClick(InventoryClickEvent e) {
				goToPreviousHolder();
			}
		});
	}

	public static interface Action {
		void execute();
	}

}

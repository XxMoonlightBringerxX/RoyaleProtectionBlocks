package company.pluginName.Bukkit.Inventories.Shared;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

public class ConfirmationInventory extends PluginChestInventory {

	public ConfirmationInventory(Player player, Action action) {
		super(player);
		setName(MessageBuilder.createMessage(MessageString.INVENTORY_CONFIRM_TITLE.toString()).toString());
		setSize(9);
		ItemStack i = ItemStacksUtils.setSkin(Material.PLAYER_HEAD.getItemStack(), CONFIRM_SKIN);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(MessageBuilder.createMessage(MessageString.INVENTORY_CONFIRM_YESNAME.toString()).toString());
		i.setItemMeta(im);
		setSlot(3, new Button(i) {
			@Override
			public void onClick(InventoryClickEvent e) {
				if (action != null) {
					action.execute();
				}
				goToPreviousHolder();
			}
		});
		i = ItemStacksUtils.setSkin(Material.PLAYER_HEAD.getItemStack(), CANCEL_SKIN);
		im = i.getItemMeta();
		im.setDisplayName(MessageBuilder.createMessage(MessageString.INVENTORY_CONFIRM_NONAME.toString()).toString());
		i.setItemMeta(im);
		setSlot(5, new Button(i) {
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

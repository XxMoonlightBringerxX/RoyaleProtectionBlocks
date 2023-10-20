package company.pluginName.Bukkit.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.MainPluginClass.Debugger.MessageType;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.Utils.EventsUtils;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.ItemStackDataUtilities;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;

public class ItemsAdderEvents implements Listener {

	@EventHandler
	public void onCustomBlockPlaceEvent(CustomBlockPlaceEvent e) {
		MainPluginClass.Debugger.log(MessageType.ITEMSADDER_BLOCK_PLACE, () -> new Object[] { e.getPlayer().getName(),
				String.valueOf(e.getBlock().getX()), String.valueOf(e.getBlock().getY()), String.valueOf(e.getBlock().getZ()) });

		String protectionBlockId = null;

		try {

			protectionBlockId = ItemStackDataUtilities.getPersistentData(e.getItemInHand(), MainPluginClass.getPlugin(),
					ProtectionBlock.PROTECTION_BLOCK_ID_KEY, String.class);
		} catch (Exception ex) {
			return;
		}

		if (protectionBlockId != null) {
			ItemStack item = e.getCustomBlockItem().clone();

			try {
				ItemStackDataUtilities.setPersistentData(item, MainPluginClass.getPlugin(), ProtectionBlock.PROTECTION_BLOCK_ID_KEY,
						protectionBlockId);
			} catch (Exception ex) {
				return;
			}

			if (!e.isCancelled()) {
				switch (EventsUtils.onItemsAdderBlockPlaceEvent(e.getPlayer(), e.getBlock(), null, item)) {
				case CANCEL:
					e.setCancelled(true);
					break;
				default:
					break;
				}
			} else {
				MainPluginClass.Debugger.log(MessageType.BLOCK_PLACE_CANCELLED);
			}
		}
	}

	@EventHandler
	public void onCustomBlockInteractEvent(CustomBlockInteractEvent e) {
		Protection protection = MainPluginClass.getPlugin().getProtectionsModule().getProtectionByBlock(e.getBlockClicked().getLocation());
		if (protection != null) {
			e.setCancelled(true);
			EventsUtils.onItemsAdderBlockInteractEvent(e.getPlayer(), protection);
		}
	}

	@EventHandler
	public void onCustomBlockBreakEvent(CustomBlockBreakEvent e) {
		MainPluginClass.Debugger.log(MessageType.ITEMSADDER_BLOCK_BREAK, () -> new Object[] { e.getPlayer().getName(),
				String.valueOf(e.getBlock().getX()), String.valueOf(e.getBlock().getY()), String.valueOf(e.getBlock().getZ()) });

		if (!e.isCancelled()) {
			switch (EventsUtils.onItemsAdderBlockBreakEvent(e.getPlayer(), e.getBlock())) {
			case SUCCESS:
			case CANCEL:
				e.setCancelled(true);
				break;
			default:
				break;
			}
		} else {
			MainPluginClass.Debugger.log(MessageType.BLOCK_BREAK_CANCELLED);
		}
	}

}

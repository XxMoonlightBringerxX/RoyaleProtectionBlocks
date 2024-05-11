package company.pluginName.Bukkit.Events.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import company.pluginName.MainPluginClass;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Utils.EventsUtils;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.ItemStackDataUtilities;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;

@PandaListener(optional = true)
public class ItemsAdderListener implements Listener {

	@PandaInject
	private MainPluginClass plugin;

	@PandaInject
	private ProtectionsService protectionsService;

	@EventHandler
	public void onCustomBlockPlaceEvent(CustomBlockPlaceEvent e) {
		Debugger.log(MessageType.ITEMSADDER_BLOCK_PLACE,
				() -> new Object[] { e.getPlayer().getName(), String.valueOf(e.getBlock().getX()),
						String.valueOf(e.getBlock().getY()), String.valueOf(e.getBlock().getZ()) });

		String protectionBlockId = null;

		try {

			protectionBlockId = ItemStackDataUtilities.getPersistentData(e.getItemInHand(), plugin,
					ProtectionBlock.PROTECTION_BLOCK_ID_KEY, String.class);
		} catch (Exception ex) {
			return;
		}

		if (protectionBlockId != null) {
			ItemStack item = e.getCustomBlockItem().clone();

			try {
				ItemStackDataUtilities.setPersistentData(item, plugin, ProtectionBlock.PROTECTION_BLOCK_ID_KEY,
						protectionBlockId);
			} catch (Exception ex) {
				return;
			}

			if (!e.isCancelled()) {
				switch (EventsUtils.onItemsAdderBlockPlaceEvent(e.getPlayer(), e.getBlock(), null, item)) {
				case SUCCESS:
				case CANCEL:
					e.setCancelled(true);
					break;
				default:
					break;
				}
			} else {
				Debugger.log(MessageType.BLOCK_PLACE_CANCELLED);
			}
		}
	}

	@EventHandler
	public void onCustomBlockInteractEvent(CustomBlockInteractEvent e) {
		Protection protection = protectionsService.findProtectionBySourceBlock(e.getBlockClicked());
		if (protection != null) {
			e.setCancelled(true);
			EventsUtils.onItemsAdderBlockInteractEvent(e.getPlayer(), protection);
		}
	}

	@EventHandler
	public void onCustomBlockBreakEvent(CustomBlockBreakEvent e) {
		Debugger.log(MessageType.ITEMSADDER_BLOCK_BREAK,
				() -> new Object[] { e.getPlayer().getName(), String.valueOf(e.getBlock().getX()),
						String.valueOf(e.getBlock().getY()), String.valueOf(e.getBlock().getZ()) });

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
			Debugger.log(MessageType.BLOCK_BREAK_CANCELLED);
		}
	}

}

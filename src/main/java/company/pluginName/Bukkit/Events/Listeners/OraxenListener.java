package company.pluginName.Bukkit.Events.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Utils.EventsUtils;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockDamageEvent;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockInteractEvent;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockPlaceEvent;
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockBreakEvent;
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockDamageEvent;
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockInteractEvent;
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockPlaceEvent;

@PandaListener(optional = true)
public class OraxenListener implements Listener {

	@PandaInject
	private ProtectionsService protectionsService;

	@EventHandler
	public void onNoteBlockPlace(OraxenNoteBlockPlaceEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}

		Debugger.log(MessageType.ORAXEN_BLOCK_PLACE,
				() -> new Object[] { e.getPlayer().getName(), String.valueOf(e.getBlock().getX()),
						String.valueOf(e.getBlock().getY()), String.valueOf(e.getBlock().getZ()) });

		if (!e.isCancelled()) {
			switch (EventsUtils.onOraxenBlockPlaceEvent(e.getPlayer(), e.getBlock(), null, e.getItemInHand())) {
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

	@EventHandler
	public void onNoteBlockInteract(OraxenNoteBlockInteractEvent e) {
		Protection protection = protectionsService.findProtectionBySourceBlock(e.getBlock());
		if (protection != null) {
			e.setCancelled(true);
			EventsUtils.onOraxenBlockInteractEvent(e.getPlayer(), protection);
		}
	}

	@EventHandler
	public void onNoteBlockDamage(OraxenNoteBlockDamageEvent e) {
		Protection protection = protectionsService.findProtectionBySourceBlock(e.getBlock());
		if (protection != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onNoteBlockBreak(OraxenNoteBlockBreakEvent e) {
		Debugger.log(MessageType.ORAXEN_BLOCK_BREAK,
				() -> new Object[] { e.getPlayer().getName(), String.valueOf(e.getBlock().getX()),
						String.valueOf(e.getBlock().getY()), String.valueOf(e.getBlock().getZ()) });

		if (!e.isCancelled()) {
			switch (EventsUtils.onOraxenBlockBreakEvent(e.getPlayer(), e.getBlock())) {
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

	@EventHandler
	public void onStringBlockPlace(OraxenStringBlockPlaceEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}

		Debugger.log(MessageType.ORAXEN_BLOCK_PLACE,
				() -> new Object[] { e.getPlayer().getName(), String.valueOf(e.getBlock().getX()),
						String.valueOf(e.getBlock().getY()), String.valueOf(e.getBlock().getZ()) });

		if (!e.isCancelled()) {
			switch (EventsUtils.onOraxenBlockPlaceEvent(e.getPlayer(), e.getBlock(), null, e.getItemInHand())) {
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

	@EventHandler
	public void onStringBlockInteract(OraxenStringBlockInteractEvent e) {
		Protection protection = protectionsService.findProtectionBySourceBlock(e.getBlock());
		if (protection != null) {
			e.setCancelled(true);
			EventsUtils.onOraxenBlockInteractEvent(e.getPlayer(), protection);
		}
	}

	@EventHandler
	public void onStringBlockDamage(OraxenStringBlockDamageEvent e) {
		Protection protection = protectionsService.findProtectionBySourceBlock(e.getBlock());
		if (protection != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onStringBlockBreak(OraxenStringBlockBreakEvent e) {
		Debugger.log(MessageType.ORAXEN_BLOCK_BREAK,
				() -> new Object[] { e.getPlayer().getName(), String.valueOf(e.getBlock().getX()),
						String.valueOf(e.getBlock().getY()), String.valueOf(e.getBlock().getZ()) });

		if (!e.isCancelled()) {
			switch (EventsUtils.onOraxenBlockBreakEvent(e.getPlayer(), e.getBlock())) {
			case SUCCESS:
			case CANCEL:
				e.setCancelled(true);
			default:
				break;
			}
		} else {
			Debugger.log(MessageType.BLOCK_BREAK_CANCELLED);
		}
	}

}

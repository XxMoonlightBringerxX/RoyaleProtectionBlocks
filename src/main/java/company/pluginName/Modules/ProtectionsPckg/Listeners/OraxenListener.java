package company.pluginName.Modules.ProtectionsPckg.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import company.pluginName.Utils.EventsUtils;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockDamageEvent;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockInteractEvent;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockPlaceEvent;
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockBreakEvent;
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockDamageEvent;
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockInteractEvent;
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockPlaceEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

@PandaListener(optional = true)
public class OraxenListener implements Listener {

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
		IProtection protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(e.getBlock());
		if (protection != null) {
			e.setCancelled(true);
			EventsUtils.onOraxenBlockInteractEvent(e.getPlayer(), protection);
		}
	}

	@EventHandler
	public void onNoteBlockDamage(OraxenNoteBlockDamageEvent e) {
		IProtection protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(e.getBlock());
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
	public void onStringBlockInteract(OraxenStringBlockInteractEvent e) {
		IProtection protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(e.getBlock());
		if (protection != null) {
			e.setCancelled(true);
			EventsUtils.onOraxenBlockInteractEvent(e.getPlayer(), protection);
		}
	}

	@EventHandler
	public void onStringBlockDamage(OraxenStringBlockDamageEvent e) {
		IProtection protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(e.getBlock());
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

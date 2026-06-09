package company.pluginName.Modules.ProtectionsPckg.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

import com.nexomc.nexo.api.events.custom_block.noteblock.NexoNoteBlockBreakEvent;
import com.nexomc.nexo.api.events.custom_block.noteblock.NexoNoteBlockDamageEvent;
import com.nexomc.nexo.api.events.custom_block.noteblock.NexoNoteBlockInteractEvent;
import com.nexomc.nexo.api.events.custom_block.noteblock.NexoNoteBlockPlaceEvent;
import com.nexomc.nexo.api.events.custom_block.stringblock.NexoStringBlockBreakEvent;
import com.nexomc.nexo.api.events.custom_block.stringblock.NexoStringBlockDamageEvent;
import com.nexomc.nexo.api.events.custom_block.stringblock.NexoStringBlockInteractEvent;
import com.nexomc.nexo.api.events.custom_block.stringblock.NexoStringBlockPlaceEvent;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import company.pluginName.Utils.EventUtilitiesV2;
import company.pluginName.Utils.EventsUtils;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.ItemType;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

@PandaListener(optional = true)
public class NexoListener implements Listener {

	@EventHandler
	public void onNoteBlockPlace(NexoNoteBlockPlaceEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}

		if (!e.isCancelled()) {
			switch (EventsUtils.onNexoBlockPlaceEvent(e.getPlayer(), e.getBlock(), null, e.getItemInHand())) {
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
	public void onNoteBlockDamage(NexoNoteBlockDamageEvent e) {
		IProtection protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(e.getBlock());
		if (protection != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onNoteBlockBreak(NexoNoteBlockBreakEvent e) {
		if (!e.isCancelled()) {
			switch (EventsUtils.onNexoBlockBreakEvent(e.getPlayer(), e.getBlock())) {
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
	public void onStringBlockPlace(NexoStringBlockPlaceEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}

		if (!e.isCancelled()) {
			switch (EventsUtils.onNexoBlockPlaceEvent(e.getPlayer(), e.getBlock(), null, e.getItemInHand())) {
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
	public void onStringBlockDamage(NexoStringBlockDamageEvent e) {
		IProtection protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(e.getBlock());
		if (protection != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onStringBlockBreak(NexoStringBlockBreakEvent e) {
		if (!e.isCancelled()) {
			switch (EventsUtils.onNexoBlockBreakEvent(e.getPlayer(), e.getBlock())) {
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

	/*
	 * Interact events
	 */

	@EventHandler
	public void onNoteBlockInteract(NexoNoteBlockInteractEvent e) {
		IProtection protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(e.getBlock());
		if (protection != null) {
			EventUtilitiesV2.onPlayerInteract(e, ItemType.NEXO, e.getPlayer(), protection, e.getAction());
		}
	}

	@EventHandler
	public void onStringBlockInteract(NexoStringBlockInteractEvent e) {
		IProtection protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(e.getBlock());
		if (protection != null) {
			EventUtilitiesV2.onPlayerInteract(e, ItemType.NEXO, e.getPlayer(), protection, Action.LEFT_CLICK_BLOCK);
		}
	}

}

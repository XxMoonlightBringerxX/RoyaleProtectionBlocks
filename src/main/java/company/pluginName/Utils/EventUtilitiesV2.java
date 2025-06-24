package company.pluginName.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;

import royale.RoyaleProtectionBlocks.Plugin.API.Enums.ItemType;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Player.PlayerInteractProtectionBlockEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Player.PlayerInteractProtectionBlockEvent.EventResult;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

public class EventUtilitiesV2 {

	public static EventResult onPlayerInteract(Event event, ItemType itemType, Player player, IProtection protection,
			Action action) {
		PlayerInteractProtectionBlockEvent customEvent;

		if (protection.isCreationInProgress()) {
			cancelIfAvailable(event);
			return EventResult.CANCELLED;
		}

		if (action == Action.LEFT_CLICK_BLOCK || protection.getProtectionBlock().getItemType() != itemType) {
			return EventResult.IGNORED;
		}

		customEvent = new PlayerInteractProtectionBlockEvent(event, player, protection, action);

		Bukkit.getPluginManager().callEvent(customEvent);

		if (customEvent.isCancelled()) {
			cancelIfAvailable(event);
			customEvent = new PlayerInteractProtectionBlockEvent(event, player, protection, action,
					EventResult.CANCELLED);
			customEvent.setCancelled(true);
			Bukkit.getPluginManager().callEvent(customEvent);
			return EventResult.CANCELLED;
		}

		cancelIfAvailable(event);
		customEvent = new PlayerInteractProtectionBlockEvent(event, player, protection, action, EventResult.INTERACTED);
		Bukkit.getPluginManager().callEvent(customEvent);
		return EventResult.INTERACTED;
	}

	private static void cancelIfAvailable(Event event) {
		if (event instanceof Cancellable) {
			((Cancellable) event).setCancelled(true);
		}
	}

}

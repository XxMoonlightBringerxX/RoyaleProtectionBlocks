package company.pluginName.Bukkit.Events.ProtectionCreationRemoval;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import royale.RoyaleProtectionBlocks.Plugin.InternalAPI.Events.Protection.ProtectionCreationAttemptEvent;
import royale.RoyaleProtectionBlocks.Plugin.InternalAPI.Events.Protection.ProtectionRemovalAttemptEvent;

@PandaListener
public class ProtectionCreationRemovalAttemptListener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionCreationAttempt(ProtectionCreationAttemptEvent event) {
		Debugger.log(MessageType.PROTECTION_CREATION_ATTEMPT,
				() -> new Object[] { event.getPlayer().getName(),
						String.valueOf(event.getProtection().getLocation().getX()),
						String.valueOf(event.getProtection().getLocation().getY()),
						String.valueOf(event.getProtection().getLocation().getZ()) });
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionRemovalAttempt(ProtectionRemovalAttemptEvent event) {
		Debugger.log(MessageType.PROTECTION_REMOVAL_ATTEMPT,
				() -> new Object[] { event.getPlayer().getName(),
						event.getProtection().getDisplayName() != null ? event.getProtection().getDisplayName()
								: event.getProtection().getRegionId(),
						String.valueOf(event.getProtection().getLocation().getX()),
						String.valueOf(event.getProtection().getLocation().getY()),
						String.valueOf(event.getProtection().getLocation().getZ()) });
	}

}

package company.pluginName.Bukkit.Events.ProtectionCreationRemoval;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.CreationCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionCreationAttemptEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionRemovalAttemptEvent;

@PandaListener
public class ProtectionCreationRemovalAttemptListener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionCreationAttempt(ProtectionCreationAttemptEvent event) {
		if (event.getCause() == CreationCause.PLAYER) {
			Debugger.log(MessageType.PROTECTION_CREATION_ATTEMPT,
					() -> new Object[] { event.getExecutor().getName(),
							String.valueOf(event.getProtection().getBukkitLocation().getX()),
							String.valueOf(event.getProtection().getBukkitLocation().getY()),
							String.valueOf(event.getProtection().getBukkitLocation().getZ()) });
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionRemovalAttempt(ProtectionRemovalAttemptEvent event) {
		if (event.getCause() == RemovalCause.PLAYER) {
			Debugger.log(MessageType.PROTECTION_REMOVAL_ATTEMPT,
					() -> new Object[] { event.getExecutor().getName(),
							event.getProtection().getDisplayName() != null ? event.getProtection().getDisplayName()
									: event.getProtection().getProtectionId(),
							String.valueOf(event.getProtection().getBukkitLocation().getX()),
							String.valueOf(event.getProtection().getBukkitLocation().getY()),
							String.valueOf(event.getProtection().getBukkitLocation().getZ()) });
		}
	}

}

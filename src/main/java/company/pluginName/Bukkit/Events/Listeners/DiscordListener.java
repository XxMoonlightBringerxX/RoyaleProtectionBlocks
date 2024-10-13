package company.pluginName.Bukkit.Events.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Utils.DiscordUtilities;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.CreationCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionCreationEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionRemovalEvent;

@PandaListener
public class DiscordListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionCreation(ProtectionCreationEvent e) {
		if (e.getCause() == CreationCause.PLAYER) {
			DiscordUtilities.sendProtectionRegisteredMessage(e.getPlayer(), (Protection) e.getProtection());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionRemoval(ProtectionRemovalEvent e) {
		if (e.getCause() == RemovalCause.PLAYER || e.getCause() == RemovalCause.AUTO_PURGE) {
			DiscordUtilities.sendProtectionUnregisteredMessage(e.getPlayer(), (Protection) e.getProtection(),
					e.getCause());
		}
	}

}

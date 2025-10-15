package company.pluginName.Bukkit.Events.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import company.pluginName.Modules.DebugPckg.DebugService;
import company.pluginName.Modules.DebugPckg.Objects.Protection.ProtectionCreationDebugMessage;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionRemovalEvent;

@PandaListener
public class DiscordListener implements Listener {

	@PandaInject
	private DebugService debugService;

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProtectionRemoval(ProtectionRemovalEvent e) {
		if (e.getCause() == RemovalCause.AUTO_PURGE) {
			debugService.sendDebugMessage(debugService.getDebugMessage(ProtectionCreationDebugMessage.class),
					ProtectionCreationDebugMessage.Data.inst((Player) e.getExecutor(), e.getProtection()), false, true);
		}
	}

}

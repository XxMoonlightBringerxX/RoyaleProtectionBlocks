package company.pluginName.Modules.ProtectionsPckg.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Player.PlayerInteractProtectionBlockEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionManagementInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionRemovalInventoryRequestInput;

@PandaListener
public class CustomEventsListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractProtectionBlockEvent event) {
		if (event.getPlayer().isSneaking() && ProtectionUtilities.canDelete(event.getProtection(), event.getPlayer())) {
			try {
				RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
						.openProtectionRemovalInventoryRequest(OpenProtectionRemovalInventoryRequestInput
								.inst(event.getPlayer(), event.getProtection()));
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(event.getPlayer());
			}
		} else if (Settings.SETTINGS_PROTECTION_OPENINVENTORYONINTERACT.getContent()
				&& ProtectionUtilities.canManage(event.getProtection(), event.getPlayer())) {
			try {
				RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
						.openProtectionManagementInventoryRequest(OpenProtectionManagementInventoryRequestInput
								.inst(event.getPlayer(), event.getProtection()));
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(event.getPlayer());
			}
		}
	}

}

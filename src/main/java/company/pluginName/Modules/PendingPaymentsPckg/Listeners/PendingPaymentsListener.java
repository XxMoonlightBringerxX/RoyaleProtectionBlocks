package company.pluginName.Modules.PendingPaymentsPckg.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import company.pluginName.Modules.PendingPaymentsPckg.PendingPaymentsService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;

@PandaListener
public class PendingPaymentsListener implements Listener {

	@PandaInject
	private PendingPaymentsService pendingPaymentsService;

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		pendingPaymentsService.processPendingPayments(e.getPlayer());
	}

}

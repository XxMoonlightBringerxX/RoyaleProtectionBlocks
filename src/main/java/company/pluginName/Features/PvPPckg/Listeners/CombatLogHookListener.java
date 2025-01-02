package company.pluginName.Features.PvPPckg.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Features.PvPPckg.Utils.CombatLogHookUtilities;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionCreationAttemptEvent;

@PandaListener(optional = true)
public class CombatLogHookListener implements Listener {

	@EventHandler
	public void onProtectionBlockCreation(ProtectionCreationAttemptEvent e) {
		if (e.getExecutor() instanceof Player
				&& CombatLogHookUtilities.SETTINGS_COMBATLOGHOOK_CANCELPROTECTIONCREATIONINCOMBAT.isTrue()
				&& CombatLogHookUtilities.isInCombat((Player) e.getExecutor())) {
			e.setCancelled(true);
			Exceptions.Protections.INCOMBAT.generateException().sendError((Player) e.getExecutor());
		}
	}

}

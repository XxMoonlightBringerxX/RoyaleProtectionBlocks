package company.pluginName.Features.ProtectionPermissionLimitPckg.Listeners;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import company.pluginName.Features.ProtectionPermissionLimitPckg.Utils.ProtectionPermissionLimitUtilities;
import company.pluginName.Features.ProtectionPermissionLimitPckg.Utils.ProtectionPermissionLimitUtilities.Summary;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.BlockReason;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionRemovalEvent;

@PandaListener
public class ProtectionPermissionLimitListener implements Listener {

	@PandaInject
	private ProtectionsService protectionsService;

	@PandaInject
	private ProtectionBlocksService protectionBlocksService;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (ProtectionPermissionLimitUtilities.SETTINGS_PROTECTION_BLOCKPROTECTIONSIFEXCEEDINGLIMITS.isTrue()) {
			Summary summary = ProtectionPermissionLimitUtilities.checkCapacity(e.getPlayer());

			ProtectionPermissionLimitUtilities.sendSummaryMessage(e.getPlayer(), summary);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onProtectionRemove(ProtectionRemovalEvent e) {
		if (ProtectionPermissionLimitUtilities.SETTINGS_PROTECTION_BLOCKPROTECTIONSIFEXCEEDINGLIMITS.isTrue()) {
			if (e.getProtection().isBlocked() && (e.getProtection().getBlockReason() == BlockReason.EXCEEDING_LIMIT
					|| e.getProtection().getBlockReason() == BlockReason.EXCEEDING_LIMIT_PROTECTION_BLOCK)) {
				Summary summary = ProtectionPermissionLimitUtilities.checkCapacity(e.getPlayer());

				if (e.getProtection().getBlockReason() == BlockReason.EXCEEDING_LIMIT) {
					summary.getUnblockedProtections().add(e.getProtection());
				} else if (e.getProtection().getBlockReason() == BlockReason.EXCEEDING_LIMIT_PROTECTION_BLOCK) {
					summary.getUnblockedProtectionsPerBlock()
							.computeIfAbsent(e.getProtection().getProtectionBlockIdentifier(),
									(id) -> new ArrayList<>())
							.add(e.getProtection());
				}

				ProtectionPermissionLimitUtilities.sendSummaryMessage(e.getPlayer(), summary);
			}
		}
	}

}

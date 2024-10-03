package company.pluginName.Bukkit.Events.ProtectionCreationRemoval;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import company.pluginName.Hooks.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.CreationCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionCreationEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionRemovalEvent;

@PandaListener
public class ProtectionCreationRemovalListener implements Listener {

	@PandaInject
	private ProtectionsService protectionsService;

	@PandaInject
	private PlaceholderAPI placeholderApi;

	@EventHandler
	public void onProtectionCreation(ProtectionCreationEvent e) {
		if (e.getCause() == CreationCause.PLAYER) {
			Debugger.log(MessageType.PROTECTION_CREATION,
					() -> new Object[] { e.getPlayer().getName(),
							e.getProtection().getDisplayName() != null ? e.getProtection().getDisplayName()
									: e.getProtection().getRegionId(),
							String.valueOf(e.getProtection().getBukkitLocation().getX()),
							String.valueOf(e.getProtection().getBukkitLocation().getY()),
							String.valueOf(e.getProtection().getBukkitLocation().getZ()) });
		}
	}

	@EventHandler
	public void onProtectionRemoval(ProtectionRemovalEvent e) {
		if (e.getCause() == RemovalCause.PLAYER) {
			Debugger.log(MessageType.PROTECTION_REMOVAL,
					() -> new Object[] { e.getPlayer().getName(),
							e.getProtection().getDisplayName() != null ? e.getProtection().getDisplayName()
									: e.getProtection().getRegionId(),
							String.valueOf(e.getProtection().getBukkitLocation().getX()),
							String.valueOf(e.getProtection().getBukkitLocation().getY()),
							String.valueOf(e.getProtection().getBukkitLocation().getZ()) });
		}
	}

}

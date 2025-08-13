package company.pluginName.Modules.ProtectionsPckg.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;

@PandaListener
public class ProtectionPermissionListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
				.findProtectionsByLocation(e.getBlock().getLocation().add(0.5D, 0.5D, 0.5D), false)
				.anyMatch(prot -> !prot.canPlace(e.getPlayer()))) {
			e.setBuild(true);
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		if (RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
				.findProtectionsByLocation(e.getBlock().getLocation().add(0.5D, 0.5D, 0.5D), false)
				.anyMatch(prot -> !prot.canBreak(e.getPlayer()))) {
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteractBlock(PlayerInteractEvent e) {
		if (e.getClickedBlock().getType().isInteractable()
				&& RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
						.findProtectionsByLocation(e.getClickedBlock().getLocation().add(0.5D, 0.5D, 0.5D), false)
						.anyMatch(prot -> !prot.canInteract(e.getPlayer()))) {
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		if (RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
				.findProtectionsByLocation(e.getRightClicked().getLocation(), false)
				.anyMatch(prot -> !prot.canInteract(e.getPlayer()))) {
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
		if (RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
				.findProtectionsByLocation(e.getRightClicked().getLocation(), false)
				.anyMatch(prot -> !prot.canInteract(e.getPlayer()))) {
			e.setCancelled(true);
		}
	}

}

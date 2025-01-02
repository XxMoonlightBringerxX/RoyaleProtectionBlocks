package company.pluginName.Modules.ProtectionsPckg.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

@PandaListener
public class BlockListener implements Listener {

	@PandaInject
	private PlayerDataService playerDataService;

	/**
	 * Event used to control the interactions of the players to the protection
	 * blocks when the protection is blocked. If the blocked right clicked is a
	 * protection block, it's ignored to leave the job to the next listener. If it's
	 * not, and the block is inside the protection, it's cancelled.
	 * 
	 * @param e
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerInteractOnBlocked(PlayerInteractEvent e) {
		PlayerData playerData = playerDataService.getPlayerData(e.getPlayer());

		if (playerData != null && playerData.isStaffMode()) {
			return;
		}

		IProtection protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(e.getClickedBlock());
		if (protection == null) {
			protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
					.findProtectionParentByLocation(e.getClickedBlock().getLocation());
			if (protection != null && protection.isBlocked()) {
				e.setCancelled(true);

				if (e.getHand() == EquipmentSlot.HAND && e.getClickedBlock().getType().isInteractable()) {

					if (System.currentTimeMillis() - playerData.getLastBlockedProtectionMessage() > 1000L) {
						playerData.setLastBlockedProtectionMessage(System.currentTimeMillis());
						Exceptions.Protections.BLOCKED.generateException().sendError(e.getPlayer());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockPlaceOnBlocked(BlockPlaceEvent e) {
		PlayerData playerData = playerDataService.getPlayerData(e.getPlayer());

		if (playerData != null && playerData.isStaffMode()) {
			return;
		}

		IProtection protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionParentByLocation(e.getBlockPlaced().getLocation());

		if (protection != null && protection.isBlocked()) {
			e.setCancelled(true);

			if (System.currentTimeMillis() - playerData.getLastBlockedProtectionMessage() > 1000L) {
				playerData.setLastBlockedProtectionMessage(System.currentTimeMillis());
				Exceptions.Protections.BLOCKED.generateException().sendError(e.getPlayer());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockBreakOnBlocked(BlockBreakEvent e) {
		PlayerData playerData = playerDataService.getPlayerData(e.getPlayer());

		if (playerData != null && playerData.isStaffMode()) {
			return;
		}

		IProtection protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(e.getBlock());

		if (protection == null) {
			protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
					.findProtectionParentByLocation(e.getBlock().getLocation());

			if (protection != null && protection.isBlocked()) {
				e.setCancelled(true);

				if (System.currentTimeMillis() - playerData.getLastBlockedProtectionMessage() > 1000L) {
					playerData.setLastBlockedProtectionMessage(System.currentTimeMillis());
					Exceptions.Protections.BLOCKED.generateException().sendError(e.getPlayer());
				}
			}
		}
	}

}

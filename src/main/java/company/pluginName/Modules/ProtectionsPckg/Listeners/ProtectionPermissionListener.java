package company.pluginName.Modules.ProtectionsPckg.Listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.DoubleChestInventory;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Features.PvPPckg.Utils.PvPUtilities;
import company.pluginName.Modules.ProtectionPermissionsPckg.ProtectionPermissionsService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;

@PandaListener
public class ProtectionPermissionListener implements Listener {

	private static final List<String> CONTROLLED_BLOCK_INVENTORIES = Arrays.asList("CHEST", "DISPENSER", "DROPPER",
			"FURNACE", "BREWING", "SHULKER_BOX", "BARREL");

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
				.findProtectionsByLocation(e.getBlock().getLocation().add(0.5D, 0.5D, 0.5D))
				.sorted((prot1, prot2) -> prot2.getPriority() - prot1.getPriority())
				.map(prot -> !prot.canPlace(e.getPlayer())).filter(Boolean.TRUE::equals).findFirst().orElse(false)) {
			e.setBuild(true);
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		if (RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
				.findProtectionsByLocation(e.getBlock().getLocation().add(0.5D, 0.5D, 0.5D))
				.sorted((prot1, prot2) -> prot2.getPriority() - prot1.getPriority())
				.map(prot -> !prot.canBreak(e.getPlayer())).filter(Boolean.TRUE::equals).findFirst().orElse(false)) {
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteractBlock(PlayerInteractEvent e) {
		if (e.getClickedBlock().getType().isInteractable() && RoyaleProtectionBlocksAPIImpl.getInstance()
				.getProtectionsService()
				.findProtectionsByLocation(e.getClickedBlock().getLocation().add(0.5D, 0.5D, 0.5D))
				.sorted((prot1, prot2) -> prot2.getPriority() - prot1.getPriority())
				.map(prot -> !prot.canInteract(e.getPlayer())).filter(Boolean.TRUE::equals).findFirst().orElse(false)) {
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		if (RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
				.findProtectionsByLocation(e.getRightClicked().getLocation())
				.sorted((prot1, prot2) -> prot2.getPriority() - prot1.getPriority())
				.map(prot -> !prot.canInteract(e.getPlayer())).filter(Boolean.TRUE::equals).findFirst().orElse(false)) {
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
		if (RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
				.findProtectionsByLocation(e.getRightClicked().getLocation())
				.sorted((prot1, prot2) -> prot2.getPriority() - prot1.getPriority())
				.map(prot -> !prot.canInteract(e.getPlayer())).filter(Boolean.TRUE::equals).findFirst().orElse(false)) {
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockInventoryOpen(InventoryOpenEvent e) {
		if (e.getPlayer() instanceof Player && e.getInventory() != null && e.getInventory().getHolder() != null) {
			List<Block> blocks = new ArrayList<>();

			if (e.getInventory().getHolder() instanceof BlockInventoryHolder) {
				blocks.add(((BlockInventoryHolder) e.getInventory().getHolder()).getBlock());
			} else if (e.getInventory().getHolder() instanceof DoubleChest) {
				blocks.add(
						((Chest) ((DoubleChest) e.getInventory().getHolder()).getLeftSide()).getLocation().getBlock());
				blocks.add(
						((Chest) ((DoubleChest) e.getInventory().getHolder()).getRightSide()).getLocation().getBlock());
			}

			if (!blocks.isEmpty() && blocks.stream()
					.map(block -> RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
							.findProtectionsByLocation(block.getLocation().add(0.5D, 0.5D, 0.5D))
							.sorted((prot1, prot2) -> prot2.getPriority() - prot1.getPriority())
							.map(prot -> !prot.canOpenInventories((Player) e.getPlayer())).findFirst().orElse(false))
					.filter(Boolean.TRUE::equals).findFirst().orElse(false)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryMoveItem(InventoryMoveItemEvent e) {
		boolean mustCheck = e.getSource() != null
				&& CONTROLLED_BLOCK_INVENTORIES.contains(e.getSource().getType().name());
		boolean isHopper = e.getDestination() != null && e.getDestination().getType() == InventoryType.HOPPER;

		if (mustCheck) {
			if (isHopper) {
				mustCheck = ProtectionPermissionsService.INVENTORYACCESS_PERMISSION
						.isPreventHoppersIfNonMembersAccessDenied();
			} else {
				mustCheck = false;
			}
		}

		if (mustCheck) {
			List<Block> blocks = new ArrayList<>();

			if (e.getSource() instanceof DoubleChestInventory) {
				blocks.add(((Chest) ((DoubleChestInventory) e.getSource()).getLeftSide()).getLocation().getBlock());
				blocks.add(((Chest) ((DoubleChestInventory) e.getSource()).getRightSide()).getLocation().getBlock());
			} else {
				blocks.add(e.getSource().getLocation().getBlock());
			}

			if (!blocks.isEmpty() && blocks.stream()
					.map(block -> RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
							.findProtectionsByLocation(block.getLocation().add(0.5D, 0.5D, 0.5D))
							.sorted((prot1, prot2) -> prot2.getPriority() - prot1.getPriority())
							.map(prot -> !prot.canOpenInventories(null)).findFirst().orElse(false))
					.filter(Boolean.TRUE::equals).findFirst().orElse(false)) {
				e.setCancelled(true);

				if (isHopper && ProtectionPermissionsService.INVENTORYACCESS_PERMISSION
						.isDropHoppersIfNonMembersAccessDenied()) {
					((Hopper) e.getDestination().getHolder()).getBlock().breakNaturally();
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPvP(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			boolean damagerAllowed = PvPUtilities.isInCombat((Player) e.getDamager())
					|| Boolean.TRUE.equals(RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
							.findProtectionsByLocation(e.getDamager().getLocation())
							.sorted((prot1, prot2) -> prot2.getPriority() - prot1.getPriority())
							.map(prot -> prot.canPvP((Player) e.getDamager())).findFirst().orElse(true));
			boolean damagedAllowed = PvPUtilities.isInCombat((Player) e.getEntity())
					|| Boolean.TRUE.equals(RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
							.findProtectionsByLocation(e.getEntity().getLocation())
							.sorted((prot1, prot2) -> prot2.getPriority() - prot1.getPriority())
							.map(prot -> prot.canPvP((Player) e.getEntity())).findFirst().orElse(true));
			if (!damagerAllowed || !damagedAllowed) {
				e.setCancelled(true);
			}
		}
	}

}

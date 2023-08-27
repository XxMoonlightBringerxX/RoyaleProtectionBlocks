package company.pluginName.Bukkit.Events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import company.pluginName.MainPluginClass;
import company.pluginName.MainPluginClass.Debugger.MessageType;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.Utils.EventsUtils;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.ItemStackDataUtilities;

public class ProtectionBlockEvents implements Listener {

	/**
	 * Event used to check if a block is placed, usually to check if the block is a
	 * protection block and, in that case, start the creation of protections.
	 * 
	 * If the utils for the event returns CANCEL, meaning that wasn't success, the
	 * event is cancelled.
	 * 
	 * @param e
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlaceBlock(BlockPlaceEvent e) {
		MainPluginClass.Debugger.log(MessageType.BLOCK_PLACE,
				() -> new Object[] { e.getPlayer().getName(), String.valueOf(e.getBlockPlaced().getX()),
						String.valueOf(e.getBlockPlaced().getY()), String.valueOf(e.getBlockPlaced().getZ()) });

		if (!e.isCancelled() && e.canBuild()) {
			switch (EventsUtils.onVanillaBlockPlaceEvent(e.getPlayer(), e.getBlock(), e.getHand(), e.getItemInHand())) {
			case CANCEL:
				e.setBuild(false);
				e.setCancelled(true);
				break;
			default:
				break;
			}
		} else {
			MainPluginClass.Debugger.log(MessageType.BLOCK_PLACE_CANCELLED);
		}
	}

	/**
	 * Event used to check if a block is broken, usually to check if the block is a
	 * protection block and, in that case, start the removal of protections.
	 * 
	 * If the utils for the event returns false, meaning that wasn't success, the
	 * event is cancelled.
	 * 
	 * @param e
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBreakBlock(BlockBreakEvent e) {
		MainPluginClass.Debugger.log(MessageType.BLOCK_BREAK,
				() -> new Object[] { e.getPlayer().getName(), String.valueOf(e.getBlock().getX()),
						String.valueOf(e.getBlock().getY()), String.valueOf(e.getBlock().getZ()) });

		if (!e.isCancelled()) {
			switch (EventsUtils.onVanillaBlockBreakEvent(e.getPlayer(), e.getBlock())) {
			case SUCCESS:
				e.setDropItems(false);
				e.setExpToDrop(0);
				break;
			case CANCEL:
				e.setCancelled(true);
				break;
			default:
				break;
			}
		} else {
			MainPluginClass.Debugger.log(MessageType.BLOCK_BREAK_CANCELLED);
		}
	}

	/**
	 * Event used to control the interactions of the players to the protection
	 * blocks. If a protection is found by the right-clicked block, then it'll
	 * execute the interaction behaviour for protections.
	 * 
	 * @param e
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		switch (e.getHand().name()) {
		case "HAND":
			if (e.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
				Protection protection = MainPluginClass.getPlugin().getProtectionsModule()
						.getProtectionByBlock(e.getClickedBlock().getLocation());
				if (protection != null) {
					e.setCancelled(true);
					EventsUtils.onVanillaBlockInteractEvent(e.getPlayer(), protection);
				}
			}
			break;
		case "OFF_HAND":
			if (e.getItem() != null) {
				String protectionBlockId = null;

				try {
					protectionBlockId = ItemStackDataUtilities.getPersistentData(e.getItem(),
							MainPluginClass.getPlugin(), ProtectionBlock.PROTECTION_BLOCK_ID_KEY, String.class);
				} catch (Exception e1) {
					MessageBuilder.createMessage(MessageString.applyPrefix(
							"An error has ocurred trying to retrieve the Protection Block ID from an item: %s"
									.formatted(e1.getMessage())))
							.sendMessage(Bukkit.getConsoleSender());
					e1.printStackTrace();
				}

				if (protectionBlockId != null && !protectionBlockId.isEmpty()) {
					e.setCancelled(true);
				}
			}
			break;
		}
	}

	/**
	 * Event used to check if the player is right-clicking the entity used to show
	 * the boundaries of the protection. Being the case, this event will proceed
	 * with the interaction behaviour for protection blocks.
	 * 
	 * @param e
	 */
	@EventHandler
	public void onInteractEntity(PlayerInteractAtEntityEvent e) {
		List<Protection> protections = MainPluginClass.getPlugin().getProtectionsModule().getProtectionsByWorld()
				.getOrDefault(e.getRightClicked().getWorld().getName(), new ArrayList<>());
		for (Protection protection : protections) {
			if (protection.isProtectionViewEntity(e.getRightClicked())) {
				e.setCancelled(true);
				EventsUtils.onVanillaBlockInteractEvent(e.getPlayer(), protection);
				break;
			}
		}
	}

	/*
	 * Protection block external removal prevention events
	 */

	@EventHandler(ignoreCancelled = true)
	public void onExplodeEntity(EntityExplodeEvent e) {
		e.blockList().removeIf(block -> MainPluginClass.getPlugin().getProtectionsModule()
				.getProtectionByBlock(block.getLocation()) != null);
	}

	@EventHandler(ignoreCancelled = true)
	public void onExplodeBlock(BlockExplodeEvent e) {
		e.blockList().removeIf(block -> MainPluginClass.getPlugin().getProtectionsModule()
				.getProtectionByBlock(block.getLocation()) != null);
	}

	@EventHandler
	public void onMobGrief(EntityChangeBlockEvent e) {
		Protection protection = MainPluginClass.getPlugin().getProtectionsModule()
				.getProtectionByBlock(e.getBlock().getLocation());
		if (protection != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamageEntity(EntityDamageEvent e) {
		List<Protection> protections = MainPluginClass.getPlugin().getProtectionsModule().getProtectionsByWorld()
				.getOrDefault(e.getEntity().getWorld().getName(), new ArrayList<>());
		for (Protection protection : protections) {
			if (protection.isProtectionViewEntity(e.getEntity())) {
				e.setCancelled(true);
				break;
			}
		}
	}

}

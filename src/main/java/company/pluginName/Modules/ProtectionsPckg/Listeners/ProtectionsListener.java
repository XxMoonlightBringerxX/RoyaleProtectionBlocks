package company.pluginName.Modules.ProtectionsPckg.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import company.pluginName.MainPluginClass;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Utils.EventsUtils;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.ItemStackDataUtilities;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

@PandaListener
public class ProtectionsListener implements Listener {

	@PandaInject
	private MainPluginClass plugin;

	/**
	 * Event used to check if a block is placed, usually to check if the block is a
	 * protection block and, in that case, start the creation of protections.
	 * 
	 * If the utils for the event returns CANCEL, meaning that wasn't success, the
	 * event is cancelled.
	 * 
	 * @param e
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlaceBlock(BlockPlaceEvent e) {
		Debugger.log(MessageType.BLOCK_PLACE,
				() -> new Object[] { e.getPlayer().getName(), String.valueOf(e.getBlockPlaced().getX()),
						String.valueOf(e.getBlockPlaced().getY()), String.valueOf(e.getBlockPlaced().getZ()) });

		if (!e.isCancelled() && e.canBuild()) {
			switch (EventsUtils.onVanillaBlockPlaceEvent(e.getPlayer(), e.getBlock(), e.getHand(), e.getItemInHand())) {
			case SUCCESS:
			case CANCEL:
				e.setBuild(false);
				e.setCancelled(true);
				return;
			default:
				break;
			}
		} else {
			Debugger.log(MessageType.BLOCK_PLACE_CANCELLED);
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
		Debugger.log(MessageType.BLOCK_BREAK,
				() -> new Object[] { e.getPlayer().getName(), String.valueOf(e.getBlock().getX()),
						String.valueOf(e.getBlock().getY()), String.valueOf(e.getBlock().getZ()) });

		if (!e.isCancelled()) {
			switch (EventsUtils.onVanillaBlockBreakEvent(e.getPlayer(), e.getBlock())) {
			case SUCCESS:
			case CANCEL:
				e.setCancelled(true);
				return;
			default:
				break;
			}
		} else {
			Debugger.log(MessageType.BLOCK_BREAK_CANCELLED);
		}
	}

	/**
	 * Event used to control the interactions of the players to the protection
	 * blocks. If a protection is found by the right-clicked block, then it'll
	 * execute the interaction behavior for protections.
	 * 
	 * @param e
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getHand() != null && e.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
			switch (e.getHand().name()) {
			case "HAND":
				IProtection protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
						.findProtectionBySourceBlock(e.getClickedBlock());
				if (protection != null) {
					e.setCancelled(true);
					EventsUtils.onVanillaBlockInteractEvent(e.getPlayer(), protection);
					return;
				}
				break;
			case "OFF_HAND":
				String protectionBlockId = null;

				try {
					protectionBlockId = ItemStackDataUtilities.getPersistentData(e.getItem(), plugin,
							ProtectionBlock.PROTECTION_BLOCK_ID_KEY, String.class);
				} catch (Exception e1) {
					MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(String.format(
							"An error has ocurred trying to retrieve the Protection Block ID from an item: %s",
							e1.getMessage()))).process().sendMessage(Bukkit.getConsoleSender());
					e1.printStackTrace();
				}

				if (protectionBlockId != null && !protectionBlockId.isEmpty()) {
					e.setCancelled(true);
					return;
				}
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onExplodeEntity(EntityExplodeEvent e) {
		e.blockList().removeIf(block -> RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(block) != null);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onExplodeBlock(BlockExplodeEvent e) {
		e.blockList().removeIf(block -> RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(block) != null);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onMobGrief(EntityChangeBlockEvent e) {
		if (RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(e.getBlock()) != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockFromTo(BlockFromToEvent e) {
		if (RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(e.getToBlock()) != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPistonExtend(BlockPistonExtendEvent e) {
		if (e.getBlocks().stream().anyMatch(block -> RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(block) != null)) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBucketFill(PlayerBucketEmptyEvent e) {
		if (RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(e.getBlockClicked().getRelative(e.getBlockFace())) != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBucketFill(BlockPistonRetractEvent e) {
		if (e.getBlocks().stream().anyMatch(block -> RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceBlock(block) != null)) {
			e.setCancelled(true);
		}
	}

}

package company.pluginName.Bukkit.Events;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.Protections.ProtectionsManagerInventory;
import company.pluginName.Exceptions.Protection.Delete.ProtectionDeleteException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveException;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

public class BukkitEvents implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlaceBlock(BlockPlaceEvent e) {
		if (e.canBuild()) {
			String protectionBlockId = ItemStacksUtils.getData(e.getItemInHand(), "ProtectionBlockId");
			if (protectionBlockId != null && !protectionBlockId.isEmpty()) {
				ProtectionBlock protectionBlock = MainPluginClass.getPlugin().getProtectionsModule()
						.getProtectionBlockById(protectionBlockId);

				if (protectionBlock != null) {
					if (protectionBlock.getPermission() == null
							|| e.getPlayer().hasPermission(protectionBlock.getPermission())) {
						if (!SettingList.SETTINGS_BANNEDWORLDS.getContent()
								.contains(e.getBlockPlaced().getLocation().getWorld().getName())) {
							try {
								MainPluginClass.getPlugin().getProtectionsModule().createProtection(e.getPlayer(),
										protectionBlock, e.getBlock().getLocation());
								MessageBuilder
										.createMessage(
												MessageString.MESSAGE_PROTECTIONS_CREATEDSUCCESSFULLY.applyPrefix())
										.sendMessage(e.getPlayer());
							} catch (ProtectionSaveException e1) {
								e.setBuild(false);
								e1.sendError(e.getPlayer());
							}
						} else {
							e.setCancelled(true);
							MessageBuilder.createMessage(MessageString.ERROR_PROTECTIONS_BANNEDWORLD.applyPrefix())
									.sendMessage(e.getPlayer());
						}
					} else {
						e.setCancelled(true);
						MessageBuilder.createMessage(MessageString.ERROR_PROTECTIONS_PERMISSIONDENIED.applyPrefix())
								.sendMessage(e.getPlayer());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBreakBlock(BlockBreakEvent e) {
		Protection protection = MainPluginClass.getPlugin().getProtectionsModule()
				.getProtectionByBlock(e.getBlock().getLocation());
		if (protection != null) {
			try {
				MainPluginClass.getPlugin().getProtectionsModule().removeProtection(e.getPlayer(), protection);
				e.setDropItems(false);
				e.setExpToDrop(0);
				e.getBlock().getWorld().dropItem(e.getBlock().getLocation(),
						protection.getProtectionBlock().getObject().generateItem());
				MessageBuilder.createMessage(MessageString.MESSAGE_PROTECTIONS_REMOVEDSUCCESSFULLY.applyPrefix())
						.sendMessage(e.getPlayer());
			} catch (ProtectionDeleteException e1) {
				e.setCancelled(true);
				e1.sendError(e.getPlayer());
			}
		}
	}

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
	public void onClickBlock(PlayerInteractEvent e) {
		if (e.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK && !e.getPlayer().isSneaking()) {
			Protection protection = MainPluginClass.getPlugin().getProtectionsModule()
					.getProtectionByBlock(e.getClickedBlock().getLocation());
			if (protection != null && protection.isOwner(e.getPlayer().getUniqueId())) {
				e.setCancelled(true);
				new ProtectionsManagerInventory(e.getPlayer(), protection).openInventory();
			}
		}
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
	public void onWorldLoad(WorldLoadEvent e) {
		MainPluginClass.getPlugin().getProtectionsModule().getProtectionsByWorld()
				.getOrDefault(e.getWorld().getName(), new ArrayList<>()).forEach(protection -> {
					if (protection.getProtectedRegion() == null) {
						try {
							MessageBuilder.createMessage(
									MessageString.applyPrefix("&7Removing protection '" + protection.getRegionId()
											+ "' as it couldn't be found on '" + protection.getWorldName() + "'"))
									.sendMessage(Bukkit.getConsoleSender());
							MainPluginClass.getPlugin().getProtectionsModule().removeProtection(protection);
						} catch (ProtectionDeleteException e1) {
							e1.sendError(Bukkit.getConsoleSender());
						}
					}
				});
	}

}

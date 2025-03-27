package company.pluginName.Bukkit.Events.Listeners;

import java.util.Collections;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityMountEvent;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionPermissionsPckg.ProtectionPermissionsService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsServiceImpl;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.SQLPckg.SQLService;
import company.pluginName.Modules.SettingsPckg.SettingsService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.Protections.Objects.ProtectionRemovalData;

@PandaListener
public class BukkitListener implements Listener {

	@PandaInject
	private ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private SettingsService protectionSettingsService;

	@PandaInject
	private ProtectionsServiceImpl protectionsService;

	@PandaInject
	private PlayerDataService playerDataService;

	@PandaInject
	private SQLService sqlService;

	@PandaInject
	private WorldGuardAPI worldGuardApi;

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (!e.getPlayer().hasPlayedBefore() && !Settings.SETTINGS_PROTECTION_STARTERBLOCK.toString().isEmpty()) {
			ProtectionBlock block = protectionBlocksService
					.getProtectionBlockById(Settings.SETTINGS_PROTECTION_STARTERBLOCK.toString());
			if (block != null) {
				ItemStack item = block.generateItem();

				if (item != null) {
					e.getPlayer().getInventory().addItem(item);
				}
			}
		}

		RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionsByOwner(e.getPlayer().getUniqueId()).forEach(IProtection::updateOwnerData);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerExit(PlayerQuitEvent e) {
		TasksUtils.execute(() -> RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionsByOwner(e.getPlayer().getUniqueId()).forEach(IProtection::updateOwnerData));
	}

	@EventHandler
	public void onMoveEvent(PlayerMoveEvent e) {
		if (!e.getFrom().getBlock().equals(e.getTo().getBlock())) {
			PlayerData playerData = playerDataService.getPlayerData(e.getPlayer());

			if (playerData != null && playerData.isTeleporting()) {
				playerData.cancelTeleport();
				Exceptions.Protections.Teleport.CANCELLEDDUEMOVEMENT.generateException().sendError(e.getPlayer());
			}
		}
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent e) {
		protectionsService.getProtectionsByWorld().getOrDefault(e.getWorld().getName(), Collections.emptyList())
				.stream().filter(protection -> protection.getProtectedRegion() == null).collect(Collectors.toList())
				.forEach(protection -> {
					if (protection.getProtectedRegion() == null) {
						try {
							protection.regenerateProtectedRegion();
						} catch (RoyaleProtectionBlocksExceptionImpl e1) {
							MessageTemplate
									.inst(PandaPrefixedStringField
											.applyPrefix("&7Removing protection '" + protection.getProtectionId()
													+ "' as it couldn't be found on '" + protection.getWorldName()
													+ "' and were problems trying to regenerate the region."))
									.process().sendMessage(Bukkit.getConsoleSender());

							try {
								RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
										.delete(ProtectionRemovalData.inst(null, protection.getProtectionId()));
							} catch (RoyaleProtectionBlocksException e2) {
								e2.sendError(Bukkit.getConsoleSender());
							}
						}
					}
				});
	}

	@EventHandler
	public void onPlayerMount(EntityMountEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();

			Protection protection = RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
					.findProtectionByLocation(e.getEntity().getLocation());

			if (protection != null && Boolean.FALSE.equals(
					protection.getPermissionValue(ProtectionPermissionsService.RIDEVEHICLES_PERMISSION, player))) {
				e.setCancelled(true);
			}
		}
	}

}

package company.pluginName.Bukkit.Events.Listeners;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.WorldLoadEvent;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.SQLPckg.SQLService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;

@PandaListener
public class BukkitListener implements Listener {

	@PandaInject
	private ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private ProtectionSettingsService protectionSettingsService;

	@PandaInject
	private ProtectionsService protectionsService;

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
				try {
					e.getPlayer().getInventory().addItem(block.getInformation().generateItem());
				} catch (RoyaleProtectionBlocksExceptionImpl e1) {
					e1.sendError(Bukkit.getConsoleSender());
				}
			}
		}

		protectionsService.findProtectionsByOwner(e.getPlayer().getUniqueId()).stream()
				.filter(protection -> protection.getOwnerOfflinePlayer() != null
						&& !protection.getOwnerOfflinePlayer().isOnline())
				.forEach(protection -> protection.setOwnerOfflinePlayer(e.getPlayer()));
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
				.forEach(protection -> {
					if (protection.getProtectedRegion() == null) {
						try {
							protection.regenerateProtectedRegion();
						} catch (RoyaleProtectionBlocksExceptionImpl e1) {
							MessageTemplate
									.inst(PandaPrefixedStringField
											.applyPrefix("&7Removing protection '" + protection.getRegionId()
													+ "' as it couldn't be found on '" + protection.getWorldName()
													+ "' and were problems trying to regenerate the region."))
									.process().sendMessage(Bukkit.getConsoleSender());

							if (protection.getUtils().isProtectionBlockShown()) {
								protection.getUtils().hideProtectionBlock();
							}

							if (protection.getBoundaries().isProtectionViewActive()) {
								protection.getBoundaries().toggleProtectionView();
							}

							try {
								protection.delete(RemovalCause.PLAYER).subscribe();
							} catch (RoyaleProtectionBlocksExceptionImpl e2) {
								e2.sendError(Bukkit.getConsoleSender());
								return;
							}
						}
					}
				});
	}

}

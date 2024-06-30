package company.pluginName.Bukkit.Events.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

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
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;

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
	private WorldGuardAPI worldGuardApi;

	@EventHandler
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

}

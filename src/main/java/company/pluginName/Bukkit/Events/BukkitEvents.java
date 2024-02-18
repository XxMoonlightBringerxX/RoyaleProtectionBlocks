package company.pluginName.Bukkit.Events;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.WorldLoadEvent;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;

@PandaListener
public class BukkitEvents implements Listener {

	@PandaInject
	private ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private ProtectionsService protectionsService;

	@PandaInject
	private PlayerDataService playerDataService;

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (!e.getPlayer().hasPlayedBefore() && !Settings.SETTINGS_PROTECTION_STARTERBLOCK.toString().isEmpty()) {
			ProtectionBlock block = protectionBlocksService
					.getProtectionBlockById(Settings.SETTINGS_PROTECTION_STARTERBLOCK.toString());
			if (block != null) {
				try {
					e.getPlayer().getInventory().addItem(block.getInformation().generateItem());
				} catch (RoyaleProtectionBlocksException e1) {
					e1.sendError(Bukkit.getConsoleSender());
					return;
				}
			}
		}
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent e) {
		protectionsService.getProtectionsByWorld().getOrDefault(e.getWorld().getName(), new ArrayList<>())
				.forEach(protection -> {
					if (protection.getProtectedRegion() == null) {
						try {
							MessageTemplate
									.inst(PandaPrefixedStringField.applyPrefix("&7Removing protection '"
											+ protection.getRegionId() + "' as it couldn't be found on '"
											+ protection.getWorldName() + "'"))
									.process().sendMessage(Bukkit.getConsoleSender());
							protectionsService.removeProtection(protection);
						} catch (RoyaleProtectionBlocksException e1) {
							e1.sendError(Bukkit.getConsoleSender());
						}
					}
				});
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

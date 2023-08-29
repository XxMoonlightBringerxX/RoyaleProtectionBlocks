package company.pluginName.Bukkit.Events;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.Protection.Delete.ProtectionDeleteException;
import company.pluginName.Exceptions.ProtectionBlocks.ProtectionBlocksGenerateItemException;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;

public class BukkitEvents implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (!e.getPlayer().hasPlayedBefore() && !SettingString.SETTINGS_PROTECTION_STARTERBLOCK.toString().isEmpty()) {
			ProtectionBlock block = MainPluginClass.getPlugin().getProtectionsModule()
					.getProtectionBlockById(SettingString.SETTINGS_PROTECTION_STARTERBLOCK.toString());
			if (block != null) {
				try {
					e.getPlayer().getInventory().addItem(block.getInformation().generateItem());
				} catch (ProtectionBlocksGenerateItemException e1) {
					e1.sendError(Bukkit.getConsoleSender());
					return;
				}
			}
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

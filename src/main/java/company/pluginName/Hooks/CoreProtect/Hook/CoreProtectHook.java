package company.pluginName.Hooks.CoreProtect.Hook;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Hooks.CoreProtect.CoreProtectAPI;
import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import net.coreprotect.CoreProtect;

public class CoreProtectHook extends PandaAbstractHook {

	@PandaInject
	private static MainPluginClass plugin;

	@Override
	public void load() throws Throwable {
		if (Bukkit.getPluginManager().getPlugin("CoreProtect") != null) {
			hooked = true;
		} else {
			throw new Exception("CoreProtect not found as plugin in the server.");
		}
	}

	@Override
	public void unload() throws Throwable {
	}

	public void registerProtectionPlacement(Player player, Block block) {
		registerProtectionPlacement(player.getName(), block);
	}

	public void registerProtectionPlacement(String playerName, Block block) {
		if (CoreProtectAPI.ENABLED.isTrue() && CoreProtectAPI.REGISTERPROTECTIONPLACEMENT.isTrue()) {
			try {
				CoreProtect.getInstance().getAPI().logPlacement(playerName, block.getLocation(), block.getType(),
						block.getBlockData());
			} catch (Throwable e) {
				MainPluginClass.getSimpleLogger().sendError("Unable to send CoreProtect placement entry:");
				e.printStackTrace();
			}
		}
	}

	public void registerProtectionRemoval(Player player, Block block) {
		registerProtectionRemoval(player.getName(), block);
	}

	public void registerProtectionRemoval(String playerName, Block block) {
		if (CoreProtectAPI.ENABLED.isTrue() && CoreProtectAPI.REGISTERPROTECTIONREMOVAL.isTrue()) {
			try {
				CoreProtect.getInstance().getAPI().logRemoval(playerName, block.getLocation(), block.getType(),
						block.getBlockData());
			} catch (Throwable e) {
				MainPluginClass.getSimpleLogger().sendError("Unable to send CoreProtect removal entry:");
				e.printStackTrace();
			}
		}
	}

}

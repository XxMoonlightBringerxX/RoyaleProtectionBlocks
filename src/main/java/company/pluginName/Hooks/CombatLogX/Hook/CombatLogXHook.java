package company.pluginName.Hooks.CombatLogX.Hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.sirblobman.combatlogx.api.ICombatLogX;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;

public class CombatLogXHook extends PandaAbstractHook {

	private ICombatLogX api;

	@Override
	public void load() throws Throwable {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("CombatLogX");
		if (plugin == null || !plugin.isEnabled()) {
			throw new NullPointerException("Unable to find DeluxeCombat data");
		}

		api = (ICombatLogX) plugin;
		;

		this.hooked = true;
	}

	@Override
	public void unload() throws Throwable {
	}

	public boolean isInCombat(Player player) {
		return this.api.getCombatManager().isInCombat(player);
	}

}

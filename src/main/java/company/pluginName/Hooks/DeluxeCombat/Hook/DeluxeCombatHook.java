package company.pluginName.Hooks.DeluxeCombat.Hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import nl.marido.deluxecombat.api.DeluxeCombatAPI;

public class DeluxeCombatHook extends PandaAbstractHook {

	private DeluxeCombatAPI api;

	@Override
	public void load() throws Throwable {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("DeluxeCombat");
		if (plugin == null || !plugin.isEnabled()) {
			throw new NullPointerException("Unable to find DeluxeCombat data");
		}

		api = new DeluxeCombatAPI();

		this.hooked = true;
	}

	@Override
	public void unload() throws Throwable {
	}

	public boolean isInCombat(Player player) {
		return this.api.isInCombat(player);
	}

}

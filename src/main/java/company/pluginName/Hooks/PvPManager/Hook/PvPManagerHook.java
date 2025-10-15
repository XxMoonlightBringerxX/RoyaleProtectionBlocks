package company.pluginName.Hooks.PvPManager.Hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import me.chancesd.pvpmanager.player.CombatPlayer;

public class PvPManagerHook extends PandaAbstractHook {

	@Override
	public void load() throws Throwable {
		if (!Bukkit.getPluginManager().isPluginEnabled("PvPManager")) {
			throw new NullPointerException("Unable to find PvPManager data");
		}

		try {
			Class.forName("me.chancesd.pvpmanager.player.CombatPlayer");
		} catch (Exception e) {
			throw new NullPointerException("Unable to find PvPManager data");
		}

		this.hooked = true;
	}

	@Override
	public void unload() throws Throwable {
	}

	public boolean isInCombat(Player player) {
		return CombatPlayer.get(player).isInCombat();
	}

}

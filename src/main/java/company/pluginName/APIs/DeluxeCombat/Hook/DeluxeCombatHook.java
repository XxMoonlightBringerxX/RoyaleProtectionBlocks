package company.pluginName.APIs.DeluxeCombat.Hook;

import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import nl.marido.deluxecombat.api.DeluxeCombatAPI;

public class DeluxeCombatHook extends PandaAbstractHook {

	private DeluxeCombatAPI api;

	@Override
	public void load() throws Throwable {
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

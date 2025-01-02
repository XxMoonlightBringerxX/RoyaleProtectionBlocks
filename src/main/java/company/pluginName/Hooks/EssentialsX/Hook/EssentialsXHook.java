package company.pluginName.Hooks.EssentialsX.Hook;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;

public class EssentialsXHook extends PandaAbstractHook {

	private Essentials api;

	@Override
	public void load() throws Throwable {
		if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
			this.api = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

			this.hooked = true;
		} else {
			throw new NullPointerException("Unable to find Essentials data");
		}
	}

	@Override
	public void unload() throws Throwable {
	}

	public void setLastLocation(Player player, Location location) {
		this.api.getUser(player).setLastLocation(location);
	}

}
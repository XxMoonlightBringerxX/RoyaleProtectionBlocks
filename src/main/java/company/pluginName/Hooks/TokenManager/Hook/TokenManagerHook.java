package company.pluginName.Hooks.TokenManager.Hook;

import java.util.OptionalLong;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import me.realized.tokenmanager.api.TokenManager;

public class TokenManagerHook extends PandaAbstractHook {

	private TokenManager api;

	@Override
	public void load() throws Throwable {
		this.api = (TokenManager) Bukkit.getServer().getPluginManager().getPlugin("TokenManager");

		if (this.api != null) {
			this.hooked = true;
		} else {
			throw new NullPointerException("Unable to find TokenManager data");
		}
	}

	@Override
	public void unload() throws Throwable {
	}

	public boolean withdraw(Player player, long amount) {
		OptionalLong current = this.api.getTokens(player);
		if (current.isEmpty() || current.getAsLong() < amount) {
			return false;
		}

		return this.api.removeTokens(player, amount);
	}

	public boolean deposit(Player player, long amount) {
		return this.api.addTokens(player, amount);
	}

}

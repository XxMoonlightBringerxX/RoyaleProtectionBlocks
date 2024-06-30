package company.pluginName.Hooks.TokenManager.Hook;

import java.util.OptionalLong;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import me.realized.tokenmanager.api.TokenManager;

public class TokenManagerHook extends PandaAbstractHook {

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private static ProtectionsService protectionsService;

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

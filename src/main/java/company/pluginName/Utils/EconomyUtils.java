package company.pluginName.Utils;

import org.bukkit.entity.Player;

import company.pluginName.Enums.EconomyService;
import company.pluginName.Hooks.TokenManager.TokenManagerAPI;
import company.pluginName.Hooks.VaultAPI.VaultAPI;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;

public class EconomyUtils {

	@PandaInject
	private static VaultAPI vaultApi;

	@PandaInject
	private static TokenManagerAPI tokenManagerApi;

	public static boolean isVaultHooked() {
		return vaultApi.isHooked();
	}

	public static boolean isTokenManagerHooked() {
		return tokenManagerApi.isHooked();
	}

	public static boolean withdraw(Player player, double amount) {
		return withdraw(null, player, amount);
	}

	public static boolean withdraw(EconomyService economyService, Player player, double amount) {
		if (economyService == null) {
			if (vaultApi.isHooked() && vaultApi.getHook().withdraw(player, amount)) {
				return true;
			} else if (tokenManagerApi.isHooked() && tokenManagerApi.getHook().withdraw(player, (long) amount)) {
				return true;
			}
		} else if (economyService == EconomyService.VAULT) {
			if (vaultApi.isHooked()) {
				return vaultApi.getHook().withdraw(player, amount);
			}
		} else if (economyService == EconomyService.TOKENMANAGER) {
			if (tokenManagerApi.isHooked()) {
				return tokenManagerApi.getHook().withdraw(player, (long) amount);
			}
		}

		return false;
	}

	public static boolean deposit(Player player, double amount) {
		return deposit(null, player, amount);
	}

	public static boolean deposit(EconomyService economyService, Player player, double amount) {
		if (economyService == null) {
			if (vaultApi.isHooked() && vaultApi.getHook().deposit(player, amount)) {
				return true;
			} else if (tokenManagerApi.isHooked() && tokenManagerApi.getHook().deposit(player, (long) amount)) {
				return true;
			}
		} else if (economyService == EconomyService.VAULT) {
			if (vaultApi.isHooked()) {
				return vaultApi.getHook().deposit(player, amount);
			}
		} else if (economyService == EconomyService.TOKENMANAGER) {
			if (tokenManagerApi.isHooked()) {
				return tokenManagerApi.getHook().deposit(player, (long) amount);
			}
		}

		return false;
	}

}

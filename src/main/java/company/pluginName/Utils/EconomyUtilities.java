package company.pluginName.Utils;

import org.bukkit.entity.Player;

import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.PandaEconomiesService;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.Enums.EconomyService;

public class EconomyUtilities {

	@PandaInject
	private static PandaEconomiesService service;

	public static boolean withdraw(Player player, double amount) {
		return withdraw(null, player, amount);
	}

	public static boolean withdraw(EconomyService economyService, Player player, double amount) {
		if (PermissionsService.ECONOMY_BYPASS.hasPermission(player)) {
			return true;
		}

		return service.withdraw(economyService, player, amount);
	}

	public static boolean deposit(Player player, double amount) {
		return deposit(null, player, amount);
	}

	public static boolean deposit(EconomyService economyService, Player player, double amount) {
		if (PermissionsService.ECONOMY_BYPASS.hasPermission(player)) {
			return true;
		}

		return service.deposit(economyService, player, amount);
	}

	public static boolean canAfford(Player player, double amount) {
		return canAfford(null, player, amount);
	}

	public static boolean canAfford(EconomyService economyService, Player player, double amount) {
		if (PermissionsService.ECONOMY_BYPASS.hasPermission(player)) {
			return true;
		}

		return service.canAfford(economyService, player, amount);
	}

	public static double getBalance(Player player) {
		return getBalance(null, player);
	}

	public static double getBalance(EconomyService economyService, Player player) {
		return service.getBalance(economyService, player);
	}

	public static boolean isEconomyEnabled(EconomyService economyService) {
		return service.isEconomyEnabled(economyService);
	}

}

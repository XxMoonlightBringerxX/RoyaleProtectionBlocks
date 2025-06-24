package company.pluginName.Modules.PendingPaymentsPckg;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.SQLPckg.SQLService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.PandaEconomiesService;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.Enums.EconomyService;
import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue.FailableRunnable;
import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue.FailableSupplier;

@PandaService
public class PendingPaymentsService {

	private Map<UUID, Map<EconomyService, Double>> pendingPayments = new HashMap<>();

	@PandaInject
	private SQLService sqlService;

	@PandaInject
	private PandaEconomiesService service;

	@LoadMethod
	private void load() throws RoyaleProtectionBlocksExceptionImpl {
		this.pendingPayments = this.sqlService.getPendingPayments();
	}

	public void deposit(UUID playerUuid, double amount) {
		deposit(null, playerUuid, amount);
	}

	public void deposit(EconomyService economyService, UUID playerUuid, double amount) {
		try {
			executeSynchronized(() -> {
				Player pl = Bukkit.getPlayer(playerUuid);

				if (pl != null) {
					service.deposit(economyService, pl, amount);
				} else {
					this.pendingPayments.computeIfAbsent(playerUuid, (uuidKey) -> new HashMap<>()).compute(
							economyService, (uuid, curAmount) -> curAmount != null ? curAmount + amount : amount);

					double finalAmount = this.pendingPayments.get(playerUuid).get(economyService);

					TasksUtils.executeOnAsync(() -> {
						try {
							this.sqlService.savePendingPayment(playerUuid, economyService, finalAmount);
						} catch (RoyaleProtectionBlocksExceptionImpl e) {
							e.sendError(Bukkit.getConsoleSender());
						}
					});
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void processPendingPayments(Player player) {
		try {
			executeSynchronized(() -> {
				Map<EconomyService, Double> pendingPayments = this.pendingPayments.remove(player.getUniqueId());

				if (pendingPayments != null) {
					pendingPayments
							.forEach((economyService, amount) -> service.deposit(economyService, player, amount));

					TasksUtils.executeOnAsync(() -> {
						try {
							this.sqlService.deletePendingPayments(player.getUniqueId());
						} catch (RoyaleProtectionBlocksExceptionImpl e) {
							e.sendError(Bukkit.getConsoleSender());
						}
					});
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void removeDeposit(UUID playerUuid, EconomyService economyService) {
		try {
			executeSynchronized(() -> {
				if (this.pendingPayments.containsKey(playerUuid)
						&& this.pendingPayments.get(playerUuid).containsKey(economyService)) {
					this.pendingPayments.get(playerUuid).remove(economyService);

					TasksUtils.executeOnAsync(() -> {
						try {
							this.sqlService.deletePendingPayment(playerUuid, economyService);
						} catch (RoyaleProtectionBlocksExceptionImpl e) {
							e.sendError(Bukkit.getConsoleSender());
						}
					});
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public <T> T executeSynchronizedWithReturn(FailableSupplier<T> func) throws Throwable {
		synchronized (this) {
			return func.get();
		}
	}

	public void executeSynchronized(FailableRunnable func) throws Throwable {
		executeSynchronizedWithReturn(() -> {
			func.run();
			return null;
		});
	}

}

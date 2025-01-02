package company.pluginName.Modules.PlayerGuardPckg;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.SQLPckg.SQLService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;

@PandaService
public class PlayerGuardService {

	@PandaInject
	private SQLService sqlService;

	private Map<UUID, Long> guardExpirationDates = new HashMap<>();

	@LoadMethod
	private void load() {
		try {
			sqlService.clearExpiredPlayerGuard();
			guardExpirationDates = sqlService.getPlayerGuards();
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			e.sendError(Bukkit.getConsoleSender());
		}
	}

	public void setGuardExpirationDate(UUID uuid, long guardExpirationDate) {
		if (guardExpirationDate < System.currentTimeMillis()) {
			removeGuardExpirationDate(uuid);
		} else {
			this.guardExpirationDates.put(uuid, guardExpirationDate);
		}
	}

	public void removeGuardExpirationDate(UUID uuid) {
		this.guardExpirationDates.remove(uuid);
	}

	public Long getGuardExpirationDate(UUID uuid) {
		return this.guardExpirationDates.getOrDefault(uuid, 0L);
	}

	public boolean isGuardActive(UUID uuid) {
		return System.currentTimeMillis() <= getGuardExpirationDate(uuid);
	}

}

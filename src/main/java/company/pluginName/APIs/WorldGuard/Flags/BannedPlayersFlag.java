package company.pluginName.APIs.WorldGuard.Flags;

import lombok.NonNull;
import relampagorojo93.LibsCollection.Utils.Bukkit.APIs.WorldGuard.WorldGuardAPI;
import relampagorojo93.LibsCollection.Utils.Bukkit.APIs.WorldGuard.Objects.AbstractStringSetFlag;

public class BannedPlayersFlag extends AbstractStringSetFlag {

	public BannedPlayersFlag(@NonNull WorldGuardAPI worldGuardApi) {
		super(worldGuardApi);
	}

	@Override
	public String getFlagName() {
		return "banned-players";
	}

}

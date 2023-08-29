package company.pluginName.APIs.WorldGuard.Flags;

import relampagorojo93.LibsCollection.Utils.Bukkit.APIs.WorldGuard.WorldGuardAPI;
import relampagorojo93.LibsCollection.Utils.Bukkit.APIs.WorldGuard.Objects.AbstractLocationFlag;

public class ProtectionBlockLocationFlag extends AbstractLocationFlag {

	public ProtectionBlockLocationFlag(WorldGuardAPI worldGuardApi) {
		super(worldGuardApi);
	}

	@Override
	public String getFlagName() {
		return "protection-block-location";
	}

}

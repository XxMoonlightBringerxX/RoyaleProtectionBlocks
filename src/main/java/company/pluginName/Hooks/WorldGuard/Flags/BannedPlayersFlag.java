package company.pluginName.Hooks.WorldGuard.Flags;

import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Hook.PandaAbstractWorldGuardHook;
import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Objects.AbstractStringSetFlag;
import lombok.NonNull;

public class BannedPlayersFlag extends AbstractStringSetFlag {

	public BannedPlayersFlag(@NonNull PandaAbstractWorldGuardHook worldGuardApi) {
		super(worldGuardApi);
	}

	@Override
	public String getFlagName() {
		return "banned-players";
	}

}

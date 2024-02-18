package company.pluginName.APIs.WorldGuard.Flags;

import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Hook.PandaAbstractWorldGuardHook;
import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Objects.AbstractLocationFlag;

public class ProtectionBlockLocationFlag extends AbstractLocationFlag {

	public ProtectionBlockLocationFlag(PandaAbstractWorldGuardHook worldGuardApi) {
		super(worldGuardApi);
	}

	@Override
	public String getFlagName() {
		return "protection-block-location";
	}

}

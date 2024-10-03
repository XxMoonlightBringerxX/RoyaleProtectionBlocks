package company.pluginName.Hooks.WorldGuard.Hook;

import java.util.Arrays;
import java.util.List;

import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Hook.PandaAbstractWorldGuardHook;
import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Objects.AbstractFlag;

public class WorldGuardHook extends PandaAbstractWorldGuardHook {

	@Override
	protected List<AbstractFlag<?>> getFlags() {
		return Arrays.asList();
	}

	@Override
	protected void registerHandlers() throws Exception {
	}

}

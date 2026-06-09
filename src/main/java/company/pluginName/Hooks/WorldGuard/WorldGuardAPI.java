package company.pluginName.Hooks.WorldGuard;

import company.pluginName.Hooks.WorldGuard.Hook.WorldGuardHook;
import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.PandaAbstractWorldGuardAPI;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;

@PandaService(priority = -1, loadOn = LoadStep.LOAD)
public class WorldGuardAPI extends PandaAbstractWorldGuardAPI<WorldGuardHook> {

	@Override
	protected WorldGuardHook newHook() throws Throwable {
		return new WorldGuardHook();
	}

	@Override
	protected boolean isRequired() {
		return true;
	}

}

package company.pluginName.Hooks.CombatLogX;

import company.pluginName.Hooks.CombatLogX.Hook.CombatLogXHook;
import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;

public class CombatLogXAPI extends PandaAPIService<CombatLogXHook> {

	@Override
	protected String getPluginName() {
		return "CombatLogX";
	}

	@Override
	protected CombatLogXHook newHook() throws Throwable {
		return new CombatLogXHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}

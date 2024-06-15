package company.pluginName.APIs.DeluxeCombat;

import company.pluginName.APIs.DeluxeCombat.Hook.DeluxeCombatHook;
import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;

public class DeluxeCombatAPI extends PandaAPIService<DeluxeCombatHook> {

	@Override
	protected String getPluginName() {
		return "DeluxeCombat";
	}

	@Override
	protected DeluxeCombatHook newHook() throws Throwable {
		return new DeluxeCombatHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}

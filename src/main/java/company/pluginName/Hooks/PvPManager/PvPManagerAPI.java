package company.pluginName.Hooks.PvPManager;

import company.pluginName.Hooks.PvPManager.Hook.PvPManagerHook;
import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;

public class PvPManagerAPI extends PandaAPIService<PvPManagerHook> {

	@Override
	protected String getPluginName() {
		return "PvPManager";
	}

	@Override
	protected PvPManagerHook newHook() throws Throwable {
		return new PvPManagerHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}

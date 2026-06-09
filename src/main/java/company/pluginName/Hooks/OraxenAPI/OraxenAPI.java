package company.pluginName.Hooks.OraxenAPI;

import company.pluginName.Hooks.OraxenAPI.Hook.OraxenHook;
import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;

public class OraxenAPI extends PandaAPIService<OraxenHook> {

	@Override
	protected String getPluginName() {
		return "OraxenAPI";
	}

	@Override
	protected OraxenHook newHook() throws Throwable {
		return new OraxenHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}

package company.pluginName.APIs.OraxenAPI;

import company.pluginName.APIs.OraxenAPI.Hook.OraxenHook;
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

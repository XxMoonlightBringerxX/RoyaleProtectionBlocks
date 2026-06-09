package company.pluginName.Hooks.NexoAPI;

import company.pluginName.Hooks.NexoAPI.Hook.NexoHook;
import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;

public class NexoAPI extends PandaAPIService<NexoHook> {

	@Override
	protected String getPluginName() {
		return "NexoAPI";
	}

	@Override
	protected NexoHook newHook() throws Throwable {
		return new NexoHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}

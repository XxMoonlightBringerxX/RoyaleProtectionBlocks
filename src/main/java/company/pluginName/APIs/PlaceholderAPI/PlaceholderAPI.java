package company.pluginName.APIs.PlaceholderAPI;

import company.pluginName.APIs.PlaceholderAPI.Hook.PlaceholderHook;
import darkpanda73.PandaUtils.PandaAPIs.Defaults.PlaceholderAPI.PandaAbstractPlaceholderAPI;

public class PlaceholderAPI extends PandaAbstractPlaceholderAPI<PlaceholderHook> {

	@Override
	protected PlaceholderHook newHook() throws Throwable {
		return new PlaceholderHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}

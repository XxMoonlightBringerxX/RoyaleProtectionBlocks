package company.pluginName.Hooks.PlaceholderAPI;

import company.pluginName.Hooks.PlaceholderAPI.Hook.PlaceholderHook;
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

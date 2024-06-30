package company.pluginName.Hooks.ItemsAdderAPI;

import company.pluginName.Hooks.ItemsAdderAPI.Hook.ItemsAdderHook;
import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;

public class ItemsAdderAPI extends PandaAPIService<ItemsAdderHook> {

	@Override
	protected String getPluginName() {
		return "ItemsAdderAPI";
	}

	@Override
	protected ItemsAdderHook newHook() throws Throwable {
		return new ItemsAdderHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}

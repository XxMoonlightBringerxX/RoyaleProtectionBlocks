package company.pluginName.APIs.ItemsAdderAPI;

import company.pluginName.APIs.ItemsAdderAPI.Hook.ItemsAdderHook;
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

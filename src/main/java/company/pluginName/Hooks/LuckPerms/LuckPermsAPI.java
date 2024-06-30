package company.pluginName.Hooks.LuckPerms;

import company.pluginName.Hooks.LuckPerms.Hook.LuckPermsHook;
import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;

public class LuckPermsAPI extends PandaAPIService<LuckPermsHook> {

	@Override
	protected String getPluginName() {
		return "LuckPerms";
	}

	@Override
	protected LuckPermsHook newHook() throws Throwable {
		return new LuckPermsHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}

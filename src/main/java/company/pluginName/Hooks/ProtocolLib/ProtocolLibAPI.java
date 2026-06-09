package company.pluginName.Hooks.ProtocolLib;

import company.pluginName.Hooks.ProtocolLib.Hook.ProtocolLibHook;
import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;

public class ProtocolLibAPI extends PandaAPIService<ProtocolLibHook> {

	@Override
	protected ProtocolLibHook newHook() throws Throwable {
		return new ProtocolLibHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

	@Override
	protected String getPluginName() {
		return "ProtocolLib";
	}

}

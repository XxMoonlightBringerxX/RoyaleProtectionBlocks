package company.pluginName.Hooks.ProtectionStonesAPI;

import company.pluginName.Hooks.ProtectionStonesAPI.Hook.ProtectionStonesHook;
import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;

@PandaService(priority = -1, loadOn = LoadStep.ENABLE)
public class ProtectionStonesAPI extends PandaAPIService<ProtectionStonesHook> {

	@Override
	protected String getPluginName() {
		return "ProtectionStones";
	}

	@Override
	protected ProtectionStonesHook newHook() throws Throwable {
		return new ProtectionStonesHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}

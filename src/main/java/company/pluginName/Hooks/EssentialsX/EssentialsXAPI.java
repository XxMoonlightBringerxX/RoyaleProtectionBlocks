package company.pluginName.Hooks.EssentialsX;

import company.pluginName.Hooks.EssentialsX.Hook.EssentialsXHook;
import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;

@PandaService(priority = -1, loadOn = LoadStep.ENABLE)
public class EssentialsXAPI extends PandaAPIService<EssentialsXHook> {

	@Override
	protected String getPluginName() {
		return "Essentials";
	}

	@Override
	protected EssentialsXHook newHook() throws Throwable {
		return new EssentialsXHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}

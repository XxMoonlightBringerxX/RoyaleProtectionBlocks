package company.pluginName.Hooks.TokenManager;

import company.pluginName.Hooks.TokenManager.Hook.TokenManagerHook;
import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;

@PandaService(priority = -1, loadOn = LoadStep.ENABLE)
public class TokenManagerAPI extends PandaAPIService<TokenManagerHook> {

	@Override
	protected String getPluginName() {
		return "TokenManager";
	}

	@Override
	protected TokenManagerHook newHook() throws Throwable {
		return new TokenManagerHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}

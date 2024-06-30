package company.pluginName.Hooks.VaultAPI;

import company.pluginName.Hooks.VaultAPI.Hook.VaultHook;
import darkpanda73.PandaUtils.PandaAPIs.Defaults.Vault.PandaAbstractVaultAPI;

public class VaultAPI extends PandaAbstractVaultAPI<VaultHook> {

	@Override
	protected VaultHook newHook() throws Throwable {
		return new VaultHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}

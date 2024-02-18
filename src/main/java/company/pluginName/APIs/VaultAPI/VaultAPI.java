package company.pluginName.APIs.VaultAPI;

import company.pluginName.APIs.VaultAPI.Hook.VaultHook;
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

package company.pluginName.Hooks.CoreProtect;

import company.pluginName.Hooks.CoreProtect.Hook.CoreProtectHook;
import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;

public class CoreProtectAPI extends PandaAPIService<CoreProtectHook> {

	@RegisteredPandaField("config")
	public static final PandaBooleanField ENABLED = new PandaBooleanField("Settings.Core-protect-hook.Enabled", false);

	@RegisteredPandaField("config")
	public static final PandaBooleanField REGISTERPROTECTIONPLACEMENT = new PandaBooleanField(
			"Settings.Core-protect-hook.Register-protection-placement", true);
	@RegisteredPandaField("config")
	public static final PandaBooleanField REGISTERPROTECTIONREMOVAL = new PandaBooleanField(
			"Settings.Core-protect-hook.Register-protection-removal", true);

	@Override
	protected CoreProtectHook newHook() throws Throwable {
		return new CoreProtectHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

	@Override
	protected String getPluginName() {
		return "CoreProtect";
	}

}

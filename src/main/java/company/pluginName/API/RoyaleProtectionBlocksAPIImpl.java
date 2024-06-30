package company.pluginName.API;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import lombok.Getter;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;

@PandaService
public class RoyaleProtectionBlocksAPIImpl extends RoyaleProtectionBlocksAPI {

	@PandaInject
	private @Getter PlayerInteractionsService playerInteractionsService;

	public RoyaleProtectionBlocksAPIImpl() {
		super();
	}

}

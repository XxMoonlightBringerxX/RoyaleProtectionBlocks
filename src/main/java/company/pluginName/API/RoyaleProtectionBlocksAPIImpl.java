package company.pluginName.API;

import company.pluginName.Modules.PlayerInteractionsPckg.PlayerInteractionsServiceImpl;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsServiceImpl;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import lombok.Getter;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;

@PandaService
public class RoyaleProtectionBlocksAPIImpl extends RoyaleProtectionBlocksAPI {

	@PandaInject
	private @Getter PlayerInteractionsServiceImpl playerInteractionsService;

	@PandaInject
	private @Getter ProtectionsServiceImpl protectionsService;

	public RoyaleProtectionBlocksAPIImpl() {
		super();
	}

	public static RoyaleProtectionBlocksAPIImpl getInstance() {
		return (RoyaleProtectionBlocksAPIImpl) RoyaleProtectionBlocksAPI.getInstance();
	}

}

package company.pluginName.Modules.ProtectionBlocksPckg.Objects.Reference;

import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Utilities.Java.Objects.ReferencedObject;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.ProtectionBlocks.IProtectionBlock;

public class ReferencedProtectionBlock extends ReferencedObject<IProtectionBlock> {

	@PandaInject
	public static ProtectionBlocksService protectionBlocksService;

	public ReferencedProtectionBlock(String identifier) {
		super(identifier, (id) -> protectionBlocksService.getProtectionBlockById(id));
	}

}

package company.pluginName.Modules.ProtectionBlocksPckg.Objects.Reference;

import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import relampagorojo93.LibsCollection.Utils.Shared.Java.Objects.ReferencedObject;

public class ReferencedProtectionBlock extends ReferencedObject<ProtectionBlock> {

	@PandaInject
	public static ProtectionBlocksService protectionBlocksService;

	public ReferencedProtectionBlock(String identifier) {
		super(identifier, (id) -> protectionBlocksService.getProtectionBlockById(id));
	}

}

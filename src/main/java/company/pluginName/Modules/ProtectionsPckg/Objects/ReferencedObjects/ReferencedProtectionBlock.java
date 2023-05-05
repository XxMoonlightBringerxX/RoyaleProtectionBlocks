package company.pluginName.Modules.ProtectionsPckg.Objects.ReferencedObjects;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import relampagorojo93.LibsCollection.Utils.Shared.Java.Objects.ReferencedObject;

public class ReferencedProtectionBlock extends ReferencedObject<ProtectionBlock> {

	public ReferencedProtectionBlock(String identifier) {
		super(identifier, (id) -> MainPluginClass.getPlugin().getProtectionsModule().getProtectionBlockById(id));
	}

}

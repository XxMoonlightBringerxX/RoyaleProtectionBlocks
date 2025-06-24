package company.pluginName.Utils;

import org.bukkit.command.CommandSender;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;

public class LogUtilities {

	private static final String FLAG_MODIFICATION_LOG = "&7A flag on a protection has been modified &e[Protection ID: %s] [Flag ID: %s] [Previous value: %s] [New value: %s]";

	public static void sendFlagModificationDebugLog(CommandSender commandSender, Protection protection, String flagId,
			Object previousValue, Object newValue) {
		MainPluginClass.getSimpleLogger().sendDebug(String.format(FLAG_MODIFICATION_LOG, protection.getProtectionId(),
				flagId, previousValue != null ? previousValue.toString() : "null", newValue.toString()));
	}

}

package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;

public class SetHomeSubCommand extends SubCommand {

	public SetHomeSubCommand(Command command) {
		super(command, "sethome", SettingString.COMMANDS_PROTECTIONBLOCKS_SETHOME_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_SETHOME_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_SETHOME_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_SETHOME_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_SETHOME_ALIASES.getContent());
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			Protection protection = MainPluginClass.getPlugin().getProtectionsModule()
					.getProtectionByLocation(pl.getLocation());
			if (protection != null) {
				if (protection.isMainOwner(pl.getUniqueId())) {
					try {
						ProtectedRegion region = protection.getProtectedRegion();
						Map<Flag<?>, Object> flags = region.getFlags();
						flags.put(Flags.TELE_LOC,
								MainPluginClass.getWorldGuardAPI().getInternalWorldGuard().adapt(pl.getLocation()));
						region.setFlags(flags);
						MessageBuilder
								.createMessage(MessageString.MESSAGE_PROTECTIONS_HOMESETSUCCESSFULLY.applyPrefix())
								.sendMessage(sender);
					} catch (Exception e) {
						MessageBuilder.createMessage(MessageString.ERROR_ERROR.applyPrefix()).sendMessage(sender);
						e.printStackTrace();
					}
				} else {
					MessageBuilder.createMessage(MessageString.ERROR_PROTECTIONS_NOTMAINOWNER.applyPrefix())
							.sendMessage(sender);
				}
			} else {
				MessageBuilder.createMessage(MessageString.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix())
						.sendMessage(sender);
			}
		} else {
			MessageBuilder.createMessage(MessageString.ERROR_CONSOLEDENIED.applyPrefix()).sendMessage(sender);
		}
		return true;
	}

}

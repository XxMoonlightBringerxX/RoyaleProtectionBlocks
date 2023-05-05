package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;

public class HideSubCommand extends SubCommand {

	public HideSubCommand(Command command) {
		super(command, "hide", SettingString.COMMANDS_PROTECTIONBLOCKS_HIDE_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_HIDE_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_HIDE_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_HIDE_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_HIDE_ALIASES.getContent());
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			Protection protection = MainPluginClass.getPlugin().getProtectionsModule()
					.getProtectionByLocation(pl.getLocation());
			if (protection != null) {
				if (protection.isMainOwner(pl.getUniqueId())) {
					ProtectionBlock block = protection.getProtectionBlock().getObject();
					if (block.getItem().getType() != Material.PAPER.getMaterial()) {
						if (protection.isProtectionBlock(protection.getProtectionBlockLocation().getBlock())) {
							protection.getProtectionBlockLocation().getBlock().setType(Material.AIR.getMaterial());
							MessageBuilder
									.createMessage(MessageString.MESSAGE_PROTECTIONS_HIDDENSUCCESSFULLY.applyPrefix())
									.sendMessage(sender);
						} else {
							MessageBuilder
									.createMessage(MessageString.ERROR_PROTECTIONS_BLOCKALREADYHIDDEN.applyPrefix())
									.sendMessage(sender);
						}
					} else {
						MessageBuilder
								.createMessage(MessageString.ERROR_PROTECTIONS_ORAXENINCOMPATIBILITY.applyPrefix())
								.sendMessage(sender);
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

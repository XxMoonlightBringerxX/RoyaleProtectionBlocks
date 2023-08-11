package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Blocks;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;

public class GiveSubCommand extends SubCommand {

	public GiveSubCommand(Command command) {
		super(command, "give", SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_GIVE_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_GIVE_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_GIVE_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_GIVE_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_BLOCKS_GIVE_ALIASES.getContent());
	}

	@Override
	public List<String> tabComplete(Command cmd, CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			return MainPluginClass.getPlugin().getProtectionsModule().getProtectionBlockById().keySet().stream()
					.collect(Collectors.toList());
		case 2:
			return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
		default:
			return EMPTY_LIST;
		}
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (args.length > 1) {
			ProtectionBlock block = MainPluginClass.getPlugin().getProtectionsModule().getProtectionBlockById(args[1]);
			if (block != null) {
				Player toGive = pl;

				if (args.length > 2) {
					toGive = Bukkit.getPlayer(args[2]);
				}

				if (toGive == null) {
					if (pl == null) {
						MessageBuilder.createMessage(MessageString.ERROR_CONSOLEDENIED.applyPrefix())
								.sendMessage(sender);
					} else {
						MessageBuilder.createMessage(MessageString.ERROR_PLAYERNOTFOUND.applyPrefix())
								.sendMessage(sender);
					}
					return true;
				}

				toGive.getInventory().addItem(block.generateItem());
			} else {
				MessageBuilder.createMessage(MessageString.ERROR_PROTECTIONS_BLOCKS_NOTFOUND.applyPrefix())
						.sendMessage(sender);
			}
		} else {
			MessageBuilder.createMessage(MessageString.applyPrefix(getUsage())).sendMessage(sender);
		}
		return true;
	}
}

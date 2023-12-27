package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.ProtectionBlocks.ProtectionBlocksShopInventory;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;

public class BuySubCommand extends SubCommand {

	public BuySubCommand(Command command) {
		super(command, "buy", SettingString.COMMANDS_PROTECTIONBLOCKS_BUY_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BUY_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BUY_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BUY_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_BUY_ALIASES.getContent());
	}

	@Override
	public List<String> tabComplete(Command cmd, CommandSender sender, String[] args) {
		if (args.length == 1) {
			return MainPluginClass.getPlugin().getProtectionsModule().getProtectionBlocks().entrySet().stream()
					.filter(entry -> entry.getValue().getInformation().isForSale()).map(Entry::getKey)
					.collect(Collectors.toList());
		}
		return EMPTY_LIST;
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (args.length > 1) {
				ProtectionBlock block = MainPluginClass.getPlugin().getProtectionsModule()
						.getProtectionBlockById(args[1]);
				if (block != null) {
					if (block.getInformation().isForSale()) {
						block.purchase(pl);
					} else {
						MessageBuilder
								.createMessage(
										MessageString.applyPrefix(MessageString.ERROR_PROTECTIONS_BLOCKS_NOTFORSALE))
								.sendMessage(sender);
					}
				} else {
					MessageBuilder
							.createMessage(MessageString.applyPrefix(MessageString.ERROR_PROTECTIONS_BLOCKS_NOTFOUND))
							.sendMessage(sender);
				}
			} else {
				new ProtectionBlocksShopInventory(pl).openInventory();
			}
		} else {
			MessageBuilder.createMessage(MessageString.applyPrefix(MessageString.ERROR_CONSOLEDENIED))
					.sendMessage(sender);
		}
		return true;
	}
}

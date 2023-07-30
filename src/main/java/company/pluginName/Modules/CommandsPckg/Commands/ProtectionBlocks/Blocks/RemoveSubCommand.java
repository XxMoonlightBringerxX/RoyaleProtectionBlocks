package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Blocks;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.ProtectionBlocks.Delete.ProtectionBlocksDeleteException;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;

public class RemoveSubCommand extends SubCommand {

	public RemoveSubCommand(Command command) {
		super(command, "remove", SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_REMOVE_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_REMOVE_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_REMOVE_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_REMOVE_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_BLOCKS_REMOVE_ALIASES.getContent());
	}

	@Override
	public List<String> tabComplete(Command cmd, CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			return MainPluginClass.getPlugin().getProtectionsModule().getProtectionBlockById().keySet().stream()
					.collect(Collectors.toList());
		default:
			return EMPTY_LIST;
		}
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (args.length > 1) {
				ProtectionBlock block = MainPluginClass.getPlugin().getProtectionsModule()
						.getProtectionBlockById(args[1]);
				if (block != null) {
					try {
						block.delete(pl);
						MessageBuilder
								.createMessage(
										MessageString.MESSAGE_PROTECTIONS_BLOCKS_REMOVEDSUCCESSFULLY.applyPrefix())
								.sendMessage(pl);
					} catch (ProtectionBlocksDeleteException e) {
						e.sendError(pl);
					}
				} else {
					MessageBuilder.createMessage(MessageString.ERROR_PROTECTIONS_BLOCKS_NOTFOUND.applyPrefix())
							.sendMessage(pl);
				}
			} else {
				MessageBuilder.createMessage(MessageString.applyPrefix(getUsage())).sendMessage(pl);
			}
		} else {
			MessageBuilder.createMessage(MessageString.ERROR_CONSOLEDENIED.applyPrefix()).sendMessage(sender);
		}
		return true;
	}
}

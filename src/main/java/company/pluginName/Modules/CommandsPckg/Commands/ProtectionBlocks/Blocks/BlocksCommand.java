package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Blocks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Bukkit.Inventories.ProtectionBlocks.ProtectionBlocksListInventory;
import company.pluginName.Modules.CommandsPckg.Base.HelpSubCommand;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;

public class BlocksCommand extends Command {

	public BlocksCommand(Command command) {
		super(command, "blocks", SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_BLOCKS_ALIASES.getContent());
		addCommand(new AddSubCommand(this));
		addCommand(new RemoveSubCommand(this));
		addCommand(new GiveSubCommand(this));
		sortCommands();
		addCommand(new HelpSubCommand(this), 0);
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean userids) {
		if (!getPermission().isEmpty() && !sender.hasPermission(getPermission())) {
			return false;
		}

		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null && args.length == 0) {
			new ProtectionBlocksListInventory(pl).openInventory();
			return true;
		}
		return super.execute(cmd, sender, args, userids);
	}
}

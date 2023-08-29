package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.Protections.ProtectionsListInventory;
import company.pluginName.Bukkit.Inventories.Protections.ProtectionsManagerInventory;
import company.pluginName.Modules.CommandsPckg.Base.HelpSubCommand;
import company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Blocks.BlocksCommand;
import company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Files.FilesCommand;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;

public class ProtectionBlocksCommand extends Command {

	public ProtectionBlocksCommand() {
		super("protectionblocks", SettingString.COMMANDS_PROTECTIONBLOCKS_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_DESCRIPTION.toString(), SettingString.COMMANDS_PROTECTIONBLOCKS_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_ALIASES.getContent());
		addCommand(new ReloadSubCommand(this));
		addCommand(new SetHomeSubCommand(this));
		addCommand(new ListSubCommand(this));
		addCommand(new AddMemberSubCommand(this));
		addCommand(new AddOwnerSubCommand(this));
		addCommand(new RemoveMemberSubCommand(this));
		addCommand(new RemoveOwnerSubCommand(this));
		addCommand(new RenameSubCommand(this));
		addCommand(new BlocksCommand(this));
		addCommand(new HideSubCommand(this));
		addCommand(new ShowSubCommand(this));
		addCommand(new ViewSubCommand(this));
		addCommand(new TransferSubCommand(this));
		addCommand(new BanSubCommand(this));
		addCommand(new UnbanSubCommand(this));
		addCommand(new KickSubCommand(this));
		addCommand(new FilesCommand(this));
		sortCommands();
		addCommand(new HelpSubCommand(this), 0);
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if (!getPermission().isEmpty() && !sender.hasPermission(getPermission())) {
			return false;
		}

		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null && args.length == 0) {
			Protection protection = MainPluginClass.getPlugin().getProtectionsModule().getProtectionByLocation(pl.getLocation());
			if (protection != null && protection.canManage(pl)) {
				new ProtectionsManagerInventory(pl, protection).openInventory();
			} else {
				new ProtectionsListInventory(pl).openInventory();
			}
			return true;
		}
		return super.onCommand(sender, cmd, label, args);
	}

}
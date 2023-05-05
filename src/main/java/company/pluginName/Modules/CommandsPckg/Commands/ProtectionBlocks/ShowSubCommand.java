package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.Utils.Bukkit.BlockUtils;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

public class ShowSubCommand extends SubCommand {

	public ShowSubCommand(Command command) {
		super(command, "show", SettingString.COMMANDS_PROTECTIONBLOCKS_SHOW_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_SHOW_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_SHOW_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_SHOW_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_SHOW_ALIASES.getContent());
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
						Block bBlock = protection.getProtectionBlockLocation().getBlock();
						if (bBlock.getType() == Material.AIR.getMaterial()) {
							ItemStack item = block.getItem();
							bBlock.setType(item.getType());
							if (item.getType() == Material.PLAYER_HEAD.getMaterial()) {
								BlockUtils.setSkin(bBlock, ItemStacksUtils.getSkin(item));
							}
							MessageBuilder
									.createMessage(MessageString.MESSAGE_PROTECTIONS_SHOWNSUCCESSFULLY.applyPrefix())
									.sendMessage(sender);
						} else {
							if (protection.getProtectionBlockLocation().getBlock().getType() == block.getItem()
									.getType()) {
								MessageBuilder
										.createMessage(MessageString.ERROR_PROTECTIONS_BLOCKALREADYSHOWN.applyPrefix())
										.sendMessage(sender);
							} else {
								MessageBuilder
										.createMessage(MessageString.ERROR_PROTECTIONS_BLOCKEDBYBLOCK.applyPrefix())
										.sendMessage(sender);
							}
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

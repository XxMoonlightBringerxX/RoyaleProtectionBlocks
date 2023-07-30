package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Blocks;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.APIs.ItemsAdderAPI;
import company.pluginName.APIs.OraxenAPI;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveException;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.FilePckg.Settings.SettingList;
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

public class AddSubCommand extends SubCommand {

	public AddSubCommand(Command command) {
		super(command, "add", SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_ADD_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_ADD_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_ADD_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_BLOCKS_ADD_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_BLOCKS_ADD_ALIASES.getContent());
	}

	@Override
	public List<String> tabComplete(Command cmd, CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			return Arrays.asList("<id>");
		case 2:
			return Arrays.asList("<x>");
		case 3:
			return Arrays.asList("<y>");
		case 4:
			return Arrays.asList("<z>");
		case 5:
			return Arrays.asList("[permission]");
		default:
			return EMPTY_LIST;
		}
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (args.length > 4) {
				ItemStack i = ItemStacksUtils.getItemInMainHand(pl);
				if (i != null && i.getType() != Material.AIR) {
					if (i.getType().isBlock()
							|| MainPluginClass.getItemsAdderAPI()
									.isCustomBlock(i) == ItemsAdderAPI.BlockCheckingResult.IS_CUSTOM_ITEM
							|| MainPluginClass.getOraxenAPI()
									.isCustomBlock(i) == OraxenAPI.BlockCheckingResult.IS_CUSTOM_ITEM) {
						ItemStack protectionBlockItemstack = i.clone();
						protectionBlockItemstack.setAmount(1);
						try {
							try {
								int x = Integer.parseInt(args[2]);
								int y = Integer.parseInt(args[3]);
								int z = Integer.parseInt(args[4]);

								if (x < 0 || y < 0 || z < 0) {
									MessageBuilder.createMessage(MessageString.ERROR_NUMBERBELOWZERO.applyPrefix())
											.sendMessage(pl);
									return true;
								}

								String permission = args.length > 5 ? args[5] : null;
								ProtectionBlock protectionBlock = new ProtectionBlock(args[1].toLowerCase(),
										protectionBlockItemstack, x / 2, y / 2, z / 2, permission);
								protectionBlock.save(pl);

								protectionBlockItemstack = protectionBlock.generateItem();
								protectionBlockItemstack.setAmount(i.getAmount());

								ItemStacksUtils.setItemInMainHand(pl, protectionBlockItemstack);
								MessageBuilder.createMessage(
										MessageString.MESSAGE_PROTECTIONS_BLOCKS_CREATEDSUCCESSFULLY.applyPrefix())
										.sendMessage(pl);
							} catch (NumberFormatException e) {
								MessageBuilder.createMessage(MessageString.ERROR_INVALIDNUMBER.applyPrefix())
										.sendMessage(pl);
							}
						} catch (ProtectionBlocksSaveException e) {
							e.sendError(pl);
						}
					} else {
						MessageBuilder.createMessage(MessageString.ERROR_NOTABLOCK.applyPrefix()).sendMessage(pl);
					}
				} else {
					MessageBuilder.createMessage(MessageString.ERROR_NOITEMINHAND.applyPrefix()).sendMessage(pl);
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

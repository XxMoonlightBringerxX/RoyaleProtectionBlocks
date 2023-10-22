package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.Protections.ProtectionsListInventory;
import company.pluginName.Exceptions.ProtectionBlocks.ProtectionBlocksGenerateItemException;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import net.milkbowl.vault.economy.EconomyResponse;
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
			return new ArrayList<>(MainPluginClass.getPlugin().getProtectionsModule().getProtectionBlocks().keySet());
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
					if (block.getInformation().getPrice() != null) {
						try {
							ItemStack item = block.getInformation().generateItem();
							boolean hasAvailableSpace = true;

							if (pl.getInventory().firstEmpty() == -1) {
								if (pl.getInventory().containsAtLeast(item, 1)) {
									if (pl.getInventory().addItem(item).isEmpty()) {
										pl.getInventory().removeItem(item);
									} else {
										hasAvailableSpace = false;
									}
								} else {
									hasAvailableSpace = false;
								}
							}

							if (hasAvailableSpace) {
								EconomyResponse response = MainPluginClass.getVaultAPI().getEcon().withdrawPlayer(pl,
										block.getInformation().getPrice());
								if (response.transactionSuccess()) {
									pl.getInventory().addItem(item);
								} else {
									MessageBuilder
											.createMessage(
													MessageString.applyPrefix(MessageString.ERROR_INSUFFICIENTBALANCE))
											.sendMessage(sender);
								}
							} else {
								MessageBuilder
										.createMessage(MessageString.applyPrefix(MessageString.ERROR_INVENTORYFULL))
										.sendMessage(sender);
							}
						} catch (ProtectionBlocksGenerateItemException e) {
							e.sendError(pl);
						}
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
				new ProtectionsListInventory(pl).openInventory();
			}
		} else {
			MessageBuilder.createMessage(MessageString.applyPrefix(MessageString.ERROR_CONSOLEDENIED))
					.sendMessage(sender);
		}
		return true;
	}
}

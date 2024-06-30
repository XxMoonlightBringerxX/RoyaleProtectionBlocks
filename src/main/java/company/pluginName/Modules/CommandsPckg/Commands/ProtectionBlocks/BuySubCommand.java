package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Bukkit.Inventories.ProtectionBlocks.ProtectionBlocksShopInventory;
import company.pluginName.Hooks.VaultAPI.VaultAPI;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "buy",
		pathName = "Buy",
		defaultName = "buy",
		defaultDescription = "Purchase protection blocks which are on sale",
		defaultUsage = "[protection block]"
)
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true
)
public class BuySubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private static VaultAPI vaultApi;

	public BuySubCommand() throws InstantiationException {
		super();
	}

	@Override
	public boolean precondition() {
		return vaultApi.getHook() != null && vaultApi.getHook().isHooked() && protectionBlocksService
				.getProtectionBlocks().values().stream().anyMatch(block -> block.getInformation().isForSale());
	}

	@Override
	public List<String> tabComplete(PandaCommand cmd, CommandSender sender, String[] args) {
		if (args.length == 1) {
			return protectionBlocksService.getProtectionBlocks().entrySet().stream()
					.filter(entry -> entry.getValue().getInformation().isForSale()).map(Entry::getKey)
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (parameters.getParameters().size() > 0) {
				ProtectionBlock block = protectionBlocksService
						.getProtectionBlockById(parameters.getParameters().get(0));
				if (block != null) {
					if (block.getInformation().isForSale()) {
						block.purchase(pl);
					} else {
						MessageTemplate.inst(Messages.ERROR_PROTECTIONS_BLOCKS_NOTFORSALE.applyPrefix()).process()
								.sendMessage(sender);
					}
				} else {
					MessageTemplate.inst(Messages.ERROR_PROTECTIONS_BLOCKS_NOTFOUND.applyPrefix()).process()
							.sendMessage(sender);
				}
			} else {
				new ProtectionBlocksShopInventory(pl).openInventory();
			}
		} else {
			MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}
}

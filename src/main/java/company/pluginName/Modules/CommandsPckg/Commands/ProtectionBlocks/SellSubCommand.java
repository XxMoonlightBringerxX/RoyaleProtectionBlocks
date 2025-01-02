package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSellRequestInput;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "sell",
		pathName = "Sell",
		defaultName = "sell",
		defaultDescription = "Put your protection for sale. Set '0' to take off its current price.",
		defaultUsage = "<new cost>",
		defaultAliases = "sl",
		defaultPermission = "protectionblocks.sell")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class SellSubCommand extends PandaSubCommand {

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	public SellSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (parameters.getParameters().size() > 0) {
				try {
					double price = Double.parseDouble(parameters.getParameters().get(0));

					Protection protection = RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
							.findProtectionParentByLocation(pl.getLocation());
					if (protection != null) {
						try {
							playerInteractionsService
									.protectionSellRequest(ProtectionSellRequestInput.inst(pl, protection, price));

							if (protection.isForSale()) {
								MessageTemplate
										.inst(Messages.MESSAGE_PROTECTIONS_PURCHASE_PRICESETSUCCESSFULLY.applyPrefix())
										.setReplacements(new Replacement("{price}",
												() -> String.format(Locale.US, "%.2f", protection.getPrice())))
										.process().sendMessage(sender);
							} else {
								MessageTemplate.inst(
										Messages.MESSAGE_PROTECTIONS_PURCHASE_PRICEUNSETSUCCESSFULLY.applyPrefix())
										.process().sendMessage(sender);
							}
						} catch (RoyaleProtectionBlocksException e) {
							e.sendError(pl);
						}
					} else {
						MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
								.sendMessage(sender);
					}
				} catch (NumberFormatException e) {
					MessageTemplate.inst(Messages.ERROR_INVALIDNUMBER.applyPrefix()).process().sendMessage(sender);
				}
			} else {
				MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process().sendMessage(pl);
			}
		} else {
			MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}

}

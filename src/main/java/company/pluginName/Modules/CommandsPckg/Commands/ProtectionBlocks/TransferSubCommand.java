package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Modules.FilePckg.Messages;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionTransferRequestInput;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "transfer",
		pathName = "Transfer",
		defaultName = "transfer",
		defaultDescription = "Transfer a protection to another player. This includes all the merged protections!",
		defaultUsage = "<username>",
		defaultAliases = "t",
		defaultPermission = "protectionblocks.transfer")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class TransferSubCommand extends PandaSubCommand {

	public TransferSubCommand() throws InstantiationException {
		super();
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		if (argIndex == 0) {
			return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
		}
		return super.generateAutocompleteList(sender, argIndex);
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		return CommandResponse.queuedAsync(() -> {
			Player pl = sender instanceof Player ? (Player) sender : null;
			if (pl != null) {
				if (parameters.getParameters().size() > 0) {
					IProtection protection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
							.findProtectionParentByLocation(pl.getLocation());
					if (protection != null) {
						try {
							Player newOwner = Bukkit.getPlayer(parameters.getParameters().get(0));
							if (newOwner != null) {
								RoyaleProtectionBlocksAPI.getInstance().getPlayerInteractionsService()
										.protectionTransferRequest(
												ProtectionTransferRequestInput.inst(pl, protection, newOwner));

								MessageTemplate
										.inst(Messages.MESSAGE_PROTECTIONS_TRANSFER_TRANSFEREDSUCCESSFULLY.applyPrefix())
										.process().sendMessage(sender);
							} else {
								throw Exceptions.Generic.PLAYERNOTONLINE.generateException();
							}
						} catch (RoyaleProtectionBlocksException e) {
							e.sendError(pl);
						}
					} else {
						MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
								.sendMessage(sender);
					}
				} else {
					MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process()
							.sendMessage(pl);
				}
			} else {
				MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
			}
		});
	}
}

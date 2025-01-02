package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberRemoveRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Owners.ProtectionOwnerRemoveRequestInput;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "leave",
		pathName = "Leave",
		defaultName = "leave",
		defaultDescription = "Leave a protection, stop being an owner or a member",
		defaultAliases = "l")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true)
public class LeaveSubCommand extends PandaSubCommand {

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	public LeaveSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			Protection protection = RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
					.findProtectionParentByLocation(pl.getLocation());
			if (protection != null) {
				if (!protection.isMainOwner(pl.getUniqueId())) {
					try {
						boolean left = false;

						if (protection.getWorldGuardOwners().list().contains(pl.getUniqueId())) {
							playerInteractionsService.protectionOwnerRemoveRequest(
									ProtectionOwnerRemoveRequestInput.inst(pl, protection, pl.getUniqueId()));
							left = true;
						}

						if (protection.getWorldGuardMembers().list().contains(pl.getUniqueId())) {
							playerInteractionsService.protectionMemberRemoveRequest(
									ProtectionMemberRemoveRequestInput.inst(pl, protection, pl.getUniqueId()));
							left = true;
						}

						if (left) {
							MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_LEFTSUCCESSFULLY.applyPrefix()).process()
									.sendMessage(sender);
						} else {
							MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTMEMBER.applyPrefix()).process()
									.sendMessage(sender);
						}
					} catch (RoyaleProtectionBlocksException e) {
						e.sendError(pl);
					}
				} else {
					MessageTemplate.inst(Messages.ERROR_PROTECTIONS_LEAVEDENIEDTOMAINOWNER.applyPrefix()).process()
							.sendMessage(sender);
				}
			} else {
				MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
						.sendMessage(sender);
			}
		} else {
			MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}
}

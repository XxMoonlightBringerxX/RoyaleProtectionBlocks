package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData.InvitationRequirement;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsServiceImpl;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.PandaCachedPlayersService;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.Objects.PandaCachedPlayer;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberAddRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberInviteRequestInput;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "addmember",
		pathName = "Add-member",
		defaultName = "addmember",
		defaultDescription = "Add a member on your current protection",
		defaultUsage = "<username>",
		defaultAliases = "am")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class AddMemberSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionsServiceImpl protectionsService;

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	@PandaInject
	private static PandaCachedPlayersService cachedPlayersService;

	@PandaInject
	private static PlayerDataService playerDataService;

	public AddMemberSubCommand() throws InstantiationException {
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
					Protection protection = protectionsService.findProtectionParentByLocation(pl.getLocation());
					if (protection != null) {
						PandaCachedPlayer member = cachedPlayersService
								.getCachedPlayer(parameters.getParameters().get(0));
						if (member != null) {
							try {
								PlayerData playerData = playerDataService.getPlayerData(member.getUuid(), true);

								if (playerData != null) {
									if (playerData.getInvitationRequirement() == InvitationRequirement.ALL_DENIED) {
										throw Exceptions.Protections.Invitations.INVITATIONBLOCKED.generateException();
									} else if (playerData
											.getInvitationRequirement() == InvitationRequirement.ALL_ACCEPTED) {
										playerInteractionsService.protectionMemberAddRequest(
												ProtectionMemberAddRequestInput.inst(pl, protection, member.getUuid()));

										MessageTemplate.inst(
												Messages.MESSAGE_PROTECTIONS_MEMBERS_ADDEDSUCCESSFULLY.applyPrefix())
												.process().sendMessage(sender);
									} else {
										playerInteractionsService
												.protectionMemberInviteRequest(ProtectionMemberInviteRequestInput
														.inst(pl, protection, member.getUuid()));

										MessageTemplate
												.inst(Messages.MESSAGE_PROTECTIONS_INVITATIONS_INVITEDSUCCESSFULLY
														.applyPrefix())
												.process().sendMessage(pl);
									}
								} else {
									throw Exceptions.Protections.Invitations.UNKNOWN.generateException();
								}
							} catch (RoyaleProtectionBlocksException e) {
								e.sendError(pl);
							}
						} else {
							MessageTemplate.inst(Messages.ERROR_PLAYERNOTFOUND.applyPrefix()).process()
									.sendMessage(sender);
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

package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Bukkit.Inventories.Shared.SearchProtectionInventory;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsServiceImpl;
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
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

@PandaSubCommandAnnotation(parentCommand = AdminCommand.class)
@PandaCommandAnnotation(
		id = "lastlogin",
		pathName = "Last-login",
		defaultName = "lastlogin",
		defaultDescription = "Shows the last time a player was connected",
		defaultAliases = "ll",
		defaultPermission = "protectionblocks.admin.lastlogin",
		defaultUsage = "[protection id]")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true)
public class LastLoginSubCommand extends PandaSubCommand {

	@RegisteredPandaField("lang")
	private static final PandaPrefixedStringField MESSAGE_LASTLOGIN_ONLINEMESSAGE = new PandaPrefixedStringField(
			"Message.Last-login.Online-message", "&aThe player &e{player} &ais currently online.");

	@RegisteredPandaField("lang")
	private static final PandaPrefixedStringField MESSAGE_LASTLOGIN_OFFLINEMESSAGE = new PandaPrefixedStringField(
			"Message.Last-login.Offline-message", "&aLast time player &e{player} &awas seen: {last_played}");

	@PandaInject
	private static ProtectionsServiceImpl protectionsService;

	@PandaInject
	private static PlayerDataService playerDataService;

	public LastLoginSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (parameters.getParameters().isEmpty()) {
				PlayerData playerData = playerDataService.getPlayerData(pl);

				if (playerData.getCurrentProtections().size() > 0) {
					if (playerData.getCurrentProtections().size() > 1) {
						new SearchProtectionInventory(
								pl, playerData.getCurrentProtections().stream()
										.map(protection -> (IProtection) protection).collect(Collectors.toList()),
								(prot) -> sendMessage(pl, prot)).openInventory();
					} else {
						sendMessage(pl, playerData.getCurrentProtections().get(0));
					}
				} else {
					MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
							.sendMessage(sender);
				}
			} else {
				Protection protection = protectionsService.findProtectionById(parameters.getParameters().get(0));

				if (protection == null) {
					return CommandResponse.trueResponse();
				}

				sendMessage(pl, protection);
			}
		} else {
			MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}

	private void sendMessage(Player player, IProtection protection) {
		if (protection.isOwnerOnline()) {
			MessageTemplate.inst(MESSAGE_LASTLOGIN_ONLINEMESSAGE.applyPrefix())
					.setReplacements(new Replacement("{player}", () -> protection.getOwnerName())).process()
					.sendMessage(player);
		} else {
			MessageTemplate.inst(MESSAGE_LASTLOGIN_OFFLINEMESSAGE.applyPrefix())
					.setReplacements(new Replacement("{player}", () -> protection.getOwnerName()),
							new Replacement("{last_played}",
									() -> PlaceholdersService.DATE_FORMAT.format(protection.getOwnerLastPlayed())))
					.process().sendMessage(player);
		}
	}

}

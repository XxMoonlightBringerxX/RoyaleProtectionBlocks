package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSplitRequestInput;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "split",
		pathName = "Split",
		defaultName = "split",
		defaultDescription = "Split merged protections",
		defaultAliases = "sp",
		defaultPermission = "protectionblocks.split",
		defaultUsage = "[protection]"
)
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true
)
public class SplitSubCommand extends PandaSubCommand {

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTION_MERGE_PARENTREMOVEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protection.Merge.Parent-removed-successfully", "&aProtection has been splitted successfully.");

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static PlayerDataService playerDataService;

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	@PandaInject
	private static PlaceholdersService placeholdersService;

	public SplitSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public List<String> tabComplete(PandaCommand cmd, CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			return protectionsService.getAllowedProtections((Player) sender)
					.filter(protection -> protection.getRegionId().toLowerCase().startsWith(args[args.length - 1]))
					.map(Protection::getRegionId).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			PlayerData playerData = playerDataService.getPlayerData(pl);
			Protection protection = parameters.getParameters().size() > 0
					? protectionsService.findProtectionById(parameters.getParameters().get(0))
					: (playerData.getCurrentProtections().size() > 0 ? playerData.getCurrentProtections().get(0)
							: null);

			try {
				if (protection != null) {
					if (protection.getParentProtection() != protection) {
						playerInteractionsService
								.protectionSplitRequest(ProtectionSplitRequestInput.inst(pl, protection));
						MessageTemplate.inst(MESSAGE_PROTECTION_MERGE_PARENTREMOVEDSUCCESSFULLY.applyPrefix()).process()
								.sendMessage(sender);
					} else {
						throw Exceptions.Protections.NOTMERGED.generateException();
					}
				} else {
					if (parameters.getParameters().size() > 0) {
						throw Exceptions.Protections.NOTFOUND.generateException();
					} else {
						MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
								.sendMessage(sender);
					}
				}
			} catch (RoyaleProtectionBlocksException e) {
				e.sendError(sender);
			}
		} else {
			MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}

}

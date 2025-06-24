package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Bukkit.Inventories.Shared.SearchProtectionInventory;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation;
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
		defaultUsage = "[protection]")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class SplitSubCommand extends PandaSubCommand {

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTION_MERGE_PARENTREMOVEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protection.Merge.Parent-removed-successfully", "&aProtection has been splitted successfully.");

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
	public boolean precondition() {
		return Settings.SETTINGS_PROTECTION_MERGE_ENABLED.isTrue();
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		if (argIndex == 0) {
			return RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
					.findAllowedParentProtectionsByPlayer((Player) sender).map(Protection::getProtectionId)
					.collect(Collectors.toList());
		}
		return super.generateAutocompleteList(sender, argIndex);
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			try {
				if (parameters.getParameters().size() > 0) {
					Protection protection = RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
							.findProtectionById(parameters.getParameters().get(0));
					if (protection != null) {
						splitProtection(pl, protection);
					} else {
						throw Exceptions.Protections.NOTFOUND.generateException();
					}
				} else {
					PlayerData playerData = playerDataService.getPlayerData(pl);
					List<IProtection> mergedAndAllowedProtections = new ArrayList<>();

					playerData.getCurrentProtections()
							.forEach(prot -> prot.getChildProtectionsRecursively().forEach(childProt -> {
								if (childProt.getChildProtections().isEmpty()
										&& childProt.isInside(SimpleLocation.of(pl.getLocation()), true)
										&& ProtectionUtilities.canMerge(prot, pl)) {
									mergedAndAllowedProtections.add(childProt);
								}
							}));

					if (mergedAndAllowedProtections.size() > 0) {
						if (mergedAndAllowedProtections.size() == 1) {
							splitProtection(pl, mergedAndAllowedProtections.get(0));
						} else {
							new SearchProtectionInventory(pl, mergedAndAllowedProtections, (prot) -> {
								splitProtection(pl, prot);
							}).openInventory();
						}
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

	private void splitProtection(Player player, IProtection prot) {
		try {
			if (prot.getParentProtection() != prot) {
				playerInteractionsService.protectionSplitRequest(ProtectionSplitRequestInput.inst(player, prot));
				MessageTemplate.inst(MESSAGE_PROTECTION_MERGE_PARENTREMOVEDSUCCESSFULLY.applyPrefix()).process()
						.sendMessage(player);
			} else {
				throw Exceptions.Protections.Merge.NOTMERGED.generateException();
			}
		} catch (RoyaleProtectionBlocksException e) {
			e.sendError(player);
		}
	}

}

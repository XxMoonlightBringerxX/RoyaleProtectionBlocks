package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Bukkit.Inventories.Protections.Merge.ProtectionMergeInventory;
import company.pluginName.Bukkit.Inventories.Shared.SearchProtectionInventory;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
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
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionMergeRequestInput;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "merge",
		pathName = "Merge",
		defaultName = "merge",
		defaultDescription = "Merge protections on other protections",
		defaultAliases = "m",
		defaultPermission = "protectionblocks.merge",
		defaultUsage = "[parent protection] [child protection]")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class MergeSubCommand extends PandaSubCommand {

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTION_MERGE_PARENTSETSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protection.Merge.Parent-set-successfully", "&aProtection has been merged successfully.");

	@PandaInject
	private static PlayerDataService playerDataService;

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	@PandaInject
	private static PlaceholdersService placeholdersService;

	public MergeSubCommand() throws InstantiationException {
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
					Optional<Protection> parentProtection = RoyaleProtectionBlocksAPIImpl.getInstance()
							.getProtectionsService().findAllowedParentProtectionsByPlayer((Player) sender)
							.filter(p -> (p.getDisplayName() != null ? p.getDisplayNameWithoutFormat()
									: p.getProtectionId()).equalsIgnoreCase(
											parameters.getParameters().stream().collect(Collectors.joining(" "))))
							.findFirst();
					if (parentProtection.isPresent()) {
						if (parameters.getParameters().size() > 1) {
							Protection childProtection = RoyaleProtectionBlocksAPIImpl.getInstance()
									.getProtectionsService().findProtectionById(parameters.getParameters().get(1));

							if (childProtection != null) {
								playerInteractionsService.protectionMergeRequest(
										ProtectionMergeRequestInput.inst(pl, childProtection, parentProtection.get()));
								MessageTemplate.inst(MESSAGE_PROTECTION_MERGE_PARENTSETSUCCESSFULLY.applyPrefix())
										.process().sendMessage(sender);
							} else {
								throw Exceptions.Protections.NOTFOUND.generateException();
							}
						} else {
							PlayerData playerData = playerDataService.getPlayerData(pl);
							List<IProtection> allowedProtections = playerData.getCurrentProtections().stream()
									.filter(prot -> ProtectionUtilities.canMerge(prot, pl))
									.collect(Collectors.toList());

							if (allowedProtections.size() > 0) {
								if (allowedProtections.size() == 1) {
									playerInteractionsService.protectionMergeRequest(ProtectionMergeRequestInput
											.inst(pl, allowedProtections.get(0), parentProtection.get()));
									MessageTemplate.inst(MESSAGE_PROTECTION_MERGE_PARENTSETSUCCESSFULLY.applyPrefix())
											.process().sendMessage(sender);
								} else {
									new SearchProtectionInventory(pl, allowedProtections, (prot) -> {
										try {
											playerInteractionsService.protectionMergeRequest(
													ProtectionMergeRequestInput.inst(pl, prot, parentProtection.get()));
											MessageTemplate
													.inst(MESSAGE_PROTECTION_MERGE_PARENTSETSUCCESSFULLY.applyPrefix())
													.process().sendMessage(sender);
										} catch (RoyaleProtectionBlocksException e) {
											e.sendError(pl);
										}
									}).openInventory();
								}
							} else {
								MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix())
										.process().sendMessage(sender);
							}
						}
					} else {
						throw Exceptions.Protections.NOTFOUND.generateException();
					}
				} else {
					PlayerData playerData = playerDataService.getPlayerData(pl);
					List<IProtection> allowedProtections = playerData.getCurrentProtections().stream()
							.filter(prot -> ProtectionUtilities.canMerge(prot, pl)).collect(Collectors.toList());

					if (allowedProtections.size() > 0) {
						if (allowedProtections.size() == 1) {
							new ProtectionMergeInventory(pl, (Protection) allowedProtections.get(0)).openInventory();
						} else {
							new SearchProtectionInventory(pl, allowedProtections, (prot) -> {
								try {
									new ProtectionMergeInventory(pl, (Protection) prot).openInventory();
								} catch (RoyaleProtectionBlocksExceptionImpl e) {
									e.sendError(sender);
								}
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

}

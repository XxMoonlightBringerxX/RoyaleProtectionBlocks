package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment.ClickEvent;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment.HoverEvent;
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
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionPriorityChangeRequestInput;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "priority",
		pathName = "priority",
		defaultName = "priority",
		defaultDescription = "Set the priority of the current protection",
		defaultAliases = "p",
		defaultPermission = "protectionblocks.priority",
		defaultUsage = "<priority> [protection id]"
)
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true
)
public class PrioritySubCommand extends PandaSubCommand {

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTION_PRIORITY_SETSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protection.Priority.Set-successfully", "&aPriority has been set successfully.");

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTION_PRIORITY_MULTIPLEPROTECTIONSFOUND = new PandaPrefixedStringField(
			"Message.Protection.Priority.Multiple-protections-found",
			"&7Multiple protections has been found in the current location. Please select the protection you want to edit its priority:");

	@RegisteredPandaField("lang")
	public static final PandaStringField MESSAGE_PROTECTION_PRIORITY_MULTIPLEPROTECTIONSFOUNDLINE = new PandaStringField(
			"Message.Protection.Priority.Multiple-protections-found-line", "&8  * &e{protection_name}");

	@RegisteredPandaField("lang")
	public static final PandaStringField MESSAGE_PROTECTION_PRIORITY_MULTIPLEPROTECTIONSFOUNDLINEHOVER = new PandaStringField(
			"Message.Protection.Priority.Multiple-protections-found-line-hover", "&eClick me!");

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static PlayerDataService playerDataService;

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	@PandaInject
	private static PlaceholdersService placeholdersService;

	public PrioritySubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (parameters.getParameters().size() > 0) {
				if (parameters.getParameters().size() > 1) {
					Optional<Protection> protection = protectionsService.getAllowedProtections((Player) sender)
							.filter(p -> (p.getDisplayName() != null ? p.getDisplayNameWithoutFormat()
									: p.getRegionId()).equalsIgnoreCase(
											parameters.getParameters().subList(1, parameters.getParameters().size())
													.stream().collect(Collectors.joining(" "))))
							.findFirst();
					if (protection.isPresent()) {
						setPriority(pl, protection.get(), parameters.getParameters().get(0));
					} else {
						MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTFOUND.applyPrefix()).process()
								.sendMessage(sender);
					}
				} else {
					PlayerData playerData = playerDataService.getPlayerData(pl);
					if (playerData.getCurrentProtections().size() > 0) {
						if (playerData.getCurrentProtections().size() > 1) {
							MessageTemplate.inst(MESSAGE_PROTECTION_PRIORITY_MULTIPLEPROTECTIONSFOUND.applyPrefix())
									.process().sendMessage(pl);
							playerData.getCurrentProtections().forEach(protection -> {
								String displayName = protection.getDisplayName() != null ? protection.getDisplayName()
										: protection.getRegionId();
								String command = "/" + getCommandPath() + " " + parameters.getParameters().get(0) + " "
										+ displayName;
								System.out.println(command);
								Replacement[] replacements = placeholdersService.getProtectionReplacements(protection);
								MessageTemplate.inst("{line}").setReplacements(new Replacement("{line}",
										() -> MessageTemplate.inst(
												MESSAGE_PROTECTION_PRIORITY_MULTIPLEPROTECTIONSFOUNDLINE.getContent())
												.setReplacements(replacements).toString(),
										new ClickEvent(ClickEvent.Action.RUN_COMMAND, command),
										new HoverEvent(HoverEvent.Action.SHOW_TEXT, MessageTemplate
												.inst(MESSAGE_PROTECTION_PRIORITY_MULTIPLEPROTECTIONSFOUNDLINEHOVER
														.getContent())
												.process().asComponentsArray())))
										.process().sendMessage(pl);
							});
						} else {
							setPriority(pl, playerData.getCurrentProtections().get(0),
									parameters.getParameters().get(0));
						}
					} else {
						MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
								.sendMessage(sender);
					}
				}
			} else {
				MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process()
						.sendMessage(sender);
			}
		} else {
			MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}

	private void setPriority(Player pl, IProtection protection, String priorityString) {
		try {
			Integer priority = Integer.parseInt(priorityString);

			try {
				playerInteractionsService.protectionPriorityChangeRequest(
						ProtectionPriorityChangeRequestInput.inst(pl, protection, priority));

				MessageTemplate.inst(MESSAGE_PROTECTION_PRIORITY_SETSUCCESSFULLY.applyPrefix()).process()
						.sendMessage(pl);
			} catch (RoyaleProtectionBlocksException e) {
				e.sendError(pl);
			}
		} catch (NumberFormatException e) {
			MessageTemplate.inst(Messages.ERROR_INVALIDNUMBER.applyPrefix());
		}

	}

}

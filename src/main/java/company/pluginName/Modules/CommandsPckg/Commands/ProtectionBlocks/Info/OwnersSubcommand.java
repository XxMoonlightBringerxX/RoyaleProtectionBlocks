package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Info;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment.ClickEvent;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;

@PandaCommandAnnotation(
		id = "owners",
		pathName = "Owners",
		defaultName = "owners",
		defaultDescription = "Get the owners from the current protection",
		defaultUsage = "[page]",
		defaultAliases = "o"
)
@PandaCommandAnnotation.Customizable(usage = true, permission = true, aliases = true)
@PandaSubCommandAnnotation(parentCommand = InfoCommand.class)
public class OwnersSubcommand extends PandaSubCommand {

	@RegisteredPandaField("lang")
	public static final PandaStringListField MESSAGE_WORLDINFO_OWNERSINFORMATION = new PandaStringListField(
			"Message.Protection.Info.Owners-information",
			Arrays.asList("", "&a&lOwners", "{protection_owners}", "", "{previous_page} {current_page} {next_page}"));

	@PandaInject
	private static ProtectionsService protectionsService;

	public OwnersSubcommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		return CommandResponse.queuedAsync(() -> {
			Player pl = sender instanceof Player ? (Player) sender : null;
			if (pl != null) {
				Protection protection = protectionsService.findProtectionByLocation(pl.getLocation());
				if (protection != null) {
					if (ProtectionUtilities.canSeeInformation(protection, pl)) {
						int page = 1;

						if (parameters.getParameters().size() > 0) {
							try {
								page = Integer.parseInt(parameters.getParameters().get(0));
							} catch (Exception e) {
							}
						}

						sendInformation(pl, protection, page);
					} else {
						Exceptions.Protections.PERMISSIONDENIED.generateException().sendError(pl);
					}
				} else {
					MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
							.sendMessage(sender);
				}
			} else {
				MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
			}
		});
	}

	private void sendInformation(Player player, Protection protection, int page) {
		int maxPage = getMaxPage(protection.getOwners().list(), 5);

		if (maxPage < 1) {
			maxPage = 1;
		}

		if (page <= 1) {
			page = 1;
		} else if (page > maxPage) {
			page = maxPage;
		}

		final int fPage = page;
		final int fMaxPage = maxPage;

		List<String> owners = protection.getOwners().list().size() != 0 ? Arrays.stream(Arrays.copyOfRange(
				protection.getOwners().list().toArray(new UUID[protection.getOwners().list().size()]), (page - 1) * 5,
				page * 5)).filter(Objects::nonNull).map(owner -> {
					OfflinePlayer ownerPlayer = OfflinePlayerUtilities.getOfflinePlayer(owner);
					return "&e".concat(
							ownerPlayer != null && ownerPlayer.getName() != null ? ownerPlayer.getName() : "???");
				}).collect(Collectors.toList()) : Arrays.asList("(empty)");

		MessageFragment previousPage = page > 1
				? new MessageFragment(() -> "&e[previous]",
						new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/".concat(getCommandPath()).concat(" " + (page - 1))))
				: new MessageFragment(() -> "&8[previous]");

		MessageFragment nextPage = page < maxPage
				? new MessageFragment(() -> "&e[next]",
						new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/".concat(getCommandPath()).concat(" " + (page + 1))))
				: new MessageFragment(() -> "&8[next]");

		MessageTemplate.inst(MESSAGE_WORLDINFO_OWNERSINFORMATION.getContent())
				.setReplacements(
						new Replacement("{protection_owners}",
								() -> owners.stream().collect(Collectors.joining(System.lineSeparator(), "&e", ""))),
						new Replacement("{previous_page}", previousPage),
						new Replacement("{current_page}", () -> ("&7" + fPage + "/" + fMaxPage)),
						new Replacement("{next_page}", nextPage))
				.process().sendMessage(player);
	}

	private int getMaxPage(Collection<?> list, int pageSize) {
		return (int) ((list.size() + (pageSize - 1)) / (double) pageSize);
	}

}
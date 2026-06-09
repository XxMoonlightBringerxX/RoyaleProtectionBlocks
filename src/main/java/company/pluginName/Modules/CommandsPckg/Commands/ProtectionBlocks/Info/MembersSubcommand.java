package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Info;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsServiceImpl;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment.ClickEvent;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.PandaCachedPlayersService;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.Objects.PandaCachedPlayer;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;

@PandaCommandAnnotation(
		id = "members",
		pathName = "Members",
		defaultName = "members",
		defaultDescription = "Get the members from the current protection",
		defaultUsage = "[page]",
		defaultAliases = "m")
@PandaCommandAnnotation.Customizable(usage = true, permission = true, aliases = true)
@PandaSubCommandAnnotation(parentCommand = InfoCommand.class)
public class MembersSubcommand extends PandaSubCommand {

	@RegisteredPandaField("lang")
	public static final PandaStringListField MESSAGE_WORLDINFO_MEMBERSINFORMATION = new PandaStringListField(
			"Message.Protection.Info.Members-information",
			Arrays.asList("", "&a&lMembers", "{protection_members}", "", "{previous_page} {current_page} {next_page}"));

	@RegisteredPandaField("lang")
	public static final PandaStringField MESSAGE_WORLDINFO_MEMBERSINFORMATIONPREVIOUSAVAILABLE = new PandaStringField(
			"Message.Protection.Info.Members-information-previous-available", "&e[previous]");

	@RegisteredPandaField("lang")
	public static final PandaStringField MESSAGE_WORLDINFO_MEMBERSINFORMATIONPREVIOUSUNAVAILABLE = new PandaStringField(
			"Message.Protection.Info.Members-information-previous-unavailable", "&8[previous]");

	@RegisteredPandaField("lang")
	public static final PandaStringField MESSAGE_WORLDINFO_MEMBERSINFORMATIONNEXTAVAILABLE = new PandaStringField(
			"Message.Protection.Info.Members-information-next-available", "&e[next]");

	@RegisteredPandaField("lang")
	public static final PandaStringField MESSAGE_WORLDINFO_MEMBERSINFORMATIONNEXTUNAVAILABLE = new PandaStringField(
			"Message.Protection.Info.Members-information-next-unavailable", "&8[next]");

	@PandaInject
	private static ProtectionsServiceImpl protectionsService;

	@PandaInject
	private static PandaCachedPlayersService cachedPlayersService;

	public MembersSubcommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		return CommandResponse.queuedAsync(() -> {
			Player pl = sender instanceof Player ? (Player) sender : null;
			if (pl != null) {
				Protection protection = protectionsService.findProtectionParentByLocation(pl.getLocation());
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
		List<UUID> members = protection.getMembers();

		int maxPage = getMaxPage(members, 5);

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

		List<String> memberNames = members.size() != 0
				? Arrays.stream(Arrays.copyOfRange(members.toArray(new UUID[members.size()]), (page - 1) * 5, page * 5))
						.filter(Objects::nonNull).map(member -> {
							PandaCachedPlayer memberPlayer = cachedPlayersService.getCachedPlayer(member);
							return "&e".concat(
									memberPlayer != null && memberPlayer.getName() != null ? memberPlayer.getName()
											: "???");
						}).collect(Collectors.toList())
				: Arrays.asList(Messages.MESSAGE_GENERAL_EMPTY.getContent());

		MessageFragment previousPage = page > 1
				? new MessageFragment(() -> MESSAGE_WORLDINFO_MEMBERSINFORMATIONPREVIOUSAVAILABLE.getContent(),
						new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/".concat(getCommandPath()).concat(" " + (page - 1))))
				: new MessageFragment(() -> MESSAGE_WORLDINFO_MEMBERSINFORMATIONPREVIOUSUNAVAILABLE.getContent());

		MessageFragment nextPage = page < maxPage
				? new MessageFragment(() -> MESSAGE_WORLDINFO_MEMBERSINFORMATIONNEXTAVAILABLE.getContent(),
						new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/".concat(getCommandPath()).concat(" " + (page + 1))))
				: new MessageFragment(() -> MESSAGE_WORLDINFO_MEMBERSINFORMATIONNEXTUNAVAILABLE.getContent());

		MessageTemplate.inst(MESSAGE_WORLDINFO_MEMBERSINFORMATION.getContent())
				.setReplacements(
						new Replacement("{protection_members}",
								() -> memberNames.stream().collect(Collectors.joining(", ", "&e", ""))),
						new Replacement("{previous_page}", previousPage),
						new Replacement("{current_page}", () -> ("&7" + fPage + "/" + fMaxPage)),
						new Replacement("{next_page}", nextPage))
				.process().sendMessage(player);
	}

	private int getMaxPage(Collection<?> list, int pageSize) {
		return (int) ((list.size() + (pageSize - 1)) / (double) pageSize);
	}

}
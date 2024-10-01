package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Info;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.ProtectionBlocksCommand;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment.ClickEvent;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;
import darkpanda73.PandaUtils.Utilities.Java.Arrays.ArrayUtilities;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "info",
		pathName = "Info",
		defaultName = "info",
		defaultDescription = "Show the information of a protection",
		defaultAliases = "i",
		defaultUsage = "[members|owners|banneds]")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class InfoCommand extends PandaCommand {

	@RegisteredPandaField("lang")
	public static final PandaStringListField MESSAGE_WORLDINFO_GENERALINFORMATION = new PandaStringListField(
			"Message.Protection.Info.General-information",
			Arrays.asList("", "&a&lProtection Information", "&f&l|&r ID: &a{protection_id}",
					"&f&l|&r Name: &a{protection_name}",
					"&f&l|&r Size: &a{protection_size_x}x{protection_size_y}x{protection_size_z}",
					"&f&l|&r Owner: &a{protection_owner}", "&f&l|&r Members: &a{protection_members}",
					"&f&l|&r Owners: &a{protection_owners}", "&f&l|&r Banneds: &a{protection_banneds}"));

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static PlaceholdersService placeholdersService;

	private MembersSubcommand membersCommand;
	private OwnersSubcommand ownersCommand;
	private BannedsSubcommand bannedsCommand;

	public InfoCommand() throws InstantiationException {
		super();
	}

	@Override
	public void initSubCommands() {
		super.initSubCommands();

		this.membersCommand = (MembersSubcommand) this.getCommands().stream()
				.filter(cmd -> cmd instanceof MembersSubcommand).findFirst().orElse(null);
		this.ownersCommand = (OwnersSubcommand) this.getCommands().stream()
				.filter(cmd -> cmd instanceof OwnersSubcommand).findFirst().orElse(null);
		this.bannedsCommand = (BannedsSubcommand) this.getCommands().stream()
				.filter(cmd -> cmd instanceof BannedsSubcommand).findFirst().orElse(null);
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			Protection protection = protectionsService.findProtectionParentByLocation(pl.getLocation());
			if (protection != null) {
				if (ProtectionUtilities.canSeeInformation(protection, pl)) {
					sendInformation(pl, protection);
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
		return new TrueResponse();
	}

	private void sendInformation(Player player, Protection protection) {
		ProtectionBlock block = protection.getProtectionBlock().getObject();

		Replacement[] replacements = new Replacement[] {
				new Replacement("{protection_id}", () -> protection.getRegionId()),
				new Replacement("{protection_name}", () -> protection
						.getDisplayName()),
				new Replacement("{protection_size_x}",
						() -> block != null ? String.valueOf((block.getInformation().getBlocksX() * 2) + 1) : "???"),
				new Replacement("{protection_size_y}",
						() -> block != null ? (block.getInformation().getBlocksY() == -1
								? Messages.MESSAGE_GENERAL_NOLIMIT.toString()
								: String.valueOf((block.getInformation().getBlocksY() * 2) + 1)) : "???"),
				new Replacement("{protection_size_z}",
						() -> block != null ? String.valueOf((block.getInformation().getBlocksZ() * 2) + 1) : "???"),
				new Replacement("{protection_owner}", () -> protection.getOwnerName()),
				new Replacement("{protection_members}",
						new MessageFragment(() -> "&7[click]",
								new ClickEvent(ClickEvent.Action.RUN_COMMAND,
										"/".concat(membersCommand.getCommandPath())))),
				new Replacement("{protection_owners}",
						new MessageFragment(() -> "&7[click]",
								new ClickEvent(ClickEvent.Action.RUN_COMMAND,
										"/".concat(ownersCommand.getCommandPath())))),
				new Replacement("{protection_banneds}", new MessageFragment(() -> "&7[click]",
						new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/".concat(bannedsCommand.getCommandPath())))) };

		Replacement[] protectionReplacements = placeholdersService.getProtectionReplacements(protection);

		MessageTemplate.inst(MESSAGE_WORLDINFO_GENERALINFORMATION.getContent())
				.setReplacements(
						ArrayUtilities.join(new Replacement[replacements.length + protectionReplacements.length],
								replacements, protectionReplacements))
				.process().sendMessage(player);
	}

}

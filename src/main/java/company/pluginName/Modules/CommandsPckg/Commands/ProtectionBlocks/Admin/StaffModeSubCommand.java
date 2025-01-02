package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
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

@PandaSubCommandAnnotation(parentCommand = AdminCommand.class)
@PandaCommandAnnotation(
		id = "staffmode",
		pathName = "Staff-mode",
		defaultName = "staffmode",
		defaultDescription = "Sets yourself or a player into the staff mode, which allows to bypass certain parts of the system like the ban and blocked protections",
		defaultAliases = "sm",
		defaultPermission = "protectionblocks.admin.staffmode")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true)
public class StaffModeSubCommand extends PandaSubCommand {

	@RegisteredPandaField("lang")
	private final static PandaPrefixedStringField MESSAGE_STAFFMODE_ENABLEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Staff-mode.Enabled-successfully", "&eStaff mode switched to: &aTrue");

	@RegisteredPandaField("lang")
	private final static PandaPrefixedStringField MESSAGE_STAFFMODE_DISABLEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Staff-mode.Disabled-successfully", "&eStaff mode switched to: &cFalse");

	@RegisteredPandaField("lang")
	private final static PandaPrefixedStringField MESSAGE_STAFFMODE_ENABLEDSUCCESSFULLYOTHER = new PandaPrefixedStringField(
			"Message.Staff-mode.Enabled-successfully-other", "&eStaff mode for &7{player} &8switched to: &aTrue");

	@RegisteredPandaField("lang")
	private final static PandaPrefixedStringField MESSAGE_STAFFMODE_DISABLEDSUCCESSFULLYOTHER = new PandaPrefixedStringField(
			"Message.Staff-mode.Disabled-successfully-other", "&eStaff mode for &7{player} &8switched to: &cFalse");

	@PandaInject
	private static PlayerDataService playerDataService;

	public StaffModeSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		AtomicReference<Player> pl = new AtomicReference<>(sender instanceof Player ? (Player) sender : null);

		if (parameters.getParameters().size() > 0) {
			pl.set(Bukkit.getPlayer(parameters.getParameters().get(0)));
		}

		if (pl.get() != null) {
			PlayerData playerData = playerDataService.getPlayerData(pl.get());

			playerData.setStaffMode(!playerData.isStaffMode());
			if (playerData.isStaffMode()) {
				MessageTemplate
						.inst(pl == sender ? MESSAGE_STAFFMODE_ENABLEDSUCCESSFULLY.applyPrefix()
								: MESSAGE_STAFFMODE_ENABLEDSUCCESSFULLYOTHER.applyPrefix())
						.setReplacements(new Replacement("{player}", () -> pl.get().getName())).process()
						.sendMessage(sender);
			} else {
				MessageTemplate
						.inst(pl == sender ? MESSAGE_STAFFMODE_DISABLEDSUCCESSFULLY.applyPrefix()
								: MESSAGE_STAFFMODE_DISABLEDSUCCESSFULLYOTHER.applyPrefix())
						.setReplacements(new Replacement("{player}", () -> pl.get().getName())).process()
						.sendMessage(sender);
			}
		} else {
			if (parameters.getParameters().size() > 0) {
				MessageTemplate.inst(Messages.ERROR_PLAYERNOTFOUND.applyPrefix()).process()
						.sendMessage(Bukkit.getConsoleSender());
			} else {
				MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process()
						.sendMessage(Bukkit.getConsoleSender());
			}
		}
		return new TrueResponse();
	}

}

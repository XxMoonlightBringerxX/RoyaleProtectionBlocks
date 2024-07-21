package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
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

@PandaSubCommandAnnotation(parentCommand = AdminCommand.class)
@PandaCommandAnnotation(
		id = "unblock",
		pathName = "Unblock",
		defaultName = "unblock",
		defaultDescription = "Allows to unblock a protection",
		defaultAliases = "ub",
		defaultPermission = "protectionblocks.admin.unblock"
)
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true
)
public class UnblockSubCommand extends PandaSubCommand {

	@RegisteredPandaField("lang")
	private final static PandaPrefixedStringField MESSAGE_PROTECTION_UNBLOCKEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protection.Unblocked-successfully", "&aThe protection has been unblocked successfully.");

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static ProtectionSettingsService protectionSettingService;

	public UnblockSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			Protection protection = protectionsService.findProtectionParentByLocation(pl.getLocation());
			if (protection != null) {
				protection.unblock();
				protection.saveData();
				MessageTemplate.inst(MESSAGE_PROTECTION_UNBLOCKEDSUCCESSFULLY.applyPrefix()).process()
						.sendMessage(sender);
			} else {
				MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
						.sendMessage(sender);
			}
		} else {
			MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}

}

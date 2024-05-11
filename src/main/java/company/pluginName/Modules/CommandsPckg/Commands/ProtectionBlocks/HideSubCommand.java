package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(id = "hide", pathName = "Hide", defaultName = "hide", defaultDescription = "Hide the protection block on your current protection", defaultAliases = "h")
@PandaCommandAnnotation.Customizable(cooldown = true, aliases = true, description = true, name = true, permission = true)
public class HideSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionsService protectionsService;

	public HideSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(PandaCommand cmd, CommandSender sender, String[] args,
			boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			Protection protection = protectionsService.findProtectionByLocation(pl.getLocation());
			if (protection != null) {
				if (ProtectionUtilities.canToggleBlock(protection, pl)) {
					if (protection.getUtils().isProtectionBlock()) {
						protection.getUtils().hideProtectionBlock();
						MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_HIDDENSUCCESSFULLY.applyPrefix()).process()
								.sendMessage(sender);
					} else {
						MessageTemplate.inst(Messages.ERROR_PROTECTIONS_BLOCKALREADYHIDDEN.applyPrefix()).process()
								.sendMessage(sender);
					}
				} else {
					MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTOWNER.applyPrefix()).process()
							.sendMessage(sender);
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

}

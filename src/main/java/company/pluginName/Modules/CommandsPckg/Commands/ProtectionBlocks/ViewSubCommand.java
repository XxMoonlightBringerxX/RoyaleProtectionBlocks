package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "view",
		pathName = "View",
		defaultName = "view",
		defaultDescription = "Show the boundaries of your current protection for a certain time",
		defaultAliases = "v")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true)
public class ViewSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionsService protectionsService;

	public ViewSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(PandaCommand cmd, CommandSender sender, String[] args,
			boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			Protection protection = protectionsService.getProtectionByLocation(pl.getLocation());
			if (protection != null) {
				if (protection.canViewBoundaries(pl)) {
					protection.toggleProtectionView();
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

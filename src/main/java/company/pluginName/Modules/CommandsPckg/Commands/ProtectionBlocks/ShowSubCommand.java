package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.block.Block;
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
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(id = "show", pathName = "Show", defaultName = "show", defaultDescription = "Show the protection block on your current protection", defaultAliases = "sw")
@PandaCommandAnnotation.Customizable(cooldown = true, aliases = true, description = true, name = true, permission = true)
public class ShowSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionsService protectionsService;

	public ShowSubCommand() throws InstantiationException {
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
					if (!protection.getUtils().isProtectionBlock()) {
						Block block = protection.getLocation().getBlock();
						if (block.getType() == Material.AIR.getMaterial()) {
							protection.getUtils().showProtectionBlock();
							MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_SHOWNSUCCESSFULLY.applyPrefix()).process()
									.sendMessage(sender);
						} else {
							MessageTemplate.inst(Messages.ERROR_PROTECTIONS_BLOCKEDBYBLOCK.applyPrefix()).process()
									.sendMessage(sender);
						}
					} else {
						MessageTemplate.inst(Messages.ERROR_PROTECTIONS_BLOCKALREADYSHOWN.applyPrefix()).process()
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

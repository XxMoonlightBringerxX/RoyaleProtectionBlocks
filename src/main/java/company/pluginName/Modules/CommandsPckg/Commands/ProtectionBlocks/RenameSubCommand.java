package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
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
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "rename",
		pathName = "Rename",
		defaultName = "rename",
		defaultDescription = "Rename your current protection name",
		defaultUsage = "<new name>",
		defaultAliases = "rn")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class RenameSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionsService protectionsService;

	public RenameSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(PandaCommand cmd, CommandSender sender, String[] args,
			boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (args.length > 1) {
				Protection protection = protectionsService.getProtectionByLocation(pl.getLocation());
				if (protection != null) {
					if (protection.isMainOwner(pl.getUniqueId())) {
						try {
							protection.setDisplayName(pl, Arrays.stream(Arrays.copyOfRange(args, 1, args.length))
									.collect(Collectors.joining(" ")));
							MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_RENAMEDSUCCESSFULLY.applyPrefix())
									.process().sendMessage(sender);
						} catch (RoyaleProtectionBlocksException e) {
							e.sendError(pl);
						}
					} else {
						MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTMAINOWNER.applyPrefix()).process()
								.sendMessage(sender);
					}
				} else {
					MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
							.sendMessage(sender);
				}
			} else {
				MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process().sendMessage(pl);
			}
		} else {
			MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}

}

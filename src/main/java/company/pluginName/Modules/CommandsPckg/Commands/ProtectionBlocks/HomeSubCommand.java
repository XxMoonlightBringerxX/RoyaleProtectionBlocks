package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
		id = "home",
		pathName = "Home",
		defaultName = "home",
		defaultDescription = "Teleport to the home of the specified protection.",
		defaultUsage = "[region id]",
		defaultAliases = "l")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class HomeSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionsService protectionsService;

	public HomeSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public List<String> tabComplete(PandaCommand cmd, CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (args.length == 1 && args[0].isEmpty()) {
				return protectionsService.getAllowedProtections((Player) sender).stream()
						.map(protection -> protection.getDisplayName() != null
								? protection.getDisplayNameWithoutFormat()
								: protection.getRegionId())
						.collect(Collectors.toList());
			}
		}
		return EMPTY_LIST;
	}

	@Override
	public CommandResponse executeCommandProcess(PandaCommand cmd, CommandSender sender, String[] args,
			boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (args.length > 1) {
				Optional<Protection> protection = protectionsService.getAllowedProtections((Player) sender).stream()
						.filter(p -> (p.getDisplayName() != null ? p.getDisplayNameWithoutFormat() : p.getRegionId())
								.equalsIgnoreCase(Arrays.stream(Arrays.copyOfRange(args, 1, args.length))
										.collect(Collectors.joining(" "))))
						.findFirst();
				if (protection.isPresent()) {
					try {
						protection.get().sendHome(pl);
						return new TrueResponse();
					} catch (RoyaleProtectionBlocksException e) {
						e.sendError(pl);
					}
				} else {
					MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTFOUND.applyPrefix()).process()
							.sendMessage(sender);
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
}

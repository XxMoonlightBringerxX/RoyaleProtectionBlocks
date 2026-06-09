package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Bukkit.Inventories.Admin.AdminProtectionsListInventory;
import company.pluginName.Modules.FilePckg.Messages;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.PandaCachedPlayersService;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.Objects.PandaCachedPlayer;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;

@PandaSubCommandAnnotation(parentCommand = AdminCommand.class)
@PandaCommandAnnotation(
		id = "list",
		pathName = "List",
		defaultName = "list",
		defaultDescription = "Open a list with all the existent protections protections",
		defaultUsage = "[username]",
		defaultAliases = "l",
		defaultPermission = "protectionblocks.admin.list")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class ListSubCommand extends PandaSubCommand {

	@PandaInject
	private static PandaCachedPlayersService cachedPlayersService;

	public ListSubCommand() throws InstantiationException {
		super();
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		if (argIndex == 0) {
			return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
		}
		return super.generateAutocompleteList(sender, argIndex);
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		return CommandResponse.queuedAsync(() -> {
			Player pl = sender instanceof Player ? (Player) sender : null;
			if (pl != null) {
				if (parameters.getParameters().size() > 0) {
					PandaCachedPlayer owner = cachedPlayersService.getCachedPlayer(parameters.getParameters().get(0));
					if (owner != null) {
						new AdminProtectionsListInventory(pl, owner).openInventory();
					} else {
						MessageTemplate.inst(Messages.ERROR_PLAYERNOTFOUND.applyPrefix()).process().sendMessage(sender);
					}
				} else {
					new AdminProtectionsListInventory(pl).openInventory();
				}
			} else {
				MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
			}
		});
	}
}

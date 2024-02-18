package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Bukkit.Inventories.Protections.ProtectionsListInventory;
import company.pluginName.Bukkit.Inventories.Protections.ProtectionsManageInventory;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.FalseResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;

@PandaCommandAnnotation(
		id = "protectionblocks",
		pathName = "Protection-blocks",
		defaultName = "protectionblocks",
		defaultDescription = "Open the GUI or execute commands",
		defaultUsage = "[help|subcommand]",
		defaultAliases = "pb")
@PandaCommandAnnotation.Customizable(aliases = true, description = true, name = true, permission = true, usage = true)
public class ProtectionBlocksCommand extends PandaCommand {

	@PandaInject
	private static ProtectionsService protectionsService;

	public ProtectionBlocksCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(PandaCommand cmd, CommandSender sender, String[] args,
			boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;

		if (pl != null && args.length == 0) {
			Protection protection = protectionsService.getProtectionByLocation(pl.getLocation());
			if (protection != null && protection.canManage(pl)) {
				new ProtectionsManageInventory(pl, protection).openInventory();
			} else {
				new ProtectionsListInventory(pl).openInventory();
			}
			return new TrueResponse();
		}

		return new FalseResponse();
	}

}
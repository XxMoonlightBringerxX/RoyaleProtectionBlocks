package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.APIs.VaultAPI.VaultAPI;
import company.pluginName.Bukkit.Inventories.ProtectionBlocks.ProtectionBlocksShopInventory;
import company.pluginName.Bukkit.Inventories.Protections.ProtectionsListInventory;
import company.pluginName.Bukkit.Inventories.Protections.ProtectionsManageInventory;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.FalseResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;

@PandaCommandAnnotation(
		id = "protectionblocks",
		pathName = "Protection-blocks",
		defaultName = "protectionblocks",
		defaultDescription = "Open the GUI or execute commands",
		defaultUsage = "[help|subcommand]",
		defaultAliases = "pb"
)
@PandaCommandAnnotation.Customizable(aliases = true, description = true, name = true, permission = true, usage = true)
public class ProtectionBlocksCommand extends PandaCommand {

	@RegisteredPandaField("config")
	private static PandaBooleanField SETTINGS_PROTECTIONBLOCK_OPENSHOPONLISTEMPTY = new PandaBooleanField(
			"Settings.Protection-block.Open-shop-on-empty-list", false);

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static VaultAPI vaultApi;

	public ProtectionBlocksCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;

		if (pl != null && parameters.getParameters().size() == 0) {
			Protection protection = protectionsService.findProtectionByLocation(pl.getLocation());
			if (protection != null && ProtectionUtilities.canManage(protection, pl)) {
				new ProtectionsManageInventory(pl, protection).openInventory();
			} else {
				if (vaultApi.isHooked() && SETTINGS_PROTECTIONBLOCK_OPENSHOPONLISTEMPTY.getContent()
						&& protectionsService.getAllowedProtections(pl).size() == 0) {
					new ProtectionBlocksShopInventory(pl).openInventory();
				} else {
					new ProtectionsListInventory(pl).openInventory();
				}
			}
			return new TrueResponse();
		}

		return new FalseResponse();
	}

}
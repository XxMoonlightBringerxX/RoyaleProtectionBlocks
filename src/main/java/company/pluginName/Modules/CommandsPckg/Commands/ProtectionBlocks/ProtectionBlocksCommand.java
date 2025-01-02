package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Bukkit.Inventories.Protections.ProtectionsListInventory;
import company.pluginName.Bukkit.Inventories.Shared.SearchProtectionInventory;
import company.pluginName.Bukkit.Inventories.Store.ProtectionBlocksStoreInventory;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.VaultAPI.VaultAPI;
import company.pluginName.Modules.PlayerInteractionsPckg.PlayerInteractionsServiceImpl;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
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
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionManagementInventoryRequestInput;

@PandaCommandAnnotation(
		id = "protectionblocks",
		pathName = "Protection-blocks",
		defaultName = "protectionblocks",
		defaultDescription = "Open the GUI or execute commands",
		defaultUsage = "[help|subcommand]",
		defaultAliases = "pb")
@PandaCommandAnnotation.Customizable(aliases = true, description = true, name = true, permission = true, usage = true)
public class ProtectionBlocksCommand extends PandaCommand {

	@RegisteredPandaField("config")
	private static PandaBooleanField SETTINGS_PROTECTIONBLOCK_OPENSHOPONLISTEMPTY = new PandaBooleanField(
			"Settings.Protection-block.Open-shop-on-empty-list", false);

	@PandaInject
	private static PlayerDataService playerDataService;

	@PandaInject
	private static PlayerInteractionsServiceImpl playerInteractionsService;

	@PandaInject
	private static VaultAPI vaultApi;

	public ProtectionBlocksCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;

		if (pl != null && parameters.getParameters().size() == 0) {
			PlayerData playerData = playerDataService.getPlayerData(pl);
			List<IProtection> allowedProtections = playerData.getCurrentProtections().stream()
					.filter(prot -> ProtectionUtilities.canManage(prot, pl)).collect(Collectors.toList());

			if (allowedProtections.size() > 0) {
				if (allowedProtections.size() == 1) {
					try {
						playerInteractionsService.openProtectionManagementInventoryRequest(
								OpenProtectionManagementInventoryRequestInput.inst(pl, allowedProtections.get(0)));
					} catch (RoyaleProtectionBlocksExceptionImpl e) {
						e.sendError(pl);
					}
				} else {
					new SearchProtectionInventory(pl, allowedProtections, (prot) -> {
						try {
							playerInteractionsService.openProtectionManagementInventoryRequest(
									OpenProtectionManagementInventoryRequestInput.inst(pl, (Protection) prot));
						} catch (RoyaleProtectionBlocksExceptionImpl e) {
							e.sendError(pl);
						}
					}).openInventory();
				}
			} else {
				if (vaultApi.isHooked() && SETTINGS_PROTECTIONBLOCK_OPENSHOPONLISTEMPTY.getContent()
						&& RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
								.findAllowedParentProtectionsByPlayer(pl).count() == 0) {
					new ProtectionBlocksStoreInventory(pl).openInventory();
				} else {
					new ProtectionsListInventory(pl).openInventory();
				}
			}
			return new TrueResponse();
		}

		return new FalseResponse();
	}

}
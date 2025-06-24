package company.pluginName.Modules.PlayersDataPckg;

import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.SQLPckg.SQLService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaPlayerDataModule.PandaPlayerDataService;

public class PlayerDataService extends PandaPlayerDataService<PlayerData> {

	@PandaInject
	private SQLService sqlService;

	@Override
	protected PlayerData generatePlayerData(UUID uuid) {
		try {
			PlayerData playerData = sqlService.getPlayerData(uuid);

			playerData.setProtectionInvitations(RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
					.findAllProtectionInvitationsByPlayerUuid(uuid).collect(Collectors.toList()));

			return playerData;
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			e.sendError(Bukkit.getConsoleSender());
			return new PlayerData(uuid);
		}
	}

}

package company.pluginName.Modules.PlayersDataPckg;

import java.util.UUID;

import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import darkpanda73.PandaUtils.Services.PandaPlayerDataModule.PandaPlayerDataService;

public class PlayerDataService extends PandaPlayerDataService<PlayerData> {

	@Override
	protected PlayerData generatePlayerData(UUID uuid) {
		return new PlayerData(uuid);
	}

}

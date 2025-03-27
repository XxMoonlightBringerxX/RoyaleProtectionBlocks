package company.pluginName.Modules.PlayersDataPckg;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
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

			Player pl = Bukkit.getPlayer(uuid);
			if (pl != null) {
				playerData.setStaffMode(PermissionsService.STAFFMODE.hasPermission(pl));
			}

			return playerData;
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			e.sendError(Bukkit.getConsoleSender());
			return new PlayerData(uuid);
		}
	}

}

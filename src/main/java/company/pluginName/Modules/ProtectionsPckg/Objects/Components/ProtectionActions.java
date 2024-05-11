package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import company.pluginName.Permissions;
import company.pluginName.APIs.VaultAPI.VaultAPI;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtectionActions {

	@PandaInject
	private static ProtectionSettingsService protectionSettingsService;

	@PandaInject
	private static VaultAPI vaultApi;

	@PandaInject
	private static PlayerDataService playerDataService;

	private Protection protection;

	public boolean kickPlayer(Player playerToKick) {
		try {
			return kickPlayer(null, playerToKick);
		} catch (RoyaleProtectionBlocksException e) {
			return false;
		}
	}

	public boolean kickPlayer(Player pl, Player playerToKick) throws RoyaleProtectionBlocksException {
		if (pl != null) {
			if (!pl.hasPermission(Permissions.PROTECTION_KICK_OTHERS)) {
				if (!this.protection.getOwners().list().contains(pl.getUniqueId())) {
					throw Exceptions.Protections.PROTECTION_KICK_DENIED.generateException();
				}
			}
		}

		Location loc = playerToKick.getLocation();
		if (protection.getProtectedRegion().contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
			TasksUtils.execute(() -> playerToKick.teleport(protectionSettingsService.getSpawn()));
			return true;
		}
		return false;
	}

	public void teleportToHome(Player pl) throws RoyaleProtectionBlocksException {
		if (this.protection.getHome() == null) {
			throw Exceptions.Protections.Teleport.NOHOMESET.generateException();
		}

		PlayerData playerData = playerDataService.getPlayerData(pl);

		if (playerData == null) {
			throw Exceptions.Protections.Teleport.UNKNOWN
					.generateException(new NullPointerException("Player data is null"));
		}

		if (Settings.SETTINGS_PROTECTION_TELEPORTCOOLDOWN.getContent() > 0
				&& !pl.hasPermission(Permissions.PROTECTION_TELEPORT_BYPASS)) {
			if (playerData.getLastTeleport() != null && (Settings.SETTINGS_PROTECTION_TELEPORTCOOLDOWN.getContent()
					* 1000) >= (System.currentTimeMillis() - playerData.getLastTeleport().getSecond())) {
				throw Exceptions.Protections.Teleport.COOLDOWNACTIVE.generateException()
						.setReplacements(new Replacement("%seconds%",
								() -> String.valueOf((int) Math.max(((playerData.getLastTeleport().getSecond()
										+ (Settings.SETTINGS_PROTECTION_TELEPORTCOOLDOWN.getContent() * 1000))
										- System.currentTimeMillis()) / 1000, 0))));
			}
		}

		if (vaultApi.getHook() != null && Settings.SETTINGS_PROTECTION_TELEPORTCOST.getContent() > 0D) {
			if (!pl.hasPermission(Permissions.PROTECTION_ECONOMY_BYPASS)
					&& !vaultApi.getHook().withdraw(pl, Settings.SETTINGS_PROTECTION_TELEPORTCOST.getContent())) {
				throw Exceptions.Protections.PROTECTION_NOTENOUGHBALANCE.generateException();
			}
		}

		playerData.teleport(this.protection);
	}

}

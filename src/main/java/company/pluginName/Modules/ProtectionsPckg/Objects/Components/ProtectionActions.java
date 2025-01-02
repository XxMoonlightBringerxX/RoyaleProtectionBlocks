package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.EssentialsX.EssentialsXAPI;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Utils.EconomyUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Player.PlayerTeleportToProtectionAttemptEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Player.PlayerTeleportToProtectionEvent;

@Data
@AllArgsConstructor
public class ProtectionActions {

	@PandaInject
	private static ProtectionSettingsService protectionSettingsService;

	@PandaInject
	private static PlayerDataService playerDataService;

	@PandaInject
	private static EssentialsXAPI essentialsXApi;

	private Protection protection;

	public boolean kickPlayer(Player playerToKick) throws RoyaleProtectionBlocksExceptionImpl {
		Location loc = playerToKick.getLocation();
		if (protection.getProtectedRegion().contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
			TasksUtils.execute(() -> playerToKick.teleport(protectionSettingsService.getSpawn()));
			return true;
		}
		return false;
	}

	public void teleportToHome(Player pl, boolean ignoreCost, boolean ignoreUnsafeWarning)
			throws RoyaleProtectionBlocksExceptionImpl {
		if (this.protection.getHome() == null) {
			throw Exceptions.Protections.Teleport.NOHOMESET.generateException();
		}

		PlayerData playerData = playerDataService.getPlayerData(pl);

		if (playerData == null) {
			throw Exceptions.Protections.Teleport.UNKNOWN
					.generateException(new NullPointerException("Player data is null"));
		}

		if (Settings.SETTINGS_PROTECTION_TELEPORTCOOLDOWN.getContent() > 0
				&& !PermissionsService.TELEPORT_BYPASS.hasPermission(pl)) {
			if (playerData.getLastTeleport() != null && (Settings.SETTINGS_PROTECTION_TELEPORTCOOLDOWN.getContent()
					* 1000) >= (System.currentTimeMillis() - playerData.getLastTeleport().getSecond())) {
				throw Exceptions.Protections.Teleport.COOLDOWNACTIVE.generateException()
						.setReplacements(new Replacement("%seconds%",
								() -> String.valueOf((int) Math.max(((playerData.getLastTeleport().getSecond()
										+ (Settings.SETTINGS_PROTECTION_TELEPORTCOOLDOWN.getContent() * 1000))
										- System.currentTimeMillis()) / 1000, 0))));
			}
		}

		if (!ignoreCost && Settings.SETTINGS_PROTECTION_TELEPORTCOST.getContent() > 0D) {
			if (!PermissionsService.ECONOMY_BYPASS.hasPermission(pl)
					&& !EconomyUtilities.withdraw(pl, Settings.SETTINGS_PROTECTION_TELEPORTCOST.getContent())) {
				throw Exceptions.Protections.NOTENOUGHBALANCE.generateException();
			}
		}

		if (!ignoreUnsafeWarning) {
			if (!this.protection.getHome().getChunk().isLoaded()) {
				this.protection.getHome().getChunk().load();
			}

			Block curBlock = this.protection.getHome().getBlock();

			if (curBlock.getType().isSolid() && curBlock.getRelative(BlockFace.UP).getType().isSolid()) {
				throw Exceptions.Protections.Teleport.UNSAFE.generateException();
			}

			int maxSafeDown = 3;
			for (int i = 0; i < maxSafeDown;) {
				if ((curBlock = curBlock.getRelative(BlockFace.DOWN)).getType().isSolid()) {
					break;
				}

				if (++i == maxSafeDown) {
					throw Exceptions.Protections.Teleport.UNSAFE.generateException();
				}
			}
		}

		PlayerTeleportToProtectionAttemptEvent event = new PlayerTeleportToProtectionAttemptEvent(pl, protection);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		Location previousLocation = pl.getLocation();

		playerData.teleport(this.protection);

		if (essentialsXApi.isHooked()) {
			essentialsXApi.getHook().setLastLocation(pl, previousLocation);
		}

		Bukkit.getPluginManager().callEvent(new PlayerTeleportToProtectionEvent(pl, protection));
	}

}

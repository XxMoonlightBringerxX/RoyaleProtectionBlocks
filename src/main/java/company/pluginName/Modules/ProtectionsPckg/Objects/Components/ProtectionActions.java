package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import company.pluginName.Permissions;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtectionActions {

	@PandaInject
	private static ProtectionSettingsService protectionSettingsService;

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

}

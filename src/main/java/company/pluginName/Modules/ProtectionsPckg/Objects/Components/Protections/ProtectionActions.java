package company.pluginName.Modules.ProtectionsPckg.Objects.Components.Protections;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Permissions;
import company.pluginName.Exceptions.Protection.ProtectionKickDeniedException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtectionActions {

	private Protection protection;

	public boolean kickPlayer(Player playerToKick) {
		try {
			return kickPlayer(null, playerToKick);
		} catch (ProtectionKickDeniedException e) {
			return false;
		}
	}

	public boolean kickPlayer(Player pl, Player playerToKick) throws ProtectionKickDeniedException {
		if (pl != null) {
			if (!pl.hasPermission(Permissions.PROTECTION_KICK_OTHERS)) {
				if (!this.protection.getOwners().list().contains(pl.getUniqueId())) {
					throw new ProtectionKickDeniedException();
				}
			}
		}

		Location loc = playerToKick.getLocation();
		if (protection.getProtectedRegion().contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
			playerToKick.teleport(MainPluginClass.getPlugin().getProtectionSettingsModule().getSpawn());
			return true;
		}
		return false;
	}

}

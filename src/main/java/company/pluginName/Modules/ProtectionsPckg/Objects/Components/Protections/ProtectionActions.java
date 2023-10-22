package company.pluginName.Modules.ProtectionsPckg.Objects.Components.Protections;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtectionActions {

	private Protection protection;

	public boolean kickPlayer(Player pl) {
		Location loc = pl.getLocation();
		if (protection.getProtectedRegion().contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
			pl.teleport(MainPluginClass.getPlugin().getProtectionSettingsModule().getSpawn());
			return true;
		}
		return false;
	}

}

package company.pluginName.Modules.ProtectionsPckg.Objects.Components.Protections;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtectionActions {

	private Protection protection;

	public boolean kickPlayer(Player pl) {
		Location loc = pl.getLocation();
		if (protection.getProtectedRegion().contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
			World world = Bukkit.getWorld(SettingString.SETTINGS_PROTECTION_SENDTOWORLDONKICK.toString());

			if (world == null) {
				world = Bukkit.getWorlds().get(0);
			}

			pl.teleport(world.getSpawnLocation());
			return true;
		}
		return false;
	}

}

package company.pluginName.Utils;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {

	public static String locationToString(Location location) {
		return Arrays
				.asList(location.getWorld().getName(), String.valueOf(location.getBlockX()),
						String.valueOf(location.getBlockY()), String.valueOf(location.getBlockZ()))
				.stream().collect(Collectors.joining(";"));
	}

	public static Location stringToLocation(String location) {
		String[] args = location.split(";");
		return new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]),
				Double.parseDouble(args[3]));
	}

}

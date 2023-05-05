package company.pluginName.Utils;

import org.bukkit.Location;
import org.bukkit.World;

public class ReflectUtils {

	public static Location unknownLocationToBukkitLocation(World world, Object unknownLocation) {
		try {
			double x = (double) unknownLocation.getClass().getMethod("getX").invoke(unknownLocation);
			double y = (double) unknownLocation.getClass().getMethod("getY").invoke(unknownLocation);
			double z = (double) unknownLocation.getClass().getMethod("getZ").invoke(unknownLocation);
			float yaw = (float) unknownLocation.getClass().getMethod("getYaw").invoke(unknownLocation);
			float pitch = (float) unknownLocation.getClass().getMethod("getPitch").invoke(unknownLocation);
			return new Location(world, x, y, z, yaw, pitch);
		} catch (Exception e) {
			return null;
		}
	}

}

package company.pluginName.Utils;

import org.bukkit.Location;

import com.sk89q.worldedit.math.BlockVector3;

public class BlockVectorUtils {

	public static BlockVector3 getIntersectingVector(BlockVector3 vector1, BlockVector3 vector2) {
		return BlockVector3.at((vector1.getBlockX() + vector2.getBlockX()) / 2,
				(vector1.getBlockY() + vector2.getBlockY()) / 2, (vector1.getBlockZ() + vector2.getBlockZ()) / 2);
	}

	public static BlockVector3 locationToVector(Location location) {
		return BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

}

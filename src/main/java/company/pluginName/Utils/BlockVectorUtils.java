package company.pluginName.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.Location;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;

public class BlockVectorUtils {

	public static BlockVector3 getIntersectingVector(BlockVector3 vector1, BlockVector3 vector2) {
		return BlockVector3.at((vector1.getBlockX() + vector2.getBlockX()) / 2,
				(vector1.getBlockY() + vector2.getBlockY()) / 2, (vector1.getBlockZ() + vector2.getBlockZ()) / 2);
	}

	public static BlockVector3 locationToVector(Location location) {
		return BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public static List<BlockVector2> mergeRegion(List<BlockVector2> originalRegion, List<BlockVector2> newRegion) {
		if (originalRegion.size() < 4) {
			return originalRegion;
		}

		if (newRegion.size() != 4) {
			return originalRegion;
		}

		BlockVector2 newRegionMinLocation = newRegion.stream().reduce(null,
				(vector1, vector2) -> (vector1 == null
						|| (vector2.getBlockX() <= vector1.getBlockX() && vector2.getBlockZ() <= vector1.getBlockZ()))
								? vector2
								: vector1);
		BlockVector2 newRegionMaxLocation = newRegion.stream().reduce(null,
				(vector1, vector2) -> (vector1 == null
						|| (vector2.getBlockX() >= vector1.getBlockX() && vector2.getBlockZ() >= vector1.getBlockZ()))
								? vector2
								: vector1);

		List<BlockVector2> list = new LinkedList<>();
		int currentIndex = IntStream.range(0, originalRegion.size()).filter(i -> {
			BlockVector2 vector = originalRegion.get(i);
			return !MathUtils.between(vector.getBlockX(), newRegionMinLocation.getBlockX(),
					newRegionMaxLocation.getBlockX())
					|| !MathUtils.between(vector.getBlockZ(), newRegionMinLocation.getBlockZ(),
							newRegionMaxLocation.getBlockZ());
		}).findFirst().orElse(-1);

		if (currentIndex == -1) {
			return newRegion;
		}

		BlockVector2 firstVector = originalRegion.get(currentIndex);
		List<BlockVector2> currentList = originalRegion;
		List<BlockVector2> checkList = newRegion;
		for (int i = 0; i < currentList.size(); i++) {
			BlockVector2 secondVector = currentList.get((currentIndex + i + 1) % currentList.size());

			boolean xIsConstant = firstVector.getBlockX() == secondVector.getBlockX();
			boolean zIsConstant = firstVector.getBlockZ() == secondVector.getBlockZ();

			int j = 0;
			for (; j < checkList.size(); j++) {
				BlockVector2 firstVector2 = checkList.get((j) % checkList.size());
				BlockVector2 secondVector2 = checkList.get((j + 1) % checkList.size());

				boolean xIsConstant2 = firstVector.getBlockX() == secondVector.getBlockX();
				boolean zIsConstant2 = firstVector.getBlockZ() == secondVector.getBlockZ();

				if ((xIsConstant && xIsConstant2 && firstVector.getBlockX() == firstVector2.getBlockX())
						|| (zIsConstant && zIsConstant2 && firstVector.getBlockZ() == firstVector2.getBlockZ())) {
					boolean intersectionXSameZ = zIsConstant && zIsConstant2
							&& (MathUtils.between(firstVector.getBlockX(), firstVector2.getBlockX(),
									secondVector2.getBlockX())
									|| MathUtils.between(secondVector.getBlockX(), firstVector2.getBlockX(),
											secondVector2.getBlockX()));
					boolean intersectionZSameX = xIsConstant && xIsConstant2
							&& (MathUtils.between(firstVector.getBlockZ(), firstVector2.getBlockZ(),
									secondVector2.getBlockZ())
									|| MathUtils.between(secondVector.getBlockZ(), firstVector2.getBlockZ(),
											secondVector2.getBlockZ()));

					if (intersectionXSameZ || intersectionZSameX) {
						firstVector = secondVector2;

						List<BlockVector2> exchangeList = checkList;
						checkList = currentList;
						currentList = exchangeList;

						currentIndex = j + 1;
						i = -1;
						break;
					}
				} else {
					boolean intersectionX = zIsConstant
							&& MathUtils.between(firstVector.getBlockZ(), firstVector2.getBlockZ(),
									secondVector2.getBlockZ())
							&& (MathUtils.betweenExclusive(firstVector2.getBlockX(), firstVector.getBlockX(),
									secondVector.getBlockX()));
					boolean intersectionZ = xIsConstant
							&& MathUtils.between(firstVector.getBlockX(), firstVector2.getBlockX(),
									secondVector2.getBlockX())
							&& (MathUtils.betweenExclusive(firstVector2.getBlockZ(), firstVector.getBlockZ(),
									secondVector.getBlockZ()));

					if (intersectionX || intersectionZ) {
						firstVector = intersectionX ? BlockVector2.at(firstVector2.getBlockX(), firstVector.getBlockZ())
								: BlockVector2.at(firstVector.getBlockX(), firstVector2.getBlockZ());

						List<BlockVector2> exchangeList = checkList;
						checkList = currentList;
						currentList = exchangeList;

						currentIndex = j + 1;
						i = -2;
						break;
					}
				}
			}

			if (j == checkList.size()) {
				firstVector = secondVector;
			}

			int indexFirstVector = list.indexOf(firstVector);
			if (indexFirstVector != -1) {
				for (int h = indexFirstVector - 1; h >= 0; h--) {
					list.remove(0);
				}

				return list;
			} else {
				if (j == checkList.size()) {
					firstVector = secondVector;
				}

				list.add(firstVector);
			}
		}

		return originalRegion;
	}

	public static List<BlockVector2> locationsToVectors(Location location1, Location location2) {
		List<BlockVector2> list = new LinkedList<>();

		int minX = Math.min(location1.getBlockX(), location2.getBlockX());
		int minZ = Math.min(location1.getBlockZ(), location2.getBlockZ());
		int maxX = Math.max(location1.getBlockX(), location2.getBlockX());
		int maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ());

		list.add(BlockVector2.at(minX, minZ));
		list.add(BlockVector2.at(maxX, minZ));
		list.add(BlockVector2.at(maxX, maxZ));
		list.add(BlockVector2.at(minX, maxZ));

		return list;
	}

}

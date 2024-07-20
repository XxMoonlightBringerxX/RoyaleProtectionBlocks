package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import java.util.function.BiFunction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionUtils.SimpleLocation.SimpleLocationArea;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaUtilities.WorldUtilities;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
public class ProtectionUtils {

	private @NonNull Protection protection;
	private SimpleLocationArea protectionArea;
	private SimpleLocationArea protectionAreaWithoutBorder;

	public SimpleLocationArea getProtectionArea() {
		if (this.protectionArea == null) {
			this.regenerateProtectionArea();
		}
		return this.protectionArea;
	}

	public SimpleLocationArea getProtectionAreaWithoutBorder() {
		if (this.protectionAreaWithoutBorder == null) {
			this.regenerateProtectionArea();
		}
		return this.protectionAreaWithoutBorder;
	}

	public void regenerateProtectionArea() {
		Location location = protection.getLocation();
		ProtectionBlock protectionBlock = protection.getProtectionBlock().getObject();

		if (protectionBlock != null) {
			Location minLocation = new Location(location.getWorld(),
					location.getBlockX() - protectionBlock.getInformation().getBlocksX(),
					protectionBlock.getInformation().getBlocksY() != -1
							? location.getBlockY() - protectionBlock.getInformation().getBlocksY()
							: WorldUtilities.getMinHeight(location.getWorld()),
					location.getBlockZ() - protectionBlock.getInformation().getBlocksZ());

			Location maxLocation = new Location(location.getWorld(),
					location.getBlockX() + protectionBlock.getInformation().getBlocksX() + 1,
					protectionBlock.getInformation().getBlocksY() != -1
							? location.getBlockY() + protectionBlock.getInformation().getBlocksY() + 1
							: location.getWorld().getMaxHeight(),
					location.getBlockZ() + protectionBlock.getInformation().getBlocksZ() + 1);

			this.protectionArea = SimpleLocationArea.of(minLocation, maxLocation);
			this.protectionAreaWithoutBorder = new SimpleLocationArea(this.protectionArea.getWorldName(),
					this.protectionArea.getMinLocation().clone().add(1, 1, 1),
					this.protectionArea.getMaxLocation().clone().substract(1, 1, 1));
		} else {
			this.protectionArea = SimpleLocationArea.of(location, location);
			this.protectionAreaWithoutBorder = this.protectionArea.clone();
		}
	}

	public boolean isInside(SimpleLocation location, boolean includeBorder) {
		return (includeBorder ? this.getProtectionArea().isInside(location)
				: this.getProtectionAreaWithoutBorder().isInside(location))
				|| protection.getChildProtections().stream().anyMatch(prot -> prot.isInside(location, includeBorder));
	}

	public boolean isInside(SimpleLocationArea locationArea, boolean includeBorder) {
		return includeBorder ? this.getProtectionArea().isInside(locationArea)
				: this.getProtectionAreaWithoutBorder().isInside(locationArea) || protection.getChildProtections()
						.stream().anyMatch(prot -> prot.isInside(locationArea, includeBorder));
	}

	public boolean isProtectionBlock() {
		return isProtectionBlock(this.protection.getLocation().getBlock());
	}

	public boolean isProtectionBlock(Block block) {
		boolean isSameLocation = this.protection.getWorldName().equals(block.getLocation().getWorld().getName())
				? block.getLocation().equals(this.protection.getLocation())
				: false;
		if (isSameLocation) {
			ProtectionBlock protectionBlock = this.protection.getProtectionBlock().getObject();

			if (protectionBlock == null) {
				return true;
			}

			return protectionBlock.getInformation().isSameType(block);
		}
		return false;
	}

	public boolean isProtectionBlockShown() {
		return isProtectionBlock();
	}

	public void showProtectionBlock() {
		Block block = this.protection.getLocation().getBlock();
		ItemStack item = this.protection.getProtectionBlock().getObject().getInformation().getItem();

		ProtectionUtilities.showBlock(block, item);
	}

	public void hideProtectionBlock() {
		ProtectionUtilities.hideBlock(this.protection.getLocation().getBlock());
	}

	@AllArgsConstructor
	@Getter
	@ToString
	@EqualsAndHashCode
	public static class SimpleLocation {

		private static final BiFunction<SimpleLocation, SimpleLocation, Boolean> HIGHER_OR_EQUAL_THAN_CHECKING = (loc1,
				loc2) -> loc1.getX() >= loc2.getX() && loc1.getY() >= loc2.getY() && loc1.getZ() >= loc2.getZ();
		private static final BiFunction<SimpleLocation, SimpleLocation, Boolean> LOWER_OR_EQUAL_THAN_CHECKING = (loc1,
				loc2) -> loc1.getX() <= loc2.getX() && loc1.getY() <= loc2.getY() && loc1.getZ() <= loc2.getZ();

		private String worldName;
		private double x;
		private double y;
		private double z;

		public SimpleLocation add(double x, double y, double z) {
			this.x += x;
			this.y += y;
			this.z += z;
			return this;
		}

		public SimpleLocation substract(double x, double y, double z) {
			this.x -= x;
			this.y -= y;
			this.z -= z;
			return this;
		}

		public SimpleLocation clone() {
			return new SimpleLocation(worldName, x, y, z);
		}

		public Location toLocation() {
			return new Location(Bukkit.getWorld(worldName), x, y, z);
		}

		public static SimpleLocation of(Location location) {
			return new SimpleLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
		}

		@Getter
		@ToString
		@EqualsAndHashCode
		public static class SimpleLocationArea {

			private String worldName;
			private SimpleLocation minLocation;
			private SimpleLocation maxLocation;

			public SimpleLocationArea(String worldName, SimpleLocation minLocation, SimpleLocation maxLocation) {
				this.worldName = worldName;
				this.minLocation = minLocation;
				this.maxLocation = maxLocation;
			}

			public SimpleLocationArea(String worldName, Location location1, Location location2) {
				this.worldName = worldName;
				this.minLocation = new SimpleLocation(worldName, Math.min(location1.getX(), location2.getX()),
						Math.min(location1.getY(), location2.getY()), Math.min(location1.getZ(), location2.getZ()));
				this.maxLocation = new SimpleLocation(worldName, Math.max(location1.getX(), location2.getX()),
						Math.max(location1.getY(), location2.getY()), Math.max(location1.getZ(), location2.getZ()));
			}

			public boolean isInside(SimpleLocation location) {
				if (!getWorldName().equals(location.getWorldName())) {
					return false;
				}

				return HIGHER_OR_EQUAL_THAN_CHECKING.apply(location, minLocation)
						&& LOWER_OR_EQUAL_THAN_CHECKING.apply(location, maxLocation);
			}

			public boolean isInside(SimpleLocationArea locationArea) {
				if (!getWorldName().equals(locationArea.getWorldName())) {
					return false;
				}

				return HIGHER_OR_EQUAL_THAN_CHECKING.apply(locationArea.getMaxLocation(), minLocation)
						&& LOWER_OR_EQUAL_THAN_CHECKING.apply(locationArea.getMinLocation(), maxLocation);
			}

			public SimpleLocationArea clone() {
				return new SimpleLocationArea(worldName, minLocation.clone(), maxLocation.clone());
			}

			public static SimpleLocationArea of(Location location1, Location location2) {
				return new SimpleLocationArea(location1.getWorld().getName(), location1, location2);
			}

		}

	}

}

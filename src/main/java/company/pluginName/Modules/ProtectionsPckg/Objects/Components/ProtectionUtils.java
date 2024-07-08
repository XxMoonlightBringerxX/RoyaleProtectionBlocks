package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import java.util.function.BiFunction;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionUtils.SimpleLocation.SimpleLocationArea;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ProtectionUtils {

	private static final BiFunction<SimpleLocation, SimpleLocation, Boolean> HIGHER_OR_EQUAL_THAN_CHECKING = (loc1,
			loc2) -> loc1.getX() >= loc2.getX() && loc1.getY() >= loc2.getY() && loc1.getZ() >= loc2.getZ();
	private static final BiFunction<SimpleLocation, SimpleLocation, Boolean> LOWER_OR_EQUAL_THAN_CHECKING = (loc1,
			loc2) -> loc1.getX() <= loc2.getX() && loc1.getY() <= loc2.getY() && loc1.getZ() <= loc2.getZ();

	private Protection protection;

	public boolean isInside(Location location) {
		return this.isInside(location, true);
	}

	public boolean isInside(Location location, boolean includeBorder) {
		return this.isInside(SimpleLocationArea.of(location, location), includeBorder);
	}

	public boolean isInside(SimpleLocationArea locationArea) {
		return this.isInside(locationArea, true);
	}

	public boolean isInside(SimpleLocationArea locationArea, boolean includeBorder) {
		if (this.protection.getLocation().getWorld().getName().equals(locationArea.getWorldName())) {
			SimpleLocation protMinLocation = SimpleLocation.of(this.protection.getMinLocation());
			SimpleLocation protMaxLocation = SimpleLocation.of(this.protection.getMaxLocation());

			if (!includeBorder) {
				protMinLocation.add(1, 1, 1);
			} else {
				protMaxLocation.add(1, 1, 1);
			}

			return HIGHER_OR_EQUAL_THAN_CHECKING.apply(locationArea.getMaxLocation(), protMinLocation)
					&& LOWER_OR_EQUAL_THAN_CHECKING.apply(locationArea.getMinLocation(), protMaxLocation);
		}

		return false;
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
	public static class SimpleLocation {

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

		public static SimpleLocation of(Location location) {
			return new SimpleLocation(location.getX(), location.getY(), location.getZ());
		}

		@Getter
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
				this.minLocation = new SimpleLocation(Math.min(location1.getX(), location2.getX()),
						Math.min(location1.getY(), location2.getY()), Math.min(location1.getZ(), location2.getZ()));
				this.maxLocation = new SimpleLocation(Math.max(location1.getX(), location2.getX()),
						Math.max(location1.getY(), location2.getY()), Math.max(location1.getZ(), location2.getZ()));
			}

			public static SimpleLocationArea of(Location location1, Location location2) {
				return new SimpleLocationArea(location1.getWorld().getName(), location1, location2);
			}

		}

	}

}

package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import java.util.function.BiFunction;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProtectionUtils {

	private static final BiFunction<Location, Location, Boolean> HIGHER_THAN_CHECKING = (loc1,
			loc2) -> loc1.getBlockX() > loc2.getBlockX() && loc1.getBlockY() > loc2.getBlockY()
					&& loc1.getBlockZ() > loc2.getBlockZ();
	private static final BiFunction<Location, Location, Boolean> HIGHER_OR_EQUAL_THAN_CHECKING = (loc1,
			loc2) -> loc1.getBlockX() >= loc2.getBlockX() && loc1.getBlockY() >= loc2.getBlockY()
					&& loc1.getBlockZ() >= loc2.getBlockZ();

	private static final BiFunction<Location, Location, Boolean> LOWER_THAN_CHECKING = (loc1,
			loc2) -> loc1.getBlockX() > loc2.getBlockX() && loc1.getBlockY() > loc2.getBlockY()
					&& loc1.getBlockZ() > loc2.getBlockZ();
	private static final BiFunction<Location, Location, Boolean> LOWER_OR_EQUAL_THAN_CHECKING = (loc1,
			loc2) -> loc1.getBlockX() <= loc2.getBlockX() && loc1.getBlockY() <= loc2.getBlockY()
					&& loc1.getBlockZ() <= loc2.getBlockZ();

	private Protection protection;

	public boolean isInside(Location location) {
		return this.isInside(location, location, true);
	}

	public boolean isInside(Location location, boolean includeBorder) {
		return this.isInside(location, location, includeBorder);
	}

	public boolean isInside(Location location1, Location location2) {
		return this.isInside(location1, location2, true);
	}

	public boolean isInside(Location location1, Location location2, boolean includeBorder) {
		if (!location1.getWorld().equals(location2.getWorld())
				|| !this.protection.getWorldName().equals(location1.getWorld().getName())) {
			return false;
		}

		Location minLocation = new Location(location1.getWorld(),
				Math.min(location1.getBlockX(), location2.getBlockX()),
				Math.min(location1.getBlockY(), location2.getBlockY()),
				Math.min(location1.getBlockZ(), location2.getBlockZ()));
		Location maxLocation = new Location(location1.getWorld(),
				Math.max(location1.getBlockX(), location2.getBlockX()),
				Math.max(location1.getBlockY(), location2.getBlockY()),
				Math.max(location1.getBlockZ(), location2.getBlockZ()));

		return (includeBorder ? HIGHER_OR_EQUAL_THAN_CHECKING.apply(maxLocation, this.protection.getMinLocation())
				: HIGHER_THAN_CHECKING.apply(maxLocation, this.protection.getMinLocation()))
				&& (includeBorder ? LOWER_OR_EQUAL_THAN_CHECKING.apply(minLocation, this.protection.getMaxLocation())
						: LOWER_THAN_CHECKING.apply(minLocation, this.protection.getMaxLocation()));
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

}

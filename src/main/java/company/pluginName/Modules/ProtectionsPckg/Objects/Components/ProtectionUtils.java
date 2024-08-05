package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaUtilities.WorldUtilities;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation.SimpleLocationArea;

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
		return includeBorder ? this.getProtectionArea().isInside(location)
				: this.getProtectionAreaWithoutBorder().isInside(location);
	}

	public boolean isInside(SimpleLocationArea locationArea, boolean includeBorder) {
		return includeBorder ? this.getProtectionArea().isInside(locationArea)
				: this.getProtectionAreaWithoutBorder().isInside(locationArea);
	}

	public boolean isInsideAny(SimpleLocation location, boolean includeBorder) {
		if (this.isInside(location, includeBorder)) {
			return true;
		}
		if (!protection.getChildProtections().isEmpty()) {
			for (int i = 0; i < protection.getChildProtections().size(); i++) {
				if (protection.getChildProtections().get(i).isInside(location, includeBorder)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isInsideAny(SimpleLocationArea locationArea, boolean includeBorder) {
		if (this.isInside(locationArea, includeBorder)) {
			return true;
		}
		if (!protection.getChildProtections().isEmpty()) {
			for (int i = 0; i < protection.getChildProtections().size(); i++) {
				if (protection.getChildProtections().get(i).isInside(locationArea, includeBorder)) {
					return true;
				}
			}
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

}

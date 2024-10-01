package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;

import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.Blocks.BlockUtilities;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ProtectionBoundaries {

	private @NonNull Protection protection;

	private BukkitTask protectionViewTask;
	private @Setter(lombok.AccessLevel.NONE) @Getter(lombok.AccessLevel.NONE) Entity protectionViewEntity;

	public void toggleProtectionView() {
		if (isProtectionViewActive()) {
			protectionViewTask.cancel();
			protectionViewTask = null;
			if (protectionViewEntity != null) {
				protectionViewEntity.remove();
				protectionViewEntity = null;
			}
		} else {
			protectionViewTask = TasksUtils.executeOnAsync(() -> {
				List<Location> locationsForParticles = getParticleCubeLocations();

				if (isProtectionViewActive()) {
					protectionViewTask = TasksUtils.execute(() -> {
						AtomicInteger seconds = new AtomicInteger(0);
						protectionViewEntity = BlockUtilities.setLocationGlowing(this.protection.getBukkitLocation());
						protectionViewTask = TasksUtils.executeOnAsyncWithTimer(() -> {
							locationsForParticles.forEach((loc) -> {
								DustOptions dustOptions = new DustOptions(Color.fromRGB((int) (Math.random() * 256),
										(int) (Math.random() * 256), (int) (Math.random() * 256)), 1.0F);
								loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, dustOptions);
							});

							seconds.set(seconds.get() + 1);
							if (seconds.get() >= Settings.SETTINGS_PROTECTION_BOUNDARIESVIEWDURATIONINSECONDS
									.getContent()) {
								TasksUtils.execute(() -> {
									protectionViewTask.cancel();
									protectionViewTask = null;
									protectionViewEntity.remove();
									protectionViewEntity = null;
								});
							}
						}, 0, 20);
					});
				}
			});
		}
	}

	public boolean isProtectionViewActive() {
		return protectionViewTask != null && !protectionViewTask.isCancelled();
	}

	public boolean isProtectionViewEntity(Entity ent) {
		return this.isProtectionViewActive() && this.protectionViewEntity != null
				&& ent.getType() == EntityType.MAGMA_CUBE
				&& this.protectionViewEntity.getUniqueId().equals(ent.getUniqueId());
	}

	private List<Location> getParticleCubeLocations() {
		List<Location> locations = new ArrayList<>();

		World world = Bukkit.getWorld(this.protection.getWorldName());
		Location loc1 = this.protection.getUtils().getProtectionArea().getMinLocation().toLocation();
		Location loc2 = this.protection.getUtils().getProtectionArea().getMaxLocation().toLocation();

		int minX = loc1.getBlockX();
		int maxX = loc2.getBlockX();
		int minY = loc1.getBlockY();
		int maxY = loc2.getBlockY();
		int minZ = loc1.getBlockZ();
		int maxZ = loc2.getBlockZ();

		for (double x = minX; x <= maxX; x += 0.25) {
			locations.add(new Location(world, x, minY, minZ));
			locations.add(new Location(world, x, minY, maxZ));
			locations.add(new Location(world, x, maxY, maxZ));
			locations.add(new Location(world, x, maxY, minZ));
		}

		for (double y = minY; y <= maxY; y += 0.25) {
			locations.add(new Location(world, minX, y, minZ));
			locations.add(new Location(world, minX, y, maxZ));
			locations.add(new Location(world, maxX, y, maxZ));
			locations.add(new Location(world, maxX, y, minZ));
		}

		for (double z = minZ; z <= maxZ; z += 0.25) {
			locations.add(new Location(world, minX, minY, z));
			locations.add(new Location(world, minX, maxY, z));
			locations.add(new Location(world, maxX, maxY, z));
			locations.add(new Location(world, maxX, minY, z));
		}

		return locations;
	}

}

package company.pluginName.Modules.ProtectionsPckg.Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.Protection.Delete.ProtectionDeleteException;
import company.pluginName.Exceptions.Protection.Delete.ProtectionDeleteUnknownException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveOverlapsException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveUnknownException;
import company.pluginName.Modules.FilePckg.Settings.SettingInt;
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import company.pluginName.Modules.ProtectionsPckg.Objects.ReferencedObjects.ReferencedProtectionBlock;
import company.pluginName.Utils.BlockVectorUtils;
import company.pluginName.Utils.OfflinePlayerUtils;
import lombok.Data;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotMessages.Objects.TextInput;
import relampagorojo93.LibsCollection.SpigotMessages.Objects.TextReplacement;
import relampagorojo93.LibsCollection.Utils.Bukkit.BlockUtils;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

@Data
public class Protection {

	private static final StringFlag GREETING_FLAG;
	private static final StringFlag FAREWELL_FLAG;

	private static String generateDefaultName(Location location) {
		return location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_"
				+ location.getBlockZ();
	}

	static {
		StringFlag flag = null;

		try {
			flag = (StringFlag) Flags.class.getField("GREET_TITLE").get(null);
		} catch (Exception e) {
			try {
				flag = (StringFlag) Flags.class.getField("GREET_MESSAGE").get(null);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		GREETING_FLAG = flag;

		flag = null;

		try {
			flag = (StringFlag) Flags.class.getField("FAREWELL_TITLE").get(null);
		} catch (Exception e) {
			try {
				flag = (StringFlag) Flags.class.getField("FAREWELL_MESSAGE").get(null);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		FAREWELL_FLAG = flag;
	}

	private String regionId;
	private UUID ownerUuid;
	private ReferencedProtectionBlock protectionBlock;
	private String worldName;
	private String displayName;
	private ProtectedRegion protectedRegion;
	private Location protectionBlockLocation;
	private BukkitTask protectionViewTask;
	private Entity protectionViewEntity;

	public Protection(UUID ownerUuid) {
		this(null, ownerUuid, null, null, null, null, null);
	}

	public Protection(String regionId, UUID ownerUuid, ReferencedProtectionBlock protectionBlock, String worldName,
			String displayName) {
		this(regionId, ownerUuid, protectionBlock, worldName, displayName, null, null);
	}

	public Protection(String regionId, UUID ownerUuid, ReferencedProtectionBlock protectionBlock, String worldName,
			String displayName, ProtectedRegion protectedRegion, Location protectionBlockLocation) {
		this.regionId = regionId;
		this.ownerUuid = ownerUuid;
		this.protectionBlock = protectionBlock;
		this.worldName = worldName;
		this.displayName = displayName;
		this.protectedRegion = protectedRegion;
		this.protectionBlockLocation = protectionBlockLocation;
	}

	/*
	 * View methods
	 */

	public void toggleProtectionView() {
		if (isProtectionViewActive()) {
			protectionViewTask.cancel();
			protectionViewTask = null;
			if (protectionViewEntity != null) {
				protectionViewEntity.remove();
				protectionViewEntity = null;
			}
		} else {
			protectionViewTask = Bukkit.getScheduler().runTaskAsynchronously(MainPluginClass.getPlugin(), () -> {
				List<Location> locationsForParticles = getParticleCubeLocations();

				if (isProtectionViewActive()) {
					protectionViewTask = Bukkit.getScheduler().runTask(MainPluginClass.getPlugin(), () -> {
						AtomicInteger seconds = new AtomicInteger(0);
						protectionViewEntity = BlockUtils.setLocationGlowing(getProtectionBlockLocation());
						protectionViewTask = Bukkit.getScheduler()
								.runTaskTimerAsynchronously(MainPluginClass.getPlugin(), () -> {
									locationsForParticles.forEach((loc) -> {
										DustOptions dustOptions = new DustOptions(
												Color.fromRGB((int) (Math.random() * 256), (int) (Math.random() * 256),
														(int) (Math.random() * 256)),
												1.0F);
										loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, dustOptions);
									});

									seconds.set(seconds.get() + 1);
									if (seconds.get() >= SettingInt.SETTINGS_PROTECTION_BOUNDARIESVIEWDURATIONINSECONDS
											.getContent()) {
										Bukkit.getScheduler().runTask(MainPluginClass.getPlugin(), () -> {
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

	public void create(Location location, ProtectionBlock protectionBlock) throws ProtectionSaveException {
		this.protectionBlock = new ReferencedProtectionBlock(protectionBlock.getId());
		this.worldName = location.getWorld().getName();
		this.regionId = generateDefaultName(location).toLowerCase();
		this.displayName = generateDefaultName(location);

		ProtectedRegion protectedRegion = new ProtectedCuboidRegion(regionId,
				BlockVector3.at(location.getBlockX() - protectionBlock.getBlocksX(),
						location.getBlockY() - protectionBlock.getBlocksY(),
						location.getBlockZ() - protectionBlock.getBlocksZ()),
				BlockVector3.at(location.getBlockX() + protectionBlock.getBlocksX(),
						location.getBlockY() + protectionBlock.getBlocksY(),
						location.getBlockZ() + protectionBlock.getBlocksZ()));

		protectedRegion.getOwners().addPlayer(ownerUuid);

		RegionManager regionManager = getRegionManager(location.getWorld());

		if (regionManager == null) {
			throw new ProtectionSaveUnknownException(new Exception("Region Manager is null"));
		}

		List<ProtectedRegion> overlaps = protectedRegion.getIntersectingRegions(regionManager.getRegions().values());

		if (overlaps.size() > 0 && overlaps.stream().anyMatch(prot -> {
			Protection protection = MainPluginClass.getPlugin().getProtectionsModule().getProtectionByRegion()
					.get(prot.getId());
			return protection == null || !protection.isMainOwner(ownerUuid);
		})) {
			throw new ProtectionSaveOverlapsException();
		}

		try {
			String playerName = OfflinePlayerUtils.getOfflinePlayer(ownerUuid).getName();
			protectedRegion.setFlag(GREETING_FLAG,
					MessageBuilder.createMessage(
							TextInput.inst().text(SettingString.SETTINGS_PROTECTION_ENTERDEFAULTMESSAGE.toString())
									.replacements(new TextReplacement("{player}", () -> playerName)))
							.toString());
			protectedRegion.setFlag(FAREWELL_FLAG,
					MessageBuilder.createMessage(
							TextInput.inst().text(SettingString.SETTINGS_PROTECTION_EXITDEFAULTMESSAGE.toString())
									.replacements(new TextReplacement("{player}", () -> playerName)))
							.toString());
			regionManager.addRegion(protectedRegion);
			regionManager.save();

			this.protectedRegion = protectedRegion;

			MainPluginClass.getPlugin().getSqlModule().saveProtection(this);
		} catch (Exception e) {
			if (regionManager.hasRegion(protectedRegion.getId())) {
				regionManager.removeRegion(protectedRegion.getId());

				try {
					regionManager.save();
				} catch (StorageException e2) {
					throw new ProtectionSaveUnknownException(e2);
				}
			}
			throw e instanceof ProtectionSaveException ? (ProtectionSaveException) e
					: new ProtectionSaveUnknownException(e);
		}
	}

	public void delete() throws ProtectionDeleteException {
		RegionManager regionManager = getRegionManager();

		if (regionManager == null) {
			throw new ProtectionDeleteUnknownException();
		}

		if (regionManager.hasRegion(regionId)) {
			try {
				regionManager.removeRegion(regionId);
				regionManager.save();

				this.protectedRegion = null;
				this.protectionBlockLocation = null;
			} catch (StorageException e) {
				throw new ProtectionDeleteUnknownException(e);
			}
		}

		MainPluginClass.getPlugin().getSqlModule().deleteProtection(this);
	}

	public ProtectedRegion getProtectedRegion() {
		if (this.protectedRegion == null) {
			RegionManager regionManager = getRegionManager();

			if (regionManager != null) {
				this.protectedRegion = regionManager.getRegion(regionId);
			}
		}

		return this.protectedRegion;
	}

	public Location getProtectionBlockLocation() {
		if (this.protectionBlockLocation == null) {
			if (getProtectedRegion() != null) {
				BlockVector3 vector = BlockVectorUtils.getIntersectingVector(getProtectedRegion().getMinimumPoint(),
						getProtectedRegion().getMaximumPoint());

				this.protectionBlockLocation = new Location(Bukkit.getWorld(worldName), vector.getBlockX(),
						vector.getBlockY(), vector.getBlockZ()).getBlock().getLocation();
			}
		}

		return this.protectionBlockLocation;
	}

	public boolean isMainOwner(UUID playerUuid) {
		return this.ownerUuid.equals(playerUuid);
	}

	public boolean isOwner(UUID ownerUuid) {
		RegionManager regionManager = getRegionManager();

		if (regionManager != null && regionManager.hasRegion(regionId)) {
			return regionManager.getRegion(regionId)
					.isOwner(MainPluginClass.getWorldGuardAPI().getInternalWorldGuard().wrapPlayer(ownerUuid));
		}

		return false;
	}

	public boolean isMember(UUID memberUuid, ProtectedRegion rg) {
		return rg.getMembers().getPlayers().contains(memberUuid.toString());
	}

	public void setDisplayName(String displayName) throws ProtectionSaveException {
		String oldDisplayName = this.displayName;
		try {
			this.displayName = displayName;
			MainPluginClass.getPlugin().getSqlModule().saveProtection(this);
		} catch (ProtectionSaveException e) {
			this.displayName = oldDisplayName;
			throw e;
		}
	}

	/**
	 * Checks if the specified block is a protection block. By using this
	 * non-provided arg method, it'll be using the protection block location from
	 * this region.
	 * 
	 * @return If the block is a protection block or not.
	 */
	public Boolean isProtectionBlock() {
		return isProtectionBlock(getProtectionBlockLocation().getBlock());
	}

	/**
	 * Checks if the specified block is a protection block. A NULL boolean means the
	 * protection block is an Oraxen protection block. A TRUE boolean means the
	 * block is the same type as the protection block linked to this protection. A
	 * FALSE boolean means the block is not the same type as the protection block
	 * linked to this protection and it's not an Oraxen protection block.
	 * 
	 * @return If the block is a protection block or not.
	 */
	public Boolean isProtectionBlock(Block block) {
		boolean isSameLocation = block.getLocation().equals(getProtectionBlockLocation());
		if (isSameLocation) {
			ProtectionBlock protectionBlock = this.protectionBlock.getObject();

			if (protectionBlock.getItem().getType() == Material.PAPER.getMaterial()) {
				return null;
			}

			if (protectionBlock != null && block.getType() == protectionBlock.getItem().getType()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the current block on the protection location is a protection block
	 * or not. Only if the {@link Protection#isProtectionBlock()} returns false,
	 * means it's not an Oraxen block or a protection block.
	 * 
	 * @return If the protection block is shown or not.
	 */
	public boolean isProtectionBlockShown() {
		return !(Boolean.FALSE.equals(isProtectionBlock(getProtectionBlockLocation().getBlock())));
	}

	public void hideProtectionBlock() {
		getProtectionBlockLocation().getBlock().setType(Material.AIR.getMaterial());
	}

	public void showProtectionBlock() {
		Block bBlock = getProtectionBlockLocation().getBlock();
		ItemStack item = getProtectionBlock().getObject().getItem();
		bBlock.setType(item.getType());
		if (item.getType() == Material.PLAYER_HEAD.getMaterial()) {
			BlockUtils.setSkin(bBlock, ItemStacksUtils.getSkin(item));
		}
	}

	private RegionManager getRegionManager() {
		World world = Bukkit.getWorld(worldName);

		if (world == null) {
			return null;
		}

		return getRegionManager(world);
	}

	private RegionManager getRegionManager(World world) {
		try {
			return MainPluginClass.getWorldGuardAPI().getInternalWorldGuard().getRegionManager(world);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<Location> getParticleCubeLocations() {
		List<Location> locations = new ArrayList<>();

		World world = Bukkit.getWorld(worldName);
		BlockVector3 pos1 = getProtectedRegion().getMinimumPoint();
		BlockVector3 pos2 = getProtectedRegion().getMaximumPoint();

		int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
		int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX()) + 1;
		int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
		int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY()) + 1;
		int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
		int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ()) + 1;

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

package company.pluginName.Modules.ProtectionsPckg.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.MainPluginClass;
import company.pluginName.Permissions;
import company.pluginName.APIs.ItemsAdderAPI;
import company.pluginName.APIs.OraxenAPI;
import company.pluginName.Exceptions.Protection.Delete.ProtectionDeleteException;
import company.pluginName.Exceptions.Protection.Delete.ProtectionDeleteUnknownException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveOverlapsException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveUnknownException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.Protections.ProtectionActions;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.Protections.ProtectionBanneds;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.Protections.ProtectionMembers;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.Protections.ProtectionOwners;
import company.pluginName.Modules.ProtectionsPckg.Objects.ReferencedObjects.ReferencedProtectionBlock;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingBoolean;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingInt;
import company.pluginName.Utils.BlockVectorUtils;
import company.pluginName.Utils.OfflinePlayerUtils;
import company.pluginName.Utils.ReflectUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import relampagorojo93.LibsCollection.Utils.Bukkit.BlockUtils;
import relampagorojo93.LibsCollection.Utils.Bukkit.WorldUtils;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

@Data
public class Protection {

	private static String generateDefaultName(Location location) {
		return location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
	}

	private String regionId;
	private UUID ownerUuid;
	private ReferencedProtectionBlock protectionBlock;
	private String worldName;
	private String displayName;
	private ProtectedRegion protectedRegion;
	private Location protectionBlockLocation;
	private BukkitTask protectionViewTask;
	private @Setter(lombok.AccessLevel.NONE) @Getter(lombok.AccessLevel.NONE) Entity protectionViewEntity;
	private @Setter(lombok.AccessLevel.NONE) long createdDate;
	private ProtectionMembers members = new ProtectionMembers(this);
	private ProtectionOwners owners = new ProtectionOwners(this);
	private ProtectionBanneds banneds = new ProtectionBanneds(this);
	private ProtectionActions actions = new ProtectionActions(this);

	public Protection(UUID ownerUuid) {
		this(null, ownerUuid, null, null, null, 0, null, null);
	}

	public Protection(String regionId, UUID ownerUuid, ReferencedProtectionBlock protectionBlock, String worldName, String displayName,
			long createdDate) {
		this(regionId, ownerUuid, protectionBlock, worldName, displayName, createdDate, null, null);
	}

	public Protection(String regionId, UUID ownerUuid, ReferencedProtectionBlock protectionBlock, String worldName, String displayName,
			long createdDate, ProtectedRegion protectedRegion, Location protectionBlockLocation) {
		this.regionId = regionId;
		this.ownerUuid = ownerUuid;
		this.protectionBlock = protectionBlock;
		this.worldName = worldName;
		this.displayName = displayName;
		this.createdDate = createdDate;
		this.protectedRegion = protectedRegion;
		this.protectionBlockLocation = protectionBlockLocation;
	}

	/*
	 * Flags methods
	 */

	public Location getHome() {
		Object location = getProtectedRegion().getFlag(Flags.TELE_LOC);
		if (location != null) {
			return ReflectUtils.unknownLocationToBukkitLocation(Bukkit.getWorld(getWorldName()), location);
		}
		return null;
	}

	public void setHome(Location location) throws Exception {
		ProtectedRegion region = getProtectedRegion();
		Map<Flag<?>, Object> flags = region.getFlags();
		flags.put(Flags.TELE_LOC, MainPluginClass.getWorldGuardAPI().getInternalWorldGuard().adapt(location));
		region.setFlags(flags);
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
						protectionViewTask = Bukkit.getScheduler().runTaskTimerAsynchronously(MainPluginClass.getPlugin(), () -> {
							locationsForParticles.forEach((loc) -> {
								DustOptions dustOptions = new DustOptions(Color.fromRGB((int) (Math.random() * 256),
										(int) (Math.random() * 256), (int) (Math.random() * 256)), 1.0F);
								loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, dustOptions);
							});

							seconds.set(seconds.get() + 1);
							if (seconds.get() >= SettingInt.SETTINGS_PROTECTION_BOUNDARIESVIEWDURATIONINSECONDS.getContent()) {
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

	public boolean isProtectionViewEntity(Entity ent) {
		return this.isProtectionViewActive() && this.protectionViewEntity != null && ent.getType() == EntityType.MAGMA_CUBE
				&& this.protectionViewEntity.getUniqueId().equals(ent.getUniqueId());
	}

	@SuppressWarnings("unchecked")
	public void create(Player creator, Location location, ProtectionBlock protectionBlock) throws ProtectionSaveException {
		this.protectionBlock = new ReferencedProtectionBlock(protectionBlock.getInformation().getId());
		this.worldName = location.getWorld().getName();
		this.regionId = generateDefaultName(location).toLowerCase();
		this.displayName = generateDefaultName(location);

		int minBlockX = location.getBlockX() - protectionBlock.getInformation().getBlocksX();
		int minBlockY = protectionBlock.getInformation().getBlocksY() == -1 ? WorldUtils.getMinHeight(location.getWorld())
				: location.getBlockY() - protectionBlock.getInformation().getBlocksY();
		int minBlockZ = location.getBlockZ() - protectionBlock.getInformation().getBlocksZ();
		int maxBlockX = location.getBlockX() + protectionBlock.getInformation().getBlocksX();
		int maxBlockY = protectionBlock.getInformation().getBlocksY() == -1 ? WorldUtils.getMaxHeight(location.getWorld())
				: location.getBlockY() + protectionBlock.getInformation().getBlocksY();
		int maxBlockZ = location.getBlockZ() + protectionBlock.getInformation().getBlocksZ();

		ProtectedRegion protectedRegion = new ProtectedCuboidRegion(regionId, BlockVector3.at(minBlockX, minBlockY, minBlockZ),
				BlockVector3.at(maxBlockX, maxBlockY, maxBlockZ));

		protectedRegion.getOwners().addPlayer(ownerUuid);

		RegionManager regionManager = getRegionManager(location.getWorld());

		if (regionManager == null) {
			throw new ProtectionSaveUnknownException(new Exception("Region Manager is null"));
		}

		List<ProtectedRegion> overlaps = protectedRegion.getIntersectingRegions(regionManager.getRegions().values());

		if (creator != null && !creator.hasPermission(Permissions.PROTECTION_OVERLAP_BYPASS) && overlaps.size() > 0 && overlaps.stream()
				.anyMatch(prot -> prot.getOwners().size() == 0 || !prot.getOwners().getUniqueIds().iterator().next().equals(ownerUuid))) {
			throw new ProtectionSaveOverlapsException();
		}

		try {
			OfflinePlayer player = OfflinePlayerUtils.getOfflinePlayer(this.ownerUuid);

			HashMap<com.sk89q.worldguard.protection.flags.Flag<?>, Object> flags = new HashMap<>();
			MainPluginClass.getPlugin().getProtectionSettingsModule().getDefaultFlags().forEach((key, value) -> {
				if (MainPluginClass.getPlaceholderAPI().isHooked()) {
					if (value instanceof String) {
						flags.put(key, MainPluginClass.getPlaceholderAPI().applyPlaceholders((String) value, player));
					} else if (value instanceof Set) {
						Set<String> set = new HashSet<>();
						((Set<String>) value)
								.forEach(string -> set.add(MainPluginClass.getPlaceholderAPI().applyPlaceholders(string, player)));
						flags.put(key, set);
					} else {
						flags.put(key, value);
					}
				} else {
					flags.put(key, value);
				}
			});
			flags.put(MainPluginClass.getWorldGuardAPI().getProtectionBlockLocationFlag().getWorldGuardFlag(),
					MainPluginClass.getWorldGuardAPI().getInternalWorldGuard().adapt(location));

			if (SettingBoolean.SETTINGS_PROTECTION_SETPLAYERPOSITIONASHOMEONCREATION.getContent() && player != null && player.isOnline()) {
				flags.put(Flags.TELE_LOC,
						MainPluginClass.getWorldGuardAPI().getInternalWorldGuard().adapt(player.getPlayer().getLocation()));
			}

			protectedRegion.setFlags(flags);
			regionManager.addRegion(protectedRegion);

			this.protectedRegion = protectedRegion;
			this.createdDate = System.currentTimeMillis();

			MainPluginClass.getPlugin().getSqlModule().saveProtection(this);
		} catch (Exception e) {
			if (regionManager.hasRegion(protectedRegion.getId())) {
				regionManager.removeRegion(protectedRegion.getId());
			}
			throw e instanceof ProtectionSaveException ? (ProtectionSaveException) e : new ProtectionSaveUnknownException(e);
		}
	}

	public void delete() throws ProtectionDeleteException {
		RegionManager regionManager = getRegionManager();

		if (regionManager == null) {
			throw new ProtectionDeleteUnknownException();
		}

		if (regionManager.hasRegion(regionId)) {
			regionManager.removeRegion(regionId);

			this.protectedRegion = null;
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
				World world = Bukkit.getWorld(worldName);
				ProtectedRegion region = getProtectedRegion();

				if (region != null) {
					Location loc = MainPluginClass.getWorldGuardAPI().getProtectionBlockLocationFlag().flagState(world, region);

					if (loc != null) {
						this.protectionBlockLocation = loc;
					} else {
						BlockVector3 vector = BlockVectorUtils.getIntersectingVector(region.getMinimumPoint(),
								getProtectedRegion().getMaximumPoint());

						this.protectionBlockLocation = new Location(Bukkit.getWorld(worldName), vector.getBlockX(), vector.getBlockY(),
								vector.getBlockZ()).getBlock().getLocation();
					}
				}
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
	public boolean isProtectionBlock() {
		return isProtectionBlock(getProtectionBlockLocation().getBlock());
	}

	public boolean isProtectionBlock(Block block) {
		boolean isSameLocation = block.getLocation().equals(getProtectionBlockLocation());
		if (isSameLocation) {
			ProtectionBlock protectionBlock = this.protectionBlock.getObject();

			if (protectionBlock == null) {
				return true;
			}

			return protectionBlock.getInformation().isSameType(block);
		}
		return false;
	}

	/**
	 * Checks if the current block on the protection location is a protection block
	 * or not.
	 * 
	 * @return If the protection block is shown or not.
	 */
	// TODO: Use a flag for the show and hide to prevent this cases instead of using
	// a checking, this will optimize the checkings.
	public boolean isProtectionBlockShown() {
		return isProtectionBlock();
	}

	public void hideProtectionBlock() {
		ItemsAdderAPI.PlaceResult itemsAdderResult = MainPluginClass.getItemsAdderAPI().setBlock(null, getProtectionBlockLocation());

		if (itemsAdderResult != ItemsAdderAPI.PlaceResult.NOT_HOOKED) {
			if (itemsAdderResult == ItemsAdderAPI.PlaceResult.PLACED) {
				return;
			}
		}

		OraxenAPI.PlaceResult oraxenResult = MainPluginClass.getOraxenAPI().setBlock(null, getProtectionBlockLocation());

		if (oraxenResult != OraxenAPI.PlaceResult.NOT_HOOKED) {
			if (oraxenResult == OraxenAPI.PlaceResult.PLACED) {
				return;
			}
		}

		getProtectionBlockLocation().getBlock().setType(Material.AIR.getMaterial());
	}

	public void showProtectionBlock() {
		Block bBlock = getProtectionBlockLocation().getBlock();
		ItemStack item = getProtectionBlock().getObject().getInformation().getItem();

		ItemsAdderAPI.PlaceResult itemsAdderResult = MainPluginClass.getItemsAdderAPI().setBlock(item, bBlock.getLocation());

		if (itemsAdderResult != ItemsAdderAPI.PlaceResult.NOT_HOOKED) {
			if (itemsAdderResult == ItemsAdderAPI.PlaceResult.PLACED) {
				return;
			}
		}

		OraxenAPI.PlaceResult oraxenResult = MainPluginClass.getOraxenAPI().setBlock(item, bBlock.getLocation());

		if (oraxenResult != OraxenAPI.PlaceResult.NOT_HOOKED) {
			if (oraxenResult == OraxenAPI.PlaceResult.PLACED) {
				return;
			}
		}

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

	/*
	 * Authority methods
	 */

	public boolean canDelete(Player pl) {
		return this.isMainOwner(pl.getUniqueId()) || pl.hasPermission(Permissions.PROTECTION_DELETE_OTHERS);
	}

	public boolean canManage(Player pl) {
		return this.isOwner(pl.getUniqueId()) || pl.hasPermission(Permissions.PROTECTION_MANAGE_OTHERS);
	}

	public boolean canToggleBlock(Player pl) {
		return this.isOwner(pl.getUniqueId()) || pl.hasPermission(Permissions.PROTECTION_TOGGLEBLOCK_OTHERS);
	}

	public boolean canViewBoundaries(Player pl) {
		return this.isOwner(pl.getUniqueId()) || pl.hasPermission(Permissions.PROTECTION_VIEW_OTHERS);
	}

}

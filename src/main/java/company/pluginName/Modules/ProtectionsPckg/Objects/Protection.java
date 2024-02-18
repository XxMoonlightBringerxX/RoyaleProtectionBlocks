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

import company.pluginName.Permissions;
import company.pluginName.APIs.ItemsAdderAPI.ItemsAdderAPI;
import company.pluginName.APIs.ItemsAdderAPI.Hook.ItemsAdderHook;
import company.pluginName.APIs.OraxenAPI.OraxenAPI;
import company.pluginName.APIs.OraxenAPI.Hook.OraxenHook;
import company.pluginName.APIs.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.APIs.VaultAPI.VaultAPI;
import company.pluginName.APIs.WorldGuard.WorldGuardAPI;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Reference.ReferencedProtectionBlock;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionActions;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionBanneds;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionMembers;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionOwners;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import company.pluginName.Modules.SQLPckg.SQLService;
import company.pluginName.Utils.BlockVectorUtils;
import company.pluginName.Utils.ReflectUtils;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.SkinUtilities;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import relampagorojo93.LibsCollection.Utils.Bukkit.BlockUtils;
import relampagorojo93.LibsCollection.Utils.Bukkit.WorldUtils;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;

@Data
public class Protection {

	@PandaInject
	private static ProtectionSettingsService protectionSettingsService;

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static SQLService sqlService;

	@PandaInject
	private static PlaceholderAPI placeholderApi;

	@PandaInject
	private static ItemsAdderAPI itemsAdderApi;

	@PandaInject
	private static OraxenAPI oraxenApi;

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	@PandaInject
	private static VaultAPI vaultApi;

	@PandaInject
	private static PlayerDataService playerDataService;

	private static String generateDefaultName(Location location) {
		return location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_"
				+ location.getBlockZ();
	}

	private String regionId;
	private UUID ownerUuid;
	private String ownerName;
	private ReferencedProtectionBlock protectionBlock;
	private String worldName;
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

	public Protection(String regionId, UUID ownerUuid, ReferencedProtectionBlock protectionBlock, String worldName,
			String displayName, long createdDate) {
		this(regionId, ownerUuid, protectionBlock, worldName, displayName, createdDate, null, null);
	}

	public Protection(String regionId, UUID ownerUuid, ReferencedProtectionBlock protectionBlock, String worldName,
			String displayName, long createdDate, ProtectedRegion protectedRegion, Location protectionBlockLocation) {
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
	 * Display name
	 */

	private @Setter(lombok.AccessLevel.NONE) String displayName;
	private @Setter(lombok.AccessLevel.NONE) String displayNameWithoutFormat;

	public void setDisplayName(String displayName) throws RoyaleProtectionBlocksException {
		setDisplayName(null, displayName);
	}

	public void setDisplayName(Player pl, String displayName) throws RoyaleProtectionBlocksException {
		if (pl != null) {
			if (!getOwners().list().contains(pl.getUniqueId())
					&& !pl.hasPermission(Permissions.PROTECTION_RENAME_OTHERS)) {
				throw Exceptions.Protections.Save.RENAMEDENIED.generateException();
			}
		}

		String displayNameWithoutFormat = MessageTemplate.inst(displayName).setColor(false).process().toString();

		if (displayNameWithoutFormat.isEmpty()) {
			throw Exceptions.Protections.Save.NOVISIBLETEXT.generateException();
		}

		if (protectionsService.getProtectionsByOwner().get(ownerUuid).stream().anyMatch(prot -> {
			return !prot.getRegionId().equals(getRegionId())
					&& (displayNameWithoutFormat.equalsIgnoreCase(prot.getRegionId())
							|| displayNameWithoutFormat.equalsIgnoreCase(prot.getDisplayNameWithoutFormat()));
		})) {
			throw Exceptions.Protections.Save.NAMEINUSE.generateException();
		}

		this.displayName = displayName;
		this.displayNameWithoutFormat = MessageTemplate.inst(displayName).setColor(false).process().toString();

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtection(this);
			} catch (RoyaleProtectionBlocksException e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public String getDisplayNameWithoutFormat() {
		if (this.displayName != null && this.displayNameWithoutFormat == null) {
			this.displayNameWithoutFormat = MessageTemplate.inst(this.displayName).setColor(false).process().toString();
		}
		return this.displayNameWithoutFormat;
	}

	/*
	 * Data methods
	 */

	public String getOwnerName() {
		if (ownerName == null) {
			OfflinePlayer owner = OfflinePlayerUtilities.getOfflinePlayer(ownerUuid);
			this.ownerName = owner != null ? owner.getName() : "";
		}
		return this.ownerName;
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
		flags.put(Flags.TELE_LOC, worldGuardApi.getHook().getInternalWorldGuard().adapt(location));
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
			protectionViewTask = TasksUtils.executeOnAsync(() -> {
				List<Location> locationsForParticles = getParticleCubeLocations();

				if (isProtectionViewActive()) {
					protectionViewTask = TasksUtils.execute(() -> {
						AtomicInteger seconds = new AtomicInteger(0);
						protectionViewEntity = BlockUtils.setLocationGlowing(getProtectionBlockLocation());
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

	@SuppressWarnings("unchecked")
	public void create(Player creator, Location location, ProtectionBlock protectionBlock)
			throws RoyaleProtectionBlocksException {
		this.protectionBlock = new ReferencedProtectionBlock(protectionBlock.getInformation().getId());
		this.worldName = location.getWorld().getName();
		this.regionId = generateDefaultName(location).toLowerCase();
		this.displayName = generateDefaultName(location);

		int minBlockX = location.getBlockX() - protectionBlock.getInformation().getBlocksX();
		int minBlockY = protectionBlock.getInformation().getBlocksY() == -1
				? WorldUtils.getMinHeight(location.getWorld())
				: location.getBlockY() - protectionBlock.getInformation().getBlocksY();
		int minBlockZ = location.getBlockZ() - protectionBlock.getInformation().getBlocksZ();
		int maxBlockX = location.getBlockX() + protectionBlock.getInformation().getBlocksX();
		int maxBlockY = protectionBlock.getInformation().getBlocksY() == -1
				? WorldUtils.getMaxHeight(location.getWorld())
				: location.getBlockY() + protectionBlock.getInformation().getBlocksY();
		int maxBlockZ = location.getBlockZ() + protectionBlock.getInformation().getBlocksZ();

		ProtectedRegion protectedRegion = new ProtectedCuboidRegion(regionId,
				BlockVector3.at(minBlockX, minBlockY, minBlockZ), BlockVector3.at(maxBlockX, maxBlockY, maxBlockZ));

		protectedRegion.getOwners().addPlayer(ownerUuid);

		RegionManager regionManager = getRegionManager(location.getWorld());

		if (regionManager == null) {
			throw Exceptions.Protections.Save.UNKNOWN.generateException(new Exception("Region Manager is"));
		}

		List<ProtectedRegion> overlaps = protectedRegion.getIntersectingRegions(regionManager.getRegions().values());

		if (creator != null && !creator.hasPermission(Permissions.PROTECTION_OVERLAP_BYPASS) && overlaps.size() > 0
				&& (!Settings.SETTINGS_PROTECTION_ALLOWREGIONSINSIDEANOTHERFROMSAMEOWNER.getContent()
						|| overlaps.stream().anyMatch(prot -> prot.getOwners().size() == 0
								|| !prot.getOwners().getUniqueIds().iterator().next().equals(ownerUuid)))) {
			throw Exceptions.Protections.Save.OVERLAPS.generateException();
		}

		try {
			OfflinePlayer player = OfflinePlayerUtilities.getOfflinePlayer(this.ownerUuid);

			HashMap<com.sk89q.worldguard.protection.flags.Flag<?>, Object> flags = new HashMap<>();
			protectionSettingsService.getDefaultFlags().forEach(defaultFlag -> {
				if (placeholderApi.getHook().isHooked()) {
					if (defaultFlag.getValue() instanceof String) {
						flags.put(defaultFlag.getFlag(),
								MessageTemplate.inst(placeholderApi.getHook()
										.applyPlaceholders((String) defaultFlag.getValue(), player)
										.replaceAll("\\\\%", "%")).process().toString());
					} else if (defaultFlag.getValue() instanceof Set) {
						Set<String> set = new HashSet<>();
						((Set<String>) defaultFlag.getValue()).forEach(string -> set.add(MessageTemplate.inst(
								placeholderApi.getHook().applyPlaceholders(string, player).replaceAll("\\\\%", "%"))
								.process().toString()));
						flags.put(defaultFlag.getFlag(), set);
					} else {
						flags.put(defaultFlag.getFlag(), defaultFlag.getValue());
					}
				} else {
					flags.put(defaultFlag.getFlag(), defaultFlag.getValue());
				}

				if (defaultFlag.getRegionGroup() != null && defaultFlag.getFlag().getRegionGroupFlag() != null
						&& defaultFlag.getFlag().getRegionGroupFlag().getDefault() != defaultFlag.getRegionGroup()) {
					flags.put(defaultFlag.getFlag().getRegionGroupFlag(), defaultFlag.getRegionGroup());
				}
			});
			flags.put(worldGuardApi.getHook().getProtectionBlockLocationFlag().getWorldGuardFlag(),
					worldGuardApi.getHook().getInternalWorldGuard().adapt(location));

			if (Settings.SETTINGS_PROTECTION_SETPLAYERPOSITIONASHOMEONCREATION.getContent() && player != null
					&& player.isOnline()) {
				flags.put(Flags.TELE_LOC,
						worldGuardApi.getHook().getInternalWorldGuard().adapt(player.getPlayer().getLocation()));
			}

			protectedRegion.setFlags(flags);
			regionManager.addRegion(protectedRegion);

			this.protectedRegion = protectedRegion;
			this.createdDate = System.currentTimeMillis();

			sqlService.saveProtection(this);
		} catch (Exception e) {
			if (regionManager.hasRegion(protectedRegion.getId())) {
				regionManager.removeRegion(protectedRegion.getId());
			}
			throw e instanceof RoyaleProtectionBlocksException ? (RoyaleProtectionBlocksException) e
					: Exceptions.Protections.Save.UNKNOWN.generateException(e);
		}
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
					Location loc = worldGuardApi.getHook().getProtectionBlockLocationFlag().flagState(world, region);

					if (loc != null) {
						this.protectionBlockLocation = loc;
					} else {
						BlockVector3 vector = BlockVectorUtils.getIntersectingVector(region.getMinimumPoint(),
								getProtectedRegion().getMaximumPoint());

						this.protectionBlockLocation = new Location(Bukkit.getWorld(worldName), vector.getBlockX(),
								vector.getBlockY(), vector.getBlockZ()).getBlock().getLocation();
					}
				}
			}
		}

		return this.protectionBlockLocation;
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
		ItemsAdderHook.PlaceResult itemsAdderResult = itemsAdderApi.getHook().setBlock(null,
				getProtectionBlockLocation());

		if (itemsAdderResult != ItemsAdderHook.PlaceResult.NOT_HOOKED) {
			if (itemsAdderResult == ItemsAdderHook.PlaceResult.PLACED) {
				return;
			}
		}

		OraxenHook.PlaceResult oraxenResult = oraxenApi.getHook().setBlock(null, getProtectionBlockLocation());

		if (oraxenResult != OraxenHook.PlaceResult.NOT_HOOKED) {
			if (oraxenResult == OraxenHook.PlaceResult.PLACED) {
				return;
			}
		}

		getProtectionBlockLocation().getBlock().setType(Material.AIR.getMaterial());
	}

	public void showProtectionBlock() {
		Block bBlock = getProtectionBlockLocation().getBlock();
		ItemStack item = getProtectionBlock().getObject().getInformation().getItem();

		ItemsAdderHook.PlaceResult itemsAdderResult = itemsAdderApi.getHook().setBlock(item, bBlock.getLocation());

		if (itemsAdderResult != ItemsAdderHook.PlaceResult.NOT_HOOKED) {
			if (itemsAdderResult == ItemsAdderHook.PlaceResult.PLACED) {
				return;
			}
		}

		OraxenHook.PlaceResult oraxenResult = oraxenApi.getHook().setBlock(item, bBlock.getLocation());

		if (oraxenResult != OraxenHook.PlaceResult.NOT_HOOKED) {
			if (oraxenResult == OraxenHook.PlaceResult.PLACED) {
				return;
			}
		}

		bBlock.setType(item.getType());
		if (item.getType() == Material.PLAYER_HEAD.getMaterial()) {
			BlockUtils.setSkin(bBlock, SkinUtilities.NMS.getSkinAsBase64(item));
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
			return worldGuardApi.getHook().getInternalWorldGuard().getRegionManager(world);
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
	 * Action methods
	 */

	public void sendHome(Player pl) throws RoyaleProtectionBlocksException {
		if (getHome() == null) {
			throw Exceptions.Protections.Teleport.NOHOMESET.generateException();
		}

		if (vaultApi.getHook() != null && Settings.SETTINGS_PROTECTION_TELEPORTCOST.getContent() > 0D) {
			if (!pl.hasPermission(Permissions.PROTECTION_ECONOMY_BYPASS)
					&& !vaultApi.getHook().withdraw(pl, Settings.SETTINGS_PROTECTION_TELEPORTCOST.getContent())) {
				throw Exceptions.Protections.PROTECTION_NOTENOUGHBALANCE.generateException();
			}
		}

		PlayerData playerData = playerDataService.getPlayerData(pl);

		if (playerData == null) {
			throw Exceptions.Protections.Teleport.UNKNOWN
					.generateException(new NullPointerException("Player data is null"));
		}

		if (Settings.SETTINGS_PROTECTION_TELEPORTCOOLDOWN.getContent() > 0) {
			if (playerData.getLastTeleport() != null && (Settings.SETTINGS_PROTECTION_TELEPORTCOOLDOWN.getContent()
					* 1000) >= (System.currentTimeMillis() - playerData.getLastTeleport().getSecond())) {
				throw Exceptions.Protections.Teleport.COOLDOWNACTIVE.generateException()
						.setReplacements(new Replacement("%seconds%",
								() -> String.valueOf((int) Math.max(((playerData.getLastTeleport().getSecond()
										+ (Settings.SETTINGS_PROTECTION_TELEPORTCOOLDOWN.getContent() * 1000))
										- System.currentTimeMillis()) / 1000, 0))));
			}
		}

		playerData.teleport(this);
	}

	/*
	 * Authority methods
	 */

	public boolean isMainOwner(UUID playerUuid) {
		return this.ownerUuid.equals(playerUuid);
	}

	public boolean isOwner(UUID playerUuid) {
		return this.getOwners().list().contains(playerUuid);
	}

	public boolean isMember(UUID playerUuid) {
		return this.getMembers().list().contains(playerUuid);
	}

	public boolean isBanned(UUID playerUuid) {
		return this.getBanneds().list().contains(playerUuid.toString());
	}

	/*
	 * Save methods
	 */

	/*
	 * Delete methods
	 */

	public void delete() throws RoyaleProtectionBlocksException {
		delete(null, false);
	}

	public void delete(boolean removeBlock) throws RoyaleProtectionBlocksException {
		delete(null, removeBlock);
	}

	public void delete(Player pl, boolean removeBlock) throws RoyaleProtectionBlocksException {
		if (pl != null) {
			if (!ProtectionUtilities.canDelete(this, pl)) {
				throw Exceptions.Protections.Delete.PERMISSIONDENIED.generateException();
			}
		}

		RegionManager regionManager = getRegionManager();

		if (regionManager == null) {
			throw Exceptions.Protections.Delete.UNKNOWN.generateException(new Exception("Region Manager is null"));
		}

		if (!regionManager.hasRegion(regionId)) {
			throw Exceptions.Protections.Delete.NOTFOUND.generateException();
		}

		if (isProtectionViewActive()) {
			toggleProtectionView();
		}

		if (removeBlock && isProtectionBlockShown()) {
			hideProtectionBlock();
		}

		regionManager.removeRegion(regionId);

		this.protectedRegion = null;

		sqlService.deleteProtection(this);
		protectionsService.unregisterProtection(this);
	}

}

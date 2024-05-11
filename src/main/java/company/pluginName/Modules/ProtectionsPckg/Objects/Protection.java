package company.pluginName.Modules.ProtectionsPckg.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.Permissions;
import company.pluginName.APIs.ItemsAdderAPI.ItemsAdderAPI;
import company.pluginName.APIs.OraxenAPI.OraxenAPI;
import company.pluginName.APIs.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.APIs.VaultAPI.VaultAPI;
import company.pluginName.APIs.WorldGuard.WorldGuardAPI;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Reference.ReferencedProtectionBlock;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionActions;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionBanneds;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionBoundaries;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionDisplayItem;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionMembers;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionOwners;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionUtils;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import company.pluginName.Modules.SQLPckg.SQLService;
import company.pluginName.Utils.BlockVectorUtils;
import company.pluginName.Utils.ReflectUtils;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.PandaUtilities.WorldUtilities;
import darkpanda73.PandaUtils.PandaUtilities.Location.LocationReference;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaIntegerField;
import darkpanda73.PandaUtils.Utilities.Java.Observable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import royale.RoyaleProtectionBlocks.Plugin.InternalAPI.Events.Protection.ProtectionCreationEvent;
import royale.RoyaleProtectionBlocks.Plugin.InternalAPI.Events.Protection.ProtectionRemovalEvent;
import royale.RoyaleProtectionBlocks.Plugin.InternalAPI.Interfaces.IProtection;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Protection implements IProtection {

	@RegisteredPandaField("config")
	private static PandaIntegerField SETTINGS_PROTECTION_MINIMUMDISTANCEBETWEENPROTECTIONS = new PandaIntegerField(
			"Settings.Protection.Minimum-distance-between-protections", -1);

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

	private @ToString.Include @EqualsAndHashCode.Include String regionId;
	private @ToString.Include UUID ownerUuid;
	private String ownerName;
	private ReferencedProtectionBlock protectionBlock;
	private @ToString.Include @EqualsAndHashCode.Include String worldName;
	private @ToString.Include @Setter(lombok.AccessLevel.NONE) long createdDate;

	private ProtectionMembers members = new ProtectionMembers(this);
	private ProtectionOwners owners = new ProtectionOwners(this);
	private ProtectionBanneds banneds = new ProtectionBanneds(this);
	private ProtectionActions actions = new ProtectionActions(this);
	private ProtectionUtils utils = new ProtectionUtils(this);
	private ProtectionBoundaries boundaries = new ProtectionBoundaries(this);
	private ProtectionDisplayItem displayItem = new ProtectionDisplayItem(this);

	public Protection(String regionId, UUID ownerUuid, ReferencedProtectionBlock protectionBlock, String worldName,
			String displayName, long createdDate) {
		this.regionId = regionId;
		this.ownerUuid = ownerUuid;
		this.protectionBlock = protectionBlock;
		this.worldName = worldName;
		this.displayName = displayName;
		this.createdDate = createdDate;
	}

	public Protection(UUID ownerUuid, Location location, ProtectionBlock protectionBlock) {
		this.ownerUuid = ownerUuid;
		this.protectionBlock = new ReferencedProtectionBlock(protectionBlock.getInformation().getId());
		this.worldName = location.getWorld().getName();
		this.regionId = ProtectionUtilities.generateDefaultName(location).toLowerCase();
		this.displayName = ProtectionUtilities.generateDefaultName(location);
		this.location = LocationReference.fromLocation(location);
	}

	/*
	 * Locationz
	 */

	private @ToString.Include LocationReference location;

	private Location minLocation;
	private Location maxLocation;

	// TODO: Remove this method and the flag after two or three more versions.
	public Location getLocation() {
		if (this.location == null) {
			ProtectedRegion region = getProtectedRegion();
			if (region != null) {
				World world = Bukkit.getWorld(worldName);
				if (world != null) {
					if (region != null) {
						Location loc = worldGuardApi.getHook().getProtectionBlockLocationFlag().flagState(world,
								region);

						if (loc != null) {
							this.location = LocationReference.fromLocation(loc);
						} else {
							BlockVector3 vector = BlockVectorUtils.getIntersectingVector(region.getMinimumPoint(),
									getProtectedRegion().getMaximumPoint());

							this.location = new LocationReference(worldName, vector.getBlockX(), vector.getBlockY(),
									vector.getBlockZ());
						}

						region.setFlag(worldGuardApi.getHook().getBannedPlayersFlag().getWorldGuardFlag(), null);
						this.saveData();
					}
				}
			}
		}

		return this.location.toLocation();
	}

	public Location getMinLocation() {
		if (this.minLocation == null) {
			Location location = getLocation();
			ProtectionBlock protectionBlock = getProtectionBlock().getObject();

			if (location != null && protectionBlock != null) {
				this.minLocation = new Location(location.getWorld(),
						location.getBlockX() - protectionBlock.getInformation().getBlocksX(),
						protectionBlock.getInformation().getBlocksY() != -1
								? location.getBlockY() - protectionBlock.getInformation().getBlocksY()
								: WorldUtilities.getMinHeight(location.getWorld()),
						location.getBlockZ() - protectionBlock.getInformation().getBlocksZ());
			}
		}

		return this.minLocation;
	}

	public Location getMaxLocation() {
		if (this.maxLocation == null) {
			Location location = getLocation();
			ProtectionBlock protectionBlock = getProtectionBlock().getObject();

			if (location != null && protectionBlock != null) {
				this.maxLocation = new Location(location.getWorld(),
						location.getBlockX() + protectionBlock.getInformation().getBlocksX(),
						protectionBlock.getInformation().getBlocksY() != -1
								? location.getBlockY() + protectionBlock.getInformation().getBlocksY()
								: location.getWorld().getMaxHeight(),
						location.getBlockZ() + protectionBlock.getInformation().getBlocksZ());
			}
		}

		return this.maxLocation;
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
			if (!ProtectionUtilities.canRename(this, pl)) {
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
	 * WorldGuard methods
	 */

	private ProtectedRegion protectedRegion;

	public ProtectedRegion getProtectedRegion() {
		if (this.protectedRegion == null) {
			RegionManager regionManager = getRegionManager();

			if (regionManager != null) {
				this.protectedRegion = regionManager.getRegion(regionId);
			} else {
				new NullPointerException("Unable to retrieve region manager").printStackTrace();
			}
		}

		if (this.protectedRegion == null) {
			new NullPointerException(String.format("Unable to retrieve protection with ID '%s'", this.regionId))
					.printStackTrace();
		}

		return this.protectedRegion;
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

	@Override
	public int getPriority() {
		return getProtectedRegion().getPriority();
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

	private boolean creationInProgress = false;
	private boolean removalInProgress = false;

	public Observable<Protection> create() throws RoyaleProtectionBlocksException {
		return create(null);
	}

	public Observable<Protection> create(Player player) throws RoyaleProtectionBlocksException {
		if (this.isCreationInProgress()) {
			throw Exceptions.Protections.Save.INPROGRESS.generateException();
		}

		return Observable.of(() -> {
			try {
				this.creationInProgress = true;

				checkCreationConditions(player);

				protectionsService.registerProtection(this);

				createProtectedRegion();

				this.createdDate = System.currentTimeMillis();

				TasksUtils.executeOnAsync(() -> this.saveData());

				TasksUtils
						.execute(() -> Bukkit.getPluginManager().callEvent(new ProtectionCreationEvent(player, this)));

				return this;
			} catch (Throwable e) {
				protectionsService.unregisterProtection(this);

				throw e;
			} finally {
				this.creationInProgress = false;
			}
		});
	}

	private void checkCreationConditions(Player player) throws RoyaleProtectionBlocksException {
		// Check if the protection is already registered
		Protection registeredProtection = protectionsService.findProtectionBySourceLocation(getLocation());
		if (registeredProtection != null) {
			if (registeredProtection != this) {
				throw Exceptions.Protections.Save.ALREADYOCCUPIED.generateException();
			}

			if (registeredProtection.isCreationInProgress()) {
				throw Exceptions.Protections.Save.INPROGRESS.generateException();
			} else {
				throw Exceptions.Protections.Save.ALREADYOCCUPIED.generateException();
			}
		}

		// Check if can create new protections
		if (player != null) {
			if (!player.hasPermission(Permissions.PROTECTION_MAX_BYPASS)) {
				ProtectionBlock protectionBlock = this.protectionBlock.getObject();
				Integer perBlockMaxCapacity = Permissions.getPerBlockMaxCapacity(player, protectionBlock);

				if (perBlockMaxCapacity != null) {
					// Checks the limit per protection block
					if (perBlockMaxCapacity <= protectionsService.getProtectionsByOwner()
							.getOrDefault(player.getUniqueId(), Collections.emptyList()).stream()
							.filter(protection -> protection.getProtectionBlock().getIdentifier()
									.equals(protectionBlock.getInformation().getId()))
							.count()) {
						throw Exceptions.Protections.Save.MAXREACHED.generateException();
					}
				} else {
					// Checks the general limit
					Integer generalMaxCapacity = Permissions.getGeneralMaxCapacity(player);
					if (generalMaxCapacity != null) {
						if (generalMaxCapacity <= protectionsService.getProtectionsByOwner()
								.getOrDefault(player.getUniqueId(), new ArrayList<>()).size()) {
							throw Exceptions.Protections.Save.MAXREACHED.generateException();
						}
					} else {
						throw Exceptions.Protections.Save.MAXREACHED.generateException();
					}
				}
			}
		}

		// Checks if there's other protections overlapping this region
		if (player != null && !player.hasPermission(Permissions.PROTECTION_OVERLAP_BYPASS)) {
			int offset = SETTINGS_PROTECTION_MINIMUMDISTANCEBETWEENPROTECTIONS.getContent();
			List<Protection> overlaps = protectionsService.findProtectionsByArea(
					offset > 0 ? getMinLocation().clone().add(-offset, -offset, -offset) : getMinLocation(),
					offset > 0 ? getMaxLocation().clone().add(offset, offset, offset) : getMaxLocation());

			if (overlaps.size() > 0
					&& !(Settings.SETTINGS_PROTECTION_ALLOWREGIONSINSIDEANOTHERFROMSAMEOWNER.getContent()
							&& overlaps.stream().allMatch(prot -> prot.isMainOwner(player.getUniqueId())))) {
				throw offset < 0 ? Exceptions.Protections.Save.OVERLAPS.generateException()
						: Exceptions.Protections.Save.OVERLAPSOFFSET.generateException()
								.setReplacements(new Replacement("%blocks%", () -> String.valueOf(offset)));
			}
		}

		// Tries to get the region manager from the world
		RegionManager regionManager = getRegionManager(getLocation().getWorld());

		if (regionManager == null) {
			throw Exceptions.Protections.Save.UNKNOWN.generateException(new Exception("Region Manager is"));
		}
	}

	@SuppressWarnings("unchecked")
	private void createProtectedRegion() throws RoyaleProtectionBlocksException {
		RegionManager regionManager = getRegionManager();

		try {
			// Gets the min and max coords for the region
			Location minLocation = getMinLocation();
			Location maxLocation = getMaxLocation();

			// Creates a region from WorldGuard which contains the min and max coords
			// calculated previously
			ProtectedRegion protectedRegion = new ProtectedCuboidRegion(regionId,
					BlockVector3.at(minLocation.getBlockX(), minLocation.getBlockY(), minLocation.getBlockZ()),
					BlockVector3.at(maxLocation.getBlockX(), maxLocation.getBlockY(), maxLocation.getBlockZ()));

			// Adds the owner of the protection as an owner of the protection on WorldGuard
			protectedRegion.getOwners().addPlayer(ownerUuid);

			// Gets the information of the player, even if it's offline.
			OfflinePlayer owner = OfflinePlayerUtilities.getOfflinePlayer(this.ownerUuid);

			HashMap<com.sk89q.worldguard.protection.flags.Flag<?>, Object> flags = new HashMap<>();
			protectionSettingsService.getDefaultFlags().forEach(defaultFlag -> {
				if (placeholderApi.getHook().isHooked()) {
					if (defaultFlag.getValue() instanceof String) {
						flags.put(defaultFlag.getFlag(),
								MessageTemplate.inst(placeholderApi.getHook()
										.applyPlaceholders((String) defaultFlag.getValue(), owner)
										.replaceAll("\\\\%", "%")).process().toString());
					} else if (defaultFlag.getValue() instanceof Set) {
						Set<String> set = new HashSet<>();
						((Set<String>) defaultFlag.getValue()).forEach(string -> set.add(MessageTemplate.inst(
								placeholderApi.getHook().applyPlaceholders(string, owner).replaceAll("\\\\%", "%"))
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

			// Sets the flag on the protection and adds it to the WorldGuad region manager
			protectedRegion.setFlags(flags);
			regionManager.addRegion(protectedRegion);

			// Prepares internal data to be updated and saves the data on database.
			this.protectedRegion = protectedRegion;
		} catch (Exception e) {
			// If the region was able to be created on WorldGuard, then it removes the
			// region from the region manager
			if (regionManager.hasRegion(getRegionId())) {
				regionManager.removeRegion(getRegionId());
			}

			throw e instanceof RoyaleProtectionBlocksException ? (RoyaleProtectionBlocksException) e
					: Exceptions.Protections.Save.UNKNOWN.generateException(e);
		}

	}

	/*
	 * Delete methods
	 */

	public Observable<Protection> delete() throws RoyaleProtectionBlocksException {
		return delete(null);
	}

	public Observable<Protection> delete(Player player) throws RoyaleProtectionBlocksException {
		if (this.isRemovalInProgress()) {
			throw Exceptions.Protections.Delete.INPROGRESS.generateException();
		}

		return Observable.of(() -> {
			try {
				this.removalInProgress = true;

				checkRemovalConditions(player);

				deleteProtectedRegion();

				protectionsService.unregisterProtection(this);

				TasksUtils.executeOnAsync(() -> this.deleteData());

				TasksUtils.execute(() -> Bukkit.getPluginManager().callEvent(new ProtectionRemovalEvent(player, this)));

				return this;
			} finally {
				this.removalInProgress = false;
			}
		});
	}

	private void checkRemovalConditions(Player player) throws RoyaleProtectionBlocksException {
		// Checks if the player has permissions to delete this protection
		if (player != null) {
			if (!ProtectionUtilities.canDelete(this, player)) {
				throw Exceptions.Protections.Delete.PERMISSIONDENIED.generateException();
			}
		}

		if (this.utils.isProtectionBlockShown()) {
			throw Exceptions.Protections.Delete.BLOCKSHOWN.generateException();
		}

		if (this.boundaries.isProtectionViewActive()) {
			throw Exceptions.Protections.Delete.VIEWACTIVE.generateException();
		}
	}

	private void deleteProtectedRegion() throws RoyaleProtectionBlocksException {
		RegionManager regionManager = getRegionManager();

		if (regionManager != null && regionManager.hasRegion(regionId)) {
			regionManager.removeRegion(regionId);
		}

		this.protectedRegion = null;
	}

	/*
	 * Save data methods
	 */

	public void saveData() {
		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtection(this);
			} catch (RoyaleProtectionBlocksException e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public void deleteData() {
		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.deleteProtection(this);
			} catch (RoyaleProtectionBlocksException e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

}

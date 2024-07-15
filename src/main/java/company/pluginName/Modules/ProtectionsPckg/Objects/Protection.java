package company.pluginName.Modules.ProtectionsPckg.Objects;

import java.util.ArrayList;
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
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.ItemsAdderAPI.ItemsAdderAPI;
import company.pluginName.Hooks.OraxenAPI.OraxenAPI;
import company.pluginName.Hooks.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.Hooks.VaultAPI.VaultAPI;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Reference.ReferencedProtectionBlock;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionActions;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionBanneds;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionBoundaries;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionDisplayItem;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionFlags;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionMembers;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionOwners;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionUtils;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionUtils.SimpleLocation.SimpleLocationArea;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import company.pluginName.Modules.SQLPckg.SQLService;
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
import darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects.PandaParametizedPermission.Parameter;
import darkpanda73.PandaUtils.Utilities.Java.Observable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.BlockReason;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionCreationEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionRemovalEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.IProtection;

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
	private ProtectionFlags flags = new ProtectionFlags(this);

	private boolean deleted = false;

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
	 * Locations
	 */

	private @ToString.Include LocationReference location;

	private Location minLocation;
	private Location maxLocation;

	public Location getLocation() {
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

	public void setDisplayName(String displayName) throws RoyaleProtectionBlocksExceptionImpl {
		String displayNameWithoutFormat = MessageTemplate.inst(displayName).setColor(false).process().toString();

		if (displayNameWithoutFormat.isEmpty()) {
			throw Exceptions.Protections.Save.NOVISIBLETEXT.generateException();
		}

		if (protectionsService.findProtectionsByOwner(ownerUuid).stream().anyMatch(prot -> {
			return !prot.getRegionId().equals(getRegionId())
					&& (displayNameWithoutFormat.equalsIgnoreCase(prot.getRegionId())
							|| displayNameWithoutFormat.equalsIgnoreCase(prot.getDisplayNameWithoutFormat()));
		})) {
			throw Exceptions.Protections.Save.NAMEINUSE.generateException();
		}

		this.displayName = displayName;
		this.displayNameWithoutFormat = MessageTemplate.inst(displayName).setColor(false).process().toString();

		saveData();
	}

	public String getDisplayNameWithoutFormat() {
		if (this.displayName != null && this.displayNameWithoutFormat == null) {
			this.displayNameWithoutFormat = MessageTemplate.inst(this.displayName).setColor(false).process().toString();
		}
		return this.displayNameWithoutFormat;
	}

	/*
	 * Block methods
	 */

	private boolean blocked;
	private BlockReason blockReason;

	public void block(BlockReason blockReason) {
		this.blocked = true;
		this.blockReason = blockReason;
	}

	public void unblock() {
		this.blocked = false;
		this.blockReason = null;
	}

	/*
	 * Data methods
	 */

	public String getOwnerName() {
		if (ownerName == null) {
			OfflinePlayer owner = OfflinePlayerUtilities.getOfflinePlayer(ownerUuid);
			this.ownerName = owner != null && owner.getName() != null ? owner.getName() : "???";
		}
		return this.ownerName;
	}

	@Override
	public String getProtectionBlockIdentifier() {
		return this.getProtectionBlock().getIdentifier();
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
			}
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
	 * Save methods
	 */

	private boolean creationInProgress = false;
	private boolean removalInProgress = false;

	public Observable<Protection> create() throws RoyaleProtectionBlocksExceptionImpl {
		return create(null);
	}

	public Observable<Protection> create(Player player) throws RoyaleProtectionBlocksExceptionImpl {
		if (this.isCreationInProgress()) {
			throw Exceptions.Protections.Save.INPROGRESS.generateException();
		}

		return Observable.of(() -> {
			try {
				this.creationInProgress = true;

				checkCreationConditions(player);

				protectionsService.registerProtection(this);
			} finally {
				this.creationInProgress = false;
			}

			try {
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

	private void checkCreationConditions(Player player) throws RoyaleProtectionBlocksExceptionImpl {
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
			if (!PermissionsService.MAX_BYPASS.hasPermission(player)) {
				Integer generalMaxCapacity = PermissionsService.getGeneralMaxCapacity(player);

				if (generalMaxCapacity != null) {
					Integer ownedProtections = protectionsService.findProtectionsByOwner(player.getUniqueId()).size();

					if (generalMaxCapacity <= ownedProtections) {
						throw Exceptions.Protections.Save.MAXREACHED.generateException();
					}
				} else {
					throw Exceptions.Protections.Save.MAXREACHED.generateException();
				}
			}

			ProtectionBlock protectionBlock = this.protectionBlock.getObject();
			if (protectionBlock != null && !PermissionsService.BLOCK_MAX_BYPASS.hasPermission(player,
					Parameter.of("block", protectionBlock.getInformation().getId()))) {
				Integer blockMaxCapacity = PermissionsService.getPerBlockMaxCapacity(player, protectionBlock);

				if (blockMaxCapacity != null) {
					Long blockOwnedProtections = protectionsService.findProtectionsByOwner(player.getUniqueId())
							.stream().filter(protection -> protection.getProtectionBlock().getIdentifier()
									.equals(protectionBlock.getInformation().getId()))
							.count();

					if (blockMaxCapacity != null && blockMaxCapacity <= blockOwnedProtections) {
						throw Exceptions.Protections.Save.BLOCKMAXREACHED.generateException();
					}
				}
			}
		}
		int offset = SETTINGS_PROTECTION_MINIMUMDISTANCEBETWEENPROTECTIONS.getContent();

		// Checks if there's other protections overlapping this region
		if (player != null && !PermissionsService.OVERLAP_BYPASS.hasPermission(player)) {
			if (protectionsService
					.findProtectionsByArea(
							offset > 0 ? getMinLocation().clone().add(-offset, -offset, -offset) : getMinLocation(),
							offset > 0 ? getMaxLocation().clone().add(offset, offset, offset) : getMaxLocation(), false)
					.anyMatch(prot -> !prot.isMainOwner(player.getUniqueId())
							|| !Settings.SETTINGS_PROTECTION_ALLOWREGIONSINSIDEANOTHERFROMSAMEOWNER.getContent())) {
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

		// Gets the min and max coords for the region
		Location minLocation = offset > 0 ? getMinLocation().clone().add(-offset, -offset, -offset) : getMinLocation();
		Location maxLocation = offset > 0 ? getMaxLocation().clone().add(offset, offset, offset) : getMaxLocation();

		// Creates a region from WorldGuard which contains the min and max coords
		// calculated previously
		ProtectedRegion protectedRegion = new ProtectedCuboidRegion(regionId,
				BlockVector3.at(minLocation.getBlockX(), minLocation.getBlockY(), minLocation.getBlockZ()),
				BlockVector3.at(maxLocation.getBlockX(), maxLocation.getBlockY(), maxLocation.getBlockZ()));

		ApplicableRegionSet set = regionManager.getApplicableRegions(protectedRegion);

		if (set.getRegions().stream().anyMatch(pr -> protectionsService.findProtectionById(pr.getId()) == null)) {
			throw Exceptions.Protections.Save.OVERLAPSWORLDGUARD.generateException();
		}
	}

	private void createProtectedRegion() throws RoyaleProtectionBlocksExceptionImpl {
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

			// Adds it to the WorldGuad region manager
			regionManager.addRegion(protectedRegion);
			this.protectedRegion = protectedRegion;

			// Sets the default falgs for the protected region.
			this.flags.resetFlags();
		} catch (Exception e) {
			// If the region was able to be created on WorldGuard, then it removes the
			// region from the region manager
			if (regionManager.hasRegion(getRegionId())) {
				regionManager.removeRegion(getRegionId());
			}

			throw e instanceof RoyaleProtectionBlocksExceptionImpl ? (RoyaleProtectionBlocksExceptionImpl) e
					: Exceptions.Protections.Save.UNKNOWN.generateException(e);
		}

	}

	/*
	 * Delete methods
	 */

	public Observable<Protection> delete(RemovalCause cause) throws RoyaleProtectionBlocksExceptionImpl {
		return delete(null, cause);
	}

	public Observable<Protection> delete(Player player, RemovalCause cause) throws RoyaleProtectionBlocksExceptionImpl {
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

				TasksUtils.execute(
						() -> Bukkit.getPluginManager().callEvent(new ProtectionRemovalEvent(player, this, cause)));

				this.deleted = true;

				return this;
			} finally {
				this.removalInProgress = false;
			}
		});
	}

	private void checkRemovalConditions(Player player) throws RoyaleProtectionBlocksExceptionImpl {
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

	private void deleteProtectedRegion() throws RoyaleProtectionBlocksExceptionImpl {
		RegionManager regionManager = getRegionManager();

		if (regionManager != null && regionManager.hasRegion(regionId)) {
			regionManager.removeRegion(regionId);
		}

		this.protectedRegion = null;
	}

	/*
	 * Regenerate methods
	 */

	public void regenerateProtectedRegion() throws RoyaleProtectionBlocksExceptionImpl {
		ProtectedRegion pr = null;

		try {
			pr = getProtectedRegion();
		} catch (NullPointerException e) {
		}

		if (pr != null) {
			Map<Flag<?>, Object> flags = getProtectedRegion().getFlags();
			DefaultDomain owners = getProtectedRegion().getOwners();
			DefaultDomain members = getProtectedRegion().getMembers();
			Set<String> banneds = getBanneds().list();

			deleteProtectedRegion();

			createProtectedRegion();

			getProtectedRegion().setFlags(flags);
			getProtectedRegion().setOwners(owners);
			getProtectedRegion().setMembers(members);
			try {
				banneds.stream().map(UUID::fromString).forEach(uuid -> {
					try {
						getBanneds().add(uuid);
					} catch (RoyaleProtectionBlocksExceptionImpl e) {
						throw new RuntimeException(e);
					}
				});
			} catch (RuntimeException e) {
				if (e.getCause() instanceof RoyaleProtectionBlocksExceptionImpl) {
					throw (RoyaleProtectionBlocksExceptionImpl) e.getCause();
				}
				throw e;
			}
		} else {
			createProtectedRegion();
		}
	}

	/*
	 * Merge methods
	 */

	private Protection parentProtection = null;
	private List<Protection> childProtections = new ArrayList<>();

	public void setParentProtection(Protection protection) {
		unsetParentProtection();

		while (protection.getParentProtection() != null) {
			protection = protection.getParentProtection();
		}

		this.parentProtection = protection;
		this.parentProtection.getChildProtections().add(this);
	}

	public void unsetParentProtection() {
		if (this.parentProtection != null) {
			this.parentProtection.getChildProtections().remove(this);
			this.parentProtection = null;
		}
	}

	public Protection getParentProtection() {
		return this.parentProtection != null ? this.parentProtection.getParentProtection() : this;
	}

	/*
	 * Save data methods
	 */

	public void saveData() {
		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtection(this);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public void deleteData() {
		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.deleteProtection(this);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	/*
	 * API methods
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

	@Override
	public void addMember(UUID memberUuid) throws RoyaleProtectionBlocksException {
		this.getMembers().add(memberUuid);
	}

	@Override
	public void removeMember(UUID memberUuid) throws RoyaleProtectionBlocksException {
		this.getMembers().remove(memberUuid);
	}

	@Override
	public void addOwner(UUID ownerUuid) throws RoyaleProtectionBlocksException {
		this.getOwners().add(ownerUuid);
	}

	@Override
	public void removeOwner(UUID ownerUuid) throws RoyaleProtectionBlocksException {
		this.getOwners().remove(ownerUuid);
	}

	@Override
	public void addBanned(UUID bannedUuid) throws RoyaleProtectionBlocksException {
		this.getBanneds().add(bannedUuid);
	}

	@Override
	public void removeBanned(UUID bannedUuid) throws RoyaleProtectionBlocksException {
		this.getBanneds().remove(bannedUuid);
	}

	@Override
	public boolean kickPlayer(Player player) throws RoyaleProtectionBlocksException {
		return this.getActions().kickPlayer(player);
	}

	@Override
	public void teleport(Player player) throws RoyaleProtectionBlocksException {
		this.getActions().teleportToHome(player);
	}

	@Override
	public void rename(String displayName) throws RoyaleProtectionBlocksException {
		this.setDisplayName(displayName);
	}

	@Override
	public boolean isInside(Location location, boolean includeBorder) {
		return this.getUtils().isInside(location, includeBorder);
	}

	@Override
	public boolean isInside(Location firstLocation, Location secondLocation, boolean includeBorder) {
		return this.getUtils().isInside(SimpleLocationArea.of(firstLocation, secondLocation), includeBorder);
	}

	public boolean isInside(SimpleLocationArea locationArea, boolean includeBorder) {
		return this.getUtils().isInside(locationArea, includeBorder);
	}

}

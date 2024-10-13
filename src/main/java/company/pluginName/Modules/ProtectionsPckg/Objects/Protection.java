package company.pluginName.Modules.ProtectionsPckg.Objects;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Reference.ReferencedProtectionBlock;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionActions;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionBoundaries;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionDisplayItem;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionUtils;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.WorldGuard.ProtectionWorldGuardFlags;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.WorldGuard.ProtectionWorldGuardMembers;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.WorldGuard.ProtectionWorldGuardOwners;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import company.pluginName.Modules.SQLPckg.SQLService;
import company.pluginName.Utils.ReflectUtils;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaIntegerField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects.PandaParametizedPermission.Parameter;
import darkpanda73.PandaUtils.Utilities.Java.Observable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.BlockReason;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.CreationCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionCreationEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionRemovalEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation.SimpleLocationArea;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Protection implements IProtection {

	@RegisteredPandaField("config")
	public static PandaIntegerField SETTINGS_PROTECTION_MINIMUMDISTANCEBETWEENPROTECTIONS = new PandaIntegerField(
			"Settings.Protection.Minimum-distance-between-protections", -1);

	@RegisteredPandaField("config")
	public static PandaIntegerField SETTINGS_PROTECTION_MAXIMUMDISPLAYNAMELENGTH = new PandaIntegerField(
			"Settings.Protection.Maximum-display-name-length", 255);

	@RegisteredPandaField("config")
	public static PandaBooleanField SETTINGS_PROTECTION_MUSTBECLOSEFORMERGE = new PandaBooleanField(
			"Settings.Protection.Must-be-close-for-merge", true);

	@RegisteredPandaField("config")
	public static PandaStringField SETTINGS_PROTECTION_DEFAULTDISPLAYNAME = new PandaStringField(
			"Settings.Protection.Default-display-name",
			"{protection_world}_{protection_location_x}_{protection_location_y}_{protection_location_z}");

	@RegisteredPandaField("config")
	public static PandaIntegerField SETTINGS_PROTECTION_DEFAULTPRIORITY = new PandaIntegerField(
			"Settings.Protection.Default-priority", 0);

	@PandaInject
	private static ProtectionSettingsService protectionSettingsService;

	@PandaInject
	private static PlaceholdersService placeholdersService;

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
	private static PlayerDataService playerDataService;

	private @ToString.Include @EqualsAndHashCode.Include String regionId;
	private @ToString.Include UUID ownerUuid;
	private ReferencedProtectionBlock protectionBlock;
	private @ToString.Include @EqualsAndHashCode.Include String worldName;
	private @ToString.Include @Setter(lombok.AccessLevel.NONE) long createdDate;

	private ProtectionWorldGuardMembers worldGuardMembers = new ProtectionWorldGuardMembers(this);
	private ProtectionWorldGuardOwners worldGuardOwners = new ProtectionWorldGuardOwners(this);
	private ProtectionWorldGuardFlags worldGuardFlags = new ProtectionWorldGuardFlags(this);
	private ProtectionActions actions = new ProtectionActions(this);
	private ProtectionUtils utils = new ProtectionUtils(this);
	private ProtectionBoundaries boundaries = new ProtectionBoundaries(this);
	private ProtectionDisplayItem displayItem = new ProtectionDisplayItem(this);

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
		this.location = SimpleLocation.of(location);
	}

	/*
	 * Locations
	 */

	private @ToString.Include SimpleLocation location;

	public Location getBukkitLocation() {
		return this.location.toLocation();
	}

	/*
	 * Display name
	 */

	private @Setter(lombok.AccessLevel.NONE) String displayName;
	private @Setter(lombok.AccessLevel.NONE) String displayNameWithoutFormat;

	public void setDisplayName(String displayName) throws RoyaleProtectionBlocksExceptionImpl {
		setDisplayName(displayName, true);
	}

	public void setDisplayName(String displayName, boolean saveData) throws RoyaleProtectionBlocksExceptionImpl {
		Integer maxLength = SETTINGS_PROTECTION_MAXIMUMDISPLAYNAMELENGTH.getContent();

		if (maxLength == null) {
			maxLength = 255;
		} else if (maxLength < 0) {
			maxLength = 0;
		} else if (maxLength > 255) {
			maxLength = 255;
		}

		int fMaxLength = maxLength;

		if (displayName.length() > fMaxLength) {
			throw Exceptions.Protections.Save.NAMETOOLONG.generateException()
					.setReplacements(new Replacement[] {
							new Replacement("{current}", () -> String.valueOf(displayName.length())),
							new Replacement("{max}", () -> String.valueOf(fMaxLength)) });
		}

		String displayNameWithoutFormat = displayName != null
				? MessageTemplate.inst(displayName).setColor(false).process().toString()
				: "";

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
		this.displayNameWithoutFormat = displayNameWithoutFormat;

		if (saveData) {
			saveData();
		}
	}

	public String getDisplayNameWithoutFormat() {
		if (this.displayName != null && this.displayNameWithoutFormat == null) {
			this.displayNameWithoutFormat = MessageTemplate.inst(this.displayName).setColor(false).process().toString();
		}
		return this.displayNameWithoutFormat;
	}

	/*
	 * Owner methods
	 */

	private OfflinePlayer ownerOfflinePlayer;
	private @Setter Long ownerLastPlayed;

	public OfflinePlayer getOwnerOfflinePlayer() {
		if (this.ownerOfflinePlayer == null) {
			this.ownerOfflinePlayer = OfflinePlayerUtilities.getOfflinePlayer(ownerUuid);
		}
		return this.ownerOfflinePlayer;
	}

	@Override
	public long getOwnerLastPlayed() {
		if (this.getOwnerOfflinePlayer().isOnline()) {
			return System.currentTimeMillis();
		}
		if (this.ownerLastPlayed == null) {
			this.ownerLastPlayed = this.getOwnerOfflinePlayer().getLastPlayed();
		}
		return this.ownerLastPlayed;
	}

	/*
	 * Data methods
	 */

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
		return getProtectedRegion() != null ? getProtectedRegion().getPriority()
				: SETTINGS_PROTECTION_DEFAULTPRIORITY.getContent();
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

				this.setDisplayName(
						MessageTemplate.inst(SETTINGS_PROTECTION_DEFAULTDISPLAYNAME.getContent())
								.setReplacements(placeholdersService.getProtectionReplacements(this)).toString(),
						false);

				TasksUtils.executeOnAsync(() -> this.saveData());

				TasksUtils.execute(() -> Bukkit.getPluginManager()
						.callEvent(new ProtectionCreationEvent(player, this, CreationCause.PLAYER)));

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
		Protection registeredProtection = protectionsService.findProtectionBySourceLocation(getBukkitLocation());
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

			int offset = SETTINGS_PROTECTION_MINIMUMDISTANCEBETWEENPROTECTIONS.getContent();

			Location minLocationWithOffset = offset > 0
					? getUtils().getProtectionArea().getMinLocation().toLocation().clone().add(-offset, -offset,
							-offset)
					: getUtils().getProtectionArea().getMinLocation().toLocation();
			Location maxLocationWithOffset = (offset > 0
					? getUtils().getProtectionArea().getMaxLocation().toLocation().clone().add(offset, offset, offset)
					: getUtils().getProtectionArea().getMaxLocation().toLocation());

			// Checks if there's other protections overlapping this region
			if (!PermissionsService.OVERLAP_BYPASS.hasPermission(player)) {
				if (protectionsService.findProtectionsByArea(minLocationWithOffset, maxLocationWithOffset, false)
						.anyMatch(prot -> !prot.isMainOwner(player.getUniqueId())
								|| !Settings.SETTINGS_PROTECTION_ALLOWREGIONSINSIDEANOTHERFROMSAMEOWNER.getContent())) {
					throw offset < 0 ? Exceptions.Protections.Save.OVERLAPS.generateException()
							: Exceptions.Protections.Save.OVERLAPSOFFSET.generateException()
									.setReplacements(new Replacement("%blocks%", () -> String.valueOf(offset)));
				}
			}

			// Tries to get the region manager from the world
			RegionManager regionManager = getRegionManager(getBukkitLocation().getWorld());

			if (regionManager == null) {
				throw Exceptions.Protections.Save.UNKNOWN.generateException(new Exception("Region Manager is"));
			}

			// Creates a region from WorldGuard which contains the min and max coords
			// calculated previously
			ProtectedRegion protectedRegion = new ProtectedCuboidRegion(regionId,
					BlockVector3.at(minLocationWithOffset.getBlockX(), minLocationWithOffset.getBlockY(),
							minLocationWithOffset.getBlockZ()),
					BlockVector3.at(maxLocationWithOffset.getBlockX(), maxLocationWithOffset.getBlockY(),
							maxLocationWithOffset.getBlockZ()));

			ApplicableRegionSet set = regionManager.getApplicableRegions(protectedRegion);

			if (set.getRegions().stream().anyMatch(pr -> protectionsService.findProtectionById(pr.getId()) == null)) {
				throw Exceptions.Protections.Save.OVERLAPSWORLDGUARD.generateException();
			}
		}
	}

	private void createProtectedRegion() throws RoyaleProtectionBlocksExceptionImpl {
		RegionManager regionManager = getRegionManager();

		try {
			// Gets the min and max coords for the region
			Location minLocation = getUtils().getProtectionArea().getMinLocation().toLocation();
			Location maxLocation = getUtils().getProtectionArea().getMaxLocation().toLocation();

			// Creates a region from WorldGuard which contains the min and max coords
			// calculated previously
			ProtectedRegion protectedRegion = new ProtectedCuboidRegion(regionId,
					BlockVector3.at(minLocation.getBlockX(), minLocation.getBlockY(), minLocation.getBlockZ()),
					BlockVector3.at(maxLocation.getBlockX() - 1, maxLocation.getBlockY() - 1,
							maxLocation.getBlockZ() - 1));

			// Set default priority
			protectedRegion.setPriority(SETTINGS_PROTECTION_DEFAULTPRIORITY.getContent());

			// Adds the owner of the protection as an owner of the protection on WorldGuard
			protectedRegion.getOwners().addPlayer(ownerUuid);

			// Adds it to the WorldGuad region manager
			regionManager.addRegion(protectedRegion);
			this.protectedRegion = protectedRegion;

			// Sets the default flags for the protected region.
			this.worldGuardFlags.resetFlags();
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
		int priority = getPriority();

		if (getProtectedRegion() != null) {
			Map<Flag<?>, Object> flags = getProtectedRegion().getFlags();
			DefaultDomain owners = getProtectedRegion().getOwners();
			DefaultDomain members = getProtectedRegion().getMembers();

			deleteProtectedRegion();

			createProtectedRegion();

			getProtectedRegion().setFlags(flags);
			getProtectedRegion().setOwners(owners);
			getProtectedRegion().setMembers(members);
		} else {
			createProtectedRegion();
		}

		getProtectedRegion().setPriority(priority);
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
		return this.getWorldGuardOwners().list().contains(playerUuid);
	}

	public boolean isMember(UUID playerUuid) {
		return this.getWorldGuardMembers().list().contains(playerUuid);
	}

	public boolean isBanned(UUID playerUuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addMember(UUID memberUuid) throws RoyaleProtectionBlocksException {
		this.getWorldGuardMembers().add(memberUuid);
	}

	@Override
	public void removeMember(UUID memberUuid) throws RoyaleProtectionBlocksException {
		this.getWorldGuardMembers().remove(memberUuid);
	}

	@Override
	public void addOwner(UUID ownerUuid) throws RoyaleProtectionBlocksException {
		this.getWorldGuardOwners().add(ownerUuid);
	}

	@Override
	public void removeOwner(UUID ownerUuid) throws RoyaleProtectionBlocksException {
		this.getWorldGuardOwners().remove(ownerUuid);
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
	public void setPriority(int priority) throws RoyaleProtectionBlocksException {
		if (priority < 0) {
			throw Exceptions.Protections.PRIORITYBELOWZERO.generateException();
		}

		getProtectedRegion().setPriority(priority);
	}

	@Override
	public void hideBlock() {
		this.getUtils().hideProtectionBlock();
	}

	@Override
	public void showBlock() {
		this.getUtils().showProtectionBlock();
	}

	@Override
	public boolean isBlockShown() {
		return this.getUtils().isProtectionBlockShown();
	}

	public boolean isInside(SimpleLocation location, boolean includeBorder) {
		return this.getUtils().isInside(location, includeBorder);
	}

	public boolean isInside(SimpleLocationArea locationArea, boolean includeBorder) {
		return this.getUtils().isInside(locationArea, includeBorder);
	}

	@Override
	public boolean isInsideAny(SimpleLocation location, boolean includeBorder) {
		return this.getUtils().isInsideAny(location, includeBorder);
	}

	@Override
	public boolean isInsideAny(SimpleLocationArea locationArea, boolean includeBorder) {
		return this.getUtils().isInsideAny(locationArea, includeBorder);
	}

	@Override
	public SimpleLocationArea getProtectionArea() {
		return this.getUtils().getProtectionArea();
	}

	@Override
	public SimpleLocationArea getProtectionAreaWithoutBorder() {
		return this.getUtils().getProtectionAreaWithoutBorder();
	}

	@Override
	public IProtection getParentProtection() {
		return this;
	}

	@Override
	public List<IProtection> getChildProtections() {
		return Collections.emptyList();
	}

	@Override
	public List<IProtection> getChildProtectionsRecursively() {
		return Collections.emptyList();
	}

	@Override
	public List<IProtection> getChildProtectionsRecursively(List<IProtection> list) {
		return Collections.emptyList();
	}

	@Override
	public boolean kickPlayer(Player player) throws RoyaleProtectionBlocksException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addBanned(UUID bannedUuid) throws RoyaleProtectionBlocksException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeBanned(UUID bannedUuid) throws RoyaleProtectionBlocksException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setParentProtection(IProtection parentProtection) throws RoyaleProtectionBlocksException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unsetParentProtection() throws RoyaleProtectionBlocksException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isBlocked() {
		throw new UnsupportedOperationException();
	}

	@Override
	public BlockReason getBlockReason() {
		throw new UnsupportedOperationException();
	}

}

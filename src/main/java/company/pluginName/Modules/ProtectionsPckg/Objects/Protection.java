package company.pluginName.Modules.ProtectionsPckg.Objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.domains.DefaultDomain;
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
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Reference.ReferencedProtectionBlock;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionActions;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionDisplayItem;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionPlayers;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionSettings;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionUtils;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.WorldGuard.ProtectionWorldGuardFlags;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import company.pluginName.Modules.SQLPckg.SQLService;
import company.pluginName.Modules.SettingsPckg.SettingsService;
import company.pluginName.Utils.ReflectUtils;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.PandaUtilities.Java.MathUtilities;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaIntegerField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.BlockReason;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.SettingGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionMergeEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionSplitEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.ProtectionBlocks.IProtectionBlock;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtectionFlags;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation.SimpleLocationArea;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.AbstractSetting;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Protection implements IProtection {

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
	private static SettingsService protectionSettingsService;

	@PandaInject
	private static PlaceholdersService placeholdersService;

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

	private @ToString.Include @EqualsAndHashCode.Include String protectionId;
	private @ToString.Include @EqualsAndHashCode.Include String worldName;
	private @ToString.Include long createdDate;
	private ReferencedProtectionBlock referencedProtectionBlock;

	private ProtectionPlayers players = new ProtectionPlayers(this);
	private ProtectionSettings settings = new ProtectionSettings(this);
	private ProtectionWorldGuardFlags worldGuardFlags = new ProtectionWorldGuardFlags(this);
	private ProtectionActions actions = new ProtectionActions(this);
	private ProtectionUtils utils = new ProtectionUtils(this);
	private ProtectionDisplayItem displayItem = new ProtectionDisplayItem(this);

	private boolean deleted = false;

	public Protection(String protectionId, UUID ownerUuid, ReferencedProtectionBlock protectionBlock, String worldName,
			String displayName, long createdDate) {
		this.protectionId = protectionId;
		this.ownerUuid = ownerUuid;
		this.referencedProtectionBlock = protectionBlock;
		this.worldName = worldName;
		this.displayName = displayName;
		this.createdDate = createdDate;
		this.updateOwnerData();
	}

	public Protection(UUID ownerUuid, Location location, IProtectionBlock protectionBlock) {
		this.ownerUuid = ownerUuid;
		this.referencedProtectionBlock = new ReferencedProtectionBlock(protectionBlock.getId());
		this.worldName = location.getWorld().getName();
		this.protectionId = ProtectionUtilities.generateDefaultName(location).toLowerCase();
		this.displayName = ProtectionUtilities.generateDefaultName(location);
		this.location = SimpleLocation.of(location);
		this.updateOwnerData();
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

		if (RoyaleProtectionBlocksAPI.getInstance().getProtectionsService().findProtectionsByOwner(ownerUuid).stream()
				.anyMatch(prot -> {
					return !prot.getProtectionId().equals(getProtectionId())
							&& (displayNameWithoutFormat.equalsIgnoreCase(prot.getProtectionId())
									|| displayNameWithoutFormat.equalsIgnoreCase(prot.getDisplayNameWithoutFormat()));
				})) {
			throw Exceptions.Protections.Save.NAMEINUSE.generateException();
		}

		this.displayName = displayName;
		this.displayNameWithoutFormat = displayNameWithoutFormat;

		if (saveData) {
			TasksUtils.executeOnAsync(() -> {
				try {
					sqlService.saveProtection(this);
				} catch (RoyaleProtectionBlocksExceptionImpl e) {
					e.sendError(Bukkit.getConsoleSender());
				}
			});
		}
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
		this.blockReason = blockReason != null ? blockReason : BlockReason.OTHERS;
	}

	public void unblock() {
		this.blocked = false;
		this.blockReason = null;
	}

	/*
	 * Guard methods
	 */

	private long guardExpirationDate = 0L;

	public void setGuardExpirationDate(long guardExpirationDate) {
		this.guardExpirationDate = guardExpirationDate > System.currentTimeMillis() ? guardExpirationDate : 0;
	}

	public long getGuardExpirationDate() {
		return Math.max(guardExpirationDate, 0L);
	}

	public boolean isGuardActive() {
		return System.currentTimeMillis() <= guardExpirationDate;
	}

	public void setGuardExpirationDateAndSave(long guardExpirationDate) {
		setGuardExpirationDate(guardExpirationDate);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtectionGuardExpirationDate(this);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public void removeGuardExpirationDateAndSave() {
		setGuardExpirationDateAndSave(0);
	}

	/*
	 * Owner methods
	 */

	private @ToString.Include UUID ownerUuid;
	private @Getter @Setter String ownerName;
	private @Setter long ownerLastPlayed = 0L;
	private @Getter @Setter boolean ownerOnline;

	public void setOwnerUuid(UUID ownerUuid) throws RoyaleProtectionBlocksExceptionImpl {
		UUID oldOwnerUuid = this.ownerUuid;
		this.ownerUuid = ownerUuid;

		try {
			this.players.getWorldGuardOwners().remove(oldOwnerUuid);
			this.players.getWorldGuardOwners().add(ownerUuid);
			this.updateOwnerData();
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			this.ownerUuid = oldOwnerUuid;
			this.players.getWorldGuardOwners().remove(ownerUuid);
			this.players.getWorldGuardOwners().add(oldOwnerUuid);
			throw Exceptions.Protections.UNKNOWN.generateException(e);
		}
	}

	public void updateOwnerData() {
		OfflinePlayer offlinePlayer = OfflinePlayerUtilities.getOfflinePlayer(ownerUuid);

		if (offlinePlayer != null) {
			this.ownerName = offlinePlayer.getName();
			this.ownerLastPlayed = offlinePlayer.getLastPlayed();
			this.ownerOnline = offlinePlayer.isOnline();
		} else {
			this.ownerName = null;
			this.ownerLastPlayed = 0L;
			this.ownerOnline = false;
		}
	}

	@Override
	public long getOwnerLastPlayed() {
		if (this.ownerOnline) {
			return System.currentTimeMillis();
		}
		return this.ownerLastPlayed;
	}

	/*
	 * Data methods
	 */

	@Override
	public String getProtectionBlockId() {
		return this.getReferencedProtectionBlock().getIdentifier();
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

	/**
	 * Economy methods
	 */

	public double price;

	public void setPrice(double price) {
		this.price = price > 0D ? MathUtilities.truncate(price, 2) : 0D;
	}

	public boolean isForSale() {
		return this.price > 0D;
	}

	/*
	 * WorldGuard methods
	 */

	private ProtectedRegion protectedRegion;

	public ProtectedRegion getProtectedRegion() {
		if (this.protectedRegion == null) {
			RegionManager regionManager = worldGuardApi.getHook().getInternalWorldGuard()
					.getRegionManagerSafely(worldName);

			if (regionManager != null) {
				this.protectedRegion = regionManager.getRegion(protectionId);
			}
		}

		return this.protectedRegion;
	}

	@Override
	public int getPriority() {
		return getProtectedRegion() != null ? getProtectedRegion().getPriority()
				: SETTINGS_PROTECTION_DEFAULTPRIORITY.getContent();
	}

	/*
	 * Init/Remove methods
	 */

	public void init() throws RoyaleProtectionBlocksExceptionImpl {
		this.setDisplayName(MessageTemplate.inst(SETTINGS_PROTECTION_DEFAULTDISPLAYNAME.getContent())
				.setReplacements(placeholdersService.getProtectionReplacements(this)).toString(), false);
		this.initProtectedRegion();
	}

	private void initProtectedRegion() throws RoyaleProtectionBlocksExceptionImpl {

		RegionManager regionManager = worldGuardApi.getHook().getInternalWorldGuard().getRegionManagerSafely(worldName);

		try {
			// Gets the min and max coords for the region
			Location minLocation = getUtils().getProtectionArea().getMinLocation().toLocation();
			Location maxLocation = getUtils().getProtectionArea().getMaxLocation().toLocation();

			// Creates a region from WorldGuard which contains the min and max coords
			// calculated previously
			ProtectedRegion protectedRegion = new ProtectedCuboidRegion(protectionId,
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
			if (regionManager.hasRegion(getProtectionId())) {
				regionManager.removeRegion(getProtectionId());
			}

			throw e instanceof RoyaleProtectionBlocksExceptionImpl ? (RoyaleProtectionBlocksExceptionImpl) e
					: Exceptions.Protections.Save.UNKNOWN.generateException(e);
		}
	}

	public void remove(boolean removeProtectedRegion) throws RoyaleProtectionBlocksExceptionImpl {
		try {
			try {
				unsetParentProtection();

				new ArrayList<>(getChildProtections()).forEach(protection -> {
					try {
						protection.unsetParentProtectionAndSave();
					} catch (RoyaleProtectionBlocksException e) {
						e.sendError(Bukkit.getConsoleSender());
					}
				});
			} catch (RuntimeException e) {
				throw e.getCause();
			}
		} catch (Throwable e) {
			if (e instanceof RoyaleProtectionBlocksExceptionImpl) {
				throw (RoyaleProtectionBlocksExceptionImpl) e;
			} else {
				throw Exceptions.Protections.Delete.UNKNOWN.generateException(e);
			}
		}

		this.deleted = true;

		if (removeProtectedRegion) {
			TasksUtils.executeOnAsync(() -> {
				try {
					removeProtectedRegion();
				} catch (RoyaleProtectionBlocksExceptionImpl e) {
					e.sendError(Bukkit.getConsoleSender());
				}
			});
		}
	}

	private void removeProtectedRegion() throws RoyaleProtectionBlocksExceptionImpl {
		RegionManager regionManager = worldGuardApi.getHook().getInternalWorldGuard().getRegionManagerSafely(worldName);

		if (regionManager != null) {
			regionManager.removeRegion(protectionId);
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

			removeProtectedRegion();
			initProtectedRegion();

			getProtectedRegion().setFlags(flags);
			getProtectedRegion().setOwners(owners);
			getProtectedRegion().setMembers(members);
		} else {
			initProtectedRegion();
		}

		getProtectedRegion().setPriority(priority);
	}

	/*
	 * Merge methods
	 */

	private IProtection parentProtection = null;
	private List<IProtection> childProtections = new ArrayList<>();

	public void setParentProtectionInstance(IProtection protection) throws RoyaleProtectionBlocksExceptionImpl {
		if (this.getChildProtectionsRecursively().contains(protection)) {
			throw Exceptions.Protections.MERGECHILDPROTECTION.generateException();
		}

		if (this.parentProtection == protection) {
			throw Exceptions.Protections.ALREADYMERGED.generateException();
		}

		if (this == protection) {
			throw Exceptions.Protections.MERGESAMEPROTECTION.generateException();
		}

		this.parentProtection = protection;
		this.parentProtection.getChildProtections().add(this);
	}

	public void setParentProtection(IProtection protection) throws RoyaleProtectionBlocksExceptionImpl {
		this.setParentProtection(null, protection);
	}

	public void setParentProtection(Player player, IProtection protection) throws RoyaleProtectionBlocksExceptionImpl {
		if (SETTINGS_PROTECTION_MUSTBECLOSEFORMERGE.isTrue()) {
			if (!protection.isInsideAny(this.getUtils().getProtectionArea(), true)) {
				throw Exceptions.Protections.MERGETOOFAR.generateException();
			}
		}

		IProtection originalParentProtection = this.parentProtection;

		setParentProtectionInstance(protection);

		if (originalParentProtection != null) {
			originalParentProtection.getChildProtections().remove(this);
			originalParentProtection = null;
		}

		try {
			this.performAllProtections((prot) -> {
				((Protection) prot).getProtectedRegion()
						.setMembers(((Protection) this.parentProtection).getProtectedRegion().getMembers());
				((Protection) prot).getProtectedRegion()
						.setOwners(((Protection) this.parentProtection).getProtectedRegion().getOwners());
				((Protection) prot).getProtectedRegion()
						.setFlags(((Protection) this.parentProtection).getProtectedRegion().getFlags());
				((Protection) prot).getProtectedRegion()
						.setPriority(((Protection) this.parentProtection).getProtectedRegion().getPriority());
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}

		TasksUtils.execute(() -> Bukkit.getPluginManager()
				.callEvent(new ProtectionMergeEvent(player, this, this.parentProtection)));
	}

	public void unsetParentProtection() throws RoyaleProtectionBlocksExceptionImpl {
		unsetParentProtection(null);
	}

	public void unsetParentProtection(Player player) throws RoyaleProtectionBlocksExceptionImpl {
		IProtection parentProtection = this.parentProtection;

		if (this.parentProtection != null) {
			this.parentProtection.getChildProtections().remove(this);
			this.parentProtection = null;

			TasksUtils.execute(() -> Bukkit.getPluginManager()
					.callEvent(new ProtectionSplitEvent(player, this, parentProtection)));
		}
	}

	public IProtection getParentProtection() {
		return this.parentProtection != null ? this.parentProtection.getParentProtection() : this;
	}

	@Override
	public List<IProtection> getChildProtectionsRecursively() {
		return getChildProtectionsRecursively(new ArrayList<>());
	}

	@Override
	public List<IProtection> getChildProtectionsRecursively(List<IProtection> list) {
		list.addAll(this.childProtections);
		this.childProtections.forEach(prot -> prot.getChildProtectionsRecursively(list));
		return list;
	}

	/*
	 * Public status methods
	 */

	private boolean publicAccess = false;

	public void setPublicAccess(boolean publicAccess) throws RoyaleProtectionBlocksException {
		if (this.publicAccess == publicAccess) {
			throw publicAccess ? Exceptions.Protections.ALREADYPUBLIC.generateException()
					: Exceptions.Protections.ALREADYPRIVATE.generateException();
		}

		this.publicAccess = publicAccess;
	}

	/*
	 * API methods
	 */

	public boolean isMainOwner(UUID playerUuid) {
		return this.ownerUuid.equals(playerUuid);
	}

	public boolean isOwner(UUID playerUuid) {
		return this.players.getOwners().contains(playerUuid);
	}

	public boolean isMember(UUID playerUuid) {
		return this.players.getMembers().contains(playerUuid);
	}

	public boolean isBanned(UUID playerUuid) {
		return this.players.getBanneds().contains(playerUuid);
	}

	@Override
	public boolean kickPlayer(Player player) throws RoyaleProtectionBlocksException {
		return this.getActions().kickPlayer(player);
	}

	@Override
	public void teleport(Player player, boolean ignoreCost, boolean ignoreUnsafeWarning)
			throws RoyaleProtectionBlocksException {
		this.getActions().teleportToHome(player, ignoreCost, ignoreUnsafeWarning);
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
	public IProtectionBlock getProtectionBlock() {
		return this.referencedProtectionBlock.getObject();
	}

	@Override
	public IProtectionFlags getFlags() {
		return this.worldGuardFlags;
	}

	@Override
	public void setParentProtectionAndSave(IProtection protection) throws RoyaleProtectionBlocksExceptionImpl {
		setParentProtection(protection);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveParentProtection(this);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	@Override
	public void unsetParentProtectionAndSave() throws RoyaleProtectionBlocksExceptionImpl {
		unsetParentProtection();

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveParentProtection(this);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	@Override
	public void blockAndSave(BlockReason blockReason) {
		block(blockReason);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveBlockStatus(this);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	@Override
	public void unblockAndSave() {
		unblock();

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveBlockStatus(this);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public void setOwnerUuidAndSave(UUID ownerUuid) throws RoyaleProtectionBlocksExceptionImpl {
		setOwnerUuid(ownerUuid);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveOwnerUuid(this);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	@Override
	public void setPriceAndSave(double price) {
		setPrice(price);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.savePrice(this);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	@Override
	public void setPublicAccessAndSave(boolean publicAccess) throws RoyaleProtectionBlocksException {
		setPublicAccess(publicAccess);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.savePublicAccess(this);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	@Override
	public List<UUID> getMembers() {
		return new ArrayList<>(this.players.getMembers());
	}

	@Override
	public List<UUID> getOwners() {
		return new ArrayList<>(this.players.getMembers());
	}

	@Override
	public List<UUID> getBanneds() {
		return new ArrayList<>(this.players.getMembers());
	}

	@Override
	public void addMemberAndSave(UUID memberUuid) throws RoyaleProtectionBlocksException {
		this.players.addMember(memberUuid);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtectionMember(this, memberUuid);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	@Override
	public void removeMemberAndSave(UUID memberUuid) throws RoyaleProtectionBlocksException {
		this.players.removeMember(memberUuid);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.deleteProtectionMember(this, memberUuid);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	@Override
	public void addOwnerAndSave(UUID ownerUuid) throws RoyaleProtectionBlocksException {
		this.players.addOwner(ownerUuid);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtectionOwner(this, ownerUuid);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	@Override
	public void removeOwnerAndSave(UUID ownerUuid) throws RoyaleProtectionBlocksException {
		this.players.removeOwner(ownerUuid);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.deleteProtectionOwner(this, ownerUuid);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	@Override
	public void addBannedAndSave(UUID bannedUuid) throws RoyaleProtectionBlocksException {
		this.players.addBanned(bannedUuid);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtectionBanned(this, bannedUuid);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	@Override
	public void removeBannedAndSave(UUID bannedUuid) throws RoyaleProtectionBlocksException {
		this.players.removeBanned(bannedUuid);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.deleteProtectionBanned(this, bannedUuid);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	@Override
	public <T extends Serializable> T getSettingValue(AbstractSetting<T> setting, Player player)
			throws RoyaleProtectionBlocksException {
		return settings.getValue(setting, player);
	}

	@Override
	public <T extends Serializable> T getSettingValue(AbstractSetting<T> setting, SettingGroup group)
			throws RoyaleProtectionBlocksException {
		return settings.getValue(setting, group);
	}

	@Override
	public String getSettingValueAsString(AbstractSetting<?> setting, Player player)
			throws RoyaleProtectionBlocksException {
		return settings.getValueAsString(setting, player);
	}

	@Override
	public String getSettingValueAsString(AbstractSetting<?> setting, SettingGroup group)
			throws RoyaleProtectionBlocksException {
		return settings.getValueAsString(setting, group);
	}

	@Override
	public <T extends Serializable> void setSettingValue(AbstractSetting<T> setting, SettingGroup group, T value)
			throws RoyaleProtectionBlocksException {
		settings.setValue(setting, group, value);
	}

	@Override
	public void setUnparsedSettingValue(AbstractSetting<?> setting, SettingGroup group, String value)
			throws RoyaleProtectionBlocksException {
		settings.setUnparsedValue(setting, group, value);
	}

	@Override
	public boolean canTeleport(Player player) {
		try {
			return isMainOwner(player.getUniqueId()) || hasStaffMode(player)
					|| this.settings.getValue(ProtectionSettingsService.TELEPORT_SETTING, player)
					|| PermissionsService.TELEPORT_OTHERS.hasPermission(player);
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			return false;
		}
	}

	@Override
	public boolean canFly(Player player) {
		try {
			return isMainOwner(player.getUniqueId()) || hasStaffMode(player)
					|| this.settings.getValue(ProtectionSettingsService.FLY_SETTING, player);
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			return false;
		}
	}

	private boolean hasStaffMode(Player pl) {
		PlayerData playerData = playerDataService.getPlayerData(pl);
		return playerData != null && playerData.isStaffMode();
	}

}

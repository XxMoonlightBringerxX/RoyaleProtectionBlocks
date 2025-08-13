package company.pluginName.Modules.ProtectionsPckg.Objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.Exceptions.Protections;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.InvitationsSubCommand;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Reference.ReferencedProtectionBlock;
import company.pluginName.Modules.ProtectionPermissionsPckg.ProtectionPermissionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionActions;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionPlayers;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionSettings;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionUtils;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.Permissions.ProtectionPermissionManager;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.WorldGuard.ProtectionWorldGuardFlags;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import company.pluginName.Modules.SQLPckg.SQLService;
import company.pluginName.Modules.SettingsPckg.SettingsService;
import company.pluginName.Utils.ReflectUtils;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment.ClickEvent;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.PandaUtilities.Java.MathUtilities;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.PandaCommandsService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaIntegerField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.BlockReason;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.PermissionGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.SettingGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionMergeEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionSplitEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.ProtectionBlocks.IProtectionBlock;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtectionFlags;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation.SimpleLocationArea;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Permissions.AbstractPermission;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Settings.AbstractSetting;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.Protections.Objects.ProtectionInvitation;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Protection implements IProtection {

	private static final ItemStack UNKNOWN_DISPLAY_ITEM = ItemBuilder.inst().setMaterial(Material.PLAYER_HEAD)
			.setAmount(1)
			.setSkin(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcwNWZkOTRhMGM0MzE5MjdmYjRlNjM5YjBmY2ZiNDk3MTdlNDEyMjg1YTAyYjQzOWUwMTEyZGEyMmIyZTJlYyJ9fX0=")
			.build();

	@RegisteredPandaField("config")
	public static PandaIntegerField SETTINGS_PROTECTION_MAXIMUMDISPLAYNAMELENGTH = new PandaIntegerField(
			"Settings.Protection.Maximum-display-name-length", 255);

	@RegisteredPandaField("config")
	public static PandaBooleanField SETTINGS_PROTECTION_MERGE_MUSTBECLOSEFORMERGE = new PandaBooleanField(
			"Settings.Protection.Merge.Must-be-close-for-merge", "Settings.Protection.Must-be-close-for-merge", true);

	@RegisteredPandaField("config")
	public static PandaStringField SETTINGS_PROTECTION_DEFAULTDISPLAYNAME = new PandaStringField(
			"Settings.Protection.Default-display-name",
			"{protection_world}_{protection_location_x}_{protection_location_y}_{protection_location_z}");

	@RegisteredPandaField("config")
	public static PandaIntegerField SETTINGS_PROTECTION_DEFAULTPRIORITY = new PandaIntegerField(
			"Settings.Protection.Default-priority", 0);

	@RegisteredPandaField("lang")
	public static PandaPrefixedStringField MESSAGE_PROTECTIONS_INVITATIONS_INVITATIONRECEIVED = new PandaPrefixedStringField(
			"Message.Protections.Invitations.Invitation-received",
			"&eYou've received a new invitation to a protection! Use &a{command} &eto accept/reject the invitation.");

	@PandaInject
	private static SettingsService protectionSettingsService;

	@PandaInject
	private static PlaceholdersService placeholdersService;

	@PandaInject
	private static SQLService sqlService;

	@PandaInject
	private static PlaceholderAPI placeholderApi;

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	@PandaInject
	private static PlayerDataService playerDataService;

	@PandaInject
	private static PandaCommandsService commandsService;

	private @ToString.Include @EqualsAndHashCode.Include String protectionId;
	private @ToString.Include @EqualsAndHashCode.Include String worldName;
	private @ToString.Include long createdDate;
	private ReferencedProtectionBlock referencedProtectionBlock;

	private @Setter(AccessLevel.NONE) ProtectionPlayers players = new ProtectionPlayers(this);
	private @Setter(AccessLevel.NONE) ProtectionSettings settings = new ProtectionSettings(this);
	private @Setter(AccessLevel.NONE) ProtectionPermissionManager permissions = new ProtectionPermissionManager(this);
	private @Setter(AccessLevel.NONE) ProtectionWorldGuardFlags worldGuardFlags = new ProtectionWorldGuardFlags(this);
	private @Setter(AccessLevel.NONE) ProtectionActions actions = new ProtectionActions(this);
	private @Setter(AccessLevel.NONE) ProtectionUtils utils = new ProtectionUtils(this);

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
	 * Display item methods
	 */

	private ItemStack displayItem;
	private @Getter(lombok.AccessLevel.NONE) @Setter(lombok.AccessLevel.NONE) ItemStack protectionBlockDisplayItem;

	public ItemStack getDisplayItem() {
		return this.displayItem != null ? this.displayItem.clone() : null;
	}

	public ItemStack getDisplayItemOrDefault() {
		if (this.displayItem != null) {
			return this.displayItem.clone();
		} else {
			if (this.protectionBlockDisplayItem == null) {
				IProtectionBlock block = this.getProtectionBlock();
				if (block != null) {
					this.protectionBlockDisplayItem = block.getItem().clone();
				}
			}

			if (this.protectionBlockDisplayItem != null) {
				return this.protectionBlockDisplayItem;
			}
		}
		return UNKNOWN_DISPLAY_ITEM.clone();
	}

	public void setDisplayItemAndSave(ItemStack displayItem) {
		this.setDisplayItem(displayItem);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtection(this);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public void setDisplayItem(ItemStack displayItem) {
		this.displayItem = displayItem != null ? displayItem.clone() : null;

		if (this.displayItem != null) {
			this.displayItem.setAmount(1);
		}
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
		} catch (Exception e) {
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

	public void clearCachedProtectedRegion() {
		this.protectedRegion = null;
	}

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

	private boolean creationInProgress = false;

	public void init() throws RoyaleProtectionBlocksExceptionImpl {
		this.creationInProgress = true;

		this.setDisplayName(MessageTemplate.inst(SETTINGS_PROTECTION_DEFAULTDISPLAYNAME.getContent())
				.setReplacements(placeholdersService.getProtectionReplacements(this)).toString(), false);
		this.initProtectedRegion();

		this.creationInProgress = false;
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
			throw Exceptions.Protections.Merge.CHILDPROTECTION.generateException();
		}

		if (this.parentProtection == protection) {
			throw Exceptions.Protections.Merge.ALREADYMERGED.generateException();
		}

		if (this == protection) {
			throw Exceptions.Protections.Merge.SAMEPROTECTION.generateException();
		}

		this.parentProtection = protection;
		this.parentProtection.getChildProtections().add(this);
	}

	public void setParentProtection(IProtection protection) throws RoyaleProtectionBlocksExceptionImpl {
		this.setParentProtection(null, protection);
	}

	public void setParentProtection(Player player, IProtection protection) throws RoyaleProtectionBlocksExceptionImpl {
		if (!this.getWorldName().equals(protection.getWorldName())) {
			throw Exceptions.Protections.Merge.DIFFERENTWORLDS.generateException();
		}

		if (SETTINGS_PROTECTION_MERGE_MUSTBECLOSEFORMERGE.isTrue()) {
			if (!protection.isInsideAny(this.getUtils().getProtectionArea(), true)) {
				throw Exceptions.Protections.Merge.TOOFAR.generateException();
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
	 * Invitations system
	 */

	private Map<UUID, ProtectionInvitation> invitedPlayers = new HashMap<>();

	public ProtectionInvitation getInvitedPlayer(UUID playerUuid) {
		return invitedPlayers.get(playerUuid);
	}

	public ProtectionInvitation addInvitedPlayer(UUID playerUuid) throws RoyaleProtectionBlocksExceptionImpl {
		if (isInvitedPlayer(playerUuid)) {
			throw Protections.Invitations.ALREADYINVITED.generateException();
		}

		ProtectionInvitation invitation = new ProtectionInvitation(this, playerUuid);

		this.invitedPlayers.put(playerUuid, invitation);

		Player pl = Bukkit.getPlayer(playerUuid);

		if (pl != null) {
			playerDataService.getPlayerData(pl).getProtectionInvitations().add(invitation);

			InvitationsSubCommand command = commandsService.getSubCommandByClass(InvitationsSubCommand.class);

			MessageTemplate.inst(MESSAGE_PROTECTIONS_INVITATIONS_INVITATIONRECEIVED.applyPrefix())
					.setReplacements(new Replacement("{command}", () -> "/" + command.getCommandPath(),
							new ClickEvent(ClickEvent.Action.RUN_COMMAND, command.getCommandPath())))
					.process().sendMessage(pl);
		}

		return invitation;
	}

	public ProtectionInvitation addInvitedPlayerAndSave(UUID playerUuid) throws RoyaleProtectionBlocksExceptionImpl {
		ProtectionInvitation invitation = this.addInvitedPlayer(playerUuid);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtectionInvitation(this, invitation);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});

		return invitation;
	}

	public void acceptInvitation(UUID playerUuid) throws RoyaleProtectionBlocksException {
		removeInvitedPlayer(playerUuid);
		this.players.addMember(playerUuid);
	}

	@Override
	public void acceptInvitationAndSave(UUID playerUuid) throws RoyaleProtectionBlocksException {
		removeInvitedPlayerAndSave(playerUuid);
		addMemberAndSave(playerUuid, true);
	}

	public void removeInvitedPlayer(UUID playerUuid) throws RoyaleProtectionBlocksExceptionImpl {
		if (!isInvitedPlayer(playerUuid)) {
			throw Protections.Invitations.NOTINVITED.generateException();
		}

		ProtectionInvitation invitation = this.invitedPlayers.remove(playerUuid);

		Player pl = Bukkit.getPlayer(playerUuid);

		if (pl != null) {
			playerDataService.getPlayerData(pl).getProtectionInvitations().remove(invitation);
		}
	}

	public void removeInvitedPlayerAndSave(UUID playerUuid) throws RoyaleProtectionBlocksExceptionImpl {
		this.removeInvitedPlayer(playerUuid);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.deleteProtectionInvitation(this, playerUuid);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public boolean isInvitedPlayer(UUID playerUuid) {
		return this.invitedPlayers.containsKey(playerUuid);
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
		return new ArrayList<>(this.players.getOwners());
	}

	@Override
	public List<UUID> getBanneds() {
		return new ArrayList<>(this.players.getBanneds());
	}

	@Override
	public void addMemberAndSave(UUID memberUuid) throws RoyaleProtectionBlocksException {
		addMemberAndSave(memberUuid, true);
	}

	public void addMemberAndSave(UUID memberUuid, boolean sqlAsyncSave) throws RoyaleProtectionBlocksException {
		this.players.addMember(memberUuid);

		Runnable runnable = () -> {
			try {
				sqlService.saveProtectionMember(this, memberUuid);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		};

		if (sqlAsyncSave) {
			TasksUtils.executeOnAsync(runnable);
		} else {
			runnable.run();
		}
	}

	@Override
	public void removeMemberAndSave(UUID memberUuid) throws RoyaleProtectionBlocksException {
		removeMemberAndSave(memberUuid, true);
	}

	public void removeMemberAndSave(UUID memberUuid, boolean sqlAsyncSave) throws RoyaleProtectionBlocksException {
		this.players.removeMember(memberUuid);

		Runnable runnable = () -> {
			try {
				sqlService.deleteProtectionMember(this, memberUuid);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		};

		if (sqlAsyncSave) {
			TasksUtils.executeOnAsync(runnable);
		} else {
			runnable.run();
		}
	}

	@Override
	public void addOwnerAndSave(UUID ownerUuid) throws RoyaleProtectionBlocksException {
		addOwnerAndSave(ownerUuid, true);
	}

	public void addOwnerAndSave(UUID ownerUuid, boolean sqlAsyncSave) throws RoyaleProtectionBlocksException {
		this.players.addOwner(ownerUuid);

		Runnable runnable = () -> {
			try {
				sqlService.saveProtectionOwner(this, ownerUuid);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		};

		if (sqlAsyncSave) {
			TasksUtils.executeOnAsync(runnable);
		} else {
			runnable.run();
		}
	}

	@Override
	public void removeOwnerAndSave(UUID ownerUuid) throws RoyaleProtectionBlocksException {
		removeOwnerAndSave(ownerUuid, true);
	}

	public void removeOwnerAndSave(UUID ownerUuid, boolean sqlAsyncSave) throws RoyaleProtectionBlocksException {
		this.players.removeOwner(ownerUuid);

		Runnable runnable = () -> {
			try {
				sqlService.deleteProtectionOwner(this, ownerUuid);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		};

		if (sqlAsyncSave) {
			TasksUtils.executeOnAsync(runnable);
		} else {
			runnable.run();
		}
	}

	@Override
	public void addBannedAndSave(UUID bannedUuid) throws RoyaleProtectionBlocksException {
		addBannedAndSave(bannedUuid, true);
	}

	public void addBannedAndSave(UUID bannedUuid, boolean sqlAsyncSave) throws RoyaleProtectionBlocksException {
		this.players.addBanned(bannedUuid);

		Runnable runnable = () -> {
			try {
				sqlService.saveProtectionBanned(this, bannedUuid);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		};

		if (sqlAsyncSave) {
			TasksUtils.executeOnAsync(runnable);
		} else {
			runnable.run();
		}
	}

	@Override
	public void removeBannedAndSave(UUID bannedUuid) throws RoyaleProtectionBlocksException {
		removeBannedAndSave(bannedUuid, true);
	}

	public void removeBannedAndSave(UUID bannedUuid, boolean sqlAsyncSave) throws RoyaleProtectionBlocksException {
		this.players.removeBanned(bannedUuid);

		Runnable runnable = () -> {
			try {
				sqlService.deleteProtectionBanned(this, bannedUuid);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		};

		if (sqlAsyncSave) {
			TasksUtils.executeOnAsync(runnable);
		} else {
			runnable.run();
		}

	}

	public void clearPlayersAndSave() {
		this.players.clearAll();

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.deleteProtectionMembers(this);
				sqlService.deleteProtectionOwners(this);
				sqlService.deleteProtectionBanneds(this);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public void clearPermissionsAndSave() {
		this.permissions.resetPermissions();

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.deleteProtectionPermissions(this);
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
	public Boolean getPermissionValue(AbstractPermission setting, OfflinePlayer player) {
		return this.permissions.getValue(setting, player);
	}

	@Override
	public Boolean getPermissionValue(AbstractPermission setting, PermissionGroup group) {
		return this.permissions.getValue(setting, group);
	}

	@Override
	public void setPermissionValue(AbstractPermission permission, PermissionGroup group, Boolean value) {
		this.permissions.setValue(permission, group, value);
	}

	@Override
	public boolean canTeleport(Player player) {
		return isMainOwner(player.getUniqueId()) || hasStaffMode(player)
				|| (ProtectionPermissionsService.TELEPORT_PERMISSION.isEnabled() && Boolean.TRUE
						.equals(this.permissions.getValue(ProtectionPermissionsService.TELEPORT_PERMISSION, player)))
				|| PermissionsService.TELEPORT_OTHERS.hasPermission(player);
	}

	@Override
	public boolean canFly(Player player) {
		return isMainOwner(player.getUniqueId()) || hasStaffMode(player)
				|| (ProtectionPermissionsService.FLY_PERMISSION.isEnabled() && Boolean.TRUE
						.equals(this.permissions.getValue(ProtectionPermissionsService.FLY_PERMISSION, player)));
	}

	@Override
	public boolean canToggleBlockVisibility(Player player) {
		return this.isMainOwner(player.getUniqueId()) || hasStaffMode(player)
				|| (ProtectionPermissionsService.TOGGLEBLOCKVISIBILITY_PERMISSION.isEnabled() && this.permissions
						.getValue(ProtectionPermissionsService.TOGGLEBLOCKVISIBILITY_PERMISSION, player))
				|| PermissionsService.HIDE_OTHERS.hasPermission(player);
	}

	@Override
	public boolean canBreak(Player player) {
		return isMainOwner(player.getUniqueId()) || hasStaffMode(player)
				|| !ProtectionPermissionsService.BREAK_PERMISSION.isEnabled() || Boolean.TRUE
						.equals(this.permissions.getValue(ProtectionPermissionsService.BREAK_PERMISSION, player));
	}

	@Override
	public boolean canPlace(Player player) {
		return isMainOwner(player.getUniqueId()) || hasStaffMode(player)
				|| !ProtectionPermissionsService.PLACE_PERMISSION.isEditable() || Boolean.TRUE
						.equals(this.permissions.getValue(ProtectionPermissionsService.PLACE_PERMISSION, player));
	}

	@Override
	public boolean canInteract(Player player) {
		return isMainOwner(player.getUniqueId()) || hasStaffMode(player)
				|| !ProtectionPermissionsService.INTERACT_PERMISSION.isEnabled() || Boolean.TRUE
						.equals(this.permissions.getValue(ProtectionPermissionsService.INTERACT_PERMISSION, player));
	}

	private boolean hasStaffMode(Player pl) {
		PlayerData playerData = playerDataService.getPlayerData(pl);
		return playerData != null && playerData.isStaffMode();
	}

	public void synchronizeWorldGuardRegion() throws RoyaleProtectionBlocksExceptionImpl {
		if (this.getParentProtection() != this) {
			throw Exceptions.Protections.REGENONLYONPARENT.generateException();
		}

		this.getMembers().forEach(uuid -> {
			try {
				performAllProtections((prot) -> {
					if (!((Protection) prot).getPlayers().getWorldGuardMembers().contains(uuid)) {
						MainPluginClass.getSimpleLogger().sendDebug(String.format(
								"Adding member with UUID '%s' to protected region on WorldGuard for protection '%s'",
								uuid.toString(), prot.getProtectionId()));
						((Protection) prot).getPlayers().getWorldGuardMembers().add(uuid);
					}
				});
			} catch (Throwable e) {
				e.printStackTrace();
			}
		});

		this.getPlayers().getWorldGuardMembers().list().forEach(uuid -> {
			try {
				performAllProtections((prot) -> {
					if (!((Protection) prot).isMember(uuid)) {
						MainPluginClass.getSimpleLogger().sendDebug(String.format(
								"Removing member with UUID '%s' to protected region on WorldGuard for protection '%s'",
								uuid.toString(), prot.getProtectionId()));
						((Protection) prot).getPlayers().getWorldGuardMembers().remove(uuid);
					}
				});
			} catch (Throwable e) {
				e.printStackTrace();
			}
		});

		this.getOwners().forEach(uuid -> {
			try {
				performAllProtections((prot) -> {
					if (!((Protection) prot).getPlayers().getWorldGuardOwners().contains(uuid)) {
						MainPluginClass.getSimpleLogger().sendDebug(String.format(
								"Adding owner with UUID '%s' to protected region on WorldGuard for protection '%s'",
								uuid.toString(), prot.getProtectionId()));
						((Protection) prot).getPlayers().getWorldGuardOwners().add(uuid);
					}
				});
			} catch (Throwable e) {
				e.printStackTrace();
			}
		});

		this.getPlayers().getWorldGuardOwners().list().forEach(uuid -> {
			try {
				performAllProtections((prot) -> {
					if (!((Protection) prot).isOwner(uuid) && !((Protection) prot).isMainOwner(uuid)) {
						MainPluginClass.getSimpleLogger().sendDebug(String.format(
								"Removing owner with UUID '%s' to protected region on WorldGuard for protection '%s'",
								uuid.toString(), prot.getProtectionId()));
						((Protection) prot).getPlayers().getWorldGuardOwners().remove(uuid);
					}
				});
			} catch (Throwable e) {
				e.printStackTrace();
			}
		});

		try {
			performAllProtections((prot) -> {
				if (!((Protection) prot).getPlayers().getWorldGuardOwners()
						.contains(((Protection) prot).getOwnerUuid())) {
					MainPluginClass.getSimpleLogger().sendDebug(String.format(
							"Adding main owner with UUID '%s' to protected region on WorldGuard for protection '%s'",
							prot.getOwnerUuid().toString(), prot.getProtectionId()));
					((Protection) prot).getPlayers().getWorldGuardOwners().add(prot.getOwnerUuid());
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}

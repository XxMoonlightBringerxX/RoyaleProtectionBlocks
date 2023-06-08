package company.pluginName.Modules.ProtectionsPckg.Objects;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

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
import company.pluginName.Modules.FilePckg.Settings.SettingString;
import company.pluginName.Modules.ProtectionsPckg.Objects.ReferencedObjects.ReferencedProtectionBlock;
import company.pluginName.Utils.BlockVectorUtils;
import company.pluginName.Utils.OfflinePlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotMessages.Objects.TextInput;
import relampagorojo93.LibsCollection.SpigotMessages.Objects.TextReplacement;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;

@Data
@AllArgsConstructor
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

	public Protection(UUID ownerUuid) {
		this(null, ownerUuid, null, null, null, null, null);
	}

	public Protection(String regionId, UUID ownerUuid, ReferencedProtectionBlock protectionBlock, String worldName,
			String displayName) {
		this(regionId, ownerUuid, protectionBlock, worldName, displayName, null, null);
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
							TextInput.inst().text(SettingString.SETTING_PROTECTION_ENTERDEFAULTMESSAGE.toString())
									.replacements(new TextReplacement("{player}", () -> playerName)))
							.toString());
			protectedRegion.setFlag(FAREWELL_FLAG,
					MessageBuilder.createMessage(
							TextInput.inst().text(SettingString.SETTING_PROTECTION_EXITDEFAULTMESSAGE.toString())
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

	public boolean isProtectionBlock(Block block) {
		boolean isSameLocation = block.getLocation().equals(getProtectionBlockLocation());
		if (isSameLocation) {
			ProtectionBlock protectionBlock = this.protectionBlock.getObject();
			boolean itemIsSame = protectionBlock != null
					&& (protectionBlock.getItem().getType() == Material.PAPER.getMaterial()
							|| block.getType() == protectionBlock.getItem().getType());
			if (itemIsSame) {
				return true;
			}
		}
		return false;
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

}

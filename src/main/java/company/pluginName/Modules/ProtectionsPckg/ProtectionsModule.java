package company.pluginName.Modules.ProtectionsPckg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import company.pluginName.MainPluginClass;
import company.pluginName.Permissions;
import company.pluginName.Exceptions.Protection.Delete.ProtectionDeleteDeniedException;
import company.pluginName.Exceptions.Protection.Delete.ProtectionDeleteException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveAlreadyOccupiedException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveMaxReachedException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveNameInUseException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveNoVisibleTextException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveRenameDeniedException;
import company.pluginName.Exceptions.ProtectionBlocks.Delete.ProtectionBlocksDeleteException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.Utils.BlockVectorUtils;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import lombok.Getter;
import relampagorojo93.LibsCollection.SpigotPlugin.LoadOn;
import relampagorojo93.LibsCollection.SpigotPlugin.PluginModule;

public class ProtectionsModule implements PluginModule {

	private @Getter HashMap<String, ProtectionBlock> protectionBlocks = new HashMap<>();
	private @Getter HashMap<UUID, List<Protection>> protectionsByOwner = new HashMap<>();
	private @Getter HashMap<String, List<Protection>> protectionsByWorld = new HashMap<>();
	private @Getter HashMap<String, Protection> protectionByRegion = new HashMap<>();

	@Override
	public LoadOn loadOn() {
		return LoadOn.ENABLE;
	}

	@Override
	public boolean optional() {
		return false;
	}

	@Override
	public boolean allowReload() {
		return false;
	}

	@Override
	public boolean load() {
		MainPluginClass.getPlugin().getSqlModule().getProtectionBlocks().stream()
				.filter(block -> block.getInformation().getItem() != null
						&& block.getInformation().getItem().getType() != Material.AIR)
				.forEach(block -> protectionBlocks.put(block.getInformation().getId().toLowerCase(), block));

		MainPluginClass.getPlugin().getSqlModule().getProtections().stream().filter(protection -> {
			if (Bukkit.getWorld(protection.getWorldName()) != null && protection.getProtectedRegion() == null) {
				try {
					MessageBuilder
							.createMessage(
									MessageString.applyPrefix("&7Removing protection '" + protection.getRegionId()
											+ "' as it couldn't be found on '" + protection.getWorldName() + "'"))
							.sendMessage(Bukkit.getConsoleSender());
					removeProtection(protection);
				} catch (ProtectionDeleteException e) {
					e.sendError(Bukkit.getConsoleSender());
				}
				return false;
			}
			return true;
		}).forEach(protection -> {
			protectionsByOwner.putIfAbsent(protection.getOwnerUuid(), new ArrayList<>());
			protectionsByOwner.get(protection.getOwnerUuid()).add(protection);
			protectionsByWorld.putIfAbsent(protection.getWorldName(), new ArrayList<>());
			protectionsByWorld.get(protection.getWorldName()).add(protection);
			protectionByRegion.put(protection.getRegionId(), protection);
		});

		return true;
	}

	@Override
	public boolean unload() {
		this.protectionsByWorld.values().forEach(list -> list.forEach(protection -> {
			if (protection.isProtectionViewActive()) {
				protection.toggleProtectionView();
			}
		}));
		this.protectionBlocks.clear();
		this.protectionsByOwner.clear();
		this.protectionsByWorld.clear();
		this.protectionByRegion.clear();
		return true;
	}

	public ProtectionBlock getProtectionBlockById(String id) {
		return protectionBlocks.get(id.toLowerCase());
	}

	public List<Protection> getAllowedProtections(OfflinePlayer pl) {
		return protectionByRegion.values().stream().filter(prot -> prot.getOwners().list().contains(pl.getUniqueId()))
				.collect(Collectors.toList());
	}

	/*
	 * Create methods
	 */

	public Protection createProtection(Player pl, ProtectionBlock protectionBlock, Location location)
			throws ProtectionSaveException {
		if (!pl.hasPermission(Permissions.PROTECTION_MAX_BYPASS)) {
			if (Permissions.getMaxCapacity(pl, protectionBlock) <= protectionsByOwner
					.getOrDefault(pl.getUniqueId(), new ArrayList<>()).size()) {
				throw new ProtectionSaveMaxReachedException();
			}
		}

		return this.createProtection(pl.getUniqueId(), pl, protectionBlock, location);
	}

	public Protection createProtection(UUID ownerUuid, Player creator, ProtectionBlock protectionBlock,
			Location location) throws ProtectionSaveException {
		for (Protection prot : protectionByRegion.values()) {
			if (prot.getProtectionBlockLocation().equals(location.getBlock().getLocation())) {
				throw new ProtectionSaveAlreadyOccupiedException();
			}
		}

		Protection protection = new Protection(ownerUuid);

		protection.create(creator, location, protectionBlock);

		protectionByRegion.put(protection.getRegionId(), protection);
		protectionsByOwner.putIfAbsent(ownerUuid, new ArrayList<>());
		protectionsByOwner.get(ownerUuid).add(protection);
		protectionsByWorld.putIfAbsent(location.getWorld().getName(), new ArrayList<>());
		protectionsByWorld.get(location.getWorld().getName()).add(protection);

		return protection;
	}

	/*
	 * Rename methods
	 */

	public void renameProtection(Protection protection, String newName) throws ProtectionSaveException {
		renameProtection(null, protection, newName);
	}

	public void renameProtection(Player pl, Protection protection, String newName) throws ProtectionSaveException {
		if (pl != null) {
			if (!protection.getOwners().list().contains(pl.getUniqueId())
					&& !pl.hasPermission(Permissions.PROTECTION_RENAME_OTHERS)) {
				throw new ProtectionSaveRenameDeniedException();
			}
		}

		if (MessageBuilder.createMessage(TextInput.inst().text(newName).color(false)).toString().isEmpty()) {
			throw new ProtectionSaveNoVisibleTextException();
		}

		if (protectionByRegion.values().stream().anyMatch(prot -> !prot.getRegionId().equals(protection.getRegionId())
				&& prot.getDisplayName().equalsIgnoreCase(newName))) {
			throw new ProtectionSaveNameInUseException();
		}

		protection.setDisplayName(newName);
	}

	/*
	 * Remove methods
	 */

	public void removeProtection(Protection protection) throws ProtectionDeleteException {
		removeProtection(null, protection);
	}

	public void removeProtection(Player pl, Protection protection) throws ProtectionDeleteException {
		if (pl != null) {
			if (!protection.canDelete(pl)) {
				throw new ProtectionDeleteDeniedException();
			}
		}

		protection.delete();

		protectionByRegion.remove(protection.getRegionId());
		if (protectionsByOwner.containsKey(protection.getOwnerUuid())) {
			protectionsByOwner.get(protection.getOwnerUuid()).remove(protection);
		}
		if (protectionsByWorld.containsKey(protection.getWorldName())) {
			protectionsByWorld.get(protection.getWorldName()).remove(protection);
		}
	}

	/*
	 * Protection blocks methods
	 */

	public void registerProtectionBlock(ProtectionBlock protectionBlock) throws ProtectionBlocksSaveException {
		protectionBlocks.putIfAbsent(protectionBlock.getInformation().getId().toLowerCase(), protectionBlock);
	}

	public void unregisterProtectionBlock(ProtectionBlock protectionBlock) throws ProtectionBlocksDeleteException {
		protectionBlocks.remove(protectionBlock.getInformation().getId());
	}

	/*
	 * Search methods
	 */

	public Protection getProtectionByLocation(Location location) {
		ApplicableRegionSet regions = getApplicableRegions(location.getWorld(),
				BlockVectorUtils.locationToVector(location));

		if (regions == null) {
			return null;
		}

		return regions.getRegions().stream().map(region -> protectionByRegion.get(region.getId()))
				.filter(prot -> prot != null).sorted((r1, r2) -> Integer.compare(r1.getProtectedRegion().getPriority(),
						r2.getProtectedRegion().getPriority()))
				.findFirst().orElse(null);
	}

	public Protection getProtectionByBlock(Location location) {
		ApplicableRegionSet regions = getApplicableRegions(location.getWorld(),
				BlockVectorUtils.locationToVector(location));

		if (regions == null) {
			return null;
		}

		return regions.getRegions().stream().map(region -> protectionByRegion.get(region.getId()))
				.filter(prot -> prot != null && prot.isProtectionBlock(location.getBlock())).findFirst().orElse(null);
	}

	private ApplicableRegionSet getApplicableRegions(World world, BlockVector3 vector) {
		if (!protectionsByWorld.containsKey(world.getName())) {
			return null;
		}

		RegionManager regionManager;

		try {
			regionManager = MainPluginClass.getWorldGuardAPI().getInternalWorldGuard().getRegionManager(world);
		} catch (Exception e) {
			return null;
		}

		return regionManager.getApplicableRegions(vector);
	}

}
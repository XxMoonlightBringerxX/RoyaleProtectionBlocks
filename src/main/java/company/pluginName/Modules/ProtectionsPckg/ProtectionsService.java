package company.pluginName.Modules.ProtectionsPckg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import company.pluginName.Permissions;
import company.pluginName.APIs.WorldGuard.WorldGuardAPI;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.SQLPckg.SQLService;
import company.pluginName.Utils.BlockVectorUtils;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import lombok.Getter;

@PandaService
public class ProtectionsService {

	@PandaInject
	private SQLService sqlService;

	@PandaInject
	private ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private WorldGuardAPI worldGuardApi;

	private @Getter HashMap<UUID, List<Protection>> protectionsByOwner = new HashMap<>();
	private @Getter HashMap<String, List<Protection>> protectionsByWorld = new HashMap<>();
	private @Getter HashMap<String, Protection> protectionByRegion = new HashMap<>();

	@LoadMethod
	private void load() {
		sqlService.getProtections().stream().filter(protection -> {
			if (Bukkit.getWorld(protection.getWorldName()) != null && protection.getProtectedRegion() == null) {
				try {
					MessageTemplate
							.inst(PandaPrefixedStringField
									.applyPrefix("&7Removing protection '" + protection.getRegionId()
											+ "' as it couldn't be found on '" + protection.getWorldName() + "'"))
							.process().sendMessage(Bukkit.getConsoleSender());
					removeProtection(protection);
				} catch (RoyaleProtectionBlocksException e) {
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
	}

	@UnloadMethod
	private void unload() {
		this.protectionsByWorld.values().forEach(list -> list.forEach(protection -> {
			if (protection.isProtectionViewActive()) {
				protection.toggleProtectionView();
			}
		}));
		this.protectionsByOwner.clear();
		this.protectionsByWorld.clear();
		this.protectionByRegion.clear();
	}

	public List<Protection> getAllowedProtections(OfflinePlayer pl) {
		return protectionByRegion.values().stream().filter(prot -> prot.getOwners().list().contains(pl.getUniqueId())
				|| prot.getMembers().list().contains(pl.getUniqueId())).collect(Collectors.toList());
	}

	/*
	 * Create methods
	 */

	public Protection createProtection(Player pl, ProtectionBlock protectionBlock, Location location)
			throws RoyaleProtectionBlocksException {
		if (!pl.hasPermission(Permissions.PROTECTION_MAX_BYPASS)) {
			Integer perBlockMaxCapacity = Permissions.getPerBlockMaxCapacity(pl, protectionBlock);

			if (perBlockMaxCapacity != null) {
				if (perBlockMaxCapacity <= protectionsByOwner.getOrDefault(pl.getUniqueId(), Collections.emptyList())
						.stream().filter(protection -> protection.getProtectionBlock().getIdentifier()
								.equals(protectionBlock.getInformation().getId()))
						.count()) {
					throw Exceptions.Protections.Save.MAXREACHED.generateException();
				}
			} else {
				Integer generalMaxCapacity = Permissions.getGeneralMaxCapacity(pl);
				if (generalMaxCapacity != null) {
					if (generalMaxCapacity <= protectionsByOwner.getOrDefault(pl.getUniqueId(), new ArrayList<>())
							.size()) {
						throw Exceptions.Protections.Save.MAXREACHED.generateException();
					}
				} else {
					throw Exceptions.Protections.Save.MAXREACHED.generateException();
				}
			}
		}

		return this.createProtection(pl.getUniqueId(), pl, protectionBlock, location);
	}

	public Protection createProtection(UUID ownerUuid, Player creator, ProtectionBlock protectionBlock,
			Location location) throws RoyaleProtectionBlocksException {
		for (Protection prot : protectionByRegion.values()) {
			if (prot.getProtectionBlockLocation().equals(location.getBlock().getLocation())) {
				throw Exceptions.Protections.Save.ALREADYOCCUPIED.generateException();
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
	 * Remove methods
	 */

	public void registerProtection(Protection protection) {
		protectionByRegion.put(protection.getRegionId(), protection);
		protectionsByOwner.putIfAbsent(protection.getOwnerUuid(), new ArrayList<>());
		protectionsByOwner.get(protection.getOwnerUuid()).add(protection);
		protectionsByWorld.putIfAbsent(protection.getProtectionBlockLocation().getWorld().getName(), new ArrayList<>());
		protectionsByWorld.get(protection.getProtectionBlockLocation().getWorld().getName()).add(protection);
	}

	public void unregisterProtection(Protection protection) {
		protectionByRegion.remove(protection.getRegionId());
		if (protectionsByOwner.containsKey(protection.getOwnerUuid())) {
			protectionsByOwner.get(protection.getOwnerUuid()).remove(protection);
		}
		if (protectionsByWorld.containsKey(protection.getWorldName())) {
			protectionsByWorld.get(protection.getWorldName()).remove(protection);
		}
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
			regionManager = worldGuardApi.getHook().getInternalWorldGuard().getRegionManager(world);
		} catch (Exception e) {
			return null;
		}

		return regionManager.getApplicableRegions(vector);
	}

}
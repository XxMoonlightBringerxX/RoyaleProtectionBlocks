package company.pluginName.Modules.ProtectionsPckg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

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
import company.pluginName.Exceptions.ProtectionBlocks.Delete.ProtectionBlocksDeleteDeniedException;
import company.pluginName.Exceptions.ProtectionBlocks.Delete.ProtectionBlocksDeleteException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveDeniedException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveNameInUseException;
import company.pluginName.Exceptions.ProtectionMembers.Delete.ProtectionMembersDeleteDeniedException;
import company.pluginName.Exceptions.ProtectionMembers.Delete.ProtectionMembersDeleteException;
import company.pluginName.Exceptions.ProtectionMembers.Save.ProtectionMembersSaveCannotAddProtectionOwnerException;
import company.pluginName.Exceptions.ProtectionMembers.Save.ProtectionMembersSaveCannotAddYourselfException;
import company.pluginName.Exceptions.ProtectionMembers.Save.ProtectionMembersSaveDeniedException;
import company.pluginName.Exceptions.ProtectionMembers.Save.ProtectionMembersSaveException;
import company.pluginName.Exceptions.ProtectionOwners.Delete.ProtectionOwnersDeleteCannotDeleteProtectionOwnerException;
import company.pluginName.Exceptions.ProtectionOwners.Delete.ProtectionOwnersDeleteDeniedException;
import company.pluginName.Exceptions.ProtectionOwners.Delete.ProtectionOwnersDeleteException;
import company.pluginName.Exceptions.ProtectionOwners.Save.ProtectionOwnersSaveCannotAddProtectionOwnerException;
import company.pluginName.Exceptions.ProtectionOwners.Save.ProtectionOwnersSaveCannotAddYourselfException;
import company.pluginName.Exceptions.ProtectionOwners.Save.ProtectionOwnersSaveDeniedException;
import company.pluginName.Exceptions.ProtectionOwners.Save.ProtectionOwnersSaveException;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.Utils.BlockVectorUtils;
import lombok.Getter;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotMessages.Objects.TextInput;
import relampagorojo93.LibsCollection.SpigotPlugin.LoadOn;
import relampagorojo93.LibsCollection.SpigotPlugin.PluginModule;

public class ProtectionsModule implements PluginModule {

	private @Getter HashMap<String, ProtectionBlock> protectionBlockById = new HashMap<>();

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
		MainPluginClass.getPlugin().getSqlModule().getProtectionBlocks()
				.forEach(block -> protectionBlockById.put(block.getId().toLowerCase(), block));

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
		protectionBlockById.clear();
		protectionsByOwner.clear();
		protectionsByWorld.clear();
		protectionByRegion.clear();
		return true;
	}

	public ProtectionBlock getProtectionBlockById(String id) {
		return protectionBlockById.get(id.toLowerCase());
	}

	public List<Protection> getAllowedProtections(Player pl) {
		return protectionByRegion.values().stream().filter(prot -> prot.isOwner(pl.getUniqueId()))
				.collect(Collectors.toList());
	}

	/*
	 * Create methods
	 */

	public Protection createProtection(ProtectionBlock protectionBlock, Location location)
			throws ProtectionSaveException {
		return createProtection(null, protectionBlock, location);
	}

	public Protection createProtection(Player pl, ProtectionBlock protectionBlock, Location location)
			throws ProtectionSaveException {
		if (pl != null) {
			if (!pl.hasPermission(Permissions.PROTECTION_MAX_BYPASS)) {
				if (Permissions.getMaxCapacity(pl) <= protectionsByOwner
						.getOrDefault(pl.getUniqueId(), new ArrayList<>()).size()) {
					throw new ProtectionSaveMaxReachedException();
				}
			}
		}

		for (Protection prot : protectionByRegion.values()) {
			if (prot.getProtectionBlockLocation().equals(location.getBlock().getLocation())) {
				throw new ProtectionSaveAlreadyOccupiedException();
			}
		}

		Protection protection = new Protection(pl.getUniqueId());

		protection.create(location, protectionBlock);

		protectionByRegion.put(protection.getRegionId(), protection);
		protectionsByOwner.putIfAbsent(pl.getUniqueId(), new ArrayList<>());
		protectionsByOwner.get(pl.getUniqueId()).add(protection);
		protectionsByWorld.putIfAbsent(location.getWorld().getName(), new ArrayList<>());
		protectionsByWorld.get(location.getWorld().getName()).add(protection);

		return protection;
	}

	public void createProtectionBlock(ProtectionBlock protectionBlock) throws ProtectionBlocksSaveException {
		createProtectionBlock(null, protectionBlock);
	}

	public void createProtectionBlock(Player pl, ProtectionBlock protectionBlock) throws ProtectionBlocksSaveException {
		if (pl != null) {
			if (!pl.hasPermission(Permissions.PROTECTION_BLOCKS_CREATE)) {
				throw new ProtectionBlocksSaveDeniedException();
			}
		}

		if (protectionBlockById.containsKey(protectionBlock.getId().toLowerCase())) {
			throw new ProtectionBlocksSaveNameInUseException();
		}

		protectionBlock.save();

		protectionBlockById.put(protectionBlock.getId().toLowerCase(), protectionBlock);
	}

	/*
	 * Rename methods
	 */

	public void renameProtection(Protection protection, String newName) throws ProtectionSaveException {
		renameProtection(null, protection, newName);
	}

	public void renameProtection(Player pl, Protection protection, String newName) throws ProtectionSaveException {
		if (pl != null) {
			if (!protection.isOwner(pl.getUniqueId()) && !pl.hasPermission(Permissions.PROTECTION_RENAME_OTHERS)) {
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
	 * Add methods
	 */

	public void addMember(Protection protection, UUID member) throws ProtectionMembersSaveException {
		addMember(null, protection, member);
	}

	public void addMember(Player pl, Protection protection, UUID member) throws ProtectionMembersSaveException {
		if (pl != null) {
			if (!protection.isOwner(pl.getUniqueId()) && !pl.hasPermission(Permissions.PROTECTION_MEMBERS_ADD_OTHERS)) {
				throw new ProtectionMembersSaveDeniedException();
			}

			if (member.equals(pl.getUniqueId())) {
				throw new ProtectionMembersSaveCannotAddYourselfException();
			}
		}

		if (protection.isMainOwner(member)) {
			throw new ProtectionMembersSaveCannotAddProtectionOwnerException();
		}

		protection.getProtectedRegion().getMembers().addPlayer(member);
	}

	public void addOwner(Protection protection, UUID member) throws ProtectionOwnersSaveException {
		addOwner(null, protection, member);
	}

	public void addOwner(Player pl, Protection protection, UUID owner) throws ProtectionOwnersSaveException {
		if (pl != null) {
			if (!protection.isMainOwner(pl.getUniqueId())
					&& !pl.hasPermission(Permissions.PROTECTION_OWNERS_ADD_OTHERS)) {
				throw new ProtectionOwnersSaveDeniedException();
			}

			if (owner.equals(pl.getUniqueId())) {
				throw new ProtectionOwnersSaveCannotAddYourselfException();
			}
		}

		if (protection.isMainOwner(owner)) {
			throw new ProtectionOwnersSaveCannotAddProtectionOwnerException();
		}

		protection.getProtectedRegion().getOwners().addPlayer(owner);
	}

	/*
	 * Remove methods
	 */

	public void removeProtection(Protection protection) throws ProtectionDeleteException {
		removeProtection(null, protection);
	}

	public void removeProtection(Player pl, Protection protection) throws ProtectionDeleteException {
		if (pl != null) {
			if (!protection.getOwnerUuid().equals(pl.getUniqueId())
					&& !pl.hasPermission(Permissions.PROTECTION_DELETE_OTHERS)) {
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

	public void removeProtectionBlock(ProtectionBlock protectionBlock) throws ProtectionBlocksDeleteException {
		removeProtectionBlock(null, protectionBlock);
	}

	public void removeProtectionBlock(Player pl, ProtectionBlock protectionBlock)
			throws ProtectionBlocksDeleteException {
		if (pl != null) {
			if (!pl.hasPermission(Permissions.PROTECTION_BLOCKS_DELETE)) {
				throw new ProtectionBlocksDeleteDeniedException();
			}
		}

		protectionBlock.delete();

		protectionBlockById.remove(protectionBlock.getId());
	}

	public void removeMember(Protection protection, UUID member) throws ProtectionMembersDeleteException {
		removeMember(null, protection, member);
	}

	public void removeMember(Player pl, Protection protection, UUID member) throws ProtectionMembersDeleteException {
		if (pl != null) {
			if (!protection.isOwner(pl.getUniqueId())
					&& !pl.hasPermission(Permissions.PROTECTION_MEMBERS_REMOVE_OTHERS)) {
				throw new ProtectionMembersDeleteDeniedException();
			}
		}

		protection.getProtectedRegion().getMembers().removePlayer(member);
	}

	public void removeOwner(Protection protection, UUID member) throws ProtectionOwnersDeleteException {
		removeOwner(null, protection, member);
	}

	public void removeOwner(Player pl, Protection protection, UUID member) throws ProtectionOwnersDeleteException {
		if (pl != null) {
			if (!protection.isMainOwner(pl.getUniqueId())
					&& !pl.hasPermission(Permissions.PROTECTION_OWNERS_REMOVE_OTHERS)) {
				throw new ProtectionOwnersDeleteDeniedException();
			}
		}

		if (protection.isMainOwner(member)) {
			throw new ProtectionOwnersDeleteCannotDeleteProtectionOwnerException();
		}

		protection.getProtectedRegion().getOwners().removePlayer(member);
	}

	/*
	 * Search methods
	 */

	public Protection getProtectionByBlock(Location location) {
		Protection protection = getProtectionByLocation(location);

		return protection != null && protection.isProtectionBlock(location.getBlock()) ? protection : null;
	}

	public Protection getProtectionByLocation(Location location) {
		ProtectedRegion region = getRegionByLocation(location.getWorld(), BlockVectorUtils.locationToVector(location));

		return region != null ? protectionByRegion.get(region.getId()) : null;
	}

	private ProtectedRegion getRegionByLocation(World world, BlockVector3 vector) {
		if (!protectionsByWorld.containsKey(world.getName())) {
			return null;
		}

		RegionManager regionManager;

		try {
			regionManager = MainPluginClass.getWorldGuardAPI().getInternalWorldGuard().getRegionManager(world);
		} catch (Exception e) {
			return null;
		}

		ApplicableRegionSet regions = regionManager.getApplicableRegions(vector);

		return regions.getRegions().stream().filter(region -> protectionByRegion.containsKey(region.getId()))
				.findFirst().orElse(null);
	}

}
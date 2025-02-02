package company.pluginName.Modules.ProtectionsPckg.Utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.ItemsAdderAPI.ItemsAdderAPI;
import company.pluginName.Hooks.ItemsAdderAPI.Hook.ItemsAdderHook;
import company.pluginName.Hooks.OraxenAPI.OraxenAPI;
import company.pluginName.Hooks.OraxenAPI.Hook.OraxenHook;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsServiceImpl;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.Blocks.BlockUtilities;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.SkinUtilities;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaIntegerField;
import darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects.PandaParametizedPermission.Parameter;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.ProtectionBlocks.IProtectionBlock;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.Protections.Objects.ProtectionCreationData;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.Protections.Objects.ProtectionRemovalData;

public class ProtectionUtilities {

	@RegisteredPandaField("config")
	public static PandaIntegerField SETTINGS_PROTECTION_MINIMUMDISTANCEBETWEENPROTECTIONS = new PandaIntegerField(
			"Settings.Protection.Minimum-distance-between-protections", -1);

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	@PandaInject
	private static ItemsAdderAPI itemsAdderApi;

	@PandaInject
	private static OraxenAPI oraxenApi;

	@PandaInject
	private static ProtectionsServiceImpl protectionsServiceImpl;

	public static String generateDefaultName(Location location) {
		return location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_"
				+ location.getBlockZ();
	}

	public static void showBlock(Block block, ItemStack item) {
		ItemsAdderHook.PlaceResult itemsAdderResult = itemsAdderApi.getHook().setBlock(item, block.getLocation());

		if (itemsAdderResult != ItemsAdderHook.PlaceResult.NOT_HOOKED) {
			if (itemsAdderResult == ItemsAdderHook.PlaceResult.PLACED) {
				return;
			}
		}

		OraxenHook.PlaceResult oraxenResult = oraxenApi.getHook().setBlock(item, block.getLocation());

		if (oraxenResult != OraxenHook.PlaceResult.NOT_HOOKED) {
			if (oraxenResult == OraxenHook.PlaceResult.PLACED) {
				return;
			}
		}

		block.setType(item.getType());
		if (item.getType() == Material.PLAYER_HEAD.getMaterial()) {
			BlockUtilities.setSkin(block, SkinUtilities.NMS.getSkinAsBase64(item));
		}
	}

	public static void hideBlock(Block block) {
		ItemsAdderHook.PlaceResult itemsAdderResult = itemsAdderApi.getHook().setBlock(null, block.getLocation());

		if (itemsAdderResult != ItemsAdderHook.PlaceResult.NOT_HOOKED) {
			if (itemsAdderResult == ItemsAdderHook.PlaceResult.PLACED) {
				return;
			}
		}

		OraxenHook.PlaceResult oraxenResult = oraxenApi.getHook().setBlock(null, block.getLocation());

		if (oraxenResult != OraxenHook.PlaceResult.NOT_HOOKED) {
			if (oraxenResult == OraxenHook.PlaceResult.PLACED) {
				return;
			}
		}

		block.setType(Material.AIR.getMaterial());
	}

	public static boolean canManage(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || protection.isOwner(pl.getUniqueId())
				|| PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canDelete(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.DELETE_OTHERS.hasPermission(pl);
	}

	public static boolean canChangeId(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canRename(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canChangeDisplayItem(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canTogglePublicAccess(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canSwitchSettings(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.MANAGE_OTHERS.hasPermission(pl);
	}

	public static boolean canAddOwner(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.OWNERS_ADD_OTHERS.hasPermission(pl);
	}

	public static boolean canRemoveOwner(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.OWNERS_REMOVE_OTHERS.hasPermission(pl);
	}

	public static boolean canAddMember(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || protection.isOwner(pl.getUniqueId())
				|| PermissionsService.MEMBERS_ADD_OTHERS.hasPermission(pl);
	}

	public static boolean canRemoveMember(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || protection.isOwner(pl.getUniqueId())
				|| PermissionsService.MEMBERS_REMOVE_OTHERS.hasPermission(pl);
	}

	public static boolean canAddBanned(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || protection.isOwner(pl.getUniqueId())
				|| PermissionsService.BANNEDS_ADD_OTHERS.hasPermission(pl);
	}

	public static boolean canRemoveBanned(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || protection.isOwner(pl.getUniqueId())
				|| PermissionsService.BANNEDS_REMOVE_OTHERS.hasPermission(pl);
	}

	public static boolean canSeeInformation(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || protection.isOwner(pl.getUniqueId())
				|| PermissionsService.INFO_OTHERS.hasPermission(pl);
	}

	public static boolean canKick(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || protection.isOwner(pl.getUniqueId())
				|| PermissionsService.KICK_OTHERS.hasPermission(pl);
	}

	public static boolean canSetHome(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || protection.isOwner(pl.getUniqueId());
	}

	public static boolean canChangePriority(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || protection.isOwner(pl.getUniqueId())
				|| PermissionsService.PRIORITY_OTHERS.hasPermission(pl);
	}

	public static boolean canMerge(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.MERGE_OTHERS.hasPermission(pl);
	}

	public static boolean canSplit(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.SPLIT_OTHERS.hasPermission(pl);
	}

	public static boolean canHideBlock(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || protection.isOwner(pl.getUniqueId())
				|| PermissionsService.HIDE_OTHERS.hasPermission(pl);
	}

	public static boolean canShowBlock(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || protection.isOwner(pl.getUniqueId())
				|| PermissionsService.SHOW_OTHERS.hasPermission(pl);
	}

	public static boolean canTransfer(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.TRANSFER_OTHERS.hasPermission(pl);
	}

	public static boolean canSell(IProtection protection, Player pl) {
		return protection.isMainOwner(pl.getUniqueId()) || PermissionsService.SELL_OTHERS.hasPermission(pl);
	}

	public static void checkCreationConditions(Protection protection, ProtectionCreationData protectionData)
			throws RoyaleProtectionBlocksExceptionImpl {
		IProtection registeredProtection = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
				.findProtectionBySourceLocation(protection.getBukkitLocation());

		if (registeredProtection != null && registeredProtection != protection) {
			throw Exceptions.Protections.Save.ALREADYOCCUPIED.generateException();
		}

		int offset = SETTINGS_PROTECTION_MINIMUMDISTANCEBETWEENPROTECTIONS.getContent();

		Location minLocationWithOffset = offset > 0
				? protection.getUtils().getProtectionArea().getMinLocation().toLocation().clone().add(-offset, -offset,
						-offset)
				: protection.getUtils().getProtectionArea().getMinLocation().toLocation();
		Location maxLocationWithOffset = (offset > 0
				? protection.getUtils().getProtectionArea().getMaxLocation().toLocation().clone().add(offset, offset,
						offset)
				: protection.getUtils().getProtectionArea().getMaxLocation().toLocation());

		if (protectionData.isCheckOverlap()) {
			if (protectionsServiceImpl.findProtectionsByArea(minLocationWithOffset, maxLocationWithOffset, false)
					.anyMatch(prot -> !prot.isMainOwner(protection.getOwnerUuid())
							|| !Settings.SETTINGS_PROTECTION_ALLOWREGIONSINSIDEANOTHERFROMSAMEOWNER.getContent())) {
				throw offset < 0 ? Exceptions.Protections.Save.OVERLAPS.generateException()
						: Exceptions.Protections.Save.OVERLAPSOFFSET.generateException()
								.setReplacements(new Replacement("%blocks%", () -> String.valueOf(offset)));
			}
		}

		if (protectionData.isCheckWorldGuardOverlap()) {
			RegionManager regionManager = worldGuardApi.getHook().getInternalWorldGuard()
					.getRegionManagerSafely(protection.getWorldName());

			if (regionManager == null) {
				throw Exceptions.Protections.Save.UNKNOWN.generateException(new Exception("Region Manager is"));
			}

			ProtectedRegion protectedRegion = new ProtectedCuboidRegion(protection.getProtectionId(),
					BlockVector3.at(minLocationWithOffset.getBlockX(), minLocationWithOffset.getBlockY(),
							minLocationWithOffset.getBlockZ()),
					BlockVector3.at(maxLocationWithOffset.getBlockX(), maxLocationWithOffset.getBlockY(),
							maxLocationWithOffset.getBlockZ()));

			ApplicableRegionSet set = regionManager.getApplicableRegions(protectedRegion);

			if (set.getRegions().stream().anyMatch(pr -> RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
					.findProtectionById(pr.getId()) == null)) {
				throw Exceptions.Protections.Save.OVERLAPSWORLDGUARD.generateException();
			}
		}
	}

	public static void checkRemovalConditions(Protection protection, ProtectionRemovalData protectionData)
			throws RoyaleProtectionBlocksExceptionImpl {
		if (protection.isDeleted()) {
			throw Exceptions.Protections.Delete.ALREADYDELETED.generateException();
		}
	}

	public static void checkIfCanBeAdded(Player player, IProtectionBlock protectionBlock, int amountToAdd)
			throws RoyaleProtectionBlocksExceptionImpl {
		if (!PermissionsService.MAX_BYPASS.hasPermission(player)) {
			Integer generalMaxCapacity = PermissionsService.getGeneralMaxCapacity(player);

			if (generalMaxCapacity != null) {
				Integer ownedProtections = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
						.findProtectionsByOwner(player.getUniqueId()).size();

				if (generalMaxCapacity < (ownedProtections + amountToAdd)) {
					throw Exceptions.Protections.Save.MAXREACHED.generateException();
				}
			} else {
				throw Exceptions.Protections.Save.MAXREACHED.generateException();
			}
		}

		if (!PermissionsService.BLOCK_MAX_BYPASS.hasPermission(player,
				Parameter.of("block", protectionBlock.getId()))) {
			Integer blockMaxCapacity = PermissionsService.getPerBlockMaxCapacity(player, protectionBlock);

			if (blockMaxCapacity != null) {
				Long blockOwnedProtections = RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
						.findProtectionsByOwner(player.getUniqueId()).stream()
						.filter(prot -> prot.getProtectionBlockId().equals(protectionBlock.getId())).count();

				if (blockMaxCapacity != null && blockMaxCapacity < (blockOwnedProtections + amountToAdd)) {
					throw Exceptions.Protections.Save.BLOCKMAXREACHED.generateException();
				}
			}
		}
	}

}

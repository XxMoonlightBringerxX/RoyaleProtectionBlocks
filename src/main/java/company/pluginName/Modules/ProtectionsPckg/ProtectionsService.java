package company.pluginName.Modules.ProtectionsPckg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionFlagsPckg.Utils.ProtectionFlagUtilities;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionUtils.SimpleLocation.SimpleLocationArea;
import company.pluginName.Modules.SQLPckg.SQLService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import lombok.Getter;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;

@PandaService
public class ProtectionsService {

	@RegisteredPandaField("config")
	private static final PandaBooleanField SETTINGS_PROTECTION_ALLOWMULTITHREADSEARCHING = new PandaBooleanField(
			"Settings.Protection.Allow-multi-thread-searching", false);

	@PandaInject
	private MainPluginClass plugin;

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
			if (Bukkit.getWorld(protection.getWorldName()) == null) {
				return false;
			}

			if (protection.getProtectedRegion() == null) {
				try {
					protection.regenerateProtectedRegion();
				} catch (RoyaleProtectionBlocksExceptionImpl e) {
					MessageTemplate
							.inst(PandaPrefixedStringField
									.applyPrefix("&7Removing protection '" + protection.getRegionId()
											+ "' as it couldn't be found on '" + protection.getWorldName()
											+ "' and were problems trying to regenerate the region."))
							.process().sendMessage(Bukkit.getConsoleSender());

					if (protection.getUtils().isProtectionBlockShown()) {
						protection.getUtils().hideProtectionBlock();
					}

					if (protection.getBoundaries().isProtectionViewActive()) {
						protection.getBoundaries().toggleProtectionView();
					}

					try {
						protection.delete(RemovalCause.REMOVE).subscribe();
					} catch (RoyaleProtectionBlocksExceptionImpl e1) {
						e1.sendError(Bukkit.getConsoleSender());
						return false;
					}
				}
			}

			Set<String> banneds = protection.getWorldGuardBanneds().get();

			if (!banneds.isEmpty()) {
				try {
					sqlService.saveProtectionBanneds(protection,
							banneds.stream().map(UUID::fromString).collect(Collectors.toList()));
					ProtectionFlagUtilities.setValue(protection.getProtectedRegion(),
							worldGuardApi.getHook().getBannedPlayersFlag().getWorldGuardFlag(), null);
				} catch (RoyaleProtectionBlocksExceptionImpl e) {
					e.sendError(Bukkit.getConsoleSender());
				}
			}

			return true;
		}).forEach(this::registerProtection);

		sqlService.getProtectionBanneds().forEach((id, banneds) -> {
			Protection protection = findProtectionById(id);
			if (protection != null) {
				protection.setBanneds(banneds);
			}
		});

		plugin.sendDebug(getClass(),
				String.format("Loaded a total amount of '%d' protection(s)", this.protectionByRegion.size()));
	}

	@UnloadMethod
	private void unload() {
		this.protectionsByWorld.values().forEach(list -> list.forEach(protection -> {
			if (protection.getBoundaries().isProtectionViewActive()) {
				protection.getBoundaries().toggleProtectionView();
			}
		}));
		this.protectionsByOwner.clear();
		this.protectionsByWorld.clear();
		this.protectionByRegion.clear();
	}

	/*
	 * Register/Unregister methods
	 */

	public synchronized void registerProtection(Protection protection) {
		protectionByRegion.put(protection.getRegionId(), protection);
		protectionsByOwner.computeIfAbsent(protection.getOwnerUuid(), (ownerUuid) -> new ArrayList<>()).add(protection);
		protectionsByWorld.computeIfAbsent(protection.getWorldName(), (worldName) -> new ArrayList<>()).add(protection);
	}

	public synchronized void unregisterProtection(Protection protection) {
		protectionByRegion.remove(protection.getRegionId());
		protectionsByOwner.computeIfAbsent(protection.getOwnerUuid(), (ownerUuid) -> new ArrayList<>())
				.remove(protection);
		protectionsByWorld.computeIfAbsent(protection.getWorldName(), (worldName) -> new ArrayList<>())
				.remove(protection);
	}

	/*
	 * Search methods
	 */

	public Protection findProtectionById(String id) {
		return this.protectionByRegion.get(id);
	}

	public Protection findProtectionByLocation(Location location) {
		return findProtectionsByLocation(location).findFirst().orElse(null);
	}

	public Stream<Protection> findProtectionsByLocation(Location location) {
		return findProtectionsByLocation(location, true);
	}

	public Stream<Protection> findProtectionsByLocation(Location location, boolean includeBorder) {
		return findProtectionsByArea(location, location, includeBorder);
	}

	public Stream<Protection> findProtectionsByArea(Location location1, Location location2) {
		return findProtectionsByArea(location1, location2, true);
	}

	public Stream<Protection> findProtectionsByArea(Location location1, Location location2, boolean includeBorder) {
		SimpleLocationArea locationArea = SimpleLocationArea.of(location1, location2);
		return StreamSupport
				.stream(protectionsByWorld.getOrDefault(location1.getWorld().getName(), Collections.emptyList())
						.spliterator(), SETTINGS_PROTECTION_ALLOWMULTITHREADSEARCHING.isTrue())
				.filter((prot) -> prot.getUtils().isInside(locationArea, includeBorder))
				.sorted((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority()));
	}

	public Protection findProtectionParentByLocation(Location location) {
		return findProtectionParentsByLocation(location).findFirst().orElse(null);
	}

	public Stream<Protection> findProtectionParentsByLocation(Location location) {
		return findProtectionParentsByLocation(location, true);
	}

	public Stream<Protection> findProtectionParentsByLocation(Location location, boolean includeBorder) {
		return findProtectionParentsByArea(location, location, includeBorder);
	}

	public Stream<Protection> findProtectionParentsByArea(Location location1, Location location2) {
		return findProtectionParentsByArea(location1, location2, true);
	}

	public Stream<Protection> findProtectionParentsByArea(Location location1, Location location2,
			boolean includeBorder) {
		SimpleLocationArea locationArea = SimpleLocationArea.of(location1, location2);
		return StreamSupport
				.stream(protectionsByWorld.getOrDefault(location1.getWorld().getName(), Collections.emptyList())
						.spliterator(), SETTINGS_PROTECTION_ALLOWMULTITHREADSEARCHING.isTrue())
				.filter((prot) -> prot.getParentProtection() == prot
						&& prot.getUtils().isInsideAny(locationArea, includeBorder))
				.sorted((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority()));
	}

	public Stream<Protection> getAllowedProtections(OfflinePlayer pl) {
		return StreamSupport
				.stream(protectionByRegion.values().spliterator(),
						SETTINGS_PROTECTION_ALLOWMULTITHREADSEARCHING.isTrue())
				.filter(prot -> prot.getWorldGuardOwners().list().contains(pl.getUniqueId())
						|| prot.getWorldGuardMembers().list().contains(pl.getUniqueId()));
	}

	public Protection findProtectionBySourceLocation(Location sourceLocation) {
		return StreamSupport
				.stream(protectionByRegion.values().spliterator(),
						SETTINGS_PROTECTION_ALLOWMULTITHREADSEARCHING.isTrue())
				.filter(prot -> prot.getWorldName().equals(sourceLocation.getWorld().getName())
						&& prot.getLocation().distance(sourceLocation) < 1D)
				.findFirst().orElse(null);
	}

	public Protection findProtectionBySourceBlock(Block sourceBlock) {
		return StreamSupport
				.stream(protectionByRegion.values().spliterator(),
						SETTINGS_PROTECTION_ALLOWMULTITHREADSEARCHING.isTrue())
				.filter(prot -> prot.getUtils().isProtectionBlock(sourceBlock)).findFirst().orElse(null);
	}

	public List<Protection> findProtectionsByOwner(UUID owner) {
		return this.protectionsByOwner.getOrDefault(owner, Collections.emptyList());
	}

}
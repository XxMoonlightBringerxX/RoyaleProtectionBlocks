package company.pluginName.Modules.ProtectionsPckg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation.SimpleLocationArea;

@PandaService
public class ProtectionsService {

	@RegisteredPandaField("config")
	public static final PandaBooleanField SETTINGS_PROTECTION_ALLOWMULTITHREADSEARCHING = new PandaBooleanField(
			"Settings.Protection.Allow-multi-thread-searching", false);

	@PandaInject
	private MainPluginClass plugin;

	@PandaInject
	private SQLService sqlService;

	@PandaInject
	private ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private WorldGuardAPI worldGuardApi;

	private @Getter HashMap<UUID, List<Protection>> protectionsByOwner = new LinkedHashMap<>();
	private @Getter HashMap<String, List<Protection>> protectionsByWorld = new LinkedHashMap<>();
	private @Getter HashMap<String, Protection> protectionByRegion = new LinkedHashMap<>();

	private final Consumer<Protection> registerProtectionFunction = (protection) -> {
		protectionByRegion.put(protection.getRegionId().toLowerCase(), protection);
		protectionsByOwner.computeIfAbsent(protection.getOwnerUuid(), (ownerUuid) -> new ArrayList<>()).add(protection);
		protectionsByWorld.computeIfAbsent(protection.getWorldName(), (worldName) -> new ArrayList<>()).add(protection);
	};

	private final Consumer<Protection> unregisterProtectionFunction = (protection) -> {
		protectionByRegion.remove(protection.getRegionId().toLowerCase());
		protectionsByOwner.computeIfAbsent(protection.getOwnerUuid(), (ownerUuid) -> new ArrayList<>())
				.remove(protection);
		protectionsByWorld.computeIfAbsent(protection.getWorldName(), (worldName) -> new ArrayList<>())
				.remove(protection);
	};

	@LoadMethod
	private void load() {
		AtomicInteger regeneratedRegions = new AtomicInteger();

		sqlService.getProtections().stream().filter(protection -> {
			if (Bukkit.getWorld(protection.getWorldName()) != null) {
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
							protection.delete(RemovalCause.PLAYER).subscribe();
						} catch (RoyaleProtectionBlocksExceptionImpl e1) {
							e1.sendError(Bukkit.getConsoleSender());
							return false;
						}
					}
				}

				if (protection.getProtectedRegion().getMaximumPoint()
						.getX() != protection.getUtils().getProtectionArea().getMaxLocation().getX() - 1
						|| protection.getProtectedRegion().getMaximumPoint()
								.getY() != protection.getUtils().getProtectionArea().getMaxLocation().getY() - 1
						|| protection.getProtectedRegion().getMaximumPoint()
								.getZ() != protection.getUtils().getProtectionArea().getMaxLocation().getZ() - 1
						|| protection.getProtectedRegion().getMinimumPoint().getX() != protection.getUtils()
								.getProtectionArea().getMinLocation().getX()
						|| protection.getProtectedRegion().getMinimumPoint().getY() != protection.getUtils()
								.getProtectionArea().getMinLocation().getY()
						|| protection.getProtectedRegion().getMinimumPoint().getZ() != protection.getUtils()
								.getProtectionArea().getMinLocation().getZ()) {
					try {
						protection.regenerateProtectedRegion();
						regeneratedRegions.incrementAndGet();
					} catch (RoyaleProtectionBlocksExceptionImpl e) {
						e.sendError(Bukkit.getConsoleSender());
					}
				}

				// TODO: Remove this code after some versions as everything should already be
				// parsed to the database.
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
		if (regeneratedRegions.get() > 0) {
			plugin.sendWarning(getClass(), String.format(
					"A total of %d protections didn't have a proper WorldGuard region. This regions have been regenerated.",
					regeneratedRegions.get()));
		}
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
		executeSynchronized(() -> registerProtectionFunction.accept(protection));
	}

	public synchronized void unregisterProtection(Protection protection) {
		executeSynchronized(() -> unregisterProtectionFunction.accept(protection));
	}

	/*
	 * Search methods
	 */

	public Protection findProtectionById(String id) {
		return executeSynchronizedWithReturn(() -> this.protectionByRegion.get(id.toLowerCase()));
	}

	public Protection findProtectionByLocation(Location location) {
		return executeSynchronizedWithReturn(() -> findProtectionsByLocation(location).findFirst().orElse(null));
	}

	public Stream<Protection> findProtectionsByLocation(Location location) {
		return executeSynchronizedWithReturn(() -> findProtectionsByLocation(location, true));
	}

	public Stream<Protection> findProtectionsByLocation(Location location, boolean includeBorder) {
		return executeSynchronizedWithReturn(() -> findProtectionsByArea(location, location, includeBorder));
	}

	public Stream<Protection> findProtectionsByArea(Location location1, Location location2) {
		return executeSynchronizedWithReturn(() -> findProtectionsByArea(location1, location2, true));
	}

	public Stream<Protection> findProtectionsByArea(Location location1, Location location2, boolean includeBorder) {
		SimpleLocationArea locationArea = SimpleLocationArea.of(location1, location2);
		return executeSynchronizedWithReturn(() -> StreamSupport
				.stream(protectionsByWorld.getOrDefault(location1.getWorld().getName(), Collections.emptyList())
						.spliterator(), SETTINGS_PROTECTION_ALLOWMULTITHREADSEARCHING.isTrue())
				.filter((prot) -> !prot.isCreationInProgress() && !prot.isDeleted()
						&& prot.getUtils().isInside(locationArea, includeBorder))
				.sorted((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority())));
	}

	public Protection findProtectionParentByLocation(Location location) {
		return executeSynchronizedWithReturn(() -> findProtectionParentsByLocation(location).findFirst().orElse(null));
	}

	public Stream<Protection> findProtectionParentsByLocation(Location location) {
		return executeSynchronizedWithReturn(() -> findProtectionParentsByLocation(location, true));
	}

	public Stream<Protection> findProtectionParentsByLocation(Location location, boolean includeBorder) {
		return executeSynchronizedWithReturn(() -> findProtectionParentsByArea(location, location, includeBorder));
	}

	public Stream<Protection> findProtectionParentsByArea(Location location1, Location location2) {
		return executeSynchronizedWithReturn(() -> findProtectionParentsByArea(location1, location2, true));
	}

	public Stream<Protection> findProtectionParentsByArea(Location location1, Location location2,
			boolean includeBorder) {
		return executeSynchronizedWithReturn(
				() -> findProtectionParentsByArea(SimpleLocationArea.of(location1, location2), true));
	}

	public Stream<Protection> findProtectionParentsByArea(SimpleLocationArea locationArea) {
		return executeSynchronizedWithReturn(() -> findProtectionParentsByArea(locationArea, true));
	}

	public Stream<Protection> findProtectionParentsByArea(SimpleLocationArea locationArea, boolean includeBorder) {
		return executeSynchronizedWithReturn(() -> StreamSupport
				.stream(protectionsByWorld.getOrDefault(locationArea.getWorldName(), Collections.emptyList())
						.spliterator(), SETTINGS_PROTECTION_ALLOWMULTITHREADSEARCHING.isTrue())
				.filter((prot) -> !prot.isCreationInProgress() && !prot.isDeleted()
						&& (prot.getParentProtection() == prot
								&& prot.getUtils().isInsideAny(locationArea, includeBorder)))
				.sorted((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority())));
	}

	public Stream<Protection> getAllowedProtections(OfflinePlayer pl) {
		return executeSynchronizedWithReturn(() -> StreamSupport
				.stream(protectionByRegion.values().spliterator(),
						SETTINGS_PROTECTION_ALLOWMULTITHREADSEARCHING.isTrue())
				.filter(prot -> !prot.isCreationInProgress() && !prot.isDeleted()
						&& (prot.getWorldGuardOwners().list().contains(pl.getUniqueId())
								|| prot.getWorldGuardMembers().list().contains(pl.getUniqueId()))));
	}

	public Protection findProtectionBySourceLocation(Location sourceLocation) {
		SimpleLocation loc = SimpleLocation.of(sourceLocation.getBlock().getLocation());
		return executeSynchronizedWithReturn(() -> StreamSupport
				.stream(protectionByRegion.values().spliterator(),
						SETTINGS_PROTECTION_ALLOWMULTITHREADSEARCHING.isTrue())
				.filter(prot -> !prot.isCreationInProgress() && !prot.isDeleted() && loc.equals(prot.getLocation()))
				.findFirst().orElse(null));
	}

	public Protection findProtectionBySourceBlock(Block sourceBlock) {
		SimpleLocation loc = SimpleLocation.of(sourceBlock.getLocation());
		Protection foundProt = executeSynchronizedWithReturn(() -> StreamSupport
				.stream(protectionByRegion.values().spliterator(),
						SETTINGS_PROTECTION_ALLOWMULTITHREADSEARCHING.isTrue())
				.filter(prot -> !prot.isCreationInProgress() && !prot.isDeleted() && loc.equals(prot.getLocation()))
				.findFirst().orElse(null));
		return foundProt != null && (foundProt.getUtils().isProtectionBlock(loc)) ? foundProt : null;
	}

	public List<Protection> findProtectionsByOwner(UUID owner) {
		return executeSynchronizedWithReturn(
				() -> this.protectionsByOwner.getOrDefault(owner, Collections.emptyList()));
	}

	/*
	 * Private methods
	 */

	private synchronized <T> T executeSynchronizedWithReturn(Supplier<T> func) {
		return func.get();
	}

	private void executeSynchronized(Runnable func) {
		executeSynchronizedWithReturn(() -> {
			func.run();
			return null;
		});
	}

}
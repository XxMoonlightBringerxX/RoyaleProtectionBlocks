package company.pluginName.Modules.PlaceholdersPckg;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPurgePckg.ProtectionsPurgeService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import darkpanda73.PandaUtils.Utilities.Java.Time.TimeUtilities;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.IProtection;

@PandaService
public class PlaceholdersService {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public static final long TTL_LIMIT = 60 * 1000;

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static ProtectionsPurgeService protectionsPurgeService;

	/*
	 * Player placeholders
	 */

	private static final List<Pair<String, Function<Player, String>>> PLAYER_PLACEHOLDERS = Arrays.asList(
			Pair.of("{player_name}", (player) -> player.getName()),
			Pair.of("{player_current}",
					(player) -> String.valueOf(protectionsService.findProtectionsByOwner(player.getUniqueId()).size())),
			Pair.of("{player_max}", (player) -> PermissionsService.MAX_BYPASS.hasPermission(player) ? "∞"
					: String.valueOf(PermissionsService.getGeneralMaxCapacity(player, 0))));

	/*
	 * Protection placeholders
	 */

	private static final List<Pair<String, Function<IProtection, String>>> PROTECTION_PLACEHOLDERS = Arrays.asList(
			Pair.of("{protection_id}", (protection) -> protection.getRegionId()),
			Pair.of("{protection_name}",
					(protection) -> protection.getDisplayName() != null ? protection.getDisplayName()
							: protection.getRegionId()),
			Pair.of("{protection_owner}", (protection) -> protection.getOwnerName()),
			Pair.of("{protection_owner_last_played}",
					(protection) -> DATE_FORMAT
							.format(new Date(protection.getOwnerOfflinePlayer().isOnline() ? System.currentTimeMillis()
									: protection.getOwnerLastPlayed()))),
			Pair.of("{protection_expires_in}",
					(protection) -> protectionsPurgeService.isRunning()
							? TimeUtilities
									.secondsToString(protectionsPurgeService.calculateExpiresIn(protection) / 1000)
							: "Never"),
			Pair.of("{protection_world}", (protection) -> protection.getBukkitLocation().getWorld().getName()),
			Pair.of("{protection_location_x}",
					(protection) -> String.valueOf(protection.getBukkitLocation().getBlockX())),
			Pair.of("{protection_location_y}",
					(protection) -> String.valueOf(protection.getBukkitLocation().getBlockY())),
			Pair.of("{protection_location_z}",
					(protection) -> String.valueOf(protection.getBukkitLocation().getBlockZ())));

	private HashMap<UUID, Pair<Long, Replacement[]>> playerReplacements = new HashMap<>();
	private HashMap<String, Pair<Long, Replacement[]>> protectionReplacements = new HashMap<>();

	@LoadMethod
	private void load() {
		TasksUtils.executeOnAsyncWithTimer(() -> {
			playerReplacements.entrySet()
					.removeIf(entry -> entry.getValue().getFirst() + TTL_LIMIT < System.currentTimeMillis());
			protectionReplacements.entrySet()
					.removeIf(entry -> entry.getValue().getFirst() + TTL_LIMIT < System.currentTimeMillis());
		}, TTL_LIMIT, TTL_LIMIT);
	}

	public Replacement[] getPlayerReplacements(Player player) {
		return executeSynchronizedWithReturn(() -> {
			Pair<Long, Replacement[]> replacement = this.playerReplacements.computeIfAbsent(player.getUniqueId(),
					(uuid) -> {
						List<Replacement> replacements = PLAYER_PLACEHOLDERS.stream()
								.map(placeholder -> new Replacement(placeholder.getFirst(),
										() -> placeholder.getSecond().apply(player)).cacheText(false))
								.collect(Collectors.toList());
						return Pair.of(null, replacements.toArray(new Replacement[replacements.size()]));
					});

			replacement.setFirst(System.currentTimeMillis());

			return replacement.getSecond();
		});
	}

	public Replacement[] getProtectionReplacements(IProtection protection) {
		return executeSynchronizedWithReturn(() -> {
			Pair<Long, Replacement[]> replacement = this.protectionReplacements
					.computeIfAbsent(protection.getRegionId(), (regionId) -> {
						List<Replacement> replacements = PROTECTION_PLACEHOLDERS.stream()
								.map(placeholder -> new Replacement(placeholder.getFirst(),
										() -> placeholder.getSecond().apply(protection)).cacheText(false))
								.collect(Collectors.toList());
						return Pair.of(null, replacements.toArray(new Replacement[replacements.size()]));
					});

			replacement.setFirst(System.currentTimeMillis());

			return replacement.getSecond();
		});
	}

	/*
	 * Private methods
	 */

	private synchronized <T> T executeSynchronizedWithReturn(Supplier<T> func) {
		return func.get();
	}

	@SuppressWarnings("unused")
	private void executeSynchronized(Runnable func) {
		executeSynchronizedWithReturn(() -> {
			func.run();
			return null;
		});
	}

}

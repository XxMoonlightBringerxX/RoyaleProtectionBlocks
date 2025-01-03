package company.pluginName.Modules.PlaceholdersPckg;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPurgePckg.ProtectionsPurgeService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import darkpanda73.PandaUtils.Utilities.Java.Time.TimeUtilities;
import lombok.AllArgsConstructor;
import lombok.Data;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.ProtectionBlocks.IProtectionBlock;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

@PandaService
public class PlaceholdersService {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public static final long TTL_LIMIT = 60 * 1000;

	private static final Replacement[] EMPTY = new Replacement[0];

	@PandaInject
	private static ProtectionsPurgeService protectionsPurgeService;

	/*
	 * Player placeholders
	 */

	private static final List<Pair<String, Function<Player, String>>> PLAYER_PLACEHOLDERS = Arrays.asList(
			Pair.of("{player_name}", (player) -> player.getName()),
			Pair.of("{player_current}",
					(player) -> String.valueOf(RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
							.findProtectionsByOwner(player.getUniqueId()).size())),
			Pair.of("{player_max}", (player) -> PermissionsService.MAX_BYPASS.hasPermission(player) ? "∞"
					: String.valueOf(PermissionsService.getGeneralMaxCapacity(player, 0))));

	/*
	 * Protection placeholders
	 */

	private static final List<Pair<String, Function<IProtection, String>>> PROTECTION_PLACEHOLDERS = Arrays.asList(
			Pair.of("{protection_id}", (protection) -> protection.getProtectionId()),
			Pair.of("{protection_name}",
					(protection) -> protection.getDisplayName() != null ? protection.getDisplayName()
							: protection.getProtectionId()),
			Pair.of("{protection_owner}", (protection) -> protection.getOwnerName()),
			Pair.of("{protection_owner_last_played}",
					(protection) -> DATE_FORMAT.format(new Date(protection.getOwnerLastPlayed()))),
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
					(protection) -> String.valueOf(protection.getBukkitLocation().getBlockZ())),
			Pair.of("{protection_price}", (protection) -> String.valueOf(protection.getPrice())),
			Pair.of("{protection_owners}", (protection) -> ((Protection) protection).getOwners().size() != 0
					? ((Protection) protection).getOwners().stream().filter(Objects::nonNull).map(owner -> {
						OfflinePlayer ownerPlayer = OfflinePlayerUtilities.getOfflinePlayer(owner);
						return ownerPlayer != null && ownerPlayer.getName() != null ? ownerPlayer.getName() : "???";
					}).collect(Collectors.joining(", "))
					: Messages.MESSAGE_GENERAL_EMPTY.getContent()),
			Pair.of("{protection_members}", (protection) -> ((Protection) protection).getMembers().size() != 0
					? ((Protection) protection).getMembers().stream().filter(Objects::nonNull).map(member -> {
						OfflinePlayer memberPlayer = OfflinePlayerUtilities.getOfflinePlayer(member);
						return memberPlayer != null && memberPlayer.getName() != null ? memberPlayer.getName() : "???";
					}).collect(Collectors.joining(", "))
					: Messages.MESSAGE_GENERAL_EMPTY.getContent()),
			Pair.of("{protection_banneds}", (protection) -> ((Protection) protection).getBanneds().size() != 0
					? ((Protection) protection).getBanneds().stream().map(banned -> {
						OfflinePlayer bannedPlayer = OfflinePlayerUtilities.getOfflinePlayer(banned);
						return bannedPlayer != null && bannedPlayer.getName() != null ? bannedPlayer.getName() : "???";
					}).collect(Collectors.joining(", "))
					: Messages.MESSAGE_GENERAL_EMPTY.getContent()));

	/*
	 * Protection blocks placeholders
	 */

	private static final List<Pair<String, Function<IProtectionBlock, String>>> PROTECTIONBLOCK_PLACEHOLDERS = Arrays
			.asList(Pair.of("{protectionblock_id}", (block) -> block.getId()),
					Pair.of("{protectionblock_blocks_x}",
							(block) -> String.valueOf(String.valueOf((block.getBlocksX() * 2) + 1))),
					Pair.of("{protectionblock_blocks_y}",
							(block) -> String
									.valueOf(block.getBlocksY() == -1 ? Messages.MESSAGE_GENERAL_NOLIMIT.toString()
											: String.valueOf((block.getBlocksY() * 2) + 1))),
					Pair.of("{protectionblock_blocks_z}",
							(block) -> String.valueOf(String.valueOf((block.getBlocksZ() * 2) + 1))));

	private HashMap<UUID, PlaceholdersRequest<Player>> playerReplacements = new HashMap<>();
	private HashMap<String, PlaceholdersRequest<IProtection>> protectionReplacements = new HashMap<>();
	private HashMap<String, PlaceholdersRequest<IProtectionBlock>> protectionBlockReplacements = new HashMap<>();

	@LoadMethod
	private void load() {
		TasksUtils.executeOnAsyncWithTimer(() -> {
			playerReplacements.entrySet()
					.removeIf(entry -> entry.getValue().getRequestTime() + TTL_LIMIT < System.currentTimeMillis());
			protectionReplacements.entrySet()
					.removeIf(entry -> entry.getValue().getRequestTime() + TTL_LIMIT < System.currentTimeMillis());
			protectionBlockReplacements.entrySet()
					.removeIf(entry -> entry.getValue().getRequestTime() + TTL_LIMIT < System.currentTimeMillis());
		}, TTL_LIMIT, TTL_LIMIT);
	}

	public Replacement[] getPlayerReplacements(Player player) {
		return getReplacements(this.playerReplacements, player, player.getUniqueId(), () -> {
			List<Replacement> replacements = PLAYER_PLACEHOLDERS.stream().map(
					placeholder -> new Replacement(placeholder.getFirst(), () -> placeholder.getSecond().apply(player))
							.cacheText(false))
					.collect(Collectors.toList());
			return new PlaceholdersRequest<>(null, player, replacements.toArray(new Replacement[replacements.size()]));
		});
	}

	public Replacement[] getProtectionReplacements(IProtection protection) {
		return getReplacements(this.protectionReplacements, protection, protection.getProtectionId(), () -> {
			List<Replacement> replacements = PROTECTION_PLACEHOLDERS.stream()
					.map(placeholder -> new Replacement(placeholder.getFirst(),
							() -> placeholder.getSecond().apply(protection)).cacheText(false))
					.collect(Collectors.toList());
			return new PlaceholdersRequest<>(null, protection,
					replacements.toArray(new Replacement[replacements.size()]));
		});
	}

	public Replacement[] getProtectionBlockReplacements(IProtectionBlock block) {
		return getReplacements(this.protectionBlockReplacements, block, block.getId(), () -> {
			List<Replacement> replacements = PROTECTIONBLOCK_PLACEHOLDERS.stream().map(
					placeholder -> new Replacement(placeholder.getFirst(), () -> placeholder.getSecond().apply(block))
							.cacheText(false))
					.collect(Collectors.toList());
			return new PlaceholdersRequest<>(null, block, replacements.toArray(new Replacement[replacements.size()]));
		});
	}

	private synchronized <K, T> Replacement[] getReplacements(Map<K, PlaceholdersRequest<T>> replacementsMap, T object,
			K key, Supplier<PlaceholdersRequest<T>> supplier) {
		if (key == null) {
			return EMPTY;
		}

		PlaceholdersRequest<T> replacement = replacementsMap.computeIfAbsent(key, (regionId) -> supplier.get());

		if (replacement.getParent() != object) {
			replacementsMap.put(key, (replacement = supplier.get()));
		}

		replacement.setRequestTime(System.currentTimeMillis());

		return replacement.getReplacements();
	}

	@AllArgsConstructor
	@Data
	public static class PlaceholdersRequest<T> {

		private Long requestTime;
		private T parent;
		private Replacement[] replacements;

	}

}

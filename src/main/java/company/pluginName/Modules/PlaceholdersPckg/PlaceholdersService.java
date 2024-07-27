package company.pluginName.Modules.PlaceholdersPckg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.IProtection;

@PandaService
public class PlaceholdersService {

	private static final long TTL_LIMIT = 60 * 1000;

	/*
	 * Player placeholders
	 */

	private static final List<Pair<String, Function<Player, String>>> PLAYER_PLACEHOLDERS = Arrays
			.asList(Pair.of("{player_name}", (player) -> player.getName()));

	/*
	 * Protection placeholders
	 */

	private static final List<Pair<String, Function<IProtection, String>>> PROTECTION_PLACEHOLDERS = Arrays.asList(
			Pair.of("{protection_id}", (protection) -> protection.getRegionId()),
			Pair.of("{protection_name}",
					(protection) -> protection.getDisplayName() != null ? protection.getDisplayName()
							: protection.getRegionId()),
			Pair.of("{protection_owner}", (protection) -> protection.getOwnerName()));

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
		Pair<Long, Replacement[]> replacement = this.playerReplacements.computeIfAbsent(player.getUniqueId(),
				(uuid) -> {
					List<Replacement> replacements = PLAYER_PLACEHOLDERS.stream()
							.map(placeholder -> new Replacement(placeholder.getFirst(),
									() -> placeholder.getSecond().apply(player)))
							.collect(Collectors.toList());
					return Pair.of(null, replacements.toArray(new Replacement[replacements.size()]));
				});

		replacement.setFirst(System.currentTimeMillis());

		return replacement.getSecond();
	}

	public Replacement[] getProtectionReplacements(IProtection protection) {
		Pair<Long, Replacement[]> replacement = this.protectionReplacements.computeIfAbsent(protection.getRegionId(),
				(regionId) -> {
					List<Replacement> replacements = PROTECTION_PLACEHOLDERS.stream()
							.map(placeholder -> new Replacement(placeholder.getFirst(),
									() -> placeholder.getSecond().apply(protection)))
							.collect(Collectors.toList());
					return Pair.of(null, replacements.toArray(new Replacement[replacements.size()]));
				});

		replacement.setFirst(System.currentTimeMillis());

		return replacement.getSecond();
	}

}

package company.pluginName.Modules.PlaceholdersPckg;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import royale.RoyaleProtectionBlocks.Plugin.InternalAPI.Interfaces.IProtection;

@PandaService
public class PlaceholdersService {

	private static final long TTL_LIMIT = 60 * 1000;

	/*
	 * Player placeholders
	 */

	private static final String PLAYER_NAME_SUPPLIER_PLACEHOLDER = "{player_name}";
	private static final Function<Player, String> PLAYER_NAME_SUPPLIER = (player) -> player.getName();

	/*
	 * Protection placeholders
	 */

	private static final String PROTECTION_ID_SUPPLIER_PLACEHOLDER = "{protection_id}";
	private static final Function<IProtection, String> PROTECTION_ID_SUPPLIER = (protection) -> protection
			.getRegionId();

	private static final String PROTECTION_NAME_SUPPLIER_PLACEHOLDER = "{protection_name}";
	private static final Function<IProtection, String> PROTECTION_NAME_SUPPLIER = (
			protection) -> protection.getDisplayName() != null ? protection.getDisplayName() : protection.getRegionId();

	private static final String PROTECTION_OWNER_SUPPLIER_PLACEHOLDER = "{protection_owner}";
	private static final Function<IProtection, String> PROTECTION_OWNER_SUPPLIER = (protection) -> protection
			.getOwnerName();

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
				(uuid) -> new Pair<>(null, new Replacement[] {
						new Replacement(PLAYER_NAME_SUPPLIER_PLACEHOLDER, () -> PLAYER_NAME_SUPPLIER.apply(player))
								.cacheText(false) }));

		replacement.setFirst(System.currentTimeMillis());

		return replacement.getSecond();
	}

	public Replacement[] getProtectionReplacements(IProtection protection) {
		Pair<Long, Replacement[]> replacement = this.protectionReplacements
				.computeIfAbsent(protection.getRegionId(),
						(uuid) -> new Pair<>(null, new Replacement[] {
								new Replacement(PROTECTION_ID_SUPPLIER_PLACEHOLDER,
										() -> PROTECTION_ID_SUPPLIER.apply(protection)).cacheText(false),
								new Replacement(PROTECTION_NAME_SUPPLIER_PLACEHOLDER,
										() -> PROTECTION_NAME_SUPPLIER.apply(protection)).cacheText(false),
								new Replacement(PROTECTION_OWNER_SUPPLIER_PLACEHOLDER,
										() -> PROTECTION_OWNER_SUPPLIER.apply(protection)).cacheText(false), }));

		replacement.setFirst(System.currentTimeMillis());

		return replacement.getSecond();
	}

}

package company.pluginName.Features.ProtectionPermissionLimitPckg.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects.PandaParametizedPermission.Parameter;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.BlockReason;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.IProtection;

public class ProtectionPermissionLimitUtilities {

	@RegisteredPandaField("config")
	public static final PandaBooleanField SETTINGS_PROTECTION_BLOCKPROTECTIONSIFEXCEEDINGLIMITS = new PandaBooleanField(
			"Settings.Protection.Block-protections-if-exceeding-limits", false);

	@RegisteredPandaField("config")
	public static final PandaStringField SETTINGS_PROTECTION_CHECKIFEXCEEDINGLIMITSEVERY = new PandaStringField(
			"Settings.Protection.Check-if-exceeding-limits-every", "30m");

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTION_EXCEEDINGLIMITWARNING = new PandaPrefixedStringField(
			"Message.Protection.Exceeding-limit-warning",
			"&cYour permissions doesn't allow to keep the current amount of protections. Please remove some of your protections until reaching the actual limit! &8(&eCurrent: &7%current%&e, Limit: &7%max%&8)");

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTION_EXCEEDINGLIMITBLOCKSPECIFICWARNING = new PandaPrefixedStringField(
			"Message.Protection.Exceeding-limit-block-specific-warning",
			"&cYour permissions doesn't allow to keep the current amount of protections using the protection block &7%block%&c. Please remove some of your protections until reaching the actual limit! &8(&eCurrent: &7%current%&e, Limit: &7%max%&8)");

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_PROTECTION_EXCEEDINGLIMITFIXED = new PandaPrefixedStringField(
			"Message.Protection.Exceeding-limit-fixed",
			"&aYou now are in the current limit, all protections have been unblocked.");

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	public static Summary checkCapacity(Player player) {
		Summary summary = new Summary(PermissionsService.getGeneralMaxCapacity(player, 0),
				PermissionsService.getAllPerBlockMaxCapacity(player));

		HashSet<Protection> modifiedProtections = new HashSet<>();

		boolean hasBypassPermission = PermissionsService.MAX_BYPASS.hasPermission(player);

		List<Protection> protections = new ArrayList<>(
				protectionsService.getProtectionsByOwner().getOrDefault(player.getUniqueId(), Collections.emptyList()));

		if (!hasBypassPermission && protections.size() > summary.getMaxCapacity()) {
			protections.forEach(prot -> {
				if (!prot.isBlocked() || prot.getBlockReason() != BlockReason.EXCEEDING_LIMIT) {
					prot.block(BlockReason.EXCEEDING_LIMIT);
					modifiedProtections.add(prot);
					summary.getBlockedProtections().add(prot);
				}
			});
		} else {
			protections.forEach(prot -> {
				if (prot.isBlocked() && prot.getBlockReason() == BlockReason.EXCEEDING_LIMIT) {
					prot.unblock();
					modifiedProtections.add(prot);
					summary.getUnblockedProtections().add(prot);
				}
			});
		}

		Map<String, List<Protection>> protectionsPerBlock = new HashMap<>();
		protections.forEach(prot -> protectionsPerBlock
				.computeIfAbsent(prot.getProtectionBlock().getIdentifier(), (key) -> new ArrayList<>()).add(prot));

		protectionsPerBlock.forEach((key, list) -> {
			boolean hasBlockBypassPermission = PermissionsService.BLOCK_MAX_BYPASS.hasPermission(player,
					Parameter.of("block", key));

			Integer blockMaxCapacity = summary.getMaxCapacityPerBlock().get(key);

			if (!hasBlockBypassPermission
					&& (blockMaxCapacity != null && protectionsPerBlock.size() > blockMaxCapacity)) {
				list.forEach(prot -> {
					if (!prot.isBlocked() || prot.getBlockReason() != BlockReason.EXCEEDING_LIMIT_PROTECTION_BLOCK) {
						prot.block(BlockReason.EXCEEDING_LIMIT_PROTECTION_BLOCK);
						modifiedProtections.add(prot);
						summary.getBlockedProtectionsPerBlock().computeIfAbsent(key, (k) -> new ArrayList<>())
								.add(prot);
					}
				});
			} else {
				list.forEach(prot -> {
					if (prot.isBlocked() && prot.getBlockReason() == BlockReason.EXCEEDING_LIMIT_PROTECTION_BLOCK) {
						prot.unblock();
						modifiedProtections.add(prot);
						summary.getUnblockedProtectionsPerBlock().computeIfAbsent(key, (k) -> new ArrayList<>())
								.add(prot);
					}
				});
			}
		});

		modifiedProtections.forEach(Protection::saveData);

		return summary;
	}

	public static void sendSummaryMessage(Player player, Summary summary) {
		if (summary.getBlockedProtections().isEmpty() && summary.getBlockedProtectionsPerBlock().isEmpty()) {
			if ((!summary.getUnblockedProtections().isEmpty() || !summary.getUnblockedProtectionsPerBlock().isEmpty())
					&& protectionsService.findProtectionsByOwner(player.getUniqueId()).stream()
							.noneMatch(protection -> protection.isBlocked())) {
				MessageTemplate.inst(MESSAGE_PROTECTION_EXCEEDINGLIMITFIXED.applyPrefix()).process()
						.sendMessage(player);
			}
		} else {
			if (!summary.getBlockedProtections().isEmpty()) {
				MessageTemplate.inst(MESSAGE_PROTECTION_EXCEEDINGLIMITWARNING.applyPrefix())
						.setReplacements(
								new Replacement("%current%",
										() -> String.valueOf(summary.getBlockedProtections().size())),
								new Replacement("%max%", () -> String.valueOf(summary.getMaxCapacity())))
						.process().sendMessage(player);
			} else if (!summary.getBlockedProtectionsPerBlock().isEmpty()) {
				summary.getBlockedProtectionsPerBlock().entrySet().stream().filter(entry -> !entry.getValue().isEmpty())
						.findFirst().ifPresent(entry -> {
							ProtectionBlock protectionBlock = protectionBlocksService
									.getProtectionBlockById(entry.getKey());

							if (protectionBlock != null) {
								ItemMeta im = protectionBlock.getInformation().getItem().getItemMeta();

								MessageTemplate
										.inst(MESSAGE_PROTECTION_EXCEEDINGLIMITBLOCKSPECIFICWARNING.applyPrefix())
										.setReplacements(
												new Replacement("%block%",
														() -> im.hasDisplayName() ? im.getDisplayName()
																: entry.getKey()),
												new Replacement("%current%",
														() -> String.valueOf(entry.getValue().size())),
												new Replacement("%max%",
														() -> String.valueOf(
																summary.getMaxCapacityPerBlock().get(entry.getKey()))))
										.process().sendMessage(player);
							}
						});
			}
		}
	}

	@Getter
	@RequiredArgsConstructor
	@ToString
	public static class Summary {

		private @NonNull Integer maxCapacity;
		private @NonNull Map<String, Integer> maxCapacityPerBlock;

		private List<IProtection> blockedProtections = new ArrayList<>();
		private List<IProtection> unblockedProtections = new ArrayList<>();

		private Map<String, List<IProtection>> blockedProtectionsPerBlock = new HashMap<>();
		private Map<String, List<IProtection>> unblockedProtectionsPerBlock = new HashMap<>();

	}

}

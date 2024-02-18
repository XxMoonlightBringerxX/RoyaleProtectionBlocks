package company.pluginName.APIs.PlaceholderAPI.Placeholders;

import java.util.Collections;
import java.util.Optional;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.Permissions;
import company.pluginName.APIs.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceHolder extends PlaceholderExpansion {

	private static final String PROTECTION_FLAG_PREFIX = "protection_flag_";
	private static final String PROTECTIONBLOCK_CURRENT_PREFIX = "protectionblock_current_";
	private static final String PROTECTIONBLOCK_MAX_PREFIX = "protectionblock_max_";

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	@Override
	public String getAuthor() {
		return "DarkPanda73";
	}

	@Override
	public String getIdentifier() {
		return "protections";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if (player != null) {
			if (identifier.toLowerCase().startsWith(PROTECTION_FLAG_PREFIX)) {
				ProtectedRegion region = getRegionIn(player);
				if (region != null) {
					String flagName = identifier.substring(PROTECTION_FLAG_PREFIX.length());
					try {
						Optional<Flag<?>> foundFlag = worldGuardApi.getHook().getInternalWorldGuard().getAllFlags()
								.stream().filter(flag -> flag.getName().equals(flagName)).findFirst();
						if (foundFlag.isPresent()) {
							String flagValue = company.pluginName.Bukkit.Inventories.Protections.Flags.Objects.Flag
									.getFlagValueAsString(foundFlag.get(), region);
							return flagValue != null ? flagValue : "";
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (identifier.toLowerCase().startsWith(PROTECTIONBLOCK_MAX_PREFIX)) {
				String blockId = identifier.substring(PROTECTIONBLOCK_MAX_PREFIX.length());
				if (!blockId.isEmpty()) {
					ProtectionBlock protectionBlock = protectionBlocksService.getProtectionBlockById(blockId);
					if (protectionBlock != null) {
						Integer blockCapacity = Permissions.getPerBlockMaxCapacity(player, protectionBlock);
						if (blockCapacity != null) {
							return blockCapacity.toString();
						}
					}
				}
			} else if (identifier.toLowerCase().startsWith(PROTECTIONBLOCK_CURRENT_PREFIX)) {
				String blockId = identifier.substring(PROTECTIONBLOCK_CURRENT_PREFIX.length());
				if (!blockId.isEmpty()) {
					ProtectionBlock protectionBlock = protectionBlocksService.getProtectionBlockById(blockId);
					if (protectionBlock != null) {
						return String.valueOf(protectionsService.getProtectionsByOwner()
								.getOrDefault(player.getUniqueId(), Collections.emptyList()).stream()
								.filter(protection -> protection.getProtectionBlock().getIdentifier().equals(blockId))
								.count());
					}
				}
			} else {
				Protection protection;
				switch (identifier.toLowerCase()) {
				case "protection_owner":
					protection = getProtectionIn(player);
					if (protection != null) {
						return protection.getOwnerName();
					}
					break;
				case "protection_id":
					protection = getProtectionIn(player);
					if (protection != null) {
						return protection.getRegionId();
					}
					break;
				case "protection_name":
					protection = getProtectionIn(player);
					if (protection != null) {
						return protection.getDisplayName() != null ? protection.getDisplayName()
								: protection.getRegionId();
					}
					break;
				case "protection_size":
					protection = getProtectionIn(player);
					if (protection != null) {
						ProtectionBlock block = protection.getProtectionBlock().getObject();
						String blocksX = String.valueOf((block.getInformation().getBlocksX() * 2) + 1);
						String blocksY = block.getInformation().getBlocksY() == -1
								? Messages.MESSAGE_GENERAL_NOLIMIT.toString()
								: String.valueOf((block.getInformation().getBlocksY() * 2) + 1);
						String blocksZ = String.valueOf((block.getInformation().getBlocksZ() * 2) + 1);
						return String.format("%sx%sx%s", blocksX, blocksY, blocksZ);
					}
					break;
				case "player_current":
					return String.valueOf(protectionsService.getProtectionsByOwner()
							.getOrDefault(player.getUniqueId(), Collections.emptyList()).size());
				case "player_max":
					Integer maxAmount = Permissions.getGeneralMaxCapacity(player);
					return maxAmount != null ? String.valueOf(maxAmount) : "---";
				}
			}
		}
		return "";
	}

	private ProtectedRegion getRegionIn(Player player) {
		try {
			return worldGuardApi.getHook().getInternalWorldGuard().getApplicableRegions(player.getLocation())
					.getRegions().stream()
					.filter(region -> protectionsService.getProtectionByRegion().get(region.getId()) != null)
					.findFirst().orElse(null);
		} catch (Exception e) {
		}
		return null;
	}

	private Protection getProtectionIn(Player player) {
		try {
			Optional<ProtectedRegion> foundRegion = worldGuardApi.getHook().getInternalWorldGuard()
					.getApplicableRegions(player.getLocation()).getRegions().stream()
					.filter(region -> protectionsService.getProtectionByRegion().get(region.getId()) != null)
					.findFirst();

			if (foundRegion.isPresent()) {
				return protectionsService.getProtectionByRegion().get(foundRegion.get().getId());
			}
		} catch (Exception e) {
		}
		return null;
	}
}

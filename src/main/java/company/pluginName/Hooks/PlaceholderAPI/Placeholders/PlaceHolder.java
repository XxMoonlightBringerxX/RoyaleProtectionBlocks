package company.pluginName.Hooks.PlaceholderAPI.Placeholders;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionFlagsPckg.ProtectionFlagsService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.ProtectionBlocks.IProtectionBlock;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtectionFlags.FlagRetrieveRequestInput;

public class PlaceHolder extends PlaceholderExpansion {

	private static final String PROTECTION_FLAG_PREFIX = "protection_flag_";
	private static final String PROTECTIONBLOCK_CURRENT_PREFIX = "protectionblock_current_";
	private static final String PROTECTIONBLOCK_MAX_PREFIX = "protectionblock_max_";

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private static ProtectionFlagsService protectionFlagsService;

	@PandaInject
	private static PlayerDataService playerDataService;

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
				IProtection protection = getProtectionIn(player);
				if (protection != null) {
					String flagName = identifier.substring(PROTECTION_FLAG_PREFIX.length());
					try {
						return protection.getFlags().getFlagValueAsString(FlagRetrieveRequestInput.inst(flagName));
					} catch (RoyaleProtectionBlocksException e) {
						return "";
					}
				}
			} else if (identifier.toLowerCase().startsWith(PROTECTIONBLOCK_MAX_PREFIX)) {
				String blockId = identifier.substring(PROTECTIONBLOCK_MAX_PREFIX.length());
				if (!blockId.isEmpty()) {
					ProtectionBlock protectionBlock = protectionBlocksService.getProtectionBlockById(blockId);
					if (protectionBlock != null) {
						Integer blockCapacity = PermissionsService.getPerBlockMaxCapacity(player, protectionBlock);
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
						return String.valueOf(RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
								.findProtectionsByOwner(player.getUniqueId()).stream()
								.filter(protection -> protection.getProtectionBlockId().equals(blockId)).count());
					}
				}
			} else {
				IProtection protection;
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
						return protection.getProtectionId();
					}
					break;
				case "protection_name":
					protection = getProtectionIn(player);
					if (protection != null) {
						return protection.getDisplayName() != null ? protection.getDisplayName()
								: protection.getProtectionId();
					}
					break;
				case "protection_size":
					protection = getProtectionIn(player);
					if (protection != null) {
						IProtectionBlock block = protection.getProtectionBlock();
						String blocksX = String.valueOf((block.getBlocksX() * 2) + 1);
						String blocksY = block.getBlocksY() == -1 ? Messages.MESSAGE_GENERAL_NOLIMIT.toString()
								: String.valueOf((block.getBlocksY() * 2) + 1);
						String blocksZ = String.valueOf((block.getBlocksZ() * 2) + 1);
						return String.format("%sx%sx%s", blocksX, blocksY, blocksZ);
					}
					break;
				case "player_current":
					return String.valueOf(RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
							.findProtectionsByOwner(player.getUniqueId()).size());
				case "player_max":
					Integer maxAmount = PermissionsService.getGeneralMaxCapacity(player);
					return maxAmount != null ? String.valueOf(maxAmount) : "---";
				}
			}
		}
		return "";
	}

	private List<? extends IProtection> getProtectionsIn(Player player) {
		PlayerData playerData = playerDataService.getPlayerData(player);
		if (playerData != null) {
			return playerData.getCurrentProtections();
		}
		return Collections.emptyList();
	}

	private IProtection getProtectionIn(Player player) {
		return getProtectionsIn(player).stream().findFirst().orElse(null);
	}

}

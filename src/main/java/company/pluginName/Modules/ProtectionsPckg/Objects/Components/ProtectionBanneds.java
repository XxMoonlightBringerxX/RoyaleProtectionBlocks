package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;

@Data
@AllArgsConstructor
public class ProtectionBanneds {

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	private Protection protection;

	public Set<String> list() {
		return worldGuardApi.getHook().getBannedPlayersFlag().flagSet(this.protection.getProtectedRegion());
	}

	public void add(UUID banned) throws RoyaleProtectionBlocksExceptionImpl {
		if (this.protection.isMainOwner(banned)) {
			throw Exceptions.Protections.Banneds.Save.CANNOTADDPROTECTIONOWNER.generateException();
		}

		Set<String> banneds = worldGuardApi.getHook().getBannedPlayersFlag()
				.flagSet(this.protection.getProtectedRegion());

		banneds.add(banned.toString());

		this.protection.getProtectedRegion().setFlag(worldGuardApi.getHook().getBannedPlayersFlag().getWorldGuardFlag(),
				banneds);

		TasksUtils.execute(() -> {
			Player bannedPlayer = Bukkit.getPlayer(banned);

			if (bannedPlayer != null) {
				try {
					if (PermissionsService.BANNEDS_BYPASS.hasPermission(bannedPlayer)
							|| protection.kickPlayer(bannedPlayer)) {
						MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_BANNED.applyPrefix()).process()
								.sendMessage(bannedPlayer);
					}
				} catch (RoyaleProtectionBlocksException e) {
					e.sendError(Bukkit.getConsoleSender());
				}
			}
		});
	}

	public void remove(UUID banned) throws RoyaleProtectionBlocksExceptionImpl {
		Set<String> banneds = worldGuardApi.getHook().getBannedPlayersFlag()
				.flagSet(this.protection.getProtectedRegion());

		banneds.remove(banned.toString());

		this.protection.getProtectedRegion().setFlag(worldGuardApi.getHook().getBannedPlayersFlag().getWorldGuardFlag(),
				banneds);
	}

}

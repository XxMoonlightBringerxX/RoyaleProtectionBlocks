package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import company.pluginName.Permissions;
import company.pluginName.APIs.WorldGuard.WorldGuardAPI;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtectionBanneds {

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	private Protection protection;

	public Set<String> list() {
		return worldGuardApi.getHook().getBannedPlayersFlag().flagSet(this.protection.getProtectedRegion());
	}

	public void add(UUID banned) throws RoyaleProtectionBlocksException {
		add(null, banned);
	}

	public void add(Player pl, UUID banned) throws RoyaleProtectionBlocksException {
		if (pl != null) {
			if (!pl.hasPermission(Permissions.PROTECTION_BANNEDS_ADD_OTHERS)) {
				if (!this.protection.getOwners().list().contains(pl.getUniqueId())) {
					throw Exceptions.Protections.Banneds.Save.PERMISSIONDENIED.generateException();
				}

				if (banned.equals(pl.getUniqueId())) {
					throw Exceptions.Protections.Banneds.Save.CANNOTADDYOURSELF.generateException();
				}
			}
		}

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
				if (bannedPlayer.hasPermission(Permissions.PROTECTION_BANNEDS_BYPASS)
						|| protection.getActions().kickPlayer(bannedPlayer)) {
					MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_BANNED.applyPrefix()).process()
							.sendMessage(bannedPlayer);
				}
			}
		});
	}

	public void remove(UUID banned) throws RoyaleProtectionBlocksException {
		remove(null, banned);
	}

	public void remove(Player pl, UUID banned) throws RoyaleProtectionBlocksException {
		if (pl != null) {
			if (!this.protection.getOwners().list().contains(pl.getUniqueId())
					&& !pl.hasPermission(Permissions.PROTECTION_BANNEDS_REMOVE_OTHERS)) {
				throw Exceptions.Protections.Banneds.Delete.PERMISSIONDENIED.generateException();
			}
		}

		Set<String> banneds = worldGuardApi.getHook().getBannedPlayersFlag()
				.flagSet(this.protection.getProtectedRegion());

		banneds.remove(banned.toString());

		this.protection.getProtectedRegion().setFlag(worldGuardApi.getHook().getBannedPlayersFlag().getWorldGuardFlag(),
				banneds);
	}

}

package company.pluginName.Modules.ProtectionsPckg.Objects.Components.Protections;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Permissions;
import company.pluginName.Exceptions.ProtectionBanneds.Delete.ProtectionBannedsDeleteDeniedException;
import company.pluginName.Exceptions.ProtectionBanneds.Delete.ProtectionBannedsDeleteException;
import company.pluginName.Exceptions.ProtectionBanneds.Save.ProtectionBannedsSaveCannotAddProtectionOwnerException;
import company.pluginName.Exceptions.ProtectionBanneds.Save.ProtectionBannedsSaveCannotAddYourselfException;
import company.pluginName.Exceptions.ProtectionBanneds.Save.ProtectionBannedsSaveDeniedException;
import company.pluginName.Exceptions.ProtectionBanneds.Save.ProtectionBannedsSaveException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtectionBanneds {

	private Protection protection;

	public Set<String> list() {
		return MainPluginClass.getWorldGuardAPI().getBannedPlayersFlag().flagSet(this.protection.getProtectedRegion());
	}

	public void add(UUID banned) throws ProtectionBannedsSaveException {
		add(null, banned);
	}

	public void add(Player pl, UUID banned) throws ProtectionBannedsSaveException {
		if (pl != null) {
			if (!pl.hasPermission(Permissions.PROTECTION_BANNEDS_ADD_OTHERS)) {
				if (!this.protection.getOwners().list().contains(pl.getUniqueId())) {
					throw new ProtectionBannedsSaveDeniedException();
				}

				if (banned.equals(pl.getUniqueId())) {
					throw new ProtectionBannedsSaveCannotAddYourselfException();
				}
			}
		}

		if (this.protection.isMainOwner(banned)) {
			throw new ProtectionBannedsSaveCannotAddProtectionOwnerException();
		}

		Player bannedPlayer = Bukkit.getPlayer(banned);

		if (bannedPlayer != null) {
			if (bannedPlayer.hasPermission(Permissions.PROTECTION_BANNEDS_BYPASS)
					|| protection.getActions().kickPlayer(bannedPlayer)) {
				MessageBuilder.createMessage(MessageString.MESSAGE_PROTECTIONS_BANNED.applyPrefix())
						.sendMessage(bannedPlayer);
			}
		}

		Set<String> banneds = MainPluginClass.getWorldGuardAPI().getBannedPlayersFlag()
				.flagSet(this.protection.getProtectedRegion());

		banneds.add(banned.toString());

		this.protection.getProtectedRegion()
				.setFlag(MainPluginClass.getWorldGuardAPI().getBannedPlayersFlag().getWorldGuardFlag(), banneds);
	}

	public void remove(UUID banned) throws ProtectionBannedsDeleteException {
		remove(null, banned);
	}

	public void remove(Player pl, UUID banned) throws ProtectionBannedsDeleteException {
		if (pl != null) {
			if (!this.protection.getOwners().list().contains(pl.getUniqueId())
					&& !pl.hasPermission(Permissions.PROTECTION_BANNEDS_REMOVE_OTHERS)) {
				throw new ProtectionBannedsDeleteDeniedException();
			}
		}

		Set<String> banneds = MainPluginClass.getWorldGuardAPI().getBannedPlayersFlag()
				.flagSet(this.protection.getProtectedRegion());

		banneds.remove(banned.toString());

		this.protection.getProtectedRegion()
				.setFlag(MainPluginClass.getWorldGuardAPI().getBannedPlayersFlag().getWorldGuardFlag(), banneds);
	}

}

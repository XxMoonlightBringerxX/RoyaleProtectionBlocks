package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.WorldGuard.ProtectionWorldGuardMembers;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.WorldGuard.ProtectionWorldGuardOwners;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import lombok.Getter;
import lombok.Setter;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;

public class ProtectionPlayers {

	private Protection protection;
	private @Getter ProtectionWorldGuardMembers worldGuardMembers;
	private @Getter ProtectionWorldGuardOwners worldGuardOwners;

	public ProtectionPlayers(Protection protection) {
		this.protection = protection;
		this.worldGuardMembers = new ProtectionWorldGuardMembers(protection);
		this.worldGuardOwners = new ProtectionWorldGuardOwners(protection);
	}

	public void clearAll() {
		this.members.clear();
		this.owners.clear();
		this.banneds.clear();

		this.worldGuardMembers.clear();
		this.worldGuardOwners.clear();

		this.worldGuardOwners.add(this.protection.getOwnerUuid());
	}

	/*
	 * Members methods
	 */

	private @Getter @Setter List<UUID> members = new ArrayList<>();

	public void addMember(UUID memberUuid) throws RoyaleProtectionBlocksException {
		if (this.protection.isMainOwner(memberUuid)) {
			throw Exceptions.Protections.Members.Save.CANNOTADDPROTECTIONOWNER.generateException();
		}

		this.members.add(memberUuid);
		this.worldGuardMembers.add(memberUuid);

		if (this.banneds.contains(memberUuid)) {
			this.protection.removeBannedAndSave(memberUuid);
		}
	}

	public void removeMember(UUID memberUuid) throws RoyaleProtectionBlocksException {
		this.members.remove(memberUuid);
		this.worldGuardMembers.remove(memberUuid);

		if (this.owners.contains(memberUuid)) {
			this.protection.removeOwnerAndSave(memberUuid);
		}
	}

	/*
	 * Owners methods
	 */

	private @Getter @Setter List<UUID> owners = new ArrayList<>();

	public void addOwner(UUID ownerUuid) throws RoyaleProtectionBlocksException {
		if (this.protection.isMainOwner(ownerUuid)) {
			throw Exceptions.Protections.Owners.Save.CANNOTADDPROTECTIONOWNER.generateException();
		}

		this.owners.add(ownerUuid);
		this.worldGuardOwners.add(ownerUuid);

		if (!this.members.contains(ownerUuid)) {
			this.protection.addMemberAndSave(ownerUuid);
		}

		if (this.banneds.contains(ownerUuid)) {
			this.protection.removeBannedAndSave(ownerUuid);
		}
	}

	public void removeOwner(UUID ownerUuid) throws RoyaleProtectionBlocksException {
		if (this.protection.isMainOwner(ownerUuid)) {
			throw Exceptions.Protections.Owners.Delete.CANNOTDELETEPROTECTIONOWNER.generateException();
		}

		this.owners.remove(ownerUuid);
		this.worldGuardOwners.remove(ownerUuid);
	}

	/*
	 * Banneds methods
	 */

	private @Getter @Setter List<UUID> banneds = new ArrayList<>();

	public void addBanned(UUID bannedUuid) throws RoyaleProtectionBlocksException {
		if (this.protection.isMainOwner(bannedUuid)) {
			throw Exceptions.Protections.Banneds.Save.CANNOTADDPROTECTIONOWNER.generateException();
		}

		this.banneds.add(bannedUuid);

		if (this.members.contains(bannedUuid)) {
			this.protection.removeMemberAndSave(bannedUuid);
		}

		if (this.owners.contains(bannedUuid)) {
			this.protection.removeOwnerAndSave(bannedUuid);
		}

		Player bannedPlayer = Bukkit.getPlayer(bannedUuid);

		if (bannedPlayer != null) {
			TasksUtils.execute(() -> {
				try {
					if (PermissionsService.BANNEDS_BYPASS.hasPermission(bannedPlayer)
							|| this.protection.kickPlayer(bannedPlayer)) {
						MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_BANNED.applyPrefix()).process()
								.sendMessage(bannedPlayer);
					}
				} catch (RoyaleProtectionBlocksException e) {
					e.sendError(Bukkit.getConsoleSender());
				}
			});
		}
	}

	public void removeBanned(UUID bannedUuid) {
		this.banneds.remove(bannedUuid);
	}

}

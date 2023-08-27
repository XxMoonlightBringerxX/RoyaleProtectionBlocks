package company.pluginName.Modules.ProtectionsPckg.Objects.Components.Protections;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import company.pluginName.Permissions;
import company.pluginName.Exceptions.ProtectionMembers.Delete.ProtectionMembersDeleteDeniedException;
import company.pluginName.Exceptions.ProtectionMembers.Delete.ProtectionMembersDeleteException;
import company.pluginName.Exceptions.ProtectionMembers.Save.ProtectionMembersSaveCannotAddProtectionOwnerException;
import company.pluginName.Exceptions.ProtectionMembers.Save.ProtectionMembersSaveCannotAddYourselfException;
import company.pluginName.Exceptions.ProtectionMembers.Save.ProtectionMembersSaveDeniedException;
import company.pluginName.Exceptions.ProtectionMembers.Save.ProtectionMembersSaveException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtectionMembers {

	private Protection protection;

	public Set<UUID> list() {
		return this.protection.getProtectedRegion().getMembers().getUniqueIds();
	}

	public void add(UUID member) throws ProtectionMembersSaveException {
		add(null, member);
	}

	public void add(Player pl, UUID member) throws ProtectionMembersSaveException {
		if (pl != null) {
			if (!pl.hasPermission(Permissions.PROTECTION_MEMBERS_ADD_OTHERS)) {
				if (!this.protection.isOwner(pl.getUniqueId())) {
					throw new ProtectionMembersSaveDeniedException();
				}

				if (member.equals(pl.getUniqueId())) {
					throw new ProtectionMembersSaveCannotAddYourselfException();
				}
			}
		}

		if (this.protection.isMainOwner(member)) {
			throw new ProtectionMembersSaveCannotAddProtectionOwnerException();
		}

		this.protection.getProtectedRegion().getMembers().addPlayer(member);
	}

	public void remove(UUID member) throws ProtectionMembersDeleteException {
		remove(null, member);
	}

	public void remove(Player pl, UUID member) throws ProtectionMembersDeleteException {
		if (pl != null) {
			if (!this.protection.isOwner(pl.getUniqueId())
					&& !pl.hasPermission(Permissions.PROTECTION_MEMBERS_REMOVE_OTHERS)) {
				throw new ProtectionMembersDeleteDeniedException();
			}
		}

		this.protection.getProtectedRegion().getMembers().removePlayer(member);
	}

}

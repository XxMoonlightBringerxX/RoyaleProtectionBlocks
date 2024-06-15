package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtectionMembers {

	private Protection protection;

	public Set<UUID> list() {
		ProtectedRegion region = this.protection.getProtectedRegion();
		return region != null ? region.getMembers().getUniqueIds() : Collections.emptySet();
	}

	public void add(UUID member) throws RoyaleProtectionBlocksException {
		add(null, member);
	}

	public void add(Player pl, UUID member) throws RoyaleProtectionBlocksException {
		if (pl != null) {
			if (!PermissionsService.MEMBERS_ADD_OTHERS.hasPermission(pl)) {
				if (!this.protection.getOwners().list().contains(pl.getUniqueId())) {
					throw Exceptions.Protections.Members.Save.PERMISSIONDENIED.generateException();
				}

				if (member.equals(pl.getUniqueId())) {
					throw Exceptions.Protections.Members.Save.CANNOTADDYOURSELF.generateException();
				}
			}
		}

		if (this.protection.isMainOwner(member)) {
			throw Exceptions.Protections.Members.Save.CANNOTADDPROTECTIONOWNER.generateException();
		}

		this.protection.getProtectedRegion().getMembers().addPlayer(member);
	}

	public void remove(UUID member) throws RoyaleProtectionBlocksException {
		remove(null, member);
	}

	public void remove(Player pl, UUID member) throws RoyaleProtectionBlocksException {
		if (pl != null && !pl.getUniqueId().equals(member)) {
			if (!this.protection.getOwners().list().contains(pl.getUniqueId())
					&& !PermissionsService.MEMBERS_REMOVE_OTHERS.hasPermission(pl)) {
				throw Exceptions.Protections.Members.Delete.PERMISSIONDENIED.generateException();
			}
		}

		this.protection.getProtectedRegion().getMembers().removePlayer(member);
	}

}

package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.Permissions;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtectionOwners {

	private Protection protection;

	public Set<UUID> list() {
		ProtectedRegion region = this.protection.getProtectedRegion();
		return region != null ? region.getOwners().getUniqueIds() : Collections.emptySet();
	}

	public void add(UUID owner) throws RoyaleProtectionBlocksException {
		add(null, owner);
	}

	public void add(Player pl, UUID owner) throws RoyaleProtectionBlocksException {
		if (pl != null) {
			if (!this.protection.isMainOwner(pl.getUniqueId())
					&& !pl.hasPermission(Permissions.PROTECTION_OWNERS_ADD_OTHERS)) {
				throw Exceptions.Protections.Owners.Save.PERMISSIONDENIED.generateException();
			}

			if (owner.equals(pl.getUniqueId())) {
				throw Exceptions.Protections.Owners.Save.CANNOTADDYOURSELF.generateException();
			}
		}

		if (this.protection.isMainOwner(owner)) {
			throw Exceptions.Protections.Owners.Save.CANNOTADDPROTECTIONOWNER.generateException();
		}

		this.protection.getProtectedRegion().getOwners().addPlayer(owner);
	}

	public void remove(UUID owner) throws RoyaleProtectionBlocksException {
		remove(null, owner);
	}

	public void remove(Player pl, UUID owner) throws RoyaleProtectionBlocksException {
		if (pl != null && !pl.getUniqueId().equals(owner)) {
			if (!this.protection.isMainOwner(pl.getUniqueId())
					&& !pl.hasPermission(Permissions.PROTECTION_OWNERS_REMOVE_OTHERS)) {
				throw Exceptions.Protections.Owners.Delete.PERMISSIONDENIED.generateException();
			}
		}

		if (this.protection.isMainOwner(owner)) {
			throw Exceptions.Protections.Owners.Delete.CANNOTDELETEPROTECTIONOWNER.generateException();
		}

		this.protection.getProtectedRegion().getOwners().removePlayer(owner);
	}

}

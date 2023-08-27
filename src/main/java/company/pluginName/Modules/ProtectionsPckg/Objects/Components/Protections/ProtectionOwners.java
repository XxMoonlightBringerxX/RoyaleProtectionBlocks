package company.pluginName.Modules.ProtectionsPckg.Objects.Components.Protections;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import company.pluginName.Permissions;
import company.pluginName.Exceptions.ProtectionOwners.Delete.ProtectionOwnersDeleteCannotDeleteProtectionOwnerException;
import company.pluginName.Exceptions.ProtectionOwners.Delete.ProtectionOwnersDeleteDeniedException;
import company.pluginName.Exceptions.ProtectionOwners.Delete.ProtectionOwnersDeleteException;
import company.pluginName.Exceptions.ProtectionOwners.Save.ProtectionOwnersSaveCannotAddProtectionOwnerException;
import company.pluginName.Exceptions.ProtectionOwners.Save.ProtectionOwnersSaveCannotAddYourselfException;
import company.pluginName.Exceptions.ProtectionOwners.Save.ProtectionOwnersSaveDeniedException;
import company.pluginName.Exceptions.ProtectionOwners.Save.ProtectionOwnersSaveException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtectionOwners {

	private Protection protection;

	public Set<UUID> list() {
		return this.protection.getProtectedRegion().getOwners().getUniqueIds();
	}

	public void add(UUID owner) throws ProtectionOwnersSaveException {
		add(null, owner);
	}

	public void add(Player pl, UUID owner) throws ProtectionOwnersSaveException {
		if (pl != null) {
			if (!pl.hasPermission(Permissions.PROTECTION_OWNERS_ADD_OTHERS)) {
				if (!this.protection.isMainOwner(pl.getUniqueId())) {
					throw new ProtectionOwnersSaveDeniedException();
				}

				if (owner.equals(pl.getUniqueId())) {
					throw new ProtectionOwnersSaveCannotAddYourselfException();
				}
			}
		}

		if (this.protection.isMainOwner(owner)) {
			throw new ProtectionOwnersSaveCannotAddProtectionOwnerException();
		}

		this.protection.getProtectedRegion().getOwners().addPlayer(owner);
	}

	public void remove(UUID owner) throws ProtectionOwnersDeleteException {
		remove(null, owner);
	}

	public void remove(Player pl, UUID owner) throws ProtectionOwnersDeleteException {
		if (pl != null) {
			if (!this.protection.isMainOwner(pl.getUniqueId())
					&& !pl.hasPermission(Permissions.PROTECTION_OWNERS_REMOVE_OTHERS)) {
				throw new ProtectionOwnersDeleteDeniedException();
			}
		}

		if (this.protection.isMainOwner(owner)) {
			throw new ProtectionOwnersDeleteCannotDeleteProtectionOwnerException();
		}

		this.protection.getProtectedRegion().getOwners().removePlayer(owner);
	}

}

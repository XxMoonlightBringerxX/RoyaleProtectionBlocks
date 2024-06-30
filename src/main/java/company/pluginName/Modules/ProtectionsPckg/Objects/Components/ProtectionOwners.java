package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
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

	public void add(UUID owner) throws RoyaleProtectionBlocksExceptionImpl {
		if (this.protection.isMainOwner(owner)) {
			throw Exceptions.Protections.Owners.Save.CANNOTADDPROTECTIONOWNER.generateException();
		}

		this.protection.getProtectedRegion().getOwners().addPlayer(owner);
	}

	public void remove(UUID owner) throws RoyaleProtectionBlocksExceptionImpl {
		if (this.protection.isMainOwner(owner)) {
			throw Exceptions.Protections.Owners.Delete.CANNOTDELETEPROTECTIONOWNER.generateException();
		}

		this.protection.getProtectedRegion().getOwners().removePlayer(owner);
	}

}

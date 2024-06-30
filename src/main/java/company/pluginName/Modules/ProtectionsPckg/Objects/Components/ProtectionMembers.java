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
public class ProtectionMembers {

	private Protection protection;

	public Set<UUID> list() {
		ProtectedRegion region = this.protection.getProtectedRegion();
		return region != null ? region.getMembers().getUniqueIds() : Collections.emptySet();
	}

	public void add(UUID member) throws RoyaleProtectionBlocksExceptionImpl {
		if (this.protection.isMainOwner(member)) {
			throw Exceptions.Protections.Members.Save.CANNOTADDPROTECTIONOWNER.generateException();
		}

		this.protection.getProtectedRegion().getMembers().addPlayer(member);
	}

	public void remove(UUID member) throws RoyaleProtectionBlocksExceptionImpl {
		this.protection.getProtectedRegion().getMembers().removePlayer(member);
	}

}

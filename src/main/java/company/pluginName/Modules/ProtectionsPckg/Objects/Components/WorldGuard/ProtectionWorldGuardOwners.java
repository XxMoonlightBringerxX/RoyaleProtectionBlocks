package company.pluginName.Modules.ProtectionsPckg.Objects.Components.WorldGuard;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtectionWorldGuardOwners {

	private Protection protection;

	public Set<UUID> list() {
		ProtectedRegion region = this.protection.getProtectedRegion();
		return region != null ? region.getOwners().getUniqueIds() : Collections.emptySet();
	}

	public void add(UUID owner) {
		this.protection.getProtectedRegion().getOwners().addPlayer(owner);
	}

	public void remove(UUID owner) {
		this.protection.getProtectedRegion().getOwners().removePlayer(owner);
	}

	public boolean contains(UUID owner) {
		return this.protection.getProtectedRegion().getOwners().contains(owner);
	}

	public void clear() {
		this.protection.getProtectedRegion().getOwners().clear();
	}

}

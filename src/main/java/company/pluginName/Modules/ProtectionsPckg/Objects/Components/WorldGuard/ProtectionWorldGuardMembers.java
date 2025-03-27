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
public class ProtectionWorldGuardMembers {

	private Protection protection;

	public Set<UUID> list() {
		ProtectedRegion region = this.protection.getProtectedRegion();
		return region != null ? region.getMembers().getUniqueIds() : Collections.emptySet();
	}

	public void add(UUID member) {
		this.protection.getProtectedRegion().getMembers().addPlayer(member);
	}

	public void remove(UUID member) {
		this.protection.getProtectedRegion().getMembers().removePlayer(member);
	}

	public void clear() {
		this.protection.getProtectedRegion().getMembers().clear();
	}

}

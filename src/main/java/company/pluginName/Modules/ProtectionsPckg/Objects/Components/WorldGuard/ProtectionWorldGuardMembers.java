package company.pluginName.Modules.ProtectionsPckg.Objects.Components.WorldGuard;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
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

	public void add(UUID member) throws RoyaleProtectionBlocksExceptionImpl {
		if (this.protection.isMainOwner(member)) {
			throw Exceptions.Protections.Members.Save.CANNOTADDPROTECTIONOWNER.generateException();
		}

		this.protection.getProtectedRegion().getMembers().addPlayer(member);

		this.protection.getChildProtections().forEach(child -> {
			try {
				child.getWorldGuardMembers().add(member);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public void remove(UUID member) throws RoyaleProtectionBlocksExceptionImpl {
		this.protection.getProtectedRegion().getMembers().removePlayer(member);

		this.protection.getChildProtections().forEach(child -> {
			try {
				child.getWorldGuardMembers().remove(member);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

}

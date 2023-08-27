package company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionBlocks;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter(lombok.AccessLevel.NONE)
public class ProtectionBlockAllowedWorlds {

	private Set<String> allowedWorlds = Collections.unmodifiableSet(new HashSet<>());

	public Set<String> get() {
		return allowedWorlds;
	}

	public synchronized void add(String world) {
		HashSet<String> newSet = new HashSet<>(allowedWorlds);
		newSet.add(world);
		this.allowedWorlds = Collections.unmodifiableSet(newSet);
	}

	public synchronized void remove(String world) {
		HashSet<String> newSet = new HashSet<>(allowedWorlds);
		newSet.remove(world);
		this.allowedWorlds = Collections.unmodifiableSet(newSet);
	}

	public synchronized void clear() {
		this.allowedWorlds = Collections.unmodifiableSet(new HashSet<>());
	}

	public synchronized void copy(ProtectionBlockAllowedWorlds allowedWorlds) {
		this.clear();
		allowedWorlds.get().forEach(this.allowedWorlds::add);
	}

}

package company.pluginName.Modules.ProtectionsPckg.Objects.Components.WorldGuard;

import java.util.Set;

import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtectionWorldGuardBanneds {

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	private Protection protection;

	public Set<String> get() {
		return worldGuardApi.getHook().getBannedPlayersFlag().flagSet(this.protection.getProtectedRegion());
	}

}

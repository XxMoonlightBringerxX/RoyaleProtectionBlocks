package company.pluginName.APIs.WorldGuard.Hook;

import java.util.Arrays;
import java.util.List;

import company.pluginName.APIs.WorldGuard.Flags.BannedPlayersFlag;
import company.pluginName.APIs.WorldGuard.Flags.ProtectionBlockLocationFlag;
import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Hook.PandaAbstractWorldGuardHook;
import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Objects.AbstractFlag;
import lombok.Getter;

public class WorldGuardHook extends PandaAbstractWorldGuardHook {

	private @Getter ProtectionBlockLocationFlag protectionBlockLocationFlag;
	private @Getter BannedPlayersFlag bannedPlayersFlag;

	public WorldGuardHook() {
		protectionBlockLocationFlag = new ProtectionBlockLocationFlag(this);
		bannedPlayersFlag = new BannedPlayersFlag(this);
	}

	@Override
	protected List<AbstractFlag<?>> getFlags() {
		return Arrays.asList(protectionBlockLocationFlag, bannedPlayersFlag);
	}

	@Override
	protected void registerHandlers() throws Exception {
	}

}

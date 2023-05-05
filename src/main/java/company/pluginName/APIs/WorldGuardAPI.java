package company.pluginName.APIs;

import java.util.Arrays;
import java.util.List;

import company.pluginName.MainPluginClass;
import lombok.Getter;
import relampagorojo93.LibsCollection.Utils.Bukkit.APIs.WorldGuard.Objects.AbstractFlag;
import relampagorojo93.LibsCollection.Utils.Bukkit.APIs.WorldGuard.Objects.AbstractStateFlag;

public class WorldGuardAPI extends relampagorojo93.LibsCollection.Utils.Bukkit.APIs.WorldGuard.WorldGuardAPI {

	private @Getter AbstractStateFlag ignitionFlag;

	public WorldGuardAPI() {
		super(MainPluginClass.getPlugin());
	}

	@Override
	protected List<AbstractFlag<?>> getFlags() {
		return Arrays.asList();
	}

	@Override
	public String getPrefix() {
		return MainPluginClass.getPlugin().getPrefix();
	}

	@Override
	public boolean isOptional() {
		return false;
	}

}

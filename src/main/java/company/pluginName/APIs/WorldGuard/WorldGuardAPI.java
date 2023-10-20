package company.pluginName.APIs.WorldGuard;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;

import com.sk89q.worldguard.session.handler.EntryFlag;

import company.pluginName.MainPluginClass;
import company.pluginName.APIs.WorldGuard.Flags.BannedPlayersFlag;
import company.pluginName.APIs.WorldGuard.Flags.ProtectionBlockLocationFlag;
import company.pluginName.APIs.WorldGuard.Handlers.BannedPlayersHandler;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import lombok.Getter;
import relampagorojo93.LibsCollection.Utils.Bukkit.APIs.WorldGuard.Objects.AbstractFlag;

public class WorldGuardAPI extends relampagorojo93.LibsCollection.Utils.Bukkit.APIs.WorldGuard.WorldGuardAPI {

	private @Getter ProtectionBlockLocationFlag protectionBlockLocationFlag;
	private @Getter BannedPlayersFlag bannedPlayersFlag;

	public WorldGuardAPI() {
		super(MainPluginClass.getPlugin());
	}

	@Override
	protected List<AbstractFlag<?>> getFlags() {
		return Arrays.asList(protectionBlockLocationFlag = new ProtectionBlockLocationFlag(this),
				bannedPlayersFlag = new BannedPlayersFlag(this));
	}

	@Override
	public String getPrefix() {
		return MainPluginClass.getPlugin().getPrefix();
	}

	@Override
	public boolean isOptional() {
		return false;
	}

	public void registerHandlers() {
		if (isHooked()) {
			try {
				getInternalWorldGuard().registerHandler(BannedPlayersHandler.FACTORY, EntryFlag.FACTORY);

				MessageBuilder
						.createMessage(
								getPrefix().concat("<WorldGuard> Registered handler to manage banned players on protections successfully."))
						.sendMessage(Bukkit.getConsoleSender());
			} catch (Exception e) {
				MessageBuilder
						.createMessage(getPrefix().concat(
								"<WorldGuard> There was an issue trying to register the handler to amange banned players on protections!"))
						.sendMessage(Bukkit.getConsoleSender());
				e.printStackTrace();
			}
		}
	}

}

package company.pluginName.APIs.WorldGuard.Handlers;

import java.util.Set;

import org.bukkit.Bukkit;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import company.pluginName.MainPluginClass;
import company.pluginName.Permissions;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;

public class BannedPlayersHandler extends Handler {

	public static final Factory FACTORY = new Factory();

	public static class Factory extends Handler.Factory<BannedPlayersHandler> {
		@Override
		public BannedPlayersHandler create(Session session) {
			return new BannedPlayersHandler(session);
		}
	}

	private static final long MESSAGE_THRESHOLD = 1000 * 2;
	private long lastMessage;

	public BannedPlayersHandler(Session session) {
		super(session);
	}

	@Override
	public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet,
			Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
		SetFlag<String> flag = MainPluginClass.getWorldGuardAPI().getBannedPlayersFlag().getWorldGuardFlag();

		if (flag != null) {
			Set<String> bannedUsers = toSet.queryValue(player, flag);

			if (bannedUsers != null) {
				if (getSession().getManager().hasBypass(player, (World) to.getExtent())
						|| player.hasPermission(Permissions.PROTECTION_BANNEDS_BYPASS)) {
					return true;
				}

				if (bannedUsers.contains(player.getUniqueId().toString()) && moveType.isCancellable()) {
					long now = System.currentTimeMillis();

					if ((now - lastMessage) > MESSAGE_THRESHOLD) {
						MessageBuilder.createMessage(MessageString.MESSAGE_PROTECTIONS_BANNED.applyPrefix())
								.sendMessage(Bukkit.getPlayer(player.getUniqueId()));
						lastMessage = now;
					}

					return false;
				}
			}
		}

		return true;
	}

}

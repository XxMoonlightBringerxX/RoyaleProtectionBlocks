package company.pluginName.Modules.PlayersDataPckg.Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import company.pluginName.Permissions;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaDoubleField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import relampagorojo93.LibsCollection.Utils.Shared.Java.StringsHelper;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class PlayerData extends darkpanda73.PandaUtils.Services.PandaPlayerDataModule.Objects.PlayerData {

	@RegisteredPandaField("lang")
	private static final PandaPrefixedStringField MESSAGE_TELEPORT_STAYONSITE = new PandaPrefixedStringField(
			"Message.Teleport.Stay-on-site", "&7Stay still for at least &e%seconds% seconds &7to get teleported...");

	@RegisteredPandaField("config")
	private static final PandaDoubleField SETTINGS_PROTECTION_TELEPORTSTAYSTILLFORSECONDS = new PandaDoubleField(
			"Settings.Protection.Teleport-stay-still-for-seconds", 0D);

	private @Setter(AccessLevel.NONE) Pair<Protection, Long> lastTeleport;
	private @Setter(AccessLevel.NONE) @Getter(AccessLevel.NONE) BukkitTask task;
	private List<Protection> currentProtections = new ArrayList<>();

	public PlayerData(UUID uuid) {
		super(uuid);
	}

	public void setLastTeleport(Protection protection) {
		this.lastTeleport = new Pair<>(protection, System.currentTimeMillis());
	}

	public void teleport(Protection protection) {
		Player pl = Bukkit.getPlayer(this.getUuid());

		Runnable teleport = () -> {
			task = null;

			if (pl != null && pl.isOnline()) {
				pl.teleport(protection.getHome());
				setLastTeleport(protection);
			}
		};

		if (SETTINGS_PROTECTION_TELEPORTSTAYSTILLFORSECONDS.getContent() > 0D
				&& !pl.hasPermission(Permissions.PROTECTION_TELEPORT_BYPASS)) {
			this.task = TasksUtils.executeWithDelay(teleport,
					SETTINGS_PROTECTION_TELEPORTSTAYSTILLFORSECONDS.getContent().longValue() * 20L);
			MessageTemplate.inst(MESSAGE_TELEPORT_STAYONSITE.applyPrefix()).setReplacements(new Replacement("%seconds%",
					() -> StringsHelper.toDecimal(SETTINGS_PROTECTION_TELEPORTSTAYSTILLFORSECONDS.getContent(), 2)))
					.process().sendMessage(pl);
		} else {
			teleport.run();
		}
	}

	public boolean isTeleporting() {
		return this.task != null;
	}

	public void cancelTeleport() {
		if (this.task != null) {
			if (!this.task.isCancelled()) {
				this.task.cancel();
			}
			this.task = null;
		}
	}

}

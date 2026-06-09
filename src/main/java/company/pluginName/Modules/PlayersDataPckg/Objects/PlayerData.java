package company.pluginName.Modules.PlayersDataPckg.Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.SQLPckg.SQLService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Services.PandaBossBarModule.Objects.TimedPandaBossBar;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaDoubleField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import darkpanda73.PandaUtils.Utilities.Java.String.StringUtilities;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.Protections.Objects.CachedQuery;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.Protections.Objects.ProtectionInvitation;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class PlayerData extends darkpanda73.PandaUtils.Services.PandaPlayerDataModule.Objects.PlayerData {

	public static enum InvitationRequirement {
		ALL_DENIED, INVITATION_REQUIRED, ALL_ACCEPTED;

		public InvitationRequirement previous() {
			return values()[(values().length + ordinal() - 1) % values().length];
		}

		public InvitationRequirement next() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}
	}

	@RegisteredPandaField("lang")
	private static final PandaPrefixedStringField MESSAGE_TELEPORT_STAYONSITE = new PandaPrefixedStringField(
			"Message.Teleport.Stay-on-site", "&7Stay still for at least &e%seconds% seconds &7to get teleported...");

	@RegisteredPandaField("config")
	private static final PandaDoubleField SETTINGS_PROTECTION_TELEPORTSTAYSTILLFORSECONDS = new PandaDoubleField(
			"Settings.Protection.Teleport-stay-still-for-seconds", 0D);

	@RegisteredPandaField("config")
	private static final PandaBooleanField SETTINGS_PLAYERSETTINGS_DEFAULTS_AUTOFLIGHT = new PandaBooleanField(
			"Settings.Player-settings.Defaults.Auto-flight", false);

	@PandaInject
	private static SQLService sqlService;

	private @Setter(AccessLevel.NONE) Pair<Protection, Long> lastTeleport;
	private @Setter(AccessLevel.NONE) @Getter(AccessLevel.NONE) BukkitTask task;
	private List<? extends IProtection> currentProtections = new ArrayList<>();

	// Player settings

	private boolean autoFlight = SETTINGS_PLAYERSETTINGS_DEFAULTS_AUTOFLIGHT.isTrue();
	private InvitationRequirement invitationRequirement = InvitationRequirement.ALL_ACCEPTED;

	// Cache fields

	private CachedQuery<?> lastCachedQuery;
	private long lastBlockedProtectionMessage = 0L;
	private boolean staffMode = false;
	private TimedPandaBossBar bossBar;

	private List<ProtectionInvitation> protectionInvitations = new ArrayList<>();

	public PlayerData(UUID uuid) {
		super(uuid);
	}

	public void setAutoFlightAndSave(boolean autoFlight) {
		this.setAutoFlight(autoFlight);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.savePlayerData(this);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public void setInvitationRequirementAndSave(InvitationRequirement invitationRequirement) {
		this.setInvitationRequirement(invitationRequirement);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.savePlayerData(this);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
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
				&& !PermissionsService.TELEPORT_BYPASS.hasPermission(pl)) {
			this.task = TasksUtils.executeWithDelay(teleport,
					SETTINGS_PROTECTION_TELEPORTSTAYSTILLFORSECONDS.getContent().longValue() * 20L);
			MessageTemplate.inst(MESSAGE_TELEPORT_STAYONSITE.applyPrefix()).setReplacements(new Replacement("%seconds%",
					() -> StringUtilities.toDecimal(SETTINGS_PROTECTION_TELEPORTSTAYSTILLFORSECONDS.getContent(), 2)))
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

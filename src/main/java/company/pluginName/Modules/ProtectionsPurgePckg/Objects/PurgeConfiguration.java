package company.pluginName.Modules.ProtectionsPurgePckg.Objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import company.pluginName.Modules.PlayerGuardPckg.PlayerGuardService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import darkpanda73.PandaUtils.Utilities.Java.Time.TimeUtilities;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

@Data
@Accessors(chain = true)
public class PurgeConfiguration {

	@PandaInject
	private static PlayerGuardService playerGuardService;

	public static final long DAYS_IN_MILLIS = 86400000;
	public static final long HOURS_IN_MILLIS = 3600000;
	public static final long MINUTES_IN_MILLIS = 60000;

	private int minutes = 0;
	private int hours = 0;
	private int days = 0;
	private long millis = 0;
	private boolean showIgnoredPlayers = false;
	private @Setter(AccessLevel.NONE) List<String> worldsNames = new ArrayList<>();

	public void copy(PurgeConfiguration configuration) {
		this.minutes = configuration.minutes;
		this.hours = configuration.hours;
		this.days = configuration.days;
		this.millis = configuration.millis;
		this.worldsNames = new ArrayList<>(configuration.worldsNames);
	}

	public String toString() {
		return TimeUtilities
				.secondsToString((long) (millis / 1000) + (minutes * 60L) + (hours * 3600L) + (days * 86400L));
	}

	public long getRemainingTime(IProtection protection) {
		long currentTime = System.currentTimeMillis();
		long purgeTime = (days * DAYS_IN_MILLIS) + (hours * HOURS_IN_MILLIS) + (hours * MINUTES_IN_MILLIS) + millis;
		long olderThan = currentTime - purgeTime;

		long guardExpirationDate = Math.max(protection.getGuardExpirationDate(),
				playerGuardService.getGuardExpirationDate(protection.getOwnerUuid()));

		return guardExpirationDate != Long.MAX_VALUE
				? Math.max(Math.max(protection.getOwnerLastPlayed(), guardExpirationDate - purgeTime) - olderThan, 0)
				: Long.MAX_VALUE;
	}

	public Stream<Pair<Protection, Long>> getRemainingTime(Collection<Protection> protections) {
		long currentTime = System.currentTimeMillis();
		long purgeTime = (days * DAYS_IN_MILLIS) + (hours * HOURS_IN_MILLIS) + (hours * MINUTES_IN_MILLIS) + millis;
		long olderThan = currentTime - purgeTime;

		return protections.stream().map(protection -> {
			IProtection prot = protection.getParentProtection();

			long guardExpirationDate = Math.max(prot.getGuardExpirationDate(),
					playerGuardService.getGuardExpirationDate(prot.getOwnerUuid()));

			return Pair.of(protection,
					Math.max(Math.max(prot.getOwnerLastPlayed(), guardExpirationDate - purgeTime) - olderThan, 0));
		}).filter(Objects::nonNull);
	}

	public static enum BasedOn {
		PLAYER_LAST_TIME, REGION_CREATED_DATE;
	}

}

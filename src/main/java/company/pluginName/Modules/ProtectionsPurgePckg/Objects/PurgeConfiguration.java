package company.pluginName.Modules.ProtectionsPurgePckg.Objects;

import java.util.Collection;
import java.util.stream.Stream;

import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Utils.TimeUtils;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import lombok.Data;
import lombok.experimental.Accessors;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.IProtection;

@Data
@Accessors(chain = true)
public class PurgeConfiguration {

	public static final long DAYS_IN_MILLIS = 86400000;
	public static final long HOURS_IN_MILLIS = 3600000;
	public static final long MINUTES_IN_MILLIS = 60000;

	private int minutes = 0;
	private int hours = 0;
	private int days = 0;
	private long millis = 0;
	private BasedOn basedOn = BasedOn.PLAYER_LAST_TIME;
	private boolean showIgnoredPlayers = false;

	public void copy(PurgeConfiguration configuration) {
		this.minutes = configuration.minutes;
		this.hours = configuration.hours;
		this.days = configuration.days;
		this.millis = configuration.millis;
		this.basedOn = configuration.basedOn;
	}

	public String toString() {
		return TimeUtils.secondsToString((long) (millis / 1000) + (minutes * 60L) + (hours * 3600L) + (days * 86400L));
	}

	public long getRemainingTime(IProtection protection) {
		long currentTime = System.currentTimeMillis();
		long olderThan = currentTime - (days * DAYS_IN_MILLIS) - (hours * HOURS_IN_MILLIS) - (hours * MINUTES_IN_MILLIS)
				- millis;

		switch (basedOn) {
		case PLAYER_LAST_TIME:
			return Math.max(protection.getOwnerLastPlayed() - olderThan, 0);
		case REGION_CREATED_DATE:
			return Math.max(protection.getCreatedDate() - olderThan, 0);
		}
		return Long.MAX_VALUE;
	}

	public Stream<Pair<Protection, Long>> getRemainingTime(Collection<Protection> protections) {
		return protections.stream().map(protection -> {
			long currentTime = System.currentTimeMillis();
			long olderThan = currentTime - (days * DAYS_IN_MILLIS) - (hours * HOURS_IN_MILLIS)
					- (hours * MINUTES_IN_MILLIS) - millis;

			switch (basedOn) {
			case PLAYER_LAST_TIME:
				return Pair.of(protection, Math.max(protection.getOwnerLastPlayed() - olderThan, 0));
			case REGION_CREATED_DATE:
				return Pair.of(protection, Math.max(protection.getCreatedDate() - olderThan, 0));
			}
			return Pair.of(protection, Long.MAX_VALUE);
		});
	}

	public static enum BasedOn {
		PLAYER_LAST_TIME, REGION_CREATED_DATE;
	}

}

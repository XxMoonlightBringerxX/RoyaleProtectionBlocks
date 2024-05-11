package company.pluginName.Modules.ProtectionsPurgePckg.Objects;

import company.pluginName.Utils.TimeUtils;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PurgeConfiguration {

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

	public static enum BasedOn {
		PLAYER_LAST_TIME, REGION_CREATED_DATE;
	}

}

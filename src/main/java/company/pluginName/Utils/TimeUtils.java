package company.pluginName.Utils;

public class TimeUtils {

	private static final char[] TIME_UNITS = { 's', 'm', 'h', 'd', 'w', 'M', 'y' };
	private static final long[] TIME_UNITS_TO_SECONDS = { 1, 60, 3600, 86400, 604800, 2592000, 31536000 };

	public static long stringToSeconds(String string) throws NumberFormatException {
		long millis = 0;
		String[] split = string.split(" ");
		for (String segment : split) {
			if (!segment.isEmpty()) {
				char timeUnit = segment.charAt(segment.length() - 1);
				int i = 0;
				for (; i < TIME_UNITS.length; i++) {
					if (TIME_UNITS[i] == timeUnit) {
						millis += (Long.parseLong(segment.substring(0, segment.length() - 1)) * TIME_UNITS_TO_SECONDS[i]);
						break;
					}
				}
				if (i == TIME_UNITS.length) {
					throw new NumberFormatException(String.format("Time unit not found for '%s'", timeUnit));
				}
			}
		}
		return millis;
	}

}

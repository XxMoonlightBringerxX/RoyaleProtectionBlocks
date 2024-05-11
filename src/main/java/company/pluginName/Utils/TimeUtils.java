package company.pluginName.Utils;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TimeUtils {

	private static final char[] TIME_UNITS = { 's', 'm', 'h', 'd', 'w', 'M', 'y' };
	private static final long[] TIME_UNITS_TO_SECONDS = { 1, 60, 3600, 86400, 604800, 2592000, 31536000 };

	public static long stringToSeconds(String string) throws NumberFormatException {
		long seconds = 0;
		String[] split = string.split(" ");
		for (String segment : split) {
			if (!segment.isEmpty()) {
				char timeUnit = segment.charAt(segment.length() - 1);
				int i = 0;
				for (; i < TIME_UNITS.length; i++) {
					if (TIME_UNITS[i] == timeUnit) {
						seconds += (Long.parseLong(segment.substring(0, segment.length() - 1))
								* TIME_UNITS_TO_SECONDS[i]);
						break;
					}
				}
				if (i == TIME_UNITS.length) {
					throw new NumberFormatException(String.format("Time unit not found for '%s'", timeUnit));
				}
			}
		}
		return seconds;
	}

	public static String secondsToString(long seconds) throws NumberFormatException {
		int[] fragments = new int[TIME_UNITS.length];
		for (int i = 0; i < TIME_UNITS.length; i++) {
			fragments[i] = (int) (seconds / TIME_UNITS_TO_SECONDS[TIME_UNITS_TO_SECONDS.length - i - 1]);
			seconds = seconds % TIME_UNITS_TO_SECONDS[TIME_UNITS_TO_SECONDS.length - i - 1];
		}
		return IntStream.range(0, fragments.length)
				.mapToObj(index -> fragments[index] > 0
						? String.format("%d%s", fragments[index], TIME_UNITS[TIME_UNITS.length - index - 1])
						: null)
				.filter(Objects::nonNull).collect(Collectors.joining(" "));
	}

}

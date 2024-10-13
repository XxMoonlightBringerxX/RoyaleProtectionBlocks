package company.pluginName.Modules.BStatsPckg.Objects;

import org.bstats.charts.SimplePie;

public class PremiumVersionCustomChart extends SimplePie {

	private static final String VALUE = "PREMIUM";

	public PremiumVersionCustomChart() {
		super("premium_version", () -> VALUE);
	}

}

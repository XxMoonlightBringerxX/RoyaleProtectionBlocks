package company.pluginName.Modules.BStatsPckg.Objects;

import org.bstats.charts.SingleLineChart;

import company.pluginName.Modules.ProtectionsPckg.ProtectionsServiceImpl;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;

public class ProvidedProtectionsCustomChart extends SingleLineChart {

	@PandaInject
	private static ProtectionsServiceImpl protectionsService;

	public ProvidedProtectionsCustomChart() {
		super("provided_protections", () -> {
			if (protectionsService != null) {
				return protectionsService.getProtectionByRegion().size();
			}
			return 0;
		});
	}

}

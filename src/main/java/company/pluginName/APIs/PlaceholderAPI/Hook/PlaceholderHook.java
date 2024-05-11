package company.pluginName.APIs.PlaceholderAPI.Hook;

import java.util.Arrays;
import java.util.List;

import company.pluginName.APIs.PlaceholderAPI.Placeholders.PlaceHolder;
import darkpanda73.PandaUtils.PandaAPIs.Defaults.PlaceholderAPI.Hook.PandaAbstractPlaceholderHook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderHook extends PandaAbstractPlaceholderHook {

	@Override
	public List<PlaceholderExpansion> getPlaceholders() {
		return Arrays.asList(new PlaceHolder());
	}

}

package company.pluginName.APIs.PlaceholderAPI;

import java.util.Arrays;
import java.util.List;

import company.pluginName.MainPluginClass;
import company.pluginName.APIs.PlaceholderAPI.Placeholders.PlaceHolder;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPI extends relampagorojo93.LibsCollection.Utils.Bukkit.APIs.PlaceholderAPI.PlaceholderAPI {

	public PlaceholderAPI() {
		super(MainPluginClass.getPlugin());
	}

	@Override
	public List<PlaceholderExpansion> getPlaceholders() {
		return Arrays.asList(new PlaceHolder());
	}

	@Override
	public String getPrefix() {
		return MessageString.PREFIX.toString();
	}

	@Override
	public boolean isOptional() {
		return false;
	}

}

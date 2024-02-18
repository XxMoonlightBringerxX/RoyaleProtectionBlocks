package company.pluginName.Bukkit.Inventories.Shared;

import java.util.function.Consumer;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Defaults.DefaultSearchPlayerInventory;

@Inventory("searchplayer")
public class SearchPlayerInventory extends DefaultSearchPlayerInventory {

	public SearchPlayerInventory(Player player, Consumer<OfflinePlayer> action) {
		super(player, action);
	}

}

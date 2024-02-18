package company.pluginName.Bukkit.Inventories.Shared;

import java.util.function.Consumer;

import org.bukkit.World;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Defaults.DefaultSearchWorldInventory;

@Inventory("searchworld")
public class SearchWorldsInventory extends DefaultSearchWorldInventory {

	public SearchWorldsInventory(Player player, Consumer<World> action) {
		super(player, action);
	}

}

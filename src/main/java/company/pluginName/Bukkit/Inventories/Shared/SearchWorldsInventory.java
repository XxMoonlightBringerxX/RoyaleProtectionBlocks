package company.pluginName.Bukkit.Inventories.Shared;

import java.util.function.Consumer;

import org.bukkit.World;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Defaults.DefaultSearchWorldInventory;

@Inventory("searchworld")
public class SearchWorldsInventory extends DefaultSearchWorldInventory {

	public SearchWorldsInventory(Player player, Consumer<World> action) {
		super(player, action);
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).process().toString();
	}

}

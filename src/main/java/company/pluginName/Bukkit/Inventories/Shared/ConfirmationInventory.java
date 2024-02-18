package company.pluginName.Bukkit.Inventories.Shared;

import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Defaults.DefaultConfirmationInventory;

@Inventory("confirmation")
public class ConfirmationInventory extends DefaultConfirmationInventory {

	public ConfirmationInventory(Player player, Runnable action) {
		super(player, action);
	}

	public ConfirmationInventory(Player player, Runnable action, boolean previousOnAction) {
		super(player, action, previousOnAction);
	}

}

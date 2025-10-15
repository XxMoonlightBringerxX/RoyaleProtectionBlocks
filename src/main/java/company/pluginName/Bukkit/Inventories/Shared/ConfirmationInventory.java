package company.pluginName.Bukkit.Inventories.Shared;

import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Defaults.DefaultConfirmationInventory;

@Inventory("confirmation")
public class ConfirmationInventory extends DefaultConfirmationInventory {

	public ConfirmationInventory(Player player, Runnable action) {
		super(player, action);
	}

	public ConfirmationInventory(Player player, Runnable action, boolean previousOnAction) {
		super(player, action, previousOnAction);
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).process().toString();
	}

}

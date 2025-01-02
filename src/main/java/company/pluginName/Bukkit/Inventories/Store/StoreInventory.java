package company.pluginName.Bukkit.Inventories.Store;

import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.ChestInventoryObject;

@Inventory("store")
public class StoreInventory extends ChestInventoryObject {

	public StoreInventory(Player player) {
		super(player);
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).toString();
	}

	@ItemExecutor("Protections-button")
	private void onClickProtectionsButton() {
		new ProtectionsStoreInventory(getPlayer()).openInventory();
	}

	@ItemExecutor("Protection-blocks-button")
	private void onClickProtectionBlocksButton() {
		new ProtectionBlocksStoreInventory(getPlayer()).openInventory();
	}

	@ItemExecutor("Close-button")
	private void onClickCloseButton() {
		goToPreviousInventory();
	}

}

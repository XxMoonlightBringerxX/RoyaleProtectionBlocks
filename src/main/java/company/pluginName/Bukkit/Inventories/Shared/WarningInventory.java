package company.pluginName.Bukkit.Inventories.Shared;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemGenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Item;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Defaults.DefaultConfirmationInventory;

@Inventory("warning")
public class WarningInventory extends DefaultConfirmationInventory {

	private String infoTitle;
	private List<String> infoDescription;

	public WarningInventory(Player player, String infoTitle, List<String> infoDescription, Runnable action) {
		super(player, action);
		this.infoTitle = infoTitle;
		this.infoDescription = infoDescription;
	}

	public WarningInventory(Player player, String infoTitle, List<String> infoDescription, Runnable action,
			boolean previousOnAction) {
		super(player, action, previousOnAction);
		this.infoTitle = infoTitle;
		this.infoDescription = infoDescription;
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).process().toString();
	}

	@ItemGenerator("Info-sign")
	private ItemStack onGenerateInfoSign(Item item) {
		return ItemBuilder.inst().fromMap(item.getData(), Item.DISPLAYITEM_KEY).setName(infoTitle)
				.setLore(infoDescription).build();
	}

}

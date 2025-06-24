package company.pluginName.Bukkit.Inventories.Shared;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

@Inventory("searchprotection")
public class SearchProtectionInventory extends PagedChestInventoryObject<IProtection> {

	@PandaInject
	private static PlaceholdersService placeholdersService;

	private List<? extends IProtection> protections;
	private Consumer<IProtection> action;

	public SearchProtectionInventory(Player player, List<? extends IProtection> protections,
			Consumer<IProtection> action) {
		super(player);

		this.protections = protections;
		this.action = action;
	}

	@Override
	protected List<? extends IProtection> getEntityList() {
		return this.protections;
	}

	@Override
	protected ItemStack generateEntityItem(IProtection protection) {
		return ItemBuilder.inst().fromMap(getChestInventoryData().getCustomFields(), "Entity")
				.setReplacements(placeholdersService.getProtectionReplacements(protection))
				.apply(((Protection) protection).getDisplayItemOrDefault().clone());
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, IProtection entity) {
		closeInventory();
		action.accept(entity);
	}

	@ItemExecutor("Close-button")
	private void onClickClose() {
		goToPreviousInventory();
	}

}
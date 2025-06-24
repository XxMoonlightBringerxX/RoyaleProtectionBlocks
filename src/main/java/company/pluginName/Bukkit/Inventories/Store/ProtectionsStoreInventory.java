package company.pluginName.Bukkit.Inventories.Store;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Utils.EconomyUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemGenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Item;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import darkpanda73.PandaUtils.Utilities.Java.Arrays.ArrayUtilities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionPurchaseRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSellRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionTeleportHomeRequestInput;

@Inventory("protections_store")
public class ProtectionsStoreInventory extends PagedChestInventoryObject<Protection> {

	public static final String ENTITY_PURCHASELORELINE_PATH = "Entity.Purchase-lore-line";
	public static final String ENTITY_TELEPORTLORELINE_PATH = "Entity.Teleport-lore-line";
	public static final String ENTITY_TELEPORTNOTSETLORELINE_PATH = "Entity.Teleport-not-set-lore-line";
	public static final String ENTITY_REMOVEPRICELORELINE_PATH = "Entity.Remove-price-lore-line";

	@AllArgsConstructor
	@Getter
	private static enum Filter {
		ALL((player, protection) -> true),
		AFFORDABLE((player, protection) -> EconomyUtilities.canAfford(player, protection.getPrice())),
		OWN((player, protection) -> protection.getOwnerUuid().equals(player.getUniqueId()));

		private BiFunction<Player, Protection, Boolean> filterFunction;

		public Filter previous() {
			return values()[(values().length + ordinal() - 1) % values().length];
		}

		public Filter next() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}
	}

	@AllArgsConstructor
	@Getter
	private static enum Sort {
		PRICE((protection1, protection2) -> Double.compare(protection1.getPrice(), protection2.getPrice())),
		NAME((protection1, protection2) -> protection1.getDisplayName().compareTo(protection2.getDisplayName())),
		WORLD((protection1, protection2) -> protection1.getWorldName().compareTo(protection2.getWorldName()));

		private Comparator<Protection> sortFunction;

		public Sort previous() {
			return values()[(values().length + ordinal() - 1) % values().length];
		}

		public Sort next() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}
	}

	@PandaInject
	private static PlaceholdersService placeholdersService;

	private Filter currentFilter = Filter.ALL;
	private Sort currentSort = Sort.PRICE;

	public ProtectionsStoreInventory(Player player) {
		super(player);
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).process().toString();
	}

	@Override
	protected List<Protection> getEntityList() {
		return RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService().findProtectionsOnSale()
				.filter(prot -> currentFilter.getFilterFunction().apply(getPlayer(), prot))
				.sorted(currentSort.getSortFunction()).collect(Collectors.toList());
	}

	@Override
	protected ItemStack generateEntityItem(Protection entity) {
		Replacement[] protectionReplacements = placeholdersService.getProtectionReplacements(entity);
		Replacement[] protectionBlockReplacements = placeholdersService
				.getProtectionBlockReplacements(entity.getProtectionBlock());

		ItemBuilder itemBuilder = ItemBuilder.inst().fromMap(getChestInventoryData().getCustomFields(), "Entity")
				.setLore(new ArrayList<>())
				.setReplacements(ArrayUtilities.join(
						new Replacement[protectionReplacements.length + protectionBlockReplacements.length],
						protectionReplacements, protectionBlockReplacements));

		itemBuilder.getLore().addAll(getChestInventoryData().getEntityLore());

		Location homeLocation = entity.getHome();

		boolean isMainOwner = entity.getOwnerUuid().equals(getPlayer().getUniqueId());

		List<String> extraLore = new ArrayList<>();

		if (!isMainOwner) {
			extraLore.add(getChestInventoryData().getCustomFields().get(ENTITY_PURCHASELORELINE_PATH).toString());
		}

		if (Settings.SETTINGS_STORE_ALLOWTELEPORT.isTrue()) {
			extraLore.add(homeLocation != null
					? getChestInventoryData().getCustomFields().get(ENTITY_TELEPORTLORELINE_PATH).toString()
					: getChestInventoryData().getCustomFields().get(ENTITY_TELEPORTNOTSETLORELINE_PATH).toString());
		}

		if (isMainOwner) {
			extraLore.add(getChestInventoryData().getCustomFields().get(ENTITY_REMOVEPRICELORELINE_PATH).toString());
		}

		if (!extraLore.isEmpty()) {
			extraLore.add(0, "&0");
			itemBuilder.getLore().addAll(extraLore);
		}

		return itemBuilder.apply(entity.getDisplayItemOrDefault().clone());
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, Protection entity) {
		boolean isMainOwner = entity.getOwnerUuid().equals(getPlayer().getUniqueId());

		if (e.isLeftClick()) {
			if (e.isShiftClick() && Settings.SETTINGS_STORE_ALLOWTELEPORT.isTrue()) {
				Location homeLocation = entity.getHome();
				if (homeLocation != null) {
					try {
						RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
								.protectionTeleportHomeRequest(
										ProtectionTeleportHomeRequestInput.inst(getPlayer(), entity)
												.setIgnoreCost(Settings.SETTINGS_STORE_IGNORETELEPORTCOST.isTrue()));
					} catch (RoyaleProtectionBlocksException e1) {
						e1.sendError(getPlayer());
					}
				}
			} else if (!isMainOwner) {
				if (Settings.SETTINGS_STORE_REQUESTCONFIRMATIONONPURCHASETHROUGHGUI.isTrue()) {
					new ConfirmationInventory(getPlayer(), () -> {
						try {
							RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
									.protectionPurchaseRequest(
											ProtectionPurchaseRequestInput.inst(getPlayer(), entity));

							MessageTemplate
									.inst(Messages.MESSAGE_PROTECTIONS_PURCHASE_PURCHASEDSUCCESSFULLY.applyPrefix())
									.process().sendMessage(getPlayer());

							updateEntityList();
						} catch (RoyaleProtectionBlocksException e1) {
							e1.sendError(getPlayer());
						}
					}).openInventory();
				} else {
					try {
						RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
								.protectionPurchaseRequest(ProtectionPurchaseRequestInput.inst(getPlayer(), entity));

						MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_PURCHASE_PURCHASEDSUCCESSFULLY.applyPrefix())
								.process().sendMessage(getPlayer());

						updateEntityList();
						updateInventory();
					} catch (RoyaleProtectionBlocksException e1) {
						e1.sendError(getPlayer());
					}
				}
			}
		} else if (e.isRightClick() && isMainOwner) {
			new ConfirmationInventory(getPlayer(), () -> {
				try {
					RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
							.protectionSellRequest(ProtectionSellRequestInput.inst(getPlayer(), entity, 0));
					updateEntityList();
					updateInventory();
					MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_PURCHASE_PRICEUNSETSUCCESSFULLY.applyPrefix())
							.process().sendMessage(getPlayer());
				} catch (RoyaleProtectionBlocksException e1) {
					e1.sendError(getPlayer());
				}
			}).openInventory();
		}
	}

	@ItemExecutor("Close-button")
	private void onClickCloseButton() {
		goToPreviousInventory();
	}

	@ItemGenerator("All-filter-button")
	private ItemStack generateAllFilterItem(Item item) {
		return this.currentFilter == Filter.ALL ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemGenerator("Affordable-filter-button")
	private ItemStack generateAffordableFilterItem(Item item) {
		return this.currentFilter == Filter.AFFORDABLE ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemGenerator("Own-filter-button")
	private ItemStack generateOwnFilterItem(Item item) {
		return this.currentFilter == Filter.OWN ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemGenerator("Price-sort-button")
	private ItemStack generatePriceSortItem(Item item) {
		return this.currentSort == Sort.PRICE ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemGenerator("Name-sort-button")
	private ItemStack generateNameSortItem(Item item) {
		return this.currentSort == Sort.NAME ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemGenerator("World-sort-button")
	private ItemStack generateWorldSortItem(Item item) {
		return this.currentSort == Sort.WORLD ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemExecutor("All-filter-button")
	@ItemExecutor("Affordable-filter-button")
	@ItemExecutor("Own-filter-button")
	private void onClickFilterButton() {
		this.currentFilter = this.currentFilter.next();
		updateEntityList();
		updateInventory();
	}

	@ItemExecutor("Price-sort-button")
	@ItemExecutor("Name-sort-button")
	@ItemExecutor("World-sort-button")
	private void onClickSortButton() {
		this.currentSort = this.currentSort.next();
		updateEntityList();
		updateInventory();
	}

}

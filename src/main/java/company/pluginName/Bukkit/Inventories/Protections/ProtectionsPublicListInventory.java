package company.pluginName.Bukkit.Inventories.Protections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemGenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Item;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import darkpanda73.PandaUtils.Utilities.Java.Arrays.ArrayUtilities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionTeleportHomeRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionTogglePublicAccessRequestInput;

@Inventory("protections_publiclist")
public class ProtectionsPublicListInventory extends PagedChestInventoryObject<Protection> {

	public static final String ENTITY_TELEPORTLORELINE_PATH = "Entity.Teleport-lore-line";
	public static final String ENTITY_TELEPORTNOTSETLORELINE_PATH = "Entity.Teleport-not-set-lore-line";
	public static final String ENTITY_SETPRIVATELORELINE_PATH = "Entity.Set-private-lore-line";

	@AllArgsConstructor
	@Getter
	private static enum Filter {
		ALL((player, protection) -> true),
		OWN((player, protection) -> protection.getOwnerUuid().equals(player.getUniqueId())),
		OTHERS((player, protection) -> !protection.getOwnerUuid().equals(player.getUniqueId()));

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
		CREATION_DATE(
				(protection1, protection2) -> Long.compare(protection1.getCreatedDate(), protection2.getCreatedDate())),
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
	private Sort currentSort = Sort.CREATION_DATE;

	public ProtectionsPublicListInventory(Player player) {
		super(player);
	}

	@Override
	protected List<Protection> getEntityList() {
		return RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService().findPublicProtections()
				.filter(protection -> currentFilter.getFilterFunction().apply(getPlayer(), protection))
				.sorted(this.currentSort.getSortFunction()).collect(Collectors.toList());

	}

	@Override
	protected ItemStack generateEntityItem(Protection protection) {
		Location homeLocation = protection.getHome();
		Replacement[] protectionReplacements = placeholdersService.getProtectionReplacements(protection);
		Replacement[] protectionBlockReplacements = placeholdersService
				.getProtectionBlockReplacements(protection.getProtectionBlock());

		ItemBuilder itemBuilder = ItemBuilder.inst().fromMap(getChestInventoryData().getCustomFields(), "Entity")
				.setReplacements(ArrayUtilities.join(
						new Replacement[protectionReplacements.length + protectionBlockReplacements.length],
						protectionReplacements, protectionBlockReplacements));

		List<String> extraLore = new ArrayList<>();
		extraLore.add("&0");
		extraLore.add(homeLocation != null
				? getChestInventoryData().getCustomFields().get(ENTITY_TELEPORTLORELINE_PATH).toString()
				: getChestInventoryData().getCustomFields().get(ENTITY_TELEPORTNOTSETLORELINE_PATH).toString());

		if (ProtectionUtilities.canTogglePublicAccess(protection, getPlayer())) {
			extraLore.add(getChestInventoryData().getCustomFields().get(ENTITY_SETPRIVATELORELINE_PATH).toString());
		}

		itemBuilder.getLore().addAll(extraLore);

		return itemBuilder.apply(protection.getDisplayItemOrDefault().clone());
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, Protection protection) {
		Location homeLocation = protection.getHome();

		if (e.getClick() == ClickType.LEFT) {
			if (homeLocation != null) {
				try {
					RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
							.protectionTeleportHomeRequest(
									ProtectionTeleportHomeRequestInput.inst(getPlayer(), protection).setIgnoreCost(
											Settings.SETTINGS_PROTECTION_PUBLICLIST_IGNORETELEPORTCOST.isTrue()));
				} catch (RoyaleProtectionBlocksException e1) {
					e1.sendError(getPlayer());
				}
			}
		} else if (e.getClick() == ClickType.RIGHT) {
			new ConfirmationInventory(getPlayer(), () -> {
				try {
					RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
							.protectionTogglePublicAccessRequest(
									ProtectionTogglePublicAccessRequestInput.inst(getPlayer(), protection, false));
					updateEntityList();
					updateInventory();
					MessageTemplate
							.inst(Messages.MESSAGE_PROTECTIONS_PUBLICACCESS_SETTOPRIVATESUCCESSFULLY.applyPrefix())
							.process().sendMessage(getPlayer());
				} catch (RoyaleProtectionBlocksException e1) {
					e1.sendError(getPlayer());
				}
			}).openInventory();
		}
	}

	@ItemGenerator("All-filter-button")
	private ItemStack generateAllFilterItem(Item item) {
		return this.currentFilter == Filter.ALL ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemGenerator("Own-filter-button")
	private ItemStack generateOwnFilterItem(Item item) {
		return this.currentFilter == Filter.OWN ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemGenerator("Others-filter-button")
	private ItemStack generateOthersFilterItem(Item item) {
		return this.currentFilter == Filter.OTHERS ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemGenerator("Creation-date-sort-button")
	private ItemStack generatePriceSortItem(Item item) {
		return this.currentSort == Sort.CREATION_DATE ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
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
	@ItemExecutor("Own-filter-button")
	@ItemExecutor("Others-filter-button")
	private void executeFilter() {
		this.currentFilter = this.currentFilter.next();
		updateEntityList();
		updateInventory();
	}

	@ItemExecutor("Creation-date-sort-button")
	@ItemExecutor("Name-sort-button")
	@ItemExecutor("World-sort-button")
	private void onClickSortButton() {
		this.currentSort = this.currentSort.next();
		updateEntityList();
		updateInventory();
	}

}

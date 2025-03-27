package company.pluginName.Bukkit.Inventories.Admin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Bukkit.Inventories.Shared.SearchPlayerInventory;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.PlayerHeadCacheService;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemGenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.GeneratedItem;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Item;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import darkpanda73.PandaUtils.Utilities.Java.Arrays.ArrayUtilities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException.Type;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionManagementInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionRemovalInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionTeleportHomeRequestInput;

@Inventory("admin_protections_list")
public class AdminProtectionsListInventory extends PagedChestInventoryObject<Protection> {

	public static final String TITLE_OTHER_PATH = "Title-other";
	public static final String ENTITY_TELEPORTLORELINE_PATH = "Entity.Teleport-lore-line";
	public static final String ENTITY_TELEPORTNOTSETLORELINE_PATH = "Entity.Teleport-not-set-lore-line";
	public static final String ENTITY_EDITLORELINE_PATH = "Entity.Edit-lore-line";
	public static final String ENTITY_DELETELORELINE_PATH = "Entity.Delete-lore-line";

	@AllArgsConstructor
	@Getter
	private static enum Sort {
		CREATION_DATE(
				(protection1, protection2) -> Long.compare(protection1.getCreatedDate(), protection2.getCreatedDate())),
		NAME((protection1, protection2) -> protection1.getDisplayName().compareTo(protection2.getDisplayName())),
		WORLD((protection1, protection2) -> protection1.getWorldName().compareTo(protection2.getWorldName()));

		private Comparator<Protection> sortFunction;

		public Sort previous() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}

		public Sort next() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}
	}

	@PandaInject
	private static PlaceholdersService placeholdersService;

	@PandaInject
	private static PlayerHeadCacheService playerHeadCacheService;

	private OfflinePlayer owner;
	private Sort currentSort = Sort.CREATION_DATE;

	private Replacement totalAmountReplacement = new Replacement("{total_amount}",
			() -> String.valueOf(this.getCurrentEntityList().size()));
	private Replacement ownerReplacement = new Replacement("{playername}", () -> this.owner.getName());

	public AdminProtectionsListInventory(Player player) {
		this(player, null);
	}

	public AdminProtectionsListInventory(Player player, OfflinePlayer owner) {
		super(player);
		this.owner = owner;
		this.updateReplacements();
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).setReplacements(getReplacements()).process().toString();
	}

	@Override
	protected List<Protection> getEntityList() {
		return (owner != null
				? RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
						.findAllowedParentProtectionsByPlayer(owner)
				: RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService().findAllProtections())
				.sorted(this.currentSort.getSortFunction()).collect(Collectors.toList());

	}

	@Override
	protected ItemStack generateEntityItem(Protection protection) {
		final boolean canTeleport = protection.canTeleport(getPlayer());
		final boolean canManage = ProtectionUtilities.canManage(protection, getPlayer());
		final boolean canDelete = ProtectionUtilities.canDelete(protection, getPlayer());

		Location homeLocation = protection.getHome();
		Location loc = protection.getBukkitLocation();
		Replacement[] replacements = {
				new Replacement("{protection}",
						() -> protection.getDisplayName() != null ? protection.getDisplayName()
								: protection.getProtectionId()),
				new Replacement("{protection_owner}",
						() -> protection.getOwnerName() != null ? protection.getOwnerName() : "???"),
				new Replacement("{world}", () -> protection.getWorldName()),
				new Replacement("{location_x}", () -> loc != null ? String.valueOf(loc.getBlockX()) : "???"),
				new Replacement("{location_y}", () -> loc != null ? String.valueOf(loc.getBlockY()) : "???"),
				new Replacement("{location_z}", () -> loc != null ? String.valueOf(loc.getBlockZ()) : "???") };
		Replacement[] protectionReplacements = placeholdersService.getProtectionReplacements(protection);

		ItemBuilder itemBuilder = ItemBuilder.inst().fromMap(getChestInventoryData().getCustomFields(), "Entity")
				.setReplacements(
						ArrayUtilities.join(new Replacement[protectionReplacements.length + replacements.length],
								protectionReplacements, replacements));

		List<String> lore = new ArrayList<>(getChestInventoryData().getEntityLore());
		lore.add("&0");

		if ((homeLocation != null && canTeleport) || protection.isMainOwner(getPlayer().getUniqueId())) {
			lore.add(homeLocation != null
					? getChestInventoryData().getCustomFields().get(ENTITY_TELEPORTLORELINE_PATH).toString()
					: getChestInventoryData().getCustomFields().get(ENTITY_TELEPORTNOTSETLORELINE_PATH).toString());
		}

		if (canManage) {
			lore.add(getChestInventoryData().getCustomFields().get(ENTITY_EDITLORELINE_PATH).toString());
		}

		if (canDelete) {
			lore.add(getChestInventoryData().getCustomFields().get(ENTITY_DELETELORELINE_PATH).toString());
		}

		return itemBuilder.setLore(lore).apply(protection.getDisplayItem().getOrDefault().clone());
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, Protection protection) {
		Location homeLocation = protection.getHome();

		if (e.getClick() == ClickType.LEFT) {
			if (homeLocation != null && protection.canTeleport(getPlayer())) {
				try {
					RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
							.protectionTeleportHomeRequest(
									ProtectionTeleportHomeRequestInput.inst(getPlayer(), protection));
				} catch (RoyaleProtectionBlocksException e1) {
					e1.sendError(getPlayer());
				}
			}
		} else if (e.getClick() == ClickType.RIGHT) {
			try {
				RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
						.openProtectionManagementInventoryRequest(
								OpenProtectionManagementInventoryRequestInput.inst(getPlayer(), protection));
			} catch (RoyaleProtectionBlocksExceptionImpl ex) {
				if (ex.getType() == Type.PROTECTIONS_BLOCKED) {
					ex.sendError(getPlayer());
				}
			}
		} else if (e.getClick() == ClickType.SHIFT_RIGHT) {
			try {
				RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
						.openProtectionRemovalInventoryRequest(
								OpenProtectionRemovalInventoryRequestInput.inst(getPlayer(), protection));
			} catch (RoyaleProtectionBlocksExceptionImpl ex) {
			}
		}

	}

	@ItemGenerator("Player-button")
	private ItemStack generatePlayerButton(Item item) {
		if (this.owner != null) {
			ItemBuilder builder = ItemBuilder.inst().fromItem(item.getItems().get(Item.DISPLAYITEM_KEY));

			builder.setMaterial(Material.PLAYER_HEAD);

			return playerHeadCacheService.processPlayerHead(builder, this.owner.getUniqueId()).getFirst();
		}
		return item.getItems().get(Item.DISPLAYITEM_KEY);
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

	@ItemExecutor("Player-button")
	private void onClickPlayerButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		if (e.isLeftClick()) {
			new SearchPlayerInventory(getPlayer(), (player) -> {
				this.owner = player.toBukkit();
				this.updateReplacements();
				this.updateEntityList();
				this.updateInventory();
			}).openInventory();
		} else if (e.isRightClick()) {
			this.owner = null;
			this.updateReplacements();
			this.updateEntityList();
			this.updateInventory();
		}
	}

	@ItemExecutor("Creation-date-sort-button")
	@ItemExecutor("Name-sort-button")
	@ItemExecutor("World-sort-button")
	private void onClickSortButton() {
		this.currentSort = this.currentSort.next();
		updateEntityList();
		updateInventory();
	}

	private void updateReplacements() {
		setReplacements(this.owner != null ? new Replacement[] { totalAmountReplacement, ownerReplacement }
				: new Replacement[] { totalAmountReplacement });
	}

}

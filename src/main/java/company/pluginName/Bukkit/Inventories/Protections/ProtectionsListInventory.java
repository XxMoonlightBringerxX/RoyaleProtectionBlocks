package company.pluginName.Bukkit.Inventories.Protections;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.API.Services.PlayerInteractionsServiceImpl;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
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
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException.Type;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionManagementInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionRemovalInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionTeleportHomeRequestInput;

@Inventory("protections_list")
public class ProtectionsListInventory extends PagedChestInventoryObject<Protection> {

	public static final String TITLE_OTHER_PATH = "Title-other";
	public static final String ENTITY_TELEPORTLORELINE_PATH = "Entity.Teleport-lore-line";
	public static final String ENTITY_TELEPORTNOTSETLORELINE_PATH = "Entity.Teleport-not-set-lore-line";
	public static final String ENTITY_EDITLORELINE_PATH = "Entity.Edit-lore-line";
	public static final String ENTITY_DELETELORELINE_PATH = "Entity.Delete-lore-line";

	@AllArgsConstructor
	public static enum Filter {
		ALL, OWN, OTHERS;

		public Filter previous() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}

		public Filter next() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}
	}

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static PlaceholdersService placeholdersService;

	@PandaInject
	private static PlayerInteractionsServiceImpl playerInteractionsService;

	private OfflinePlayer owner;
	private Filter filter = Filter.ALL;

	public ProtectionsListInventory(Player player) {
		this(player, player);
	}

	public ProtectionsListInventory(Player player, OfflinePlayer owner) {
		super(player);
		this.owner = owner;

		Replacement[] playerReplacements = placeholdersService.getPlayerReplacements(player);
		setReplacements(playerReplacements);
	}

	@Override
	protected String getTitle() {
		Replacement[] replacements = new Replacement[] { new Replacement("{playername}", () -> owner.getName()) };

		return this.owner.getUniqueId().equals(getPlayer().getUniqueId())
				? MessageTemplate.inst(super.getTitle()).toString()
				: MessageTemplate.inst(getChestInventoryData().getCustomFields().get(TITLE_OTHER_PATH).toString())
						.setReplacements(
								ArrayUtilities.join(new Replacement[getReplacements().length + replacements.length],
										getReplacements(), replacements))
						.process().toString();
	}

	@Override
	protected List<Protection> getEntityList() {
		return protectionsService.getAllowedProtections(owner).filter(protection -> {
			switch (filter) {
			case ALL:
				return true;
			case OWN:
				return protection.isMainOwner(owner.getUniqueId());
			case OTHERS:
				return !protection.isMainOwner(owner.getUniqueId());
			default:
				return false;
			}
		}).collect(Collectors.toList());

	}

	@Override
	protected ItemStack generateEntityItem(Protection protection) {
		final boolean canTeleport = ProtectionUtilities.canTeleport(protection, getPlayer());
		final boolean canManage = ProtectionUtilities.canManage(protection, getPlayer());
		final boolean canDelete = ProtectionUtilities.canDelete(protection, getPlayer());

		Location homeLocation = protection.getHome();
		Location loc = protection.getBukkitLocation();
		Replacement[] replacements = {
				new Replacement("{protection}",
						() -> protection.getDisplayName() != null ? protection.getDisplayName()
								: protection.getRegionId()),
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
			if (homeLocation != null && ProtectionUtilities.canTeleport(protection, getPlayer())) {
				try {
					playerInteractionsService.protectionTeleportHomeRequest(
							ProtectionTeleportHomeRequestInput.inst(getPlayer(), protection));
				} catch (RoyaleProtectionBlocksException e1) {
					e1.sendError(getPlayer());
				}
			}
		} else if (e.getClick() == ClickType.RIGHT) {
			try {
				playerInteractionsService.openProtectionManagementInventoryRequest(
						OpenProtectionManagementInventoryRequestInput.inst(getPlayer(), protection));
			} catch (RoyaleProtectionBlocksExceptionImpl ex) {
				if (ex.getType() == Type.PROTECTIONS_BLOCKED) {
					ex.sendError(getPlayer());
				}
			}
		} else if (e.getClick() == ClickType.SHIFT_RIGHT) {
			try {
				playerInteractionsService.openProtectionRemovalInventoryRequest(
						OpenProtectionRemovalInventoryRequestInput.inst(getPlayer(), protection));
			} catch (RoyaleProtectionBlocksExceptionImpl ex) {
			}
		}

	}

	@ItemGenerator("All-filter-button")
	private ItemStack generateAllFilterItem(Item item) {
		return this.filter == Filter.ALL ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemGenerator("Own-filter-button")
	private ItemStack generateOwnFilterItem(Item item) {
		return this.filter == Filter.OWN ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemGenerator("Others-filter-button")
	private ItemStack generateOthersFilterItem(Item item) {
		return this.filter == Filter.OTHERS ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemExecutor("All-filter-button")
	@ItemExecutor("Own-filter-button")
	@ItemExecutor("Others-filter-button")
	private void executeFilter() {
		this.filter = this.filter.next();
		updateEntityList();
		updateInventory();
	}

}

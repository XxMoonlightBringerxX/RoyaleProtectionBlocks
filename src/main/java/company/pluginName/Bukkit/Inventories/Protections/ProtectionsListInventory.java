package company.pluginName.Bukkit.Inventories.Protections;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemGenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Item;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import lombok.AllArgsConstructor;

@Inventory("protections_list")
public class ProtectionsListInventory extends PagedChestInventoryObject<Protection> {

	public static final String TITLE_OTHER_PATH = "Title-other";
	public static final String ENTITY_TELEPORTLORELINE_PATH = "Entity.Teleport-lore-line";
	public static final String ENTITY_TELEPORTNOTSETLORELINE_PATH = "Entity.Teleport-not-set-lore-line";
	public static final String ENTITY_EDITLORELINE_PATH = "Entity.Edit-lore-line";

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

	private OfflinePlayer owner;
	private Filter filter = Filter.ALL;

	public ProtectionsListInventory(Player player) {
		this(player, player);
	}

	public ProtectionsListInventory(Player player, OfflinePlayer owner) {
		super(player);
		this.owner = owner;
	}

	@Override
	protected String getTitle() {
		return this.owner.getUniqueId().equals(getPlayer().getUniqueId()) ? super.getTitle()
				: MessageTemplate.inst(getChestInventoryData().getCustomFields().get(TITLE_OTHER_PATH).toString())
						.setReplacements(new Replacement("{playername}", () -> owner.getName())).process().toString();
	}

	@Override
	protected List<Protection> getEntityList() {
		return protectionsService.getAllowedProtections(owner).stream().filter(protection -> {
			switch (filter) {
			case ALL:
				return true;
			case OWN:
				return protection.isMainOwner(getPlayer().getUniqueId());
			case OTHERS:
				return !protection.isMainOwner(getPlayer().getUniqueId());
			default:
				return false;
			}
		}).collect(Collectors.toList());

	}

	@Override
	protected ItemStack generateEntityItem(Protection protection) {
		final boolean canTeleport = protection.canTeleport(getPlayer());
		final boolean canManage = protection.canManage(getPlayer());

		Location homeLocation = protection.getHome();
		Location loc = protection.getProtectionBlockLocation();
		ProtectionBlock block = protection.getProtectionBlock().getObject();
		Replacement[] replacements = {
				new Replacement("{protection}",
						() -> protection.getDisplayName() != null ? protection.getDisplayName()
								: protection.getRegionId()),
				new Replacement("{world}", () -> protection.getWorldName()),
				new Replacement("{location_x}", () -> loc != null ? String.valueOf(loc.getBlockX()) : "???"),
				new Replacement("{location_y}", () -> loc != null ? String.valueOf(loc.getBlockY()) : "???"),
				new Replacement("{location_z}", () -> loc != null ? String.valueOf(loc.getBlockZ()) : "???") };

		ItemBuilder itemBuilder = ItemBuilder.inst().fromMap(getChestInventoryData().getCustomFields(), "Entity")
				.setReplacements(replacements);

		if (block != null) {
			itemBuilder.setMaterial(block.getInformation().getItem().getType());
		} else {
			itemBuilder.setMaterial(Material.PLAYER_HEAD).setAmount(1).setSkin(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcwNWZkOTRhMGM0MzE5MjdmYjRlNjM5YjBmY2ZiNDk3MTdlNDEyMjg1YTAyYjQzOWUwMTEyZGEyMmIyZTJlYyJ9fX0=");
		}

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

		return itemBuilder.setLore(lore).apply(block.getInformation().getItem().clone());
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, Protection entity) {
		Location homeLocation = entity.getHome();

		if (e.getClick() == ClickType.LEFT) {
			if (homeLocation != null && entity.canTeleport(getPlayer())) {
				try {
					entity.sendHome(getPlayer());
				} catch (RoyaleProtectionBlocksException e1) {
					e1.sendError(getPlayer());
				}
			}
		} else {
			if (entity.canManage(getPlayer())) {
				new ProtectionsManageInventory(getPlayer(), entity).openInventory();
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

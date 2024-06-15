package company.pluginName.Bukkit.Inventories.ProtectionBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemGenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Item;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import relampagorojo93.LibsCollection.Utils.Shared.Java.StringsHelper;

@Inventory("protectionblocks_list")
public class ProtectionBlocksListInventory extends PagedChestInventoryObject<ProtectionBlock> {

	private static final String ENTITY_COPYLORELINE_PATH = "Entity.Copy-lore-line";
	private static final String ENTITY_EDITLORELINE_PATH = "Entity.Edit-lore-line";
	private static final String ENTITY_REMOVELORELINE_PATH = "Entity.Remove-lore-line";

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	private boolean canEdit;
	private boolean canCreate;
	private boolean canDelete;

	public ProtectionBlocksListInventory(Player player) {
		super(player);
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).process().toString();
	}

	@Override
	protected List<ProtectionBlock> getEntityList() {
		return new ArrayList<>(protectionBlocksService.getProtectionBlocks().values());
	}

	@Override
	protected ItemStack generateEntityItem(ProtectionBlock entity) {
		Replacement[] replacements = {
				new Replacement("{blocks_x}", () -> String.valueOf((entity.getInformation().getBlocksX() * 2) + 1)),
				new Replacement("{blocks_y}",
						() -> entity.getInformation().getBlocksY() == -1 ? Messages.MESSAGE_GENERAL_NOLIMIT.toString()
								: String.valueOf((entity.getInformation().getBlocksY() * 2) + 1)),
				new Replacement("{blocks_z}", () -> String.valueOf((entity.getInformation().getBlocksZ() * 2) + 1)),
				new Replacement("{block_id}", () -> entity.getInformation().getId()),
				new Replacement("{block_permission}",
						() -> entity.getInformation().getPermission() != null ? entity.getInformation().getPermission()
								: "&7&o---"),
				new Replacement("{block_allowed_worlds}",
						() -> entity.getAllowedWorlds().get().size() > 0
								? entity.getAllowedWorlds().get().stream().collect(Collectors.joining(", "))
								: "&7&oAll"),
				new Replacement("{block_price}",
						() -> entity.getInformation().getPrice() != null
								? StringsHelper.toCurrency(entity.getInformation().getPrice())
								: "&7&o---") };

		ItemBuilder itemBuilder = ItemBuilder.inst().fromMap(getChestInventoryData().getCustomFields(), "Entity")
				.setReplacements(replacements);
		itemBuilder.getLore().clear();

		if (entity != null) {
			itemBuilder.fromItem(entity.getInformation().getItem());
		} else {
			itemBuilder.setMaterial(Material.PLAYER_HEAD).setAmount(1).setSkin(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcwNWZkOTRhMGM0MzE5MjdmYjRlNjM5YjBmY2ZiNDk3MTdlNDEyMjg1YTAyYjQzOWUwMTEyZGEyMmIyZTJlYyJ9fX0=");
		}

		List<String> lore = itemBuilder.getLore().size() > 0 ? itemBuilder.getLore() : new ArrayList<>();
		lore.addAll(getChestInventoryData().getEntityLore());
		lore.add("&0");
		lore.add(getChestInventoryData().getCustomFields().get(ENTITY_COPYLORELINE_PATH).toString());

		if (canEdit) {
			lore.add(getChestInventoryData().getCustomFields().get(ENTITY_EDITLORELINE_PATH).toString());
		}

		if (canDelete) {
			lore.add(getChestInventoryData().getCustomFields().get(ENTITY_REMOVELORELINE_PATH).toString());
		}

		return itemBuilder.setLore(lore).apply(entity.getInformation().getItem().clone());
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, ProtectionBlock entity) {
		if (e.getClick() == ClickType.LEFT && !e.isShiftClick()) {
			try {
				e.getWhoClicked().getOpenInventory().setCursor(entity.getInformation().generateItem());
			} catch (RoyaleProtectionBlocksException e1) {
				e1.sendError(getPlayer());
				return;
			}
		} else if (e.getClick() == ClickType.SHIFT_LEFT && canEdit) {
			new ProtectionBlockManagerInventory(getPlayer(), entity).openInventory();
		} else if (e.getClick() == ClickType.RIGHT && canDelete) {
			new ConfirmationInventory(getPlayer(), () -> {
				try {
					entity.delete(getPlayer());
					MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_BLOCKS_REMOVEDSUCCESSFULLY.applyPrefix())
							.process().sendMessage(getPlayer());
				} catch (RoyaleProtectionBlocksException e1) {
					e1.sendError(getPlayer());
				}
			}).openInventory();
		}
	}

	@Override
	protected void onPreUpdate() {
		canEdit = PermissionsService.BLOCKS_EDIT.hasPermission(getPlayer());
		canCreate = PermissionsService.BLOCKS_CREATE.hasPermission(getPlayer());
		canDelete = PermissionsService.BLOCKS_DELETE.hasPermission(getPlayer());
	}

	@ItemGenerator("Create-block-button")
	private ItemStack generateCreateBlockButton(Item item) {
		return canCreate ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemExecutor("Create-block-button")
	private void executeCreateBlockButton(Item item) {
		new ProtectionBlockManagerInventory(getPlayer()).openInventory();

	}

}

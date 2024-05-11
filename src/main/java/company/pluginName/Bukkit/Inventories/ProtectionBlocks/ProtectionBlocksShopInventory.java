package company.pluginName.Bukkit.Inventories.ProtectionBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import relampagorojo93.LibsCollection.Utils.Shared.Java.StringsHelper;

@Inventory("protectionblocks_shop")
public class ProtectionBlocksShopInventory extends PagedChestInventoryObject<ProtectionBlock> {

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	public ProtectionBlocksShopInventory(Player player) {
		super(player);
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).process().toString();
	}

	@Override
	protected List<ProtectionBlock> getEntityList() {
		return protectionBlocksService.getProtectionBlocks().values().stream()
				.filter(block -> block.getInformation()
						.isForSale())
				.sorted((block1, block2) -> block1.getInformation().getPrice() != null
						? (block2.getInformation().getPrice() != null
								? block1.getInformation().getPrice().compareTo(block2.getInformation().getPrice())
								: 1)
						: -1)
				.collect(Collectors.toList());
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
				new Replacement("{block_permission}", () -> entity.getInformation().getPermission()),
				new Replacement("{block_allowed_worlds}",
						() -> entity.getAllowedWorlds().get().stream().collect(Collectors.joining(", "))),
				new Replacement("{block_price}",
						() -> entity.getInformation().getPrice() != null
								? StringsHelper.toCurrency(entity.getInformation().getPrice())
								: "---") };

		ItemBuilder itemBuilder = ItemBuilder.inst().fromMap(getChestInventoryData().getCustomFields(), "Entity")
				.setReplacements(replacements);

		if (entity != null) {
			itemBuilder.fromItem(entity.getInformation().getItem());
		} else {
			itemBuilder.setMaterial(Material.PLAYER_HEAD).setAmount(1).setSkin(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcwNWZkOTRhMGM0MzE5MjdmYjRlNjM5YjBmY2ZiNDk3MTdlNDEyMjg1YTAyYjQzOWUwMTEyZGEyMmIyZTJlYyJ9fX0=");
		}

		List<String> lore = itemBuilder.getLore().length > 0 ? new ArrayList<>(Arrays.asList(itemBuilder.getLore()))
				: new ArrayList<>();
		lore.addAll(getChestInventoryData().getEntityLore());

		return itemBuilder.setLore(lore).apply(entity.getInformation().getItem().clone());
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, ProtectionBlock entity) {
		if (Settings.SETTINGS_PROTECTIONBLOCK_REQUESTCONFIRMATIONONPURCHASETHROUGHGUI.getContent()) {
			new ConfirmationInventory(getPlayer(), () -> {
				entity.purchase(getPlayer());
			}).openInventory();
		} else {
			entity.purchase(getPlayer());
		}
	}

}

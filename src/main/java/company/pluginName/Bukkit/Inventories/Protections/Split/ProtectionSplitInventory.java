package company.pluginName.Bukkit.Inventories.Protections.Split;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Bukkit.Inventories.v2.Protections.ProtectionsManageInventory;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsServiceImpl;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import darkpanda73.PandaUtils.Utilities.Java.Arrays.ArrayUtilities;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSplitRequestInput;

@Inventory("protections_split")
public class ProtectionSplitInventory extends PagedChestInventoryObject<Protection> {

	@PandaInject
	private static ProtectionsServiceImpl protectionsService;

	@PandaInject
	private static PlaceholdersService placeholdersService;

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	private Protection protection;

	public ProtectionSplitInventory(Player player, Protection protection) throws RoyaleProtectionBlocksExceptionImpl {
		super(player);

		if (Settings.SETTINGS_PROTECTION_MERGE_ENABLED.isFalse()) {
			throw Exceptions.Protections.Merge.DISABLED.generateException();
		}

		this.protection = protection;

		Replacement[] playerReplacements = placeholdersService.getPlayerReplacements(getPlayer());
		Replacement[] protectionReplacements = placeholdersService.getProtectionReplacements(protection);
		setReplacements(ArrayUtilities.join(new Replacement[playerReplacements.length + protectionReplacements.length],
				playerReplacements, protectionReplacements));
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).setReplacements(getReplacements()).process().toString();
	}

	@Override
	protected List<Protection> getEntityList() {
		return this.protection.getChildProtections().stream().map(protection -> (Protection) protection)
				.collect(Collectors.toList());
	}

	@ItemExecutor("Close-button")
	private void executeCloseButton() {
		goToPreviousInventory();
	}

	@Override
	protected ItemStack generateEntityItem(Protection entity) {
		Replacement[] protectionReplacements = placeholdersService.getProtectionReplacements(entity);

		ItemBuilder itemBuilder = ItemBuilder.inst().fromMap(getChestInventoryData().getCustomFields(), "Entity")
				.setReplacements(protectionReplacements);

		return itemBuilder.apply(entity.getDisplayItemOrDefault().clone());
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, Protection entity) {
		try {
			playerInteractionsService.protectionSplitRequest(ProtectionSplitRequestInput.inst(getPlayer(), entity));
			new ProtectionsManageInventory(getPlayer(), entity).setPreviousInventory(null).openInventory();
		} catch (RoyaleProtectionBlocksException e1) {
			e1.sendError(getPlayer());
		}
	}

}

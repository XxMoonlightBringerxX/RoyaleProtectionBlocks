package company.pluginName.Bukkit.Inventories.Protections.Merge;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Bukkit.Inventories.Protections.ProtectionsManageInventory;
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
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import darkpanda73.PandaUtils.Utilities.Java.Arrays.ArrayUtilities;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionMergeRequestInput;

@Inventory("protections_merge")
public class ProtectionMergeInventory extends PagedChestInventoryObject<Protection> {

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static PlaceholdersService placeholdersService;

	@PandaInject
	private static PlayerInteractionsService playerInteractionsService;

	private Protection protection;

	public ProtectionMergeInventory(Player player, Protection protection) {
		super(player);

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
		return protectionsService.getAllowedProtections(getPlayer())
				.filter(protection -> protection != this.protection
						&& ProtectionUtilities.canMerge(protection, getPlayer())
						&& !this.protection.getChildProtectionsRecursively().contains(protection)
						&& (Protection.SETTINGS_PROTECTION_MUSTBECLOSEFORMERGE.isFalse()
								|| protection.isInside(this.protection.getProtectionArea(), true)))
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

		return itemBuilder.apply(entity.getDisplayItem().getOrDefault().clone());
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, Protection entity) {
		try {
			playerInteractionsService
					.protectionMergeRequest(ProtectionMergeRequestInput.inst(getPlayer(), protection, entity));
			new ProtectionsManageInventory(getPlayer(), entity).openInventory();
		} catch (RoyaleProtectionBlocksException e1) {
			e1.sendError(getPlayer());
		}
	}

}

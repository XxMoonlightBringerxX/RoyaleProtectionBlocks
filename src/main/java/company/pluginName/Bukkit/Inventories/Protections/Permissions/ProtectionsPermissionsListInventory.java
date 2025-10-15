package company.pluginName.Bukkit.Inventories.Protections.Permissions;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.ProtectionPermissionsPckg.ProtectionPermissionsService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsServiceImpl;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import darkpanda73.PandaUtils.Utilities.Java.Arrays.ArrayUtilities;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Permissions.PermissionInterface;

@Inventory("protections_permissions_list")
public class ProtectionsPermissionsListInventory extends PagedChestInventoryObject<PermissionInterface> {

	@PandaInject
	private static ProtectionsServiceImpl protectionsService;

	@PandaInject
	private static ProtectionPermissionsService protectionPermissionsService;

	@PandaInject
	private static PlaceholdersService placeholdersService;

	private Protection protection;

	public ProtectionsPermissionsListInventory(Player player, Protection protection) {
		super(player);

		this.protection = protection;

		Replacement[] protectionReplacements = placeholdersService.getProtectionReplacements(protection);
		Replacement[] playerReplacements = placeholdersService.getPlayerReplacements(player);
		setReplacements(ArrayUtilities.join(new Replacement[playerReplacements.length + protectionReplacements.length],
				playerReplacements, protectionReplacements));
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).setReplacements(getReplacements()).toString();
	}

	@Override
	protected List<PermissionInterface> getEntityList() {
		return protectionPermissionsService.getPermissions().stream()
				.filter(perm -> perm.isEnabled() && perm.isEditable()).collect(Collectors.toList());

	}

	@Override
	protected ItemStack generateEntityItem(PermissionInterface permission) {
		return permission.getDisplayItem();
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, PermissionInterface entity) {
		new ProtectionsPermissionsManageInventory(getPlayer(), protection, entity).openInventory();
	}

	@ItemExecutor("Close-button")
	private void executeCloseButton() {
		goToPreviousInventory();
	}

}

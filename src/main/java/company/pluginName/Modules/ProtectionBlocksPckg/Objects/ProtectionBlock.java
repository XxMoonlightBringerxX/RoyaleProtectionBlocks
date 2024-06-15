package company.pluginName.Modules.ProtectionBlocksPckg.Objects;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import company.pluginName.APIs.VaultAPI.VaultAPI;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Components.ProtectionBlockAllowedWorlds;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Components.ProtectionBlockInformation;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.SQLPckg.SQLService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.milkbowl.vault.economy.EconomyResponse;

@Data
@Accessors(chain = true)
@Setter(lombok.AccessLevel.NONE)
@RequiredArgsConstructor
public class ProtectionBlock {

	@PandaInject
	private static PandaPluginClass plugin;

	@PandaInject
	private static SQLService sqlService;

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static VaultAPI vaultApi;

	public static final String PROTECTION_BLOCK_ID_KEY = "ProtectionBlockId";

	private @NonNull ProtectionBlockInformation information;
	private @NonNull ProtectionBlockAllowedWorlds allowedWorlds;

	public ProtectionBlock() {
		this(new ProtectionBlockInformation());
	}

	public ProtectionBlock(ProtectionBlockInformation information) {
		this(information, new ProtectionBlockAllowedWorlds());
	}

	public ProtectionBlock(ProtectionBlock protectionBlock) {
		this.information = new ProtectionBlockInformation();
		this.allowedWorlds = new ProtectionBlockAllowedWorlds();

		this.information.setId(protectionBlock.getInformation().getId());
		this.copy(protectionBlock);
	}

	public void purchase(Player pl) {
		try {
			ItemStack item = getInformation().generateItem();
			boolean hasAvailableSpace = true;

			if (pl.getInventory().firstEmpty() == -1) {
				if (pl.getInventory().containsAtLeast(item, 1)) {
					if (pl.getInventory().addItem(item).isEmpty()) {
						pl.getInventory().removeItem(item);
					} else {
						hasAvailableSpace = false;
					}
				} else {
					hasAvailableSpace = false;
				}
			}

			if (hasAvailableSpace) {
				if (PermissionsService.ECONOMY_BYPASS.hasPermission(pl)) {
					pl.getInventory().addItem(item);
				} else {
					EconomyResponse response = vaultApi.getHook().getEconomy().withdrawPlayer(pl,
							getInformation().getPrice());
					if (response.transactionSuccess()) {
						pl.getInventory().addItem(item);
					} else {
						MessageTemplate.inst(Messages.ERROR_INSUFFICIENTBALANCE.applyPrefix()).process()
								.sendMessage(pl);
					}
				}
			} else {
				MessageTemplate.inst(Messages.ERROR_INVENTORYFULL.applyPrefix()).process().sendMessage(pl);
			}
		} catch (RoyaleProtectionBlocksException e) {
			e.sendError(pl);
		}
	}

	public void save() throws RoyaleProtectionBlocksException {
		this.save(null);
	}

	public void save(Player player) throws RoyaleProtectionBlocksException {
		if (player != null) {
			if (!PermissionsService.BLOCKS_CREATE.hasPermission(player)) {
				throw Exceptions.Protections.Blocks.Save.PERMISSIONDENIED.generateException();
			}
		}

		if (this.getInformation().getId() == null) {
			throw Exceptions.Protections.Blocks.Save.IDNULL.generateException();
		}

		if (this.getInformation().getItem() == null) {
			throw Exceptions.Protections.Blocks.Save.ITEMNULL.generateException();
		}

		ProtectionBlock registeredProtectionBlock = protectionBlocksService
				.getProtectionBlockById(this.getInformation().getId());

		if (registeredProtectionBlock != null && registeredProtectionBlock != this) {
			throw Exceptions.Protections.Blocks.Save.IDINUSE.generateException();
		}

		sqlService.saveProtectionBlock(this);
		protectionBlocksService.registerProtectionBlock(this);
	}

	public void delete() throws RoyaleProtectionBlocksException {
		this.delete(null);
	}

	public void delete(Player player) throws RoyaleProtectionBlocksException {
		if (player != null) {
			if (!PermissionsService.BLOCKS_DELETE.hasPermission(player)) {
				throw Exceptions.Protections.Blocks.Delete.PERMISSIONDENIED.generateException();
			}
		}

		protectionsService.getProtectionsByWorld().values()
				.forEach(protections -> new ArrayList<>(protections).forEach(protection -> {
					if (protection.getProtectionBlock().getIdentifier().equals(this.getInformation().getId())) {
						if (protection.getUtils().isProtectionBlockShown()) {
							protection.getUtils().hideProtectionBlock();
						}

						if (protection.getBoundaries().isProtectionViewActive()) {
							protection.getBoundaries().toggleProtectionView();
						}

						try {
							protection.delete().subscribe();
						} catch (RoyaleProtectionBlocksException e1) {
							e1.printStackTrace();
						}
					}
				}));
		sqlService.deleteProtectionBlock(this);
		protectionBlocksService.unregisterProtectionBlock(this);
	}

	public void copy(ProtectionBlock protectionBlock) {
		this.information.copy(protectionBlock.getInformation());
		this.allowedWorlds.copy(protectionBlock.getAllowedWorlds());
	}

}

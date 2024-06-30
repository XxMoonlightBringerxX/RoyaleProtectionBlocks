package company.pluginName.API.Services;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import company.pluginName.Bukkit.Inventories.Protections.ProtectionsManageInventory;
import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Features.PvPPckg.Utils.CombatLogHookUtilities;
import company.pluginName.Hooks.CombatLogX.CombatLogXAPI;
import company.pluginName.Hooks.DeluxeCombat.DeluxeCombatAPI;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionRemovalAttemptEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionManagementInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionRemovalInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionKickRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionRemovalRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionRenameRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSetHomeRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionTeleportHomeRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Banneds.ProtectionBannedAddRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Banneds.ProtectionBannedRemoveRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberAddRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberRemoveRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Owners.ProtectionOwnerAddRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Owners.ProtectionOwnerRemoveRequestInput;

@PandaService
public class PlayerInteractionsServiceImpl extends PlayerInteractionsService {

	@PandaInject
	private ProtectionsService protectionsService;

	@PandaInject
	private DeluxeCombatAPI deluxeCombatApi;

	@PandaInject
	private CombatLogXAPI combatLogXApi;

	public void openProtectionManagementInventoryRequest(OpenProtectionManagementInventoryRequestInput input)
			throws RoyaleProtectionBlocksExceptionImpl {
		if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (input.getProtection().isBlocked()) {
			throw Exceptions.Protections.BLOCKED.generateException();
		}

		if (!ProtectionUtilities.canManage(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		Protection internalProtection = protectionToInternalProtection(input.getProtection());

		new ProtectionsManageInventory(input.getPlayer(), internalProtection).openInventory();
	}

	public void openProtectionRemovalInventoryRequest(OpenProtectionRemovalInventoryRequestInput input)
			throws RoyaleProtectionBlocksExceptionImpl {
		if (CombatLogHookUtilities.SETTINGS_COMBATLOGHOOK_CANCELPROTECTIONREMOVALINCOMBAT.isTrue()
				&& CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		Protection internalProtection = protectionToInternalProtection(input.getProtection());

		new ConfirmationInventory(input.getPlayer(), () -> {
			try {
				protectionRemovalRequest(ProtectionRemovalRequestInput.inst(input.getPlayer(), internalProtection)
						.setCause(input.getCause()));
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(input.getPlayer());
			}
		}).openInventory();
	}

	public void protectionRemovalRequest(ProtectionRemovalRequestInput input)
			throws RoyaleProtectionBlocksExceptionImpl {
		if (CombatLogHookUtilities.SETTINGS_COMBATLOGHOOK_CANCELPROTECTIONREMOVALINCOMBAT.isTrue()
				&& CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (!ProtectionUtilities.canDelete(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Delete.PERMISSIONDENIED.generateException();
		}

		Protection internalProtection = protectionToInternalProtection(input.getProtection());
		ProtectionBlock protectionBlock = internalProtection.getProtectionBlock().getObject();

		try {
			ProtectionRemovalAttemptEvent attemptEvent = new ProtectionRemovalAttemptEvent(input.getPlayer(),
					internalProtection);
			Bukkit.getPluginManager().callEvent(attemptEvent);

			if (attemptEvent.isCancelled()) {
				throw Exceptions.Protections.Delete.CANCELLED.generateException();
			}

			if (internalProtection.getUtils().isProtectionBlockShown()) {
				internalProtection.getUtils().hideProtectionBlock();
			}

			if (internalProtection.getBoundaries().isProtectionViewActive()) {
				internalProtection.getBoundaries().toggleProtectionView();
			}

			ItemStack protectionBlockItem = (protectionBlock != null) ? protectionBlock.getInformation().generateItem()
					: null;

			TasksUtils.executeOnAsync(() -> {
				try {
					internalProtection.delete(input.getPlayer(), input.getCause()).subscribe((deletedProtection) -> {
						MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_REMOVEDSUCCESSFULLY.applyPrefix()).process()
								.sendMessage(input.getPlayer());

						TasksUtils.execute(() -> {
							if (input.getPlayer().isOnline() && protectionBlockItem != null) {
								input.getPlayer().getInventory().addItem(protectionBlockItem)
										.forEach((index, remainingItem) -> internalProtection.getLocation().getWorld()
												.dropItem(internalProtection.getLocation(), remainingItem));
							}

							if (input.getPlayer().isOnline()
									&& input.getPlayer().getOpenInventory().getType() != InventoryType.CRAFTING) {
								input.getPlayer().closeInventory();
							}
						});
					}, (throwable) -> {
						if (!(throwable instanceof RoyaleProtectionBlocksExceptionImpl)) {
							throwable = Exceptions.Protections.Delete.UNKNOWN.generateException(throwable);
						}

						((RoyaleProtectionBlocksExceptionImpl) throwable).sendError(input.getPlayer());
					});
				} catch (RoyaleProtectionBlocksExceptionImpl e) {
					e.sendError(input.getPlayer());
				}
			});
		} catch (RoyaleProtectionBlocksExceptionImpl e1) {
			if (e1.getExceptionType() == Exceptions.Protections.Delete.CANCELLED) {
				Debugger.log(MessageType.PROTECTION_REMOVAL_ATTEMPT_CANCELLED,
						() -> new Object[] { internalProtection.getRegionId() });
			} else {
				e1.sendError(input.getPlayer());
			}
		}
	}

	public void protectionMemberAddRequest(ProtectionMemberAddRequestInput input)
			throws RoyaleProtectionBlocksException {
		if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (input.getProtection().isBlocked()) {
			throw Exceptions.Protections.BLOCKED.generateException();
		}

		if (!ProtectionUtilities.canAddMember(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Members.Save.PERMISSIONDENIED.generateException();
		}

		if (input.getMember().equals(input.getPlayer().getUniqueId())) {
			throw Exceptions.Protections.Members.Save.CANNOTADDYOURSELF.generateException();
		}

		input.getProtection().addMember(input.getMember());
	}

	public void protectionMemberRemoveRequest(ProtectionMemberRemoveRequestInput input)
			throws RoyaleProtectionBlocksException {
		if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (!input.getPlayer().getUniqueId().equals(input.getMember())) {
			if (input.getProtection().isBlocked()) {
				throw Exceptions.Protections.BLOCKED.generateException();
			}

			if (!ProtectionUtilities.canRemoveMember(input.getProtection(), input.getPlayer())) {
				throw Exceptions.Protections.Members.Delete.PERMISSIONDENIED.generateException();
			}
		}

		input.getProtection().removeMember(input.getMember());
	}

	public void protectionOwnerAddRequest(ProtectionOwnerAddRequestInput input) throws RoyaleProtectionBlocksException {
		if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (input.getProtection().isBlocked()) {
			throw Exceptions.Protections.BLOCKED.generateException();
		}

		if (!ProtectionUtilities.canAddOwner(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Owners.Save.PERMISSIONDENIED.generateException();
		}

		if (input.getOwner().equals(input.getPlayer().getUniqueId())) {
			throw Exceptions.Protections.Owners.Save.CANNOTADDYOURSELF.generateException();
		}

		input.getProtection().addOwner(input.getOwner());
	}

	public void protectionOwnerRemoveRequest(ProtectionOwnerRemoveRequestInput input)
			throws RoyaleProtectionBlocksException {
		if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (!input.getPlayer().getUniqueId().equals(input.getOwner())) {
			if (input.getProtection().isBlocked()) {
				throw Exceptions.Protections.BLOCKED.generateException();
			}

			if (!ProtectionUtilities.canRemoveOwner(input.getProtection(), input.getPlayer())) {
				throw Exceptions.Protections.Owners.Delete.PERMISSIONDENIED.generateException();
			}
		}

		input.getProtection().removeOwner(input.getOwner());
	}

	public void protectionBannedAddRequest(ProtectionBannedAddRequestInput input)
			throws RoyaleProtectionBlocksException {
		if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (input.getProtection().isBlocked()) {
			throw Exceptions.Protections.BLOCKED.generateException();
		}

		if (!ProtectionUtilities.canAddBanned(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Banneds.Save.PERMISSIONDENIED.generateException();
		}

		if (input.getBanned().equals(input.getPlayer().getUniqueId())) {
			throw Exceptions.Protections.Banneds.Save.CANNOTADDYOURSELF.generateException();
		}

		input.getProtection().addBanned(input.getBanned());
	}

	public void protectionBannedRemoveRequest(ProtectionBannedRemoveRequestInput input)
			throws RoyaleProtectionBlocksException {
		if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (input.getProtection().isBlocked()) {
			throw Exceptions.Protections.BLOCKED.generateException();
		}

		if (!ProtectionUtilities.canRemoveBanned(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Banneds.Delete.PERMISSIONDENIED.generateException();
		}

		input.getProtection().removeBanned(input.getBanned());
	}

	public boolean protectionKickRequest(ProtectionKickRequestInput input) throws RoyaleProtectionBlocksException {
		if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (input.getProtection().isBlocked()) {
			throw Exceptions.Protections.BLOCKED.generateException();
		}

		if (!ProtectionUtilities.canKick(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.KICKDENIED.generateException();
		}

		return input.getProtection().kickPlayer(input.getKicked());
	}

	public void protectionSetHomeRequest(ProtectionSetHomeRequestInput input) throws Exception {
		if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (input.getProtection().isBlocked()) {
			throw Exceptions.Protections.BLOCKED.generateException();
		}

		if (!ProtectionUtilities.canSetHome(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		input.getProtection().setHome(input.getPlayer().getLocation());
	}

	public void protectionTeleportHomeRequest(ProtectionTeleportHomeRequestInput input)
			throws RoyaleProtectionBlocksException {
		if (CombatLogHookUtilities.SETTINGS_COMBATLOGHOOK_CANCELPROTECTIONTELEPORTINCOMBAT.isTrue()
				&& CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (!ProtectionUtilities.canTeleport(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		input.getProtection().teleport(input.getPlayer());
	}

	public void protectionRenameRequest(ProtectionRenameRequestInput input) throws RoyaleProtectionBlocksException {
		if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (input.getProtection().isBlocked()) {
			throw Exceptions.Protections.BLOCKED.generateException();
		}

		if (!ProtectionUtilities.canRename(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		input.getProtection().rename(input.getNewName());
	}

	private Protection protectionToInternalProtection(IProtection protection)
			throws RoyaleProtectionBlocksExceptionImpl {
		if (!(protection instanceof Protection)) {
			Protection internalProtection = protectionsService.findProtectionById(protection.getRegionId());

			if (internalProtection == null) {
				throw Exceptions.Protections.NOTFOUND.generateException();
			}

			return internalProtection;
		}
		return (Protection) protection;
	}

}

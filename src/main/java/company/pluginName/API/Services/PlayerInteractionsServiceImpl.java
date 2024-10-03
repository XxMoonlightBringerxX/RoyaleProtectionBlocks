package company.pluginName.API.Services;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import company.pluginName.Bukkit.Inventories.Protections.ProtectionsManageInventory;
import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Features.PvPPckg.Utils.CombatLogHookUtilities;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionRemovalAttemptEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionManagementInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionRemovalInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionHideBlockRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionKickRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionMergeRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionPriorityChangeRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionRemovalRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionRenameRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSetHomeRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionShowBlockRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSplitRequestInput;
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

	@Override
	public void openProtectionManagementInventoryRequest(OpenProtectionManagementInventoryRequestInput input)
			throws RoyaleProtectionBlocksExceptionImpl {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canManage(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		Protection internalProtection = protectionToInternalProtection(input.getProtection().getParentProtection());

		new ProtectionsManageInventory(input.getPlayer(), internalProtection).openInventory();
	}

	@Override
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

	@Override
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
					internalProtection, RemovalCause.PLAYER);
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
										.forEach((index, remainingItem) -> internalProtection.getBukkitLocation()
												.getWorld()
												.dropItem(internalProtection.getBukkitLocation(), remainingItem));
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

	@Override
	public void protectionMemberAddRequest(ProtectionMemberAddRequestInput input)
			throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canAddMember(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Members.Save.PERMISSIONDENIED.generateException();
		}

		if (input.getMember().equals(input.getPlayer().getUniqueId())) {
			throw Exceptions.Protections.Members.Save.CANNOTADDYOURSELF.generateException();
		}

		try {
			input.getProtection().performAllProtections(prot -> prot.addMember(input.getMember()));
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			throw e;
		} catch (Throwable e) {
			throw Exceptions.Protections.Save.UNKNOWN.generateException(e);
		}
	}

	@Override
	public void protectionMemberRemoveRequest(ProtectionMemberRemoveRequestInput input)
			throws RoyaleProtectionBlocksException {
		if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (!input.getPlayer().getUniqueId().equals(input.getMember())) {
			if (!ProtectionUtilities.canRemoveMember(input.getProtection(), input.getPlayer())) {
				throw Exceptions.Protections.Members.Delete.PERMISSIONDENIED.generateException();
			}
		}

		try {
			input.getProtection().performAllProtections(prot -> prot.removeMember(input.getMember()));
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			throw e;
		} catch (Throwable e) {
			throw Exceptions.Protections.Save.UNKNOWN.generateException(e);
		}
	}

	@Override
	public void protectionOwnerAddRequest(ProtectionOwnerAddRequestInput input) throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canAddOwner(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Owners.Save.PERMISSIONDENIED.generateException();
		}

		if (input.getOwner().equals(input.getPlayer().getUniqueId())) {
			throw Exceptions.Protections.Owners.Save.CANNOTADDYOURSELF.generateException();
		}

		try {
			input.getProtection().performAllProtections(prot -> prot.addOwner(input.getOwner()));
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			throw e;
		} catch (Throwable e) {
			throw Exceptions.Protections.Save.UNKNOWN.generateException(e);
		}
	}

	@Override
	public void protectionOwnerRemoveRequest(ProtectionOwnerRemoveRequestInput input)
			throws RoyaleProtectionBlocksException {
		if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (!input.getPlayer().getUniqueId().equals(input.getOwner())) {
			if (!ProtectionUtilities.canRemoveOwner(input.getProtection(), input.getPlayer())) {
				throw Exceptions.Protections.Owners.Delete.PERMISSIONDENIED.generateException();
			}
		}

		try {
			input.getProtection().performAllProtections(prot -> prot.removeOwner(input.getOwner()));
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			throw e;
		} catch (Throwable e) {
			throw Exceptions.Protections.Save.UNKNOWN.generateException(e);
		}
	}

	@Override
	public void protectionSetHomeRequest(ProtectionSetHomeRequestInput input) throws Exception {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canSetHome(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		input.getProtection().setHome(input.getPlayer().getLocation());
	}

	@Override
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

	@Override
	public void protectionRenameRequest(ProtectionRenameRequestInput input) throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canRename(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		input.getProtection().rename(input.getNewName());
	}

	@Override
	public void protectionPriorityChangeRequest(ProtectionPriorityChangeRequestInput input)
			throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canChangePriority(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		if (!PermissionsService.PRIORITY_MAX_BYPASS.hasPermission(input.getPlayer())) {
			Integer maxPriority = PermissionsService.getPriorityMaxAvailable(input.getPlayer(), Integer.MAX_VALUE);
			if (maxPriority < input.getNewPriority()) {
				throw Exceptions.Protections.PRIORITYTOOHIGH.generateException()
						.setReplacements(new Replacement("{max}", () -> String.valueOf(maxPriority)));
			}
		}

		try {
			if (input.isRecursive()) {
				input.getProtection().performAllProtections((prot) -> prot.setPriority(input.getNewPriority()));
			} else {
				input.getProtection().setPriority(input.getNewPriority());
			}
		} catch (Throwable e) {
			if (e.getCause() instanceof RoyaleProtectionBlocksException) {
				throw (RoyaleProtectionBlocksException) e.getCause();
			} else {
				throw Exceptions.Protections.Save.UNKNOWN.generateException(e);
			}
		}
	}

	@Override
	public void protectionHideBlockRequest(ProtectionHideBlockRequestInput input)
			throws RoyaleProtectionBlocksException {
		// Preconditions

		try {
			if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
				throw Exceptions.Protections.INCOMBAT.generateException();
			}

			if (!ProtectionUtilities.canHideBlock(input.getProtection(), input.getPlayer())) {
				throw Exceptions.Protections.PERMISSIONDENIED.generateException();
			}

			if (input.getProtection().checkAllMatchAllProtections(prot -> !prot.isBlockShown())) {
				throw (!input.getProtection().getChildProtections().isEmpty()
						? Exceptions.Protections.BLOCKALREADYHIDDENMULTIPLE
						: Exceptions.Protections.BLOCKALREADYHIDDEN).generateException();
			}

			// Actions

			input.getProtection().performAllProtections(prot -> {
				if (prot.isBlockShown()) {
					prot.hideBlock();
				}
			});
		} catch (RoyaleProtectionBlocksException e) {
			throw e;
		} catch (Throwable e) {
			throw Exceptions.Protections.UNKNOWN.generateException(e);
		}
	}

	@Override
	public void protectionShowBlockRequest(ProtectionShowBlockRequestInput input)
			throws RoyaleProtectionBlocksException {
		// Preconditions

		try {
			checkIfModifiable(input.getPlayer(), input.getProtection());

			if (!ProtectionUtilities.canHideBlock(input.getProtection(), input.getPlayer())) {
				throw Exceptions.Protections.PERMISSIONDENIED.generateException();
			}

			if (input.getProtection().checkAllMatchAllProtections(prot -> prot.isBlockShown())) {
				throw (!input.getProtection().getChildProtections().isEmpty()
						? Exceptions.Protections.BLOCKALREADYSHOWNMULTIPLE
						: Exceptions.Protections.BLOCKALREADYSHOWN).generateException();
			}

			if (input.getProtection().checkAnyMatchAllProtections(prot -> !input.getProtection().isBlockShown()
					&& input.getProtection().getBukkitLocation().getBlock().getType() != Material.AIR.getMaterial())) {
				throw (!input.getProtection().getChildProtections().isEmpty()
						? Exceptions.Protections.BLOCKOVERLAPINGMULTIPLE
						: Exceptions.Protections.BLOCKOVERLAPING).generateException();
			}

			// Actions

			input.getProtection().performAllProtections(prot -> {
				if (!prot.isBlockShown()) {
					prot.showBlock();
				}
			});
		} catch (RoyaleProtectionBlocksException e) {
			throw e;
		} catch (Throwable e) {
			throw Exceptions.Protections.UNKNOWN.generateException(e);
		}
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

	private void checkIfModifiable(Player player, IProtection protection) throws RoyaleProtectionBlocksExceptionImpl {
		if (CombatLogHookUtilities.isInCombat(player)) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (protection.isDeleted()) {
			throw Exceptions.Protections.NOTFOUND.generateException();
		}
	}

	@Override
	public boolean protectionKickRequest(ProtectionKickRequestInput input) throws RoyaleProtectionBlocksException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void protectionMergeRequest(ProtectionMergeRequestInput input) throws RoyaleProtectionBlocksException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void protectionSplitRequest(ProtectionSplitRequestInput input) throws RoyaleProtectionBlocksException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void protectionBannedAddRequest(ProtectionBannedAddRequestInput input)
			throws RoyaleProtectionBlocksException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void protectionBannedRemoveRequest(ProtectionBannedRemoveRequestInput input)
			throws RoyaleProtectionBlocksException {
		throw new UnsupportedOperationException();
	}

}

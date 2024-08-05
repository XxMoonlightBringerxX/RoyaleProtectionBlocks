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
import company.pluginName.Hooks.CombatLogX.CombatLogXAPI;
import company.pluginName.Hooks.DeluxeCombat.DeluxeCombatAPI;
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

	@PandaInject
	private DeluxeCombatAPI deluxeCombatApi;

	@PandaInject
	private CombatLogXAPI combatLogXApi;

	@Override
	public void openProtectionManagementInventoryRequest(OpenProtectionManagementInventoryRequestInput input)
			throws RoyaleProtectionBlocksExceptionImpl {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canManage(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		Protection internalParentProtection = protectionToInternalProtection(
				input.getProtection().getParentProtection());
		Protection internalProtection = protectionToInternalProtection(input.getProtection());

		new ProtectionsManageInventory(input.getPlayer(), internalParentProtection, internalProtection).openInventory();
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

		input.getProtection().addMember(input.getMember());
	}

	@Override
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

	@Override
	public void protectionOwnerAddRequest(ProtectionOwnerAddRequestInput input) throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canAddOwner(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Owners.Save.PERMISSIONDENIED.generateException();
		}

		if (input.getOwner().equals(input.getPlayer().getUniqueId())) {
			throw Exceptions.Protections.Owners.Save.CANNOTADDYOURSELF.generateException();
		}

		input.getProtection().addOwner(input.getOwner());
	}

	@Override
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

	@Override
	public void protectionBannedAddRequest(ProtectionBannedAddRequestInput input)
			throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canAddBanned(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Banneds.Save.PERMISSIONDENIED.generateException();
		}

		if (input.getBanned().equals(input.getPlayer().getUniqueId())) {
			throw Exceptions.Protections.Banneds.Save.CANNOTADDYOURSELF.generateException();
		}

		input.getProtection().addBanned(input.getBanned());
	}

	@Override
	public void protectionBannedRemoveRequest(ProtectionBannedRemoveRequestInput input)
			throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canRemoveBanned(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Banneds.Delete.PERMISSIONDENIED.generateException();
		}

		input.getProtection().removeBanned(input.getBanned());
	}

	@Override
	public boolean protectionKickRequest(ProtectionKickRequestInput input) throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (PermissionsService.KICK_BYPASS.hasPermission(input.getKicked())) {
			throw Exceptions.Protections.KICKDENIEDBYPASS.generateException();
		}

		if (!ProtectionUtilities.canKick(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.KICKDENIED.generateException();
		}

		return input.getProtection().kickPlayer(input.getKicked());
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
			input.getProtection().setPriority(input.getNewPriority());
			if (input.isRecursive()) {
				input.getProtection().getChildProtections().forEach(child -> {
					try {
						child.setPriority(input.getNewPriority());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
			}
		} catch (RuntimeException e) {
			if (e.getCause() instanceof RoyaleProtectionBlocksException) {
				throw (RoyaleProtectionBlocksException) e.getCause();
			} else {
				throw Exceptions.Protections.Save.UNKNOWN.generateException(e);
			}
		}
	}

	@Override
	public void protectionMergeRequest(ProtectionMergeRequestInput input) throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());
		checkIfModifiable(input.getPlayer(), input.getParentProtection());

		if (!ProtectionUtilities.canMerge(input.getProtection(), input.getPlayer())
				|| !ProtectionUtilities.canMerge(input.getParentProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		input.getProtection().setParentProtection(input.getParentProtection());
		((Protection) input.getProtection()).saveData();
	}

	@Override
	public void protectionSplitRequest(ProtectionSplitRequestInput input) throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canSplit(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		input.getProtection().unsetParentProtection();
		((Protection) input.getProtection()).saveData();
	}

	@Override
	public void protectionHideBlockRequest(ProtectionHideBlockRequestInput input)
			throws RoyaleProtectionBlocksException {
		// Preconditions

		if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (!ProtectionUtilities.canHideBlock(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		if (!input.getProtection().isBlockShown() && (!input.isRecursive()
				|| input.getProtection().getChildProtections().stream().noneMatch(IProtection::isBlockShown))) {
			throw (input.isRecursive() ? Exceptions.Protections.BLOCKALREADYHIDDENMULTIPLE
					: Exceptions.Protections.BLOCKALREADYHIDDEN).generateException();
		}

		// Actions

		if (input.getProtection().isBlockShown()) {
			input.getProtection().hideBlock();
		}
		if (input.isRecursive()) {
			input.getProtection().getChildProtections().forEach(child -> {
				if (child.isBlockShown()) {
					child.hideBlock();
				}
			});
		}
	}

	@Override
	public void protectionShowBlockRequest(ProtectionShowBlockRequestInput input)
			throws RoyaleProtectionBlocksException {
		// Preconditions

		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canHideBlock(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		if (input.getProtection().isBlockShown() && (!input.isRecursive()
				|| input.getProtection().getChildProtections().stream().allMatch(IProtection::isBlockShown))) {
			throw (input.isRecursive() ? Exceptions.Protections.BLOCKALREADYSHOWNMULTIPLE
					: Exceptions.Protections.BLOCKALREADYSHOWN).generateException();
		}

		if ((!input.getProtection().isBlockShown()
				&& input.getProtection().getLocation().getBlock().getType() != Material.AIR.getMaterial())
				|| (input.isRecursive()
						&& input.getProtection().getChildProtections().stream().anyMatch(child -> !child.isBlockShown()
								&& child.getLocation().getBlock().getType() != Material.AIR.getMaterial()))) {
			throw (input.isRecursive() ? Exceptions.Protections.BLOCKOVERLAPINGMULTIPLE
					: Exceptions.Protections.BLOCKOVERLAPING).generateException();
		}

		// Actions

		if (!input.getProtection().isBlockShown()) {
			input.getProtection().showBlock();
		}
		if (input.isRecursive()) {
			input.getProtection().getChildProtections().forEach(child -> {
				if (!child.isBlockShown()) {
					child.showBlock();
				}
			});
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

		if (protection.isBlocked()) {
			throw Exceptions.Protections.BLOCKED.generateException();
		}

		if (protection.isDeleted()) {
			throw Exceptions.Protections.NOTFOUND.generateException();
		}
	}

}

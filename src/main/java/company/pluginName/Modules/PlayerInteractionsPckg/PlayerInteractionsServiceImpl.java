package company.pluginName.Modules.PlayerInteractionsPckg;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Bukkit.Inventories.Shared.WarningInventory;
import company.pluginName.Bukkit.Inventories.v2.Protections.ProtectionsManageInventory;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Features.PvPPckg.Utils.CombatLogHookUtilities;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.PendingPaymentsPckg.PendingPaymentsService;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData.InvitationRequirement;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionPermissionsPckg.ProtectionPermissionsService;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsServiceImpl;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import company.pluginName.Utils.EconomyUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.Enums.EconomyService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;
import lombok.Getter;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.BlockReason;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.CreationCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionMergeAttemptEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionSplitAttemptEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException.Type;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.ProtectionBlocks.IProtectionBlock;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.PlayerInteractionsService;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionManagementInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionRemovalInventoryRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionChangeDisplayItemRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionCreationRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionHideBlockRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionKickRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionMergeRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionPriorityChangeRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionPurchaseRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionRemovalRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionRenameRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSellRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSetHomeRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionShowBlockRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSplitRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSwitchPermissionRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionSwitchSettingRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionTeleportHomeRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionTogglePublicAccessRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.ProtectionTransferRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Banneds.ProtectionBannedAddRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Banneds.ProtectionBannedRemoveRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberAcceptInvitationRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberAddRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberInviteRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberRemoveInvitationRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Members.ProtectionMemberRemoveRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Owners.ProtectionOwnerAddRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Protections.Owners.ProtectionOwnerRemoveRequestInput;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.Protections.Objects.ProtectionCreationData;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.Protections.Objects.ProtectionRemovalData;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.Protections.Objects.ProtectionTransferData;

@PandaService(priority = 1001)
public class PlayerInteractionsServiceImpl extends PlayerInteractionsService {

	@RegisteredPandaField("lang")
	private static final PandaStringField MESSAGE_TELEPORT_UNSAFEWARNINGTITLE = new PandaStringField(
			"Message.Teleport.Unsafe-warning-title", "&cUnsafe location");
	@RegisteredPandaField("lang")
	private static final PandaStringListField MESSAGE_TELEPORT_UNSAFEWARNINGDESCRIPTION = new PandaStringListField(
			"Message.Teleport.Unsafe-warning-description",
			Arrays.asList("&7Your current home location seems", "&7not to include any solid platform or",
					"&7contains dangerous elements, which makes it", "&7unsafe. Do you wish to teleport anyway?"));

	@RegisteredPandaField("lang")
	private static final PandaPrefixedStringField MESSAGE_PROTECTION_SETTINGS_CHARGEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protection.Settings.Charged-successfully", "&aYou've been charged a total amount of &e{amount}$");

	@RegisteredPandaField("config")
	private static final PandaBooleanField SETTINGS_PROTECTION_SHOWUNSAFETELEPORTWARNINGMENU = new PandaBooleanField(
			"Settings.Protection.Show-unsafe-teleport-warning-menu", true);

	@PandaInject
	private ProtectionsServiceImpl protectionsService;

	@PandaInject
	private ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private ProtectionSettingsService protectionSettingsService;

	@PandaInject
	private ProtectionPermissionsService protectionPermissionsService;

	@PandaInject
	private PendingPaymentsService pendingPaymentsService;

	@PandaInject
	private PlayerDataService playerDataService;

	// TODO: Move to ProtectionSettingsService
	private @Getter EconomyService protectionsStoreEconomyService;
	private @Getter EconomyService protectionBlocksStoreEconomyService;

	@LoadMethod
	private void load() {
		try {
			protectionsStoreEconomyService = Settings.SETTINGS_STORE_PROTECTIONECONOMYSERVICE.hasContent()
					? EconomyService
							.valueOf(Settings.SETTINGS_STORE_PROTECTIONECONOMYSERVICE.getContent().toUpperCase())
					: null;

			if (protectionsStoreEconomyService != null
					&& !EconomyUtilities.isEconomyEnabled(protectionsStoreEconomyService)) {
				MainPluginClass.getSimpleLogger().sendWarning(String.format(
						"The defined economy for the protections store could not be found installed on the server (%s). In order to use the protections store, you must install the economy plugin or switch the defined economy plugin for one of your current economy plugins.",
						protectionsStoreEconomyService.name()));
				protectionsStoreEconomyService = null;
			}
		} catch (IllegalArgumentException e) {
			MainPluginClass.getSimpleLogger().sendError(String.format("Invalid protections store economy service (%s)",
					Settings.SETTINGS_STORE_PROTECTIONECONOMYSERVICE.getContent().toUpperCase()));
		}
		try {
			protectionBlocksStoreEconomyService = Settings.SETTINGS_STORE_PROTECTIONBLOCKECONOMYSERVICE.hasContent()
					? EconomyService
							.valueOf(Settings.SETTINGS_STORE_PROTECTIONBLOCKECONOMYSERVICE.getContent().toUpperCase())
					: null;

			if (protectionBlocksStoreEconomyService != null
					&& !EconomyUtilities.isEconomyEnabled(protectionBlocksStoreEconomyService)) {
				MainPluginClass.getSimpleLogger().sendWarning(String.format(
						"The defined economy for the protection blocks store could not be found installed on the server (%s). In order to use the protection blocks store, you must install the economy plugin or switch the defined economy plugin for one of your current economy plugins.",
						protectionBlocksStoreEconomyService.name()));
				protectionBlocksStoreEconomyService = null;
			}
		} catch (IllegalArgumentException e) {
			MainPluginClass.getSimpleLogger()
					.sendError(String.format("Invalid protection blocks store economy service (%s)",
							Settings.SETTINGS_STORE_PROTECTIONBLOCKECONOMYSERVICE.getContent().toUpperCase()));
		}
	}

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
	public void protectionCreationRequest(ProtectionCreationRequestInput input) throws RoyaleProtectionBlocksException {
		Location playerLocation = input.getPlayer().getLocation().clone();

		if (CombatLogHookUtilities.SETTINGS_COMBATLOGHOOK_CANCELPROTECTIONREMOVALINCOMBAT.isTrue()
				&& CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (input.getProtectionBlock().getPermission() != null
				&& !input.getPlayer().hasPermission(input.getProtectionBlock().getPermission())) {
			throw Exceptions.Protections.Save.PERMISSIONDENIEDPROTECTIONBLOCK.generateException();
		}

		ProtectionBlock protectionBlock = protectionBlocksService
				.getProtectionBlockById(input.getProtectionBlock().getId());
		String worldName = input.getLocation().getWorld().getName();
		boolean emptyAllowedWorlds = protectionBlock.getBlockAllowedWorlds().get().isEmpty();
		boolean allowedWorld = protectionBlock.getBlockAllowedWorlds().get().contains(worldName);
		boolean bannedWorld = Settings.SETTINGS_BANNEDWORLDS.getContent().contains(worldName);

		if (!emptyAllowedWorlds && !allowedWorld) {
			throw Exceptions.Protections.Save.NOTALLOWEDWORLD.generateException();
		}

		if (emptyAllowedWorlds && bannedWorld) {
			throw Exceptions.Protections.Save.BANNEDWORLD.generateException();
		}

		ProtectionUtilities.checkIfCanBeAdded(input.getPlayer(), input.getProtectionBlock(), 1);

		Protection protection = RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
				.create(ProtectionCreationData.inst(input.getPlayer(), input.getOwnerUuid(), input.getProtectionBlock(),
						input.getLocation(), CreationCause.PLAYER));

		if (Settings.SETTINGS_PROTECTION_SETPLAYERPOSITIONASHOMEONCREATION.getContent()) {
			try {
				protection.setHome(playerLocation);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (input.getPlayer().isOnline()) {
			MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_CREATEDSUCCESSFULLY.applyPrefix()).process()
					.sendMessage(input.getPlayer());
		}

		TasksUtils.execute(() -> protection.getUtils().showProtectionBlock());
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
				protectionRemovalRequest(ProtectionRemovalRequestInput.inst(input.getPlayer(), internalProtection));
			} catch (RoyaleProtectionBlocksException e) {
				e.sendError(input.getPlayer());
			}
		}, false).openInventory();
	}

	@Override
	public void protectionRemovalRequest(ProtectionRemovalRequestInput input) throws RoyaleProtectionBlocksException {
		if (CombatLogHookUtilities.SETTINGS_COMBATLOGHOOK_CANCELPROTECTIONREMOVALINCOMBAT.isTrue()
				&& CombatLogHookUtilities.isInCombat(input.getPlayer())) {
			throw Exceptions.Protections.INCOMBAT.generateException();
		}

		if (!ProtectionUtilities.canDelete(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Delete.PERMISSIONDENIED.generateException();
		}

		if (input.getProtection().isBlocked() && input.getProtection().getBlockReason() == BlockReason.OTHERS
				&& !PermissionsService.ADMIN_BLOCK_BYPASS.hasPermission(input.getPlayer())) {
			throw Exceptions.Protections.BLOCKED.generateException();
		}

		RoyaleProtectionBlocksAPI.getInstance().getProtectionsService().delete(ProtectionRemovalData
				.inst(input.getPlayer(), input.getProtection().getProtectionId(), RemovalCause.PLAYER));

		MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_REMOVEDSUCCESSFULLY.applyPrefix()).process()
				.sendMessage(input.getPlayer());

		TasksUtils.execute(() -> {
			IProtectionBlock protectionBlock = input.getProtection().getProtectionBlock();
			ItemStack protectionBlockItem = (protectionBlock != null) ? protectionBlock.generateItem() : null;

			if (input.getPlayer().isOnline() && input.getPlayer().getGameMode() != GameMode.CREATIVE
					&& protectionBlockItem != null) {
				input.getPlayer().getInventory().addItem(protectionBlockItem)
						.forEach((index, remainingItem) -> input.getProtection().getBukkitLocation().getWorld()
								.dropItem(input.getProtection().getBukkitLocation(), remainingItem));
			}

			if (input.getPlayer().isOnline()
					&& input.getPlayer().getOpenInventory().getType() != InventoryType.CRAFTING) {
				input.getPlayer().closeInventory();
			}
		});
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

		PlayerData playerData = playerDataService.getPlayerData(input.getMember());
		if (playerData != null) {
			if (playerData.getInvitationRequirement() == InvitationRequirement.ALL_DENIED) {
				throw Exceptions.Protections.Invitations.INVITATIONBLOCKED.generateException();
			} else if (playerData.getInvitationRequirement() == InvitationRequirement.INVITATION_REQUIRED) {
				throw Exceptions.Protections.Invitations.INVITATIONREQUIRED.generateException();
			}
		}

		try {
			input.getProtection().performAllProtections(prot -> {
				if (!prot.isMember(input.getMember())) {
					prot.addMemberAndSave(input.getMember());
				}
			});
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			throw e;
		} catch (Throwable e) {
			throw Exceptions.Protections.Save.UNKNOWN.generateException(e);
		}
	}

	@Override
	public void protectionMemberInviteRequest(ProtectionMemberInviteRequestInput input)
			throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canAddMember(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Members.Save.PERMISSIONDENIED.generateException();
		}

		if (input.getMember().equals(input.getPlayer().getUniqueId())) {
			throw Exceptions.Protections.Members.Save.CANNOTADDYOURSELF.generateException();
		}

		PlayerData playerData = playerDataService.getPlayerData(input.getMember());
		if (playerData != null && playerData.getInvitationRequirement() == InvitationRequirement.ALL_DENIED) {
			throw Exceptions.Protections.Invitations.INVITATIONBLOCKED.generateException();
		}

		try {
			input.getProtection().addInvitedPlayerAndSave(input.getMember());
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			throw e;
		} catch (Throwable e) {
			throw Exceptions.Protections.Save.UNKNOWN.generateException(e);
		}
	}

	@Override
	public void protectionMemberAcceptInvitationRequest(ProtectionMemberAcceptInvitationRequestInput input)
			throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!input.getProtection().isInvitedPlayer(input.getPlayer().getUniqueId())) {
			throw Exceptions.Protections.Invitations.NOTINVITEDYOURSELF.generateException();
		}

		try {
			input.getProtection().acceptInvitationAndSave(input.getPlayer().getUniqueId());
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			throw e;
		} catch (Throwable e) {
			throw Exceptions.Protections.Save.UNKNOWN.generateException(e);
		}
	}

	@Override
	public void protectionMemberRemoveInvitationRequest(ProtectionMemberRemoveInvitationRequestInput input)
			throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!input.getMember().equals(input.getPlayer().getUniqueId())
				&& !ProtectionUtilities.canRemoveMember(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Members.Delete.PERMISSIONDENIED.generateException();
		}

		if (!input.getProtection().isInvitedPlayer(input.getMember())) {
			throw input.getMember().equals(input.getPlayer().getUniqueId())
					? Exceptions.Protections.Invitations.NOTINVITEDYOURSELF.generateException()
					: Exceptions.Protections.Invitations.NOTINVITED.generateException();
		}

		try {
			input.getProtection().removeInvitedPlayerAndSave(input.getMember());
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
			if (input.getProtection().isBlocked()) {
				throw Exceptions.Protections.BLOCKED.generateException();
			}

			if (!ProtectionUtilities.canRemoveMember(input.getProtection(), input.getPlayer())) {
				throw Exceptions.Protections.Members.Delete.PERMISSIONDENIED.generateException();
			}
		}

		try {
			input.getProtection().performAllProtections(prot -> {
				if (prot.isMember(input.getMember())) {
					prot.removeMemberAndSave(input.getMember());
				}
			});
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
			input.getProtection().performAllProtections(prot -> {
				if (!prot.isOwner(input.getOwner())) {
					prot.addOwnerAndSave(input.getOwner());
				}
			});
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
			if (input.getProtection().isBlocked()) {
				throw Exceptions.Protections.BLOCKED.generateException();
			}

			if (!ProtectionUtilities.canRemoveOwner(input.getProtection(), input.getPlayer())) {
				throw Exceptions.Protections.Owners.Delete.PERMISSIONDENIED.generateException();
			}
		}

		try {
			input.getProtection().performAllProtections(prot -> {
				if (prot.isOwner(input.getOwner())) {
					prot.removeOwnerAndSave(input.getOwner());
				}
			});
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			throw e;
		} catch (Throwable e) {
			throw Exceptions.Protections.Save.UNKNOWN.generateException(e);
		}
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

		try {
			input.getProtection().performAllProtections(prot -> {
				if (!prot.isBanned(input.getBanned())) {
					prot.addBannedAndSave(input.getBanned());
				}
			});
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			throw e;
		} catch (Throwable e) {
			throw Exceptions.Protections.Save.UNKNOWN.generateException(e);
		}
	}

	@Override
	public void protectionBannedRemoveRequest(ProtectionBannedRemoveRequestInput input)
			throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canRemoveBanned(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Banneds.Delete.PERMISSIONDENIED.generateException();
		}

		try {
			input.getProtection().performAllProtections(prot -> {
				if (prot.isBanned(input.getBanned())) {
					prot.removeBannedAndSave(input.getBanned());
				}
			});
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			throw e;
		} catch (Throwable e) {
			throw Exceptions.Protections.Save.UNKNOWN.generateException(e);
		}
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

		if (!input.getProtection().isForSale() && !input.getProtection().canTeleport(input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		try {
			input.getProtection().teleport(input.getPlayer(), input.isIgnoreCost(), input.isIgnoreUnsafeWarning());
		} catch (RoyaleProtectionBlocksException e) {
			if (SETTINGS_PROTECTION_SHOWUNSAFETELEPORTWARNINGMENU.isTrue()
					&& e.getType() == Type.PROTECTIONS_TELEPORT_UNSAFE) {
				new WarningInventory(input.getPlayer(), MESSAGE_TELEPORT_UNSAFEWARNINGTITLE.getContent(),
						MESSAGE_TELEPORT_UNSAFEWARNINGDESCRIPTION.getContent(), () -> {
							try {
								input.getProtection().teleport(input.getPlayer(), false, true);
							} catch (RoyaleProtectionBlocksException e1) {
								e1.sendError(input.getPlayer());
							}
						}, false).openInventory();
			} else {
				throw e;
			}
		}
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
	public void protectionChangeDisplayItemRequest(ProtectionChangeDisplayItemRequestInput input)
			throws RoyaleProtectionBlocksException {
		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canChangeDisplayItem(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.Save.PERMISSIONDENIED.generateException();
		}

		input.getProtection().setDisplayItemAndSave(input.getNewDisplayItem());
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
	public void protectionMergeRequest(ProtectionMergeRequestInput input) throws RoyaleProtectionBlocksException {
		if (Settings.SETTINGS_PROTECTION_MERGE_ENABLED.isFalse()) {
			throw Exceptions.Protections.Merge.DISABLED.generateException();
		}

		checkIfModifiable(input.getPlayer(), input.getProtection());
		checkIfModifiable(input.getPlayer(), input.getParentProtection());

		if (!input.getProtection().getOwnerUuid().equals(input.getParentProtection().getOwnerUuid())) {
			throw Exceptions.Protections.Merge.DIFFERENTOWNERS.generateException();
		}

		if (!ProtectionUtilities.canMerge(input.getProtection(), input.getPlayer())
				|| !ProtectionUtilities.canMerge(input.getParentProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		ProtectionMergeAttemptEvent attemptEvent = new ProtectionMergeAttemptEvent(input.getPlayer(),
				input.getProtection(), input.getParentProtection());
		Bukkit.getPluginManager().callEvent(attemptEvent);

		if (attemptEvent.isCancelled()) {
			throw Exceptions.Protections.CANCELLED.generateException();
		}

		input.getProtection().setParentProtectionAndSave(input.getParentProtection());
	}

	@Override
	public void protectionSplitRequest(ProtectionSplitRequestInput input) throws RoyaleProtectionBlocksException {
		if (Settings.SETTINGS_PROTECTION_MERGE_ENABLED.isFalse()) {
			throw Exceptions.Protections.Merge.DISABLED.generateException();
		}

		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canSplit(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		ProtectionSplitAttemptEvent attemptEvent = new ProtectionSplitAttemptEvent(input.getPlayer(),
				input.getProtection());
		Bukkit.getPluginManager().callEvent(attemptEvent);

		if (attemptEvent.isCancelled()) {
			throw Exceptions.Protections.CANCELLED.generateException();
		}

		input.getProtection().unsetParentProtectionAndSave();
	}

	@Override
	public void protectionHideBlockRequest(ProtectionHideBlockRequestInput input)
			throws RoyaleProtectionBlocksException {
		// Preconditions

		try {
			if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
				throw Exceptions.Protections.INCOMBAT.generateException();
			}

			if (!input.getProtection().canToggleBlockVisibility(input.getPlayer())) {
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

			if (!input.getProtection().canToggleBlockVisibility(input.getPlayer())) {
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

	@Override
	public void protectionTransferRequest(ProtectionTransferRequestInput input) throws RoyaleProtectionBlocksException {
		try {
			// Preconditions

			if (input.getProtection().getOwnerUuid().equals(input.getNewOwner().getUniqueId())) {
				throw Exceptions.Protections.Transfer.SAMEOWNER.generateException();
			}

			checkIfModifiable(input.getPlayer(), input.getProtection());

			if (!ProtectionUtilities.canTransfer(input.getProtection(), input.getPlayer())) {
				throw Exceptions.Protections.PERMISSIONDENIED.generateException();
			}

			int amountOfProtections = input.getProtection().getChildProtectionsRecursively().size();

			try {
				ProtectionUtilities.checkIfCanBeAdded(input.getPlayer(), input.getProtection().getProtectionBlock(),
						amountOfProtections);
			} catch (RoyaleProtectionBlocksException e) {
				if (e.getType() == Type.PROTECTIONS_SAVE_MAXREACHED) {
					throw Exceptions.Protections.Transfer.MAXREACHED.generateException();
				} else if (e.getType() == Type.PROTECTIONS_SAVE_BLOCKMAXREACHED) {
					throw Exceptions.Protections.Transfer.BLOCKMAXREACHED.generateException();
				} else {
					throw e;
				}
			}

			// Actions

			RoyaleProtectionBlocksAPI.getInstance().getProtectionsService().transfer(ProtectionTransferData
					.inst(input.getProtection().getProtectionId(), input.getNewOwner().getUniqueId()));
		} catch (RoyaleProtectionBlocksException e) {
			throw e;
		} catch (Throwable e) {
			throw Exceptions.Protections.UNKNOWN.generateException(e);
		}
	}

	@Override
	public void protectionSellRequest(ProtectionSellRequestInput input) throws RoyaleProtectionBlocksException {
		// Preconditions
		if (this.protectionsStoreEconomyService == null) {
			throw Exceptions.Protections.STOREUNAVAILABLE.generateException();
		}

		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!input.getProtection().isForSale() && input.getPrice() <= 0D) {
			throw Exceptions.Protections.Purchase.ALREADYNOTFORSALE.generateException();
		}

		if (!ProtectionUtilities.canSell(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		// Actions

		input.getProtection().setPriceAndSave(input.getPrice());
	}

	@Override
	public void protectionPurchaseRequest(ProtectionPurchaseRequestInput input) throws RoyaleProtectionBlocksException {
		UUID oldOwner = input.getProtection().getOwnerUuid();

		try {
			// Preconditions
			if (this.protectionsStoreEconomyService == null) {
				throw Exceptions.Protections.STOREUNAVAILABLE.generateException();
			}

			if (input.getProtection().getOwnerUuid().equals(input.getPlayer().getUniqueId())) {
				throw Exceptions.Protections.Purchase.SAMEOWNER.generateException();
			}

			if (!input.getProtection().isForSale()) {
				throw Exceptions.Protections.Purchase.NOTFORSALE.generateException();
			}

			if (CombatLogHookUtilities.isInCombat(input.getPlayer())) {
				throw Exceptions.Protections.INCOMBAT.generateException();
			}

			if (input.getProtection().isDeleted()) {
				throw Exceptions.Protections.NOTFOUND.generateException();
			}

			int amountOfProtections = 1 + input.getProtection().getChildProtectionsRecursively().size();

			try {
				ProtectionUtilities.checkIfCanBeAdded(input.getPlayer(), input.getProtection().getProtectionBlock(),
						amountOfProtections);
			} catch (RoyaleProtectionBlocksException e) {
				if (e.getType() == Type.PROTECTIONS_SAVE_MAXREACHED) {
					throw Exceptions.Protections.Purchase.MAXREACHED.generateException();
				} else if (e.getType() == Type.PROTECTIONS_SAVE_BLOCKMAXREACHED) {
					throw Exceptions.Protections.Purchase.BLOCKMAXREACHED.generateException();
				} else {
					throw e;
				}
			}

			if (!EconomyUtilities.withdraw(this.protectionsStoreEconomyService, input.getPlayer(),
					input.getProtection().getPrice())) {
				throw Exceptions.Protections.Purchase.NOTENOUGHBALANCE.generateException();
			}

			pendingPaymentsService.deposit(this.protectionsStoreEconomyService, oldOwner,
					input.getProtection().getPrice());

			try {
				// Actions

				RoyaleProtectionBlocksAPI.getInstance().getProtectionsService().transfer(ProtectionTransferData
						.inst(input.getProtection().getProtectionId(), input.getPlayer().getUniqueId()));

				input.getProtection().setPriceAndSave(0);
			} catch (Throwable e) {
				pendingPaymentsService.removeDeposit(oldOwner, this.protectionsStoreEconomyService);
				throw e;
			}
		} catch (RoyaleProtectionBlocksException e) {
			throw e;
		} catch (Throwable e) {
			throw Exceptions.Protections.UNKNOWN.generateException(e);
		}
	}

	@Override
	public void protectionTogglePublicAccessRequest(ProtectionTogglePublicAccessRequestInput input)
			throws RoyaleProtectionBlocksException {
		// Preconditions

		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canTogglePublicAccess(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		// Actions

		input.getProtection().setPublicAccessAndSave(input.isPublicAccess());
	}

	@Override
	public <T extends Serializable> void protectionSwitchSettingRequest(ProtectionSwitchSettingRequestInput<T> input)
			throws RoyaleProtectionBlocksException {
		throw new UnsupportedOperationException();

//		// Preconditions
//
//		checkIfModifiable(input.getPlayer(), input.getProtection());
//
//		if (!ProtectionUtilities.canSwitchSettings(input.getProtection(), input.getPlayer())) {
//			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
//		}
//
//		if (protectionSettingsService.getSetting(input.getSetting().getId()) == null) {
//			throw Exceptions.Protections.Settings.NOTFOUND.generateException();
//		}
//
//		if (input.getSetting().getPermission() != null
//				&& !input.getPlayer().hasPermission(input.getSetting().getPermission())) {
//			throw Exceptions.Protections.Settings.PERMISSIONDENIED.generateException();
//		}
//
//		if (input.getSetting().getCost() > 0D && EconomyUtilities.isEconomyEnabled(EconomyService.VAULT)) {
//			if (!PermissionsService.ECONOMY_BYPASS.hasPermission(input.getPlayer())) {
//				if (!EconomyUtilities.withdraw(EconomyService.VAULT, input.getPlayer(), input.getSetting().getCost())) {
//					throw Exceptions.Protections.Settings.NOTENOUGHBALANCE.generateException();
//				} else {
//					MessageTemplate.inst(MESSAGE_PROTECTION_SETTINGS_CHARGEDSUCCESSFULLY.applyPrefix())
//							.setReplacements(
//									new Replacement("{amount}", () -> String.valueOf(input.getSetting().getCost())))
//							.process().sendMessage(input.getPlayer());
//				}
//			}
//		}
//
//		try {
//			// T previousValue = input.getProtection().getSettingValue(input.getSetting(),
//			// input.getGroup());
//
//			input.getProtection().setSettingValue(input.getSetting(), input.getGroup(), input.getValue());
//
//			// TODO Make changes to include setting modification event for discord
//		} catch (Exception e) {
//			if (input.getPlayer() != null && input.getSetting().getCost() > 0D) {
//				EconomyUtilities.deposit(EconomyService.VAULT, input.getPlayer(), input.getSetting().getCost());
//			}
//			throw Exceptions.Protections.Settings.UNKNOWN.generateException(e);
//		}
	}

	@Override
	public void protectionSwitchPermissionRequest(ProtectionSwitchPermissionRequestInput input)
			throws RoyaleProtectionBlocksException {
		// Preconditions

		checkIfModifiable(input.getPlayer(), input.getProtection());

		if (!ProtectionUtilities.canSwitchPermissions(input.getProtection(), input.getPlayer())) {
			throw Exceptions.Protections.PERMISSIONDENIED.generateException();
		}

		if (protectionPermissionsService.getPermission(input.getPermission().getId()) == null) {
			throw Exceptions.Protections.Permissions.NOTFOUND.generateException();
		}

		if (!input.getPermission().isEditable()) {
			throw Exceptions.Protections.Permissions.NOTEDITABLE.generateException();
		}

		if (!input.getPermission().isEditable(input.getGroup())) {
			throw Exceptions.Protections.Permissions.NOTEDITABLEGROUP.generateException();
		}

		if (input.getPermission().getPermission() != null
				&& !input.getPlayer().hasPermission(input.getPermission().getPermission())) {
			throw Exceptions.Protections.Permissions.PERMISSIONDENIED.generateException();
		}

		if (input.getPermission().getCost() != null && input.getPermission().getCost() > 0D
				&& EconomyUtilities.isEconomyEnabled(EconomyService.VAULT)) {
			if (!PermissionsService.ECONOMY_BYPASS.hasPermission(input.getPlayer())) {
				if (!EconomyUtilities.withdraw(EconomyService.VAULT, input.getPlayer(),
						input.getPermission().getCost())) {
					throw Exceptions.Protections.Settings.NOTENOUGHBALANCE.generateException();
				} else {
					MessageTemplate.inst(MESSAGE_PROTECTION_SETTINGS_CHARGEDSUCCESSFULLY.applyPrefix())
							.setReplacements(
									new Replacement("{amount}", () -> String.valueOf(input.getPermission().getCost())))
							.process().sendMessage(input.getPlayer());
				}
			}
		}

		try {
			input.getProtection().setPermissionValue(input.getPermission(), input.getGroup(), input.getValue());
		} catch (Exception e) {
			if (input.getPlayer() != null && input.getPermission().getCost() != null
					&& input.getPermission().getCost() > 0D) {
				EconomyUtilities.deposit(EconomyService.VAULT, input.getPlayer(), input.getPermission().getCost());
			}
			throw Exceptions.Protections.Permissions.UNKNOWN.generateException(e);
		}

	}

	private Protection protectionToInternalProtection(IProtection protection)
			throws RoyaleProtectionBlocksExceptionImpl {
		if (!(protection instanceof Protection)) {
			Protection internalProtection = protectionsService.findProtectionById(protection.getProtectionId());

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

		if (protection.isBlocked() && !PermissionsService.ADMIN_BLOCK_BYPASS.hasPermission(player)) {
			throw Exceptions.Protections.BLOCKED.generateException();
		}

		if (protection.isDeleted()) {
			throw Exceptions.Protections.NOTFOUND.generateException();
		}
	}

}

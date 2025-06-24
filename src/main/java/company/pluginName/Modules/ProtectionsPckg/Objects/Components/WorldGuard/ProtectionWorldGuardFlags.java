package company.pluginName.Modules.ProtectionsPckg.Objects.Components.WorldGuard;

import java.util.Collection;

import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionFlagsPckg.ProtectionFlagsService;
import company.pluginName.Modules.ProtectionFlagsPckg.Objects.ProtectionFlag;
import company.pluginName.Modules.ProtectionFlagsPckg.Utils.ProtectionFlagUtilities;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.FlagsUtilities;
import company.pluginName.Utils.DiscordUtilities;
import company.pluginName.Utils.EconomyUtilities;
import company.pluginName.Utils.LogUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import lombok.AllArgsConstructor;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtectionFlags;

@AllArgsConstructor
public class ProtectionWorldGuardFlags implements IProtectionFlags {

	@RegisteredPandaField("lang")
	private static final PandaPrefixedStringField MESSAGE_PROTECTION_FLAGS_CHARGEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protection.Flags.Charged-successfully", "&aYou've been charged a total amount of &e{amount}$");

	@PandaInject
	private static ProtectionFlagsService protectionFlagsService;

	private Protection protection;

	public void resetFlags() {
		resetFlags((Player) null);
	}

	public void resetFlags(Player player) {
		FlagsUtilities.resetFlags(protection, protection.getOwnerUuid());
	}

	public void resetFlags(Collection<String> flagsToReset) {
		resetFlags((Player) null, flagsToReset);
	}

	public void resetFlags(Player player, Collection<String> flagsToReset) {
		FlagsUtilities.resetFlags(protection, protection.getOwnerUuid(), flagsToReset);
	}

	public void setFlag(FlagModificationRequestInput<?> input) throws RoyaleProtectionBlocksExceptionImpl {
		ProtectionFlag protectionFlag = protectionFlagsService.getFlag(input.getFlagId());

		if (protectionFlag == null) {
			throw Exceptions.Protections.Flags.NOTFOUND.generateException();
		}

		if (input.getExecutor() != null) {
			if (protectionFlag.getPermission() != null
					&& !input.getExecutor().hasPermission(protectionFlag.getPermission())) {
				throw Exceptions.Protections.Flags.PERMISSIONDENIED.generateException();
			}

			if (protectionFlag.getCostPerChange() != null) {
				if (!PermissionsService.ECONOMY_BYPASS.hasPermission(input.getExecutor())) {
					if (!EconomyUtilities.withdraw(protectionFlag.getEconomyService(), input.getExecutor(),
							protectionFlag.getCostPerChange())) {
						throw Exceptions.Protections.Flags.NOTENOUGHBALANCE.generateException();
					} else {
						MessageTemplate.inst(MESSAGE_PROTECTION_FLAGS_CHARGEDSUCCESSFULLY.applyPrefix())
								.setReplacements(new Replacement("{amount}",
										() -> String.valueOf(protectionFlag.getCostPerChange())))
								.process().sendMessage(input.getExecutor());
					}
				}
			}
		}

		try {
			Object previousValue = protectionFlag.retrieveValue(protection.getProtectedRegion());

			protectionFlag.modifyValue(protection.getProtectedRegion(), input.getValue());

			if (this.protection.getParentProtection() == this.protection) {
				DiscordUtilities.sendFlagModificationMessage(input.getExecutor(), protection,
						protectionFlag.getWorldGuardFlag().getName(), previousValue, input.getValue());
				LogUtilities.sendFlagModificationDebugLog(input.getExecutor(), protection,
						protectionFlag.getWorldGuardFlag().getName(), previousValue, input.getValue());
			}
		} catch (Exception e) {
			if (input.getExecutor() != null && protectionFlag.getCostPerChange() != null) {
				EconomyUtilities.deposit(protectionFlag.getEconomyService(), input.getExecutor(),
						protectionFlag.getCostPerChange());
			}
			throw Exceptions.Protections.Flags.UNKNOWN.generateException(e);
		}
	}

	@Override
	public Object getFlagValue(FlagRetrieveRequestInput input) throws RoyaleProtectionBlocksException {
		ProtectionFlag protectionFlag = protectionFlagsService.getFlag(input.getFlagId());

		if (protectionFlag == null) {
			throw Exceptions.Protections.Flags.NOTFOUND.generateException();
		}

		return protectionFlag.retrieveValue(protection.getProtectedRegion());
	}

	public String getFlagValueAsString(FlagRetrieveRequestInput input) throws RoyaleProtectionBlocksException {
		String flagValue = ProtectionFlagUtilities.valueToString(getFlagValue(input));
		return flagValue != null ? flagValue : "";
	}

}

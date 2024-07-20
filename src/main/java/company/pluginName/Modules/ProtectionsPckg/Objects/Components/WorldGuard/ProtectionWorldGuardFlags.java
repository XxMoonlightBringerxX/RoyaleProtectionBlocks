package company.pluginName.Modules.ProtectionsPckg.Objects.Components.WorldGuard;

import java.util.Collection;

import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionFlagsPckg.Objects.ProtectionFlag;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.FlagsUtilities;
import company.pluginName.Utils.DiscordUtilities;
import company.pluginName.Utils.EconomyUtils;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@AllArgsConstructor
public class ProtectionWorldGuardFlags {

	@RegisteredPandaField("lang")
	private static final PandaPrefixedStringField MESSAGE_PROTECTION_FLAGS_CHARGEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protection.Flags.Charged-successfully", "&aYou've been charged a total amount of &e{amount}$");

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

	public void setFlag(FlagModificationRequest request) throws RoyaleProtectionBlocksExceptionImpl {
		if (request.getExecutor() != null) {
			if (request.getFlag().getPermission() != null
					&& !request.getExecutor().hasPermission(request.getFlag().getPermission())) {
				throw Exceptions.Protections.Flags.PERMISSIONDENIED.generateException();
			}

			if (request.getFlag().getCostPerChange() != null) {
				if (!PermissionsService.ECONOMY_BYPASS.hasPermission(request.getExecutor())) {
					if (!EconomyUtils.withdraw(request.getFlag().getEconomyService(), request.getExecutor(),
							request.getFlag().getCostPerChange())) {
						throw Exceptions.Protections.Flags.NOTENOUGHBALANCE.generateException();
					} else {
						MessageTemplate.inst(MESSAGE_PROTECTION_FLAGS_CHARGEDSUCCESSFULLY.applyPrefix())
								.setReplacements(new Replacement("{amount}",
										() -> String.valueOf(request.getFlag().getCostPerChange())))
								.process().sendMessage(request.getExecutor());
					}
				}
			}
		}

		try {
			Object previousValue = request.getFlag().retrieveValue(protection.getProtectedRegion());

			request.getFlag().modifyValue(protection.getProtectedRegion(), request.getValue());

			if (request.getExecutor() != null) {
				DiscordUtilities.sendFlagModificationMessage(request.getExecutor(), protection,
						request.getFlag().getWorldGuardFlag().getName(), previousValue, request.getValue());
			}
		} catch (Exception e) {
			if (request.getExecutor() != null && request.getFlag().getCostPerChange() != null) {
				EconomyUtils.deposit(request.getFlag().getEconomyService(), request.getExecutor(),
						request.getFlag().getCostPerChange());
			}
			throw Exceptions.Protections.Flags.UNKNOWN.generateException(e);
		}
	}

	@Data
	@Accessors(chain = true)
	public static class FlagModificationRequest {

		private Player executor;
		private ProtectionFlag flag;
		private Object value;
		private boolean discordMessage;

		public FlagModificationRequest(ProtectionFlag flag, Object value) {
			this.flag = flag;
			this.value = value;
		}

		public static FlagModificationRequest inst(ProtectionFlag flag, Object value) {
			return new FlagModificationRequest(flag, value);
		}

		public static FlagModificationRequest inst(Player executor, ProtectionFlag flag, Object value) {
			return new FlagModificationRequest(flag, value).setExecutor(executor);
		}

	}

}

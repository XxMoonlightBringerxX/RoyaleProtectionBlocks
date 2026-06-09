package company.pluginName.Modules.DebugPckg.Objects.Protection;

import org.bukkit.OfflinePlayer;

import company.pluginName.Modules.DebugPckg.Objects.DebugMessage;
import company.pluginName.Modules.DiscordPckg.DiscordMessagesService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessageInstance;
import darkpanda73.PandaUtils.Utilities.Java.Arrays.ArrayUtilities;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

public class ProtectionRemovalDebugMessage extends DebugMessage<ProtectionRemovalDebugMessage.Data> {

	public ProtectionRemovalDebugMessage() {
		super("Protection-removal", DiscordMessagesService.PROTECTION_REMOVAL_MESSAGE_TEMPLATE);
	}

	@Override
	public String generateLogMessage(Data data) {
		return String.format(
				"Player %s removed protection '%s' with owner '%s' with protection block '%s' from location world(%s) x(%d) y(%d) z(%d). Cause: %s",
				data.getExecutor().getName(), data.getProtection().getProtectionId(),
				data.getProtection().getOwnerName(), data.getProtection().getProtectionBlockId(),
				data.getProtection().getWorldName(), data.getProtection().getSimpleLocation().getBlockX(),
				data.getProtection().getSimpleLocation().getBlockY(),
				data.getProtection().getSimpleLocation().getBlockZ(), data.getCause().name());
	}

	@Override
	public DiscordMessageInstance generateDiscordMessageInstance(Data data) {
		Replacement[] protectionReplacements = placeholdersService.getProtectionReplacements(data.getProtection());
		Replacement[] customReplacements = new Replacement[] {
				new Replacement("{removal_cause}", () -> data.getCause().name()) };

		return new DiscordMessageInstance(discordMessageTemplate, data.getExecutor(),
				ArrayUtilities.join(new Replacement[protectionReplacements.length + customReplacements.length],
						protectionReplacements, customReplacements));
	}

	@lombok.Data
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Data {

		private OfflinePlayer executor;
		private IProtection protection;
		private RemovalCause cause;

		public static Data inst(OfflinePlayer executor, IProtection protection, RemovalCause cause) {
			return new Data(executor, protection, cause);
		}

	}

}

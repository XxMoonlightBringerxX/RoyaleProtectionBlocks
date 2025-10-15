package company.pluginName.Modules.DebugPckg.Objects.Protection;

import org.bukkit.OfflinePlayer;

import company.pluginName.Modules.DebugPckg.Objects.DebugMessage;
import company.pluginName.Modules.DiscordPckg.DiscordMessagesService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessageInstance;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

public class ProtectionCreationDebugMessage extends DebugMessage<ProtectionCreationDebugMessage.Data> {

	public ProtectionCreationDebugMessage() {
		super("Protection-creation", DiscordMessagesService.PROTECTION_CREATION_MESSAGE_TEMPLATE);
	}

	@Override
	public String generateLogMessage(Data data) {
		return String.format(
				"Player %s created protection '%s' with owner '%s' using protection block '%s' from location world(%s) x(%d) y(%d) z(%d)",
				data.getExecutor().getName(), data.getProtection().getProtectionId(),
				data.getProtection().getOwnerName(), data.getProtection().getProtectionBlockId(),
				data.getProtection().getWorldName(), data.getProtection().getSimpleLocation().getBlockX(),
				data.getProtection().getSimpleLocation().getBlockY(),
				data.getProtection().getSimpleLocation().getBlockZ());
	}

	@Override
	public DiscordMessageInstance generateDiscordMessageInstance(Data data) {
		Replacement[] protectionReplacements = placeholdersService.getProtectionReplacements(data.getProtection());

		return new DiscordMessageInstance(discordMessageTemplate, data.getExecutor(), protectionReplacements);
	}

	@lombok.Data
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Data {

		private OfflinePlayer executor;
		private IProtection protection;

		public static Data inst(OfflinePlayer executor, IProtection protection) {
			return new Data(executor, protection);
		}

	}

}

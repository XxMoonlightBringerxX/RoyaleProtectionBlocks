package company.pluginName.Modules.DebugPckg.Objects.Protection;

import java.util.Arrays;

import org.bukkit.OfflinePlayer;

import company.pluginName.Modules.DebugPckg.Objects.DebugMessage;
import company.pluginName.Modules.DiscordPckg.DiscordMessagesService;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.ProtectionFlagsPckg.Utils.ProtectionFlagUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessageInstance;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;
import darkpanda73.PandaUtils.Utilities.Java.Arrays.ArrayUtilities;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

public class ProtectionFlagModificationDebugMessage extends DebugMessage<ProtectionFlagModificationDebugMessage.Data> {

	@PandaInject
	private static PlaceholdersService placeholdersService;

	@RegisteredPandaField("discord")
	public static final PandaStringListField FLAGS_TO_LISTEN = new PandaStringListField(
			"Templates.Protection-flag-modification.Flags-to-listen", Arrays.asList("pvp", "tnt", "chest-access"));

	public ProtectionFlagModificationDebugMessage() {
		super("Protection-flag-modification", DiscordMessagesService.PROTECTION_FLAG_MODIFICATION_MESSAGE_TEMPLATE);
	}

	@Override
	public String generateLogMessage(Data data) {
		return String.format(
				"Player %s has modified flag '%s' on protection '%s'. &8[&7Previous value: &e%s&8] [&7New value: &e%s&8]",
				data.getExecutor().getName(), data.getFlagId(), data.getProtection().getProtectionId(),
				ProtectionFlagUtilities.valueToString(data.getPreviousValue()),
				ProtectionFlagUtilities.valueToString(data.getNewValue()));
	}

	@Override
	public DiscordMessageInstance generateDiscordMessageInstance(Data data) {
		if (FLAGS_TO_LISTEN.getContent().contains(data.getFlagId())) {
			Replacement[] protectionReplacements = placeholdersService.getProtectionReplacements(data.getProtection());
			Replacement[] customReplacements = new Replacement[] { new Replacement("{flag_id}", () -> data.getFlagId()),
					new Replacement("{previous_value}",
							() -> MessageTemplate.inst(ProtectionFlagUtilities.valueToString(data.getPreviousValue()))
									.setColor(false).toString()),
					new Replacement("{new_value}",
							() -> MessageTemplate.inst(ProtectionFlagUtilities.valueToString(data.getNewValue()))
									.setColor(false).toString()) };

			return new DiscordMessageInstance(discordMessageTemplate, data.getExecutor(),
					ArrayUtilities.join(new Replacement[protectionReplacements.length + customReplacements.length],
							protectionReplacements, customReplacements));
		}
		return null;
	}

	@lombok.Data
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Data {

		private OfflinePlayer executor;
		private IProtection protection;
		private String flagId;
		private Object previousValue;
		private Object newValue;

		public static Data inst(OfflinePlayer executor, IProtection protection, String flagId, Object previousValue,
				Object newValue) {
			return new Data(executor, protection, flagId, previousValue, newValue);
		}

	}

}

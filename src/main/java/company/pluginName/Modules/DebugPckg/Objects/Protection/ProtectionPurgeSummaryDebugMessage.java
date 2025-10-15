package company.pluginName.Modules.DebugPckg.Objects.Protection;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;

import company.pluginName.Modules.DebugPckg.Objects.DebugMessage;
import company.pluginName.Modules.DiscordPckg.DiscordMessagesService;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPurgePckg.Objects.PurgeConfiguration;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessageInstance;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

public class ProtectionPurgeSummaryDebugMessage extends DebugMessage<ProtectionPurgeSummaryDebugMessage.Data> {

	private static final String NEW_LINE = "\\n";

	@RegisteredPandaField("discord")
	private static final PandaStringListField PURGED_PROTECTION_LINE_FORMAT = new PandaStringListField(
			"Templates.Protection-purge-summary.Purged-protection-line-format", Arrays.asList("* {protection_name}"));

	@PandaInject
	private static PlaceholdersService placeholdersService;

	public ProtectionPurgeSummaryDebugMessage() {
		super(DiscordMessagesService.PROTECTION_PURGE_SUMMARY_MESSAGE_TEMPLATE);
	}

	@Override
	public String generateLogMessage(Data data) {
		return null;
	}

	@Override
	public DiscordMessageInstance generateDiscordMessageInstance(Data data) {
		StringBuilder purgedProtections = new StringBuilder(data.getPurgedProtections().stream().map(protection -> {
			Replacement[] protectionReplacements = placeholdersService.getProtectionReplacements(protection);

			return MessageTemplate.inst(PURGED_PROTECTION_LINE_FORMAT.getContent())
					.setReplacements(protectionReplacements).process().getStrings().stream()
					.collect(Collectors.joining(NEW_LINE));
		}).limit(5).collect(Collectors.joining(NEW_LINE)));

		if (data.getPurgedProtections().size() > 5) {
			purgedProtections.append(NEW_LINE).append("[...]");
		}

		Replacement[] replacements = new Replacement[] {
				new Replacement("{older_than}", () -> data.getPurgeConfiguration().toString()),
				new Replacement("{purged_protections_size}", () -> String.valueOf(data.getPurgedProtections().size())),
				new Replacement("{purged_protections}", () -> purgedProtections.toString()) };

		return new DiscordMessageInstance(discordMessageTemplate, data.getExecutor(), replacements);
	}

	@lombok.Data
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Data {

		private OfflinePlayer executor;
		private PurgeConfiguration purgeConfiguration;
		private List<Protection> purgedProtections;

		public static Data inst(OfflinePlayer executor, PurgeConfiguration purgeConfiguration,
				List<Protection> purgedProtections) {
			return new Data(executor, purgeConfiguration, purgedProtections);
		}

	}

}

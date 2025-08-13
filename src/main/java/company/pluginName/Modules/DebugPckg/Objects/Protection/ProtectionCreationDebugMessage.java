package company.pluginName.Modules.DebugPckg.Objects.Protection;

import company.pluginName.Modules.DebugPckg.Objects.DebugMessage;
import company.pluginName.Utils.DiscordUtilities;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage.EmbedObject;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage.EmbedObject.Thumbnail;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

public class ProtectionCreationDebugMessage extends DebugMessage<ProtectionCreationDebugMessage.Data> {

	public ProtectionCreationDebugMessage() {
		super("Protection-creation");

		discordEnabledField = new PandaBooleanField("Discord.Logs.Protection-creation",
				"Discord.Logs.Protection-register", true);
	}

	@Override
	public String generateLogMessage(Data data) {
		return String.format(
				"Player %s created protection '%s' with owner '%s' using protection block '%s' from location world(%s) x(%f) y(%f) z(%f)",
				data.getPlayerName(), data.getProtectionId(), data.getOwnerName(), data.getProtectionBlockId(),
				data.getWorldName(), data.getX(), data.getY(), data.getZ());
	}

	@Override
	public DiscordMessage generateDiscordMessage(Data data) {
		DiscordMessage message = new DiscordMessage()
				.addEmbed(new EmbedObject()
						.setTitle(String.format("[%s] New registered protection",
								DiscordUtilities.intoCodeString(
										data.getPlayerName() != null ? data.getPlayerName() : "Console")))
						.setDescription("A new protection has been registered")
						.addField("World", DiscordUtilities.intoCodeString(data.getWorldName()))
						.addField("Coordinates",
								DiscordUtilities.intoCodeString(
										String.format("x: %f, y: %f, z: %f", data.getX(), data.getY(), data.getZ())))
						.addField("Protection ID", DiscordUtilities.intoCodeString(data.getProtectionId()))
						.addField("Owner", DiscordUtilities.intoCodeString(data.getOwnerName()))
						.addField("Protection block ID", DiscordUtilities.intoCodeString(data.getProtectionBlockId())));

		if (data.getPlayerName() != null) {
			message.getEmbeds().get(0)
					.setThumbnail(new Thumbnail(String.format("https://mineskin.eu/helm/%s", data.getPlayerName())));
		}

		return message;
	}

	@lombok.Data
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Data {

		private String playerName;
		private String ownerName;
		private String protectionId;
		private String protectionBlockId;
		private String worldName;
		private double x;
		private double y;
		private double z;

		public static Data inst(String playerName, String ownerName, String protectionId, String protectionBlockId,
				String worldName, double x, double y, double z) {
			return new Data(playerName, ownerName, protectionId, protectionBlockId, worldName, x, y, z);
		}

	}

}

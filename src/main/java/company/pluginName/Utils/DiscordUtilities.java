package company.pluginName.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPurgePckg.Objects.PurgeConfiguration;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.PandaDiscordWebhookService;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage.EmbedObject;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage.EmbedObject.Thumbnail;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;

public class DiscordUtilities {

	@PandaInject
	private static PandaDiscordWebhookService pandaDiscordWebhookService;

	@RegisteredPandaField("config")
	public static final PandaBooleanField DISCORD_LOGS_PROTECTIONREGISTER = new PandaBooleanField(
			"Discord.Logs.Protection-register", true);

	@RegisteredPandaField("config")
	public static final PandaBooleanField DISCORD_LOGS_PROTECTIONUNREGISTER = new PandaBooleanField(
			"Discord.Logs.Protection-unregister", true);

	@RegisteredPandaField("config")
	public static final PandaBooleanField DISCORD_LOGS_PURGESUMMARY = new PandaBooleanField(
			"Discord.Logs.Purge-summary", true);

	@RegisteredPandaField("config")
	public static final PandaStringListField DISCORD_LOGS_FLAGSMODIFICATION = new PandaStringListField(
			"Discord.Logs.Flags-modification", Arrays.asList("pvp", "tnt", "chest-access"));

	public static void sendProtectionRegisteredMessage(CommandSender commandSender, Protection protection) {
		if (Boolean.TRUE.equals(DISCORD_LOGS_PROTECTIONREGISTER.getContent()) && pandaDiscordWebhookService != null
				&& pandaDiscordWebhookService.isWebhookDefined()) {
			Location location = protection.getBukkitLocation();

			DiscordMessage message = new DiscordMessage()
					.addEmbed(
							new EmbedObject()
									.setTitle(String.format("[%s] New registered protection",
											intoCodeString(
													(commandSender != null ? commandSender.getName() : "Console"))))
									.setDescription("A new protection has been registered")
									.addField("World", intoCodeString(protection.getWorldName()))
									.addField("Coordinates",
											intoCodeString(String.format("x: %d, y: %d, z: %d", location.getBlockX(),
													location.getBlockY(), location.getBlockZ())))
									.addField("Protection ID", intoCodeString(protection.getProtectionId()))
									.addField("Owner", intoCodeString(protection.getOwnerName())).addField(
											"Protection block ID", intoCodeString(protection.getProtectionBlockId())));

			if (commandSender != null && commandSender instanceof Player) {
				message.getEmbeds().get(0).setThumbnail(
						new Thumbnail(String.format("https://mineskin.eu/helm/%s", commandSender.getName())));
			}

			pandaDiscordWebhookService.sendMessage(message);
		}
	}

	public static void sendProtectionUnregisteredMessage(CommandSender commandSender, Protection protection,
			RemovalCause removalCause) {
		if (Boolean.TRUE.equals(DISCORD_LOGS_PROTECTIONUNREGISTER.getContent()) && pandaDiscordWebhookService != null
				&& pandaDiscordWebhookService.isWebhookDefined()) {
			Location location = protection.getBukkitLocation();

			DiscordMessage message = new DiscordMessage()
					.addEmbed(
							new EmbedObject()
									.setTitle(String.format("[%s] Unregistered protection",
											intoCodeString(
													(commandSender != null ? commandSender.getName() : "Console"))))
									.setDescription("A protection has been unregistered")
									.addField("World", intoCodeString(protection.getWorldName()))
									.addField("Coordinates",
											intoCodeString(String.format("x: %d, y: %d, z: %d", location.getBlockX(),
													location.getBlockY(), location.getBlockZ())))
									.addField("Protection ID", intoCodeString(protection.getProtectionId()))
									.addField("Owner", intoCodeString(protection.getOwnerName()))
									.addField("Protection block ID", intoCodeString(protection.getProtectionBlockId()))
									.addField("Removal cause", intoCodeString(removalCause.name())));

			if (commandSender != null && commandSender instanceof Player) {
				message.getEmbeds().get(0).setThumbnail(
						new Thumbnail(String.format("https://mineskin.eu/helm/%s", commandSender.getName())));
			}

			pandaDiscordWebhookService.sendMessage(message);
		}
	}

	public static void sendFlagModificationMessage(CommandSender commandSender, Protection protection, String flagId,
			Object previousValue, Object newValue) {
		if (DISCORD_LOGS_FLAGSMODIFICATION.getContent() != null
				&& DISCORD_LOGS_FLAGSMODIFICATION.getContent().contains(flagId) && pandaDiscordWebhookService != null
				&& pandaDiscordWebhookService.isWebhookDefined()) {
			DiscordMessage message = new DiscordMessage()
					.addEmbed(
							new EmbedObject()
									.setTitle(String.format("[%s] Flag modified",
											intoCodeString(
													(commandSender != null ? commandSender.getName() : "Console"))))
									.setDescription("A flag on a protection has been modified")
									.addField("Protection ID", intoCodeString(protection.getProtectionId()))
									.addField("Flag ID", intoCodeString(flagId))
									.addField("Previous value",
											intoCodeString(previousValue != null ? previousValue.toString() : "null"))
									.addField("New value", intoCodeString(newValue.toString())));

			if (commandSender != null && commandSender instanceof Player) {
				message.getEmbeds().get(0).setThumbnail(
						new Thumbnail(String.format("https://mineskin.eu/helm/%s", commandSender.getName())));
			}

			pandaDiscordWebhookService.sendMessage(message);
		}
	}

	public static void sendPurgeSummaryMessage(Player player, PurgeConfiguration purgeConfiguration,
			List<Protection> purgedProtections) {
		if (Boolean.TRUE.equals(DISCORD_LOGS_PURGESUMMARY.getContent()) && purgedProtections != null
				&& purgedProtections.size() > 0 && pandaDiscordWebhookService != null
				&& pandaDiscordWebhookService.isWebhookDefined()) {
			String lineSeparator = "\\n";

			String removedProtections = purgedProtections
					.subList(0, purgedProtections.size() > 10 ? 9 : purgedProtections.size()).stream()
					.map(protection -> String.format("* %s", protection.getProtectionId()))
					.collect(Collectors.joining(lineSeparator));

			if (purgedProtections.size() > 10) {
				removedProtections += lineSeparator + "* [...]";
			}

			DiscordMessage message = new DiscordMessage().addEmbed(new EmbedObject()
					.setTitle(String.format("[%s] Purged protections",
							intoCodeString((player != null ? player.getName() : "Console"))))
					.setDescription("Some protections have been removed due a purge")
					.addField("Older than", intoCodeString(purgeConfiguration.toString()))
					.addField("Amount of protections", intoCodeString(String.valueOf(purgedProtections.size())))
					.addField("Protections", intoCodeString(removedProtections)));

			if (player != null) {
				message.getEmbeds().get(0)
						.setThumbnail(new Thumbnail(String.format("https://mineskin.eu/helm/%s", player.getName())));
			}

			pandaDiscordWebhookService.sendMessage(message);
		}
	}

	public static String intoCodeString(String value) {
		return String.format("`%s`", value);
	}

}

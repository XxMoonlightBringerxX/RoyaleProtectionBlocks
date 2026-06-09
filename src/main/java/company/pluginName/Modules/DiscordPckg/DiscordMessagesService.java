package company.pluginName.Modules.DiscordPckg;

import java.util.Arrays;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.PandaDiscordService;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage.EmbedObject;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage.EmbedObject.Thumbnail;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessageTemplate;

@PandaService(injectable = false)
public class DiscordMessagesService {

	public static final DiscordMessageTemplate PROTECTION_CREATION_MESSAGE_TEMPLATE = new DiscordMessageTemplate(
			"Protection-creation",
			new DiscordMessage().addEmbed(new EmbedObject().setTitle("[{executor_name}] New registered protection")
					.setDescription(Arrays.asList("A new protection has been registered"))
					.addField("World", Arrays.asList("`{protection_world}`"))
					.addField("Coordinates", Arrays.asList(
							"`x: {protection_location_x}, y: {protection_location_y}, z: {protection_location_z}`"))
					.addField("Protection ID", Arrays.asList("`{protection_id}`"))
					.addField("Owner", Arrays.asList("`{protection_owner}`"))
					.addField("Protection block ID", Arrays.asList("`{protection_block_id}`"))
					.setThumbnail(new Thumbnail("{executor_icon_url}"))));

	public static final DiscordMessageTemplate PROTECTION_REMOVAL_MESSAGE_TEMPLATE = new DiscordMessageTemplate(
			"Protection-removal",
			new DiscordMessage().addEmbed(new EmbedObject().setTitle("[{executor_name}] Unregistered protection")
					.setDescription(Arrays.asList("A protection has been unregistered"))
					.addField("World", Arrays.asList("`{protection_world}`"))
					.addField("Coordinates", Arrays.asList(
							"`x: {protection_location_x}, y: {protection_location_y}, z: {protection_location_z}`"))
					.addField("Protection ID", Arrays.asList("`{protection_id}`"))
					.addField("Owner", Arrays.asList("`{protection_owner}`"))
					.addField("Protection block ID", Arrays.asList("`{protection_block_id}`"))
					.addField("Removal cause", Arrays.asList("`{removal_cause}`"))
					.setThumbnail(new Thumbnail("{executor_icon_url}"))));

	public static final DiscordMessageTemplate PROTECTION_PURGE_SUMMARY_MESSAGE_TEMPLATE = new DiscordMessageTemplate(
			"Protection-purge-summary",
			new DiscordMessage().addEmbed(new EmbedObject().setTitle("[{executor_name}] Purged protections")
					.setDescription(Arrays.asList("Some protections have been removed due a purge"))
					.addField("Older than", Arrays.asList("`{older_than}`"))
					.addField("Amount of protections", Arrays.asList("`{purged_protections_size}`"))
					.addField("Protections", Arrays.asList("`{purged_protections}`"))
					.setThumbnail(new Thumbnail("{executor_icon_url}"))));

	public static final DiscordMessageTemplate PROTECTION_FLAG_MODIFICATION_MESSAGE_TEMPLATE = new DiscordMessageTemplate(
			"Protection-flag-modification",
			new DiscordMessage().addEmbed(new EmbedObject().setTitle("[{executor_name}] Flag modified")
					.setDescription(Arrays.asList("A flag on a protection has been modified"))
					.addField("Protection ID", Arrays.asList("{protection_id}"))
					.addField("Flag ID", Arrays.asList("{flag_id}"))
					.addField("Previous value", Arrays.asList("{previous_value}"))
					.addField("New value", Arrays.asList("{new_value}"))
					.setThumbnail(new Thumbnail("{executor_icon_url}"))));

	@PandaInject
	private PandaDiscordService discordService;

	@PostInjectMethod
	private void postInject() {
		discordService.registerTemplate(PROTECTION_CREATION_MESSAGE_TEMPLATE);
		discordService.registerTemplate(PROTECTION_REMOVAL_MESSAGE_TEMPLATE);
		discordService.registerTemplate(PROTECTION_PURGE_SUMMARY_MESSAGE_TEMPLATE);
		discordService.registerTemplate(PROTECTION_FLAG_MODIFICATION_MESSAGE_TEMPLATE);
	}

}

package company.pluginName.Modules.DebugPckg.Objects;

import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessageInstance;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessageTemplate;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import lombok.Getter;

public abstract class DebugMessage<T> {

	@PandaInject
	protected static PlaceholdersService placeholdersService;

	protected @Getter PandaBooleanField logEnabledField;
	protected @Getter DiscordMessageTemplate discordMessageTemplate;

	public DebugMessage(String path) {
		this.logEnabledField = new PandaBooleanField("Settings.Debug.Messages." + path, false);
	}

	public DebugMessage(DiscordMessageTemplate discordMessageTemplate) {
		this.discordMessageTemplate = discordMessageTemplate;
	}

	public DebugMessage(String path, DiscordMessageTemplate discordMessageTemplate) {
		this.logEnabledField = new PandaBooleanField("Settings.Debug.Messages." + path, false);
		this.discordMessageTemplate = discordMessageTemplate;
	}

	public abstract String generateLogMessage(T data);

	public boolean isLogEnabled() {
		return this.logEnabledField != null && this.logEnabledField.isTrue();
	}

	public abstract DiscordMessageInstance generateDiscordMessageInstance(T data);

	public boolean isDiscordEnabled() {
		return this.discordMessageTemplate != null && this.discordMessageTemplate.isEnabled();
	}

}

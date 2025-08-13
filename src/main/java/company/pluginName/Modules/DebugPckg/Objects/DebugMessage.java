package company.pluginName.Modules.DebugPckg.Objects;

import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import lombok.Getter;

public abstract class DebugMessage<T> {

	protected @Getter PandaBooleanField logEnabledField;
	protected @Getter PandaBooleanField discordEnabledField;

	public DebugMessage(String path) {
		logEnabledField = new PandaBooleanField("Settings.Debug.Messages." + path, false);
		discordEnabledField = new PandaBooleanField("Discord.Logs." + path, true);
	}

	public abstract String generateLogMessage(T data);

	public boolean isLogEnabled() {
		return this.logEnabledField.isTrue();
	}

	public abstract DiscordMessage generateDiscordMessage(T data);

	public boolean isDiscordEnabled() {
		return this.discordEnabledField.isTrue();
	}

}

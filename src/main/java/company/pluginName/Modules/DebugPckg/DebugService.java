package company.pluginName.Modules.DebugPckg;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.DebugPckg.Objects.DebugMessage;
import company.pluginName.Modules.DebugPckg.Objects.Protection.ProtectionCreationDebugMessage;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.PandaDiscordWebhookService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;

@PandaService
public class DebugService {

	@RegisteredPandaField("config")
	public static final PandaBooleanField ENABLED = new PandaBooleanField("Settings.Debug.Enabled", false);

	@PandaInject
	private PandaFilesService filesService;

	@PandaInject
	private PandaDiscordWebhookService pandaDiscordWebhookService;

	private HashMap<Class<? extends DebugMessage<?>>, DebugMessage<?>> debugMessages = new HashMap<>();

	public DebugService() {
		registerDebugMessage(ProtectionCreationDebugMessage.class);
	}

	@PostInjectMethod
	private void postInject() {
		PandaYamlFile pandaFile = this.filesService.getYaml("config");

		this.debugMessages.values().forEach(debugMessage -> pandaFile.registerFieldsFromObject(debugMessage));
	}

	public <T extends DebugMessage<?>> void registerDebugMessage(Class<T> debugMessageClass) {
		try {
			debugMessages.put(debugMessageClass, debugMessageClass.getConstructor().newInstance());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends DebugMessage<?>> T getDebugMessage(Class<T> debugMessageClass) {
		return (T) this.debugMessages.get(debugMessageClass);
	}

	public <T> void sendDebugMessage(DebugMessage<T> debugMessage, T data) {
		if (ENABLED.isTrue() && debugMessage.getLogEnabledField().isTrue()) {
			MainPluginClass.getSimpleLogger().sendDebug(debugMessage.generateLogMessage(data));
		}

		if (debugMessage.getDiscordEnabledField().isTrue() && pandaDiscordWebhookService != null
				&& pandaDiscordWebhookService.isWebhookDefined()) {
			pandaDiscordWebhookService.sendMessage(debugMessage.generateDiscordMessage(data));
		}
	}

}

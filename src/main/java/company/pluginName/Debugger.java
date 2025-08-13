package company.pluginName;

import java.util.function.Supplier;

import org.bukkit.Bukkit;

import company.pluginName.Modules.FilePckg.FilesService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import lombok.Getter;

public class Debugger {

	@RegisteredPandaField("config")
	public static final PandaBooleanField SETTINGS_DEBUG_ENABLED = new PandaBooleanField("Settings.Debug.Enabled",
			false);

	@PandaInject
	public static FilesService filesService;

	@PostInjectMethod
	public static void postInject() {
		PandaYamlFile file = filesService.getYaml("config");
		if (file != null) {
			for (MessageType messageType : MessageType.values()) {
				file.registerFields(messageType.getField());
			}
		}
	}

	public static void log(MessageType messageType) {
		log(messageType, () -> new Object[0]);
	}

	public static void log(MessageType messageType, Supplier<Object[]> argsSupplier) {
		try {
			if (SETTINGS_DEBUG_ENABLED.getContent() && messageType.getField().getContent()) {
				MessageTemplate
						.inst(PandaPrefixedStringField
								.applyPrefix(String.format(messageType.getMessage(), argsSupplier.get())))
						.process().sendMessage(Bukkit.getConsoleSender());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Getter
	public static enum MessageType {
		BLOCK_PLACE("Block-place", "Player %s is attempting to place a block on location x(%s) y(%s) z(%s)"),

		ORAXEN_BLOCK_PLACE("Oraxen-block-place",
				"Player %s is attempting to place an oraxen block on location x(%s) y(%s) z(%s)"),

		ITEMSADDER_BLOCK_PLACE("Itemsadder-block-place",
				"Player %s is attempting to place an itemsadder block on location x(%s) y(%s) z(%s)"),

		BLOCK_PLACE_NOT_PROTECTION_BLOCK("Block-place-not-protection-block",
				"Item used by %s is currently not a protection block"),

		BLOCK_PLACE_IS_PROTECTION_BLOCK("Block-place-is-protection-block",
				"Protection block the ID '%s' has been found linked to the item used by %s"),

		BLOCK_PLACE_CANCELLED_BUILD("Block-place-cancelled-build",
				"Attempt to place a protection block was cancelled due player can't build"),

		BLOCK_PLACE_CANCELLED("Block-place-cancelled",
				"Attempt to place a protection block was already cancelled by another plugin"),

		BLOCK_BREAK("Block-break", "Player %s is attempting to break a block on location x(%s) y(%s) z(%s)"),

		ORAXEN_BLOCK_BREAK("Oraxen-block-break",
				"Player %s is attempting to break an oraxen block on location x(%s) y(%s) z(%s)"),

		ITEMSADDER_BLOCK_BREAK("Itemsadder-block-break",
				"Player %s is attempting to break an itemsadder block on location x(%s) y(%s) z(%s)"),

		BLOCK_BREAK_CANCELLED("Block-break-cancelled",
				"Attempt to break a protection block was already cancelled by another plugin"),

		PROTECTION_CREATION_ATTEMPT("Protection-creation-attempt",
				"Player %s is attempting to create protection from location x(%s) y(%s) z(%s)"),

		PROTECTION_CREATION_ATTEMPT_CANCELLED("Protection-creation-attempt-cancelled",
				"The creation attempt for protection in x(%s) y(%s) z(%s) has been cancelled by another plugin"),

		PROTECTION_REMOVAL_ATTEMPT("Protection-removal-attempt",
				"Player %s is attempting to remove '%s' from location x(%s) y(%s) z(%s)"),

		PROTECTION_REMOVAL_ATTEMPT_CANCELLED("Protection-removal-attempt-cancelled",
				"The removal attempt for protection '%s' has been cancelled by another plugin"),

		PROTECTION_REMOVAL("Protection-removal", "Player %s removed protection '%s' from location x(%s) y(%s) z(%s)"),

		PLAYER_TELEPORT_TO_PROTECTION_ATTEMPT("Player-teleport-to-protection-attempt",
				"Player '%s' is attempting to teleport to protection '%s'"),

		PLAYER_TELEPORT_TO_PROTECTION_ATTEMPT_CANCELLED("Player-teleport-to-protection-attempt-cancelled",
				"The teleport attempt from '%s' to '%s' has been cancelled by another plugin");

		private String id;
		private String message;
		private PandaBooleanField field;

		private MessageType(String id, String message) {
			this.id = id;
			this.message = message;
			this.field = new PandaBooleanField(String.format("Settings.Debug.Messages.%s", id), true);

		}

	}

}

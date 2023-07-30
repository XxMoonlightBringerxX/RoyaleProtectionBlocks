package company.pluginName.Exceptions;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import company.pluginName.Modules.FilePckg.Messages.MessageString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public abstract class RoyaleProtectionBlocksException extends Exception {

	private static final long serialVersionUID = 7457666443050515225L;
	private MessageString messageString;

	public RoyaleProtectionBlocksException(String string) {
		super(string);
	}

	public RoyaleProtectionBlocksException(String string, Exception exception) {
		super(string, exception);
	}

	public void sendError(CommandSender dest) {
		if (messageString == null) {
			messageString = Arrays.stream(MessageString.values())
					.filter(msg -> msg.getPath().equals("Error.Exception." + this.getMessage())).findFirst()
					.orElse(MessageString.ERROR_ERROR);
		}
		MessageBuilder.createMessage(messageString.applyPrefix()).sendMessage(dest);
		if (this.getCause() != null) {
			this.printStackTrace();
		}
	}

}

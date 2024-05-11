package company.pluginName.Exceptions;

import org.bukkit.command.CommandSender;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaThrowableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class RoyaleProtectionBlocksException extends Exception {

	private static final long serialVersionUID = 7457666443050515225L;

	private PandaThrowableField<RoyaleProtectionBlocksException> exceptionType;

	private Replacement[] replacements;

	public RoyaleProtectionBlocksException(PandaThrowableField<RoyaleProtectionBlocksException> exceptionType) {
		super(exceptionType.getContent());
		this.exceptionType = exceptionType;
	}

	public RoyaleProtectionBlocksException(PandaThrowableField<RoyaleProtectionBlocksException> exceptionType,
			Throwable exception) {
		super(exceptionType.getContent(), exception);
		this.exceptionType = exceptionType;
	}

	public RoyaleProtectionBlocksException setReplacements(Replacement... textReplacements) {
		this.replacements = textReplacements;
		return this;
	}

	public void sendError(CommandSender dest) {
		MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(exceptionType.getContent()))
				.setReplacements(replacements).process().sendMessage(dest);
		if (this.getCause() != null) {
			this.printStackTrace();
		}
	}

}

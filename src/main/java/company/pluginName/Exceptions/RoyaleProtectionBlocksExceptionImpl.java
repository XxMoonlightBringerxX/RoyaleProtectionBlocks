package company.pluginName.Exceptions;

import org.bukkit.command.CommandSender;

import company.pluginName.Exceptions.Exceptions.ThrowableField;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class RoyaleProtectionBlocksExceptionImpl extends RoyaleProtectionBlocksException {

	private static final long serialVersionUID = 7457666443050515225L;

	private ThrowableField exceptionType;

	private Replacement[] replacements;

	public RoyaleProtectionBlocksExceptionImpl(ThrowableField exceptionType) {
		super(exceptionType.getContent());
		this.exceptionType = exceptionType;
	}

	public RoyaleProtectionBlocksExceptionImpl(ThrowableField exceptionType, Throwable exception) {
		super(exceptionType.getContent(), exception);
		this.exceptionType = exceptionType;
	}

	public RoyaleProtectionBlocksExceptionImpl setReplacements(Replacement... textReplacements) {
		this.replacements = textReplacements;
		return this;
	}

	@Override
	public Type getType() {
		return this.exceptionType.getExceptionType();
	}

	public void sendError(CommandSender dest) {
		MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(exceptionType.getContent()))
				.setReplacements(replacements).process().sendMessage(dest);
		if (this.getCause() != null) {
			this.printStackTrace();
		}
	}

}

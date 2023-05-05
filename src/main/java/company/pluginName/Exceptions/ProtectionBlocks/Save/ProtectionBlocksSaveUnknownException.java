package company.pluginName.Exceptions.ProtectionBlocks.Save;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionBlocksSaveUnknownException extends ProtectionBlocksSaveException {

	private static final long serialVersionUID = -917550965578241248L;

	public ProtectionBlocksSaveUnknownException() {
		this(null);
	}

	public ProtectionBlocksSaveUnknownException(Exception exception) {
		super("protection.blocks.save.Unknown", exception);
	}

}

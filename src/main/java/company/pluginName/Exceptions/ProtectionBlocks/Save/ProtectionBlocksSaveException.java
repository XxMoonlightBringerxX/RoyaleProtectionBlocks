package company.pluginName.Exceptions.ProtectionBlocks.Save;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionBlocksSaveException extends RoyaleProtectionBlocksException {

	private static final long serialVersionUID = -4580932956867724251L;

	public ProtectionBlocksSaveException(String message) {
		super(message);
	}

	public ProtectionBlocksSaveException(String message, Exception exception) {
		super(message, exception);
	}

}

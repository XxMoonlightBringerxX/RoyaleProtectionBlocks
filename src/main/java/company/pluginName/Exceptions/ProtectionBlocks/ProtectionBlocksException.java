package company.pluginName.Exceptions.ProtectionBlocks;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionBlocksException extends RoyaleProtectionBlocksException {

	private static final long serialVersionUID = 6740726295632629586L;

	public ProtectionBlocksException(String message) {
		super(message);
	}

	public ProtectionBlocksException(String message, Exception exception) {
		super(message, exception);
	}

}

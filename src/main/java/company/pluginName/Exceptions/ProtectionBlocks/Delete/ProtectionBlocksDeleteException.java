package company.pluginName.Exceptions.ProtectionBlocks.Delete;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionBlocksDeleteException extends RoyaleProtectionBlocksException {

	private static final long serialVersionUID = 5443106724348049345L;

	public ProtectionBlocksDeleteException(String message) {
		super(message);
	}

	public ProtectionBlocksDeleteException(String message, Exception exception) {
		super(message, exception);
	}

}

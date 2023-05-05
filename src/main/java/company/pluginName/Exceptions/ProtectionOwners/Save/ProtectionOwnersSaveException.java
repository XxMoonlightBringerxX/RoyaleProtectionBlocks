package company.pluginName.Exceptions.ProtectionOwners.Save;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionOwnersSaveException extends RoyaleProtectionBlocksException {

	private static final long serialVersionUID = 5540515514329018028L;

	public ProtectionOwnersSaveException(String message) {
		super(message);
	}

	public ProtectionOwnersSaveException(String message, Exception exception) {
		super(message, exception);
	}

}

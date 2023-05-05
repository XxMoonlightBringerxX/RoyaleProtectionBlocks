package company.pluginName.Exceptions.Protection.Save;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionSaveException extends RoyaleProtectionBlocksException {

	private static final long serialVersionUID = -5708725399909515288L;

	public ProtectionSaveException(String message) {
		super(message);
	}

	public ProtectionSaveException(String message, Exception exception) {
		super(message, exception);
	}

}

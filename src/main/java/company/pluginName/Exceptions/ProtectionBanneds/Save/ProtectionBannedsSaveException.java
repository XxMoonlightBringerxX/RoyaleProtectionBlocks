package company.pluginName.Exceptions.ProtectionBanneds.Save;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionBannedsSaveException extends RoyaleProtectionBlocksException {

	private static final long serialVersionUID = -1456058852438037401L;

	public ProtectionBannedsSaveException(String message) {
		super(message);
	}

	public ProtectionBannedsSaveException(String message, Exception exception) {
		super(message, exception);
	}

}

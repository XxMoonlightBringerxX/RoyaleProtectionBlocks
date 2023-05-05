package company.pluginName.Exceptions.ProtectionOwners.Delete;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionOwnersDeleteException extends RoyaleProtectionBlocksException {

	private static final long serialVersionUID = 3595739037134297020L;

	public ProtectionOwnersDeleteException(String message) {
		super(message);
	}

	public ProtectionOwnersDeleteException(String message, Exception exception) {
		super(message, exception);
	}

}

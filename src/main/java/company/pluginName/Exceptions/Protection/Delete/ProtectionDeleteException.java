package company.pluginName.Exceptions.Protection.Delete;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionDeleteException extends RoyaleProtectionBlocksException {

	private static final long serialVersionUID = -6986681299892560890L;

	public ProtectionDeleteException(String message) {
		super(message);
	}

	public ProtectionDeleteException(String message, Exception exception) {
		super(message, exception);
	}

}

package company.pluginName.Exceptions.ProtectionBanneds.Delete;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionBannedsDeleteException extends RoyaleProtectionBlocksException {

	private static final long serialVersionUID = 86100524238483346L;

	public ProtectionBannedsDeleteException(String message) {
		super(message);
	}

	public ProtectionBannedsDeleteException(String message, Exception exception) {
		super(message, exception);
	}

}

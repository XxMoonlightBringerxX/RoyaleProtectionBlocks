package company.pluginName.Exceptions.ProtectionBanneds.Delete;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionBannedsDeleteUnknownException extends ProtectionBannedsDeleteException {

	private static final long serialVersionUID = 5178039534332249099L;

	public ProtectionBannedsDeleteUnknownException() {
		this(null);
	}

	public ProtectionBannedsDeleteUnknownException(Exception exception) {
		super("protection.banneds.delete.Unknown", exception);
	}

}

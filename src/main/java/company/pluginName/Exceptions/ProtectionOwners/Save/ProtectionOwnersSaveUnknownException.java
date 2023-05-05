package company.pluginName.Exceptions.ProtectionOwners.Save;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionOwnersSaveUnknownException extends ProtectionOwnersSaveException {

	private static final long serialVersionUID = -2835766080278629606L;

	public ProtectionOwnersSaveUnknownException() {
		this(null);
	}

	public ProtectionOwnersSaveUnknownException(Exception exception) {
		super("protection.owners.save.Unknown", exception);
	}

}

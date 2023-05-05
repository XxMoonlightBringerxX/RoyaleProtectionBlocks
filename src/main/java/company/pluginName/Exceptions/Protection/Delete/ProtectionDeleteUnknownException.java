package company.pluginName.Exceptions.Protection.Delete;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionDeleteUnknownException extends ProtectionDeleteException {

	private static final long serialVersionUID = -8474952746678049694L;

	public ProtectionDeleteUnknownException() {
		this(null);
	}

	public ProtectionDeleteUnknownException(Exception exception) {
		super("protection.delete.Unknown", exception);
	}

}

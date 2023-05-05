package company.pluginName.Exceptions.Protection.Save;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionSaveUnknownException extends ProtectionSaveException {

	private static final long serialVersionUID = -752576279046136689L;

	public ProtectionSaveUnknownException() {
		this(null);
	}

	public ProtectionSaveUnknownException(Exception exception) {
		super("protection.save.Unknown", exception);
	}

}

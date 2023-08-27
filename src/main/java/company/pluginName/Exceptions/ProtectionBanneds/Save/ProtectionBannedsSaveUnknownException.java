package company.pluginName.Exceptions.ProtectionBanneds.Save;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionBannedsSaveUnknownException extends ProtectionBannedsSaveException {

	private static final long serialVersionUID = 6853695564654072004L;

	public ProtectionBannedsSaveUnknownException() {
		this(null);
	}

	public ProtectionBannedsSaveUnknownException(Exception exception) {
		super("protection.banneds.save.Unknown", exception);
	}

}

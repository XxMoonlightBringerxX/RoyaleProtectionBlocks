package company.pluginName.Exceptions.ProtectionOwners.Delete;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionOwnersDeleteUnknownException extends ProtectionOwnersDeleteException {

	private static final long serialVersionUID = -8893817282965169695L;

	public ProtectionOwnersDeleteUnknownException() {
		this(null);
	}

	public ProtectionOwnersDeleteUnknownException(Exception exception) {
		super("protection.owners.delete.Unknown", exception);
	}

}

package company.pluginName.Exceptions.ProtectionMembers.Delete;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionMembersDeleteUnknownException extends ProtectionMembersDeleteException {

	private static final long serialVersionUID = -7640817515584265270L;

	public ProtectionMembersDeleteUnknownException() {
		this(null);
	}

	public ProtectionMembersDeleteUnknownException(Exception exception) {
		super("protection.members.delete.Unknown", exception);
	}

}

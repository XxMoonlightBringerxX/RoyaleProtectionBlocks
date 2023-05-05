package company.pluginName.Exceptions.ProtectionMembers.Save;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionMembersSaveUnknownException extends ProtectionMembersSaveException {

	private static final long serialVersionUID = -8174059635744350433L;

	public ProtectionMembersSaveUnknownException() {
		this(null);
	}

	public ProtectionMembersSaveUnknownException(Exception exception) {
		super("protection.members.save.Unknown", exception);
	}

}

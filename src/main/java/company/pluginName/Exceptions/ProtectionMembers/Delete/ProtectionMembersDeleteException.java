package company.pluginName.Exceptions.ProtectionMembers.Delete;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionMembersDeleteException extends RoyaleProtectionBlocksException {

	private static final long serialVersionUID = 5656030554672920257L;

	public ProtectionMembersDeleteException(String message) {
		super(message);
	}

	public ProtectionMembersDeleteException(String message, Exception exception) {
		super(message, exception);
	}

}

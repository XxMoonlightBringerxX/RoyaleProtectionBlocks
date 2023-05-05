package company.pluginName.Exceptions.ProtectionMembers.Save;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionMembersSaveException extends RoyaleProtectionBlocksException {

	private static final long serialVersionUID = -4131011306524021178L;

	public ProtectionMembersSaveException(String message) {
		super(message);
	}

	public ProtectionMembersSaveException(String message, Exception exception) {
		super(message, exception);
	}

}

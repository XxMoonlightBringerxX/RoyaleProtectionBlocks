package company.pluginName.Exceptions.ProtectionBlocks.Delete;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class ProtectionBlocksDeleteUnknownException extends ProtectionBlocksDeleteException {

	private static final long serialVersionUID = 2670716767191636333L;

	public ProtectionBlocksDeleteUnknownException() {
		this(null);
	}

	public ProtectionBlocksDeleteUnknownException(Exception exception) {
		super("protection.blocks.delete.Unknown", exception);
	}

}

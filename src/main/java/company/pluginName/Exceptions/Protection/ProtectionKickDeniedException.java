package company.pluginName.Exceptions.Protection;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;

public class ProtectionKickDeniedException extends RoyaleProtectionBlocksException {

	private static final long serialVersionUID = -3795277261155762574L;

	public ProtectionKickDeniedException() {
		super("protection.kick.Denied");
	}

}

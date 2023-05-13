package company.pluginName.Exceptions.ProtectionBlocks.Save;

public class ProtectionBlocksSaveIdInUseException extends ProtectionBlocksSaveException {

	private static final long serialVersionUID = 8865443036167821138L;

	public ProtectionBlocksSaveIdInUseException() {
		super("protection.blocks.save.IdInUse");
	}
}

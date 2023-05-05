package company.pluginName.Exceptions.ProtectionBlocks.Save;

public class ProtectionBlocksSaveNameInUseException extends ProtectionBlocksSaveException {

	private static final long serialVersionUID = 8865443036167821138L;

	public ProtectionBlocksSaveNameInUseException() {
		super("protection.blocks.save.NameInUse");
	}
}

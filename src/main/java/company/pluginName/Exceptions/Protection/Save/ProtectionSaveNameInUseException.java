package company.pluginName.Exceptions.Protection.Save;

public class ProtectionSaveNameInUseException extends ProtectionSaveException {

	private static final long serialVersionUID = 2924532014676036297L;

	public ProtectionSaveNameInUseException() {
		super("protection.save.NameInUse");
	}
}

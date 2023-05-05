package company.pluginName.Exceptions.Protection.Save;

public class ProtectionSaveDeniedException extends ProtectionSaveException {

	private static final long serialVersionUID = 756219112913664261L;

	public ProtectionSaveDeniedException() {
		super("protection.save.PermissionDenied");
	}
}

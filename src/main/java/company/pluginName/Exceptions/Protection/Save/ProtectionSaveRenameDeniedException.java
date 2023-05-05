package company.pluginName.Exceptions.Protection.Save;

public class ProtectionSaveRenameDeniedException extends ProtectionSaveException {

	private static final long serialVersionUID = 2924532014676036297L;

	public ProtectionSaveRenameDeniedException() {
		super("protection.save.RenamePermissionDenied");
	}
}

package company.pluginName.Exceptions.Protection.Delete;

public class ProtectionDeleteDeniedException extends ProtectionDeleteException {

	private static final long serialVersionUID = 8834976289404341862L;

	public ProtectionDeleteDeniedException() {
		super("protection.delete.PermissionDenied");
	}
}

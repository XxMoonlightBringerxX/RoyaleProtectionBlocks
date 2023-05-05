package company.pluginName.Exceptions.ProtectionOwners.Save;

public class ProtectionOwnersSaveDeniedException extends ProtectionOwnersSaveException {

	private static final long serialVersionUID = 8821207880121102153L;

	public ProtectionOwnersSaveDeniedException() {
		super("protection.owners.save.PermissionDenied");
	}
}

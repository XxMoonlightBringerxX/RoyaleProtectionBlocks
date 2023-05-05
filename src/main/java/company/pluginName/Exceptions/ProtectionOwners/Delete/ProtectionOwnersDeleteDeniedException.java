package company.pluginName.Exceptions.ProtectionOwners.Delete;

public class ProtectionOwnersDeleteDeniedException extends ProtectionOwnersDeleteException {

	private static final long serialVersionUID = -6557441044713907129L;

	public ProtectionOwnersDeleteDeniedException() {
		super("protection.owners.delete.PermissionDenied");
	}
}

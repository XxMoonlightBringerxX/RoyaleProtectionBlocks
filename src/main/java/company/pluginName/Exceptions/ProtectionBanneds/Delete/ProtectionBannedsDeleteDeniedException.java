package company.pluginName.Exceptions.ProtectionBanneds.Delete;

public class ProtectionBannedsDeleteDeniedException extends ProtectionBannedsDeleteException {

	private static final long serialVersionUID = -7540110515680272935L;

	public ProtectionBannedsDeleteDeniedException() {
		super("protection.banneds.delete.PermissionDenied");
	}
}

package company.pluginName.Exceptions.ProtectionOwners.Save;

public class ProtectionOwnersSaveCannotAddYourselfException extends ProtectionOwnersSaveException {

	private static final long serialVersionUID = -7367355364253009L;

	public ProtectionOwnersSaveCannotAddYourselfException() {
		super("protection.owners.save.CannotAddYourself");
	}
}

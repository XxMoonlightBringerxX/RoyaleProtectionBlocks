package company.pluginName.Exceptions.ProtectionBanneds.Save;

public class ProtectionBannedsSaveCannotAddYourselfException extends ProtectionBannedsSaveException {

	private static final long serialVersionUID = 8423786400869195756L;

	public ProtectionBannedsSaveCannotAddYourselfException() {
		super("protection.banneds.save.CannotAddYourself");
	}
}

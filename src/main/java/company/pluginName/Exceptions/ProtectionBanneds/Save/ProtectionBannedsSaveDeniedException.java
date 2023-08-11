package company.pluginName.Exceptions.ProtectionBanneds.Save;

public class ProtectionBannedsSaveDeniedException extends ProtectionBannedsSaveException {

	private static final long serialVersionUID = 4066855894695293160L;

	public ProtectionBannedsSaveDeniedException() {
		super("protection.banneds.save.PermissionDenied");
	}
}

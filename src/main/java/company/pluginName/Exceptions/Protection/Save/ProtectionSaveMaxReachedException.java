package company.pluginName.Exceptions.Protection.Save;

public class ProtectionSaveMaxReachedException extends ProtectionSaveException {

	private static final long serialVersionUID = 4284982380201432429L;

	public ProtectionSaveMaxReachedException() {
		super("protection.save.MaxReached");
	}
}

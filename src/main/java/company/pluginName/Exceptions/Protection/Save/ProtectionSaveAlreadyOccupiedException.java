package company.pluginName.Exceptions.Protection.Save;

public class ProtectionSaveAlreadyOccupiedException extends ProtectionSaveException {

	private static final long serialVersionUID = 4510094535263026102L;

	public ProtectionSaveAlreadyOccupiedException() {
		super("protection.save.AlreadyOccupied");
	}
}

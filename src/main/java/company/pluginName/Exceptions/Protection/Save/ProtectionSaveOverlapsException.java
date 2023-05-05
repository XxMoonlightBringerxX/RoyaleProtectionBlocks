package company.pluginName.Exceptions.Protection.Save;

public class ProtectionSaveOverlapsException extends ProtectionSaveException {

	private static final long serialVersionUID = 5699087276968898324L;

	public ProtectionSaveOverlapsException() {
		super("protection.save.Overlaps");
	}
}

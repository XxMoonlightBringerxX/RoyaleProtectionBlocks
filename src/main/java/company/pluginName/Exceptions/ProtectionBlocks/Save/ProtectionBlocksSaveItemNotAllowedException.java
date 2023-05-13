package company.pluginName.Exceptions.ProtectionBlocks.Save;

public class ProtectionBlocksSaveItemNotAllowedException extends ProtectionBlocksSaveException {

	private static final long serialVersionUID = -8803885989912904582L;

	public ProtectionBlocksSaveItemNotAllowedException() {
		super("protection.blocks.save.ItemNotAllowed");
	}
}

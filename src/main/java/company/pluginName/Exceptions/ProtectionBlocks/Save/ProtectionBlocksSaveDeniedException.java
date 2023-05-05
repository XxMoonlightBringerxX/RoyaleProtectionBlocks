package company.pluginName.Exceptions.ProtectionBlocks.Save;

public class ProtectionBlocksSaveDeniedException extends ProtectionBlocksSaveException {

	private static final long serialVersionUID = -1058611617926677013L;

	public ProtectionBlocksSaveDeniedException() {
		super("protection.blocks.save.PermissionDenied");
	}
}

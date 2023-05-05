package company.pluginName.Exceptions.ProtectionBlocks.Delete;

public class ProtectionBlocksDeleteDeniedException extends ProtectionBlocksDeleteException {

	private static final long serialVersionUID = 9049728502875884193L;

	public ProtectionBlocksDeleteDeniedException() {
		super("protection.blocks.delete.PermissionDenied");
	}
}

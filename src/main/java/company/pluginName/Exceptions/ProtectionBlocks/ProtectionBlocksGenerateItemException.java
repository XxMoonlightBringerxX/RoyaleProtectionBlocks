package company.pluginName.Exceptions.ProtectionBlocks;

public class ProtectionBlocksGenerateItemException extends ProtectionBlocksException {

	private static final long serialVersionUID = -5997199631279540412L;

	public ProtectionBlocksGenerateItemException() {
		super("protection.blocks.GenerateItem");
	}

	public ProtectionBlocksGenerateItemException(Exception exception) {
		super("protection.blocks.GenerateItem", exception);
	}
}

package company.pluginName.Exceptions.ProtectionMembers.Save;

public class ProtectionMembersSaveDeniedException extends ProtectionMembersSaveException {

	private static final long serialVersionUID = -8299631525671964824L;

	public ProtectionMembersSaveDeniedException() {
		super("protection.members.save.PermissionDenied");
	}
}

package company.pluginName.Exceptions.ProtectionMembers.Delete;

public class ProtectionMembersDeleteDeniedException extends ProtectionMembersDeleteException {

	private static final long serialVersionUID = 8666422019194675786L;

	public ProtectionMembersDeleteDeniedException() {
		super("protection.members.delete.PermissionDenied");
	}
}

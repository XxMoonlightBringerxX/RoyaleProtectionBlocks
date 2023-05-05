package company.pluginName.Exceptions.ProtectionMembers.Save;

public class ProtectionMembersSaveCannotAddYourselfException extends ProtectionMembersSaveException {

	private static final long serialVersionUID = -755893118367465860L;

	public ProtectionMembersSaveCannotAddYourselfException() {
		super("protection.members.save.CannotAddYourself");
	}
}

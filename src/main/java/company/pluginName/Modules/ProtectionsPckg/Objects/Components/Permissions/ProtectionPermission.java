package company.pluginName.Modules.ProtectionsPckg.Objects.Components.Permissions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class ProtectionPermission {

	private final String id;
	private Boolean nonMembersValue;
	private Boolean membersValue;
	private Boolean ownersValue;

}

package company.pluginName.Modules.ProtectionSettingsPckg.Objects;

import lombok.Data;

@Data
public class SettingValue<T> {

	private T nonMembersValue;
	private T membersValue;
	private T ownersValue;

}

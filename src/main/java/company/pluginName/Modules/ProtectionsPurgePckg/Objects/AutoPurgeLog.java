package company.pluginName.Modules.ProtectionsPurgePckg.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@AllArgsConstructor
public class AutoPurgeLog {

	private long executionMillis;
	private long olderThanMillis;
	private int removedProtections;

}

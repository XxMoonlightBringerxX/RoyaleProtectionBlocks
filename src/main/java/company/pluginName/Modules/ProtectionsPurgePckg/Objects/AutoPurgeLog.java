package company.pluginName.Modules.ProtectionsPurgePckg.Objects;

import java.util.List;

import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@AllArgsConstructor
public class AutoPurgeLog {

	private long executionMillis;
	private long olderThanMillis;
	private List<Protection> removedProtections;

}

package company.pluginName.Modules.ProtectionsPckg.Objects;

import java.util.UUID;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ProtectionMember {

	public static enum WorldGuardRole {
		MEMBER, OWNER;

		public WorldGuardRole previous() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}

		public WorldGuardRole next() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}
	}

	private @NonNull Protection protection;
	private @NonNull UUID memberUuid;
	private WorldGuardRole worldGuardRole = WorldGuardRole.MEMBER;
	private boolean canInteract;
	private boolean canBuild;

}

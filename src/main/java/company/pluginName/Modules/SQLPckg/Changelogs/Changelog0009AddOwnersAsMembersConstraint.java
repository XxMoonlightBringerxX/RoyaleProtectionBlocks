package company.pluginName.Modules.SQLPckg.Changelogs;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.ManualStatement;

public class Changelog0009AddOwnersAsMembersConstraint extends SQLChangelog {

	public Changelog0009AddOwnersAsMembersConstraint() {
		super(9, "Adding owners as members", (sqlConnection) -> {
			sqlConnection.executeManual(ManualStatement.inst("INSERT INTO " + sqlConnection.getPrefix()
					+ Changelog0008RegenerateMembersAndOwnersConstraint.PROTECTION_MEMBERS_TABLE.getName()
					+ " SELECT * FROM " + sqlConnection.getPrefix()
					+ Changelog0008RegenerateMembersAndOwnersConstraint.PROTECTION_OWNERS_TABLE.getName()
					+ " po WHERE (SELECT COUNT(*) FROM " + sqlConnection.getPrefix()
					+ Changelog0008RegenerateMembersAndOwnersConstraint.PROTECTION_MEMBERS_TABLE.getName()
					+ " pm WHERE pm.RegionId = po.RegionId AND pm.MemberUuid = po.OwnerUuid) = 0"));
		});
	}

}

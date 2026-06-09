package company.pluginName.Modules.SQLPckg.Changelogs;

import java.sql.Types;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.AlterTableStatement.AlterTableAddColumnStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;

public class Changelog0013AddInvitationRequirementColumn extends SQLChangelog {

	public Changelog0013AddInvitationRequirementColumn() {
		super(13, "Adding invitation requirement status column", (sqlConnection) -> {
			sqlConnection.executeAlterTable(AlterTableAddColumnStatement.inst("PlayerData",
					new Column("InvitationRequirement", Types.VARCHAR, "VARCHAR(64)")));
		});
	}

}

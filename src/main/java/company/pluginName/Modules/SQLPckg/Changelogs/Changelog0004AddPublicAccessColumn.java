package company.pluginName.Modules.SQLPckg.Changelogs;

import java.sql.Types;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.AlterTableStatement.AlterTableAddColumnStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;

public class Changelog0004AddPublicAccessColumn extends SQLChangelog {

	public Changelog0004AddPublicAccessColumn() {
		super(4, "Adding public access column", (sqlConnection) -> {
			sqlConnection.executeAlterTable(AlterTableAddColumnStatement.inst("Protections",
					new Column("PublicAccess", Types.BOOLEAN, "BOOLEAN", "0")));
		});
	}

}

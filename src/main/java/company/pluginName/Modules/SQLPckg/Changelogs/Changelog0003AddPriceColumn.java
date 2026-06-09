package company.pluginName.Modules.SQLPckg.Changelogs;

import java.sql.Types;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.AlterTableStatement.AlterTableAddColumnStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;

public class Changelog0003AddPriceColumn extends SQLChangelog {

	public Changelog0003AddPriceColumn() {
		super(3, "Adding price column", (sqlConnection) -> {
			sqlConnection.executeAlterTable(AlterTableAddColumnStatement.inst("Protections",
					new Column("Price", Types.DECIMAL, "DECIMAL(12, 2)")));
		});
	}

}

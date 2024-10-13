package company.pluginName.Modules.SQLPckg.Changelogs;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.DropTableStatement;

public class Changelog0001DeleteAutoPurgeLogsTable extends SQLChangelog {

	public Changelog0001DeleteAutoPurgeLogsTable() {
		super(1, "Delete AutoPurgeLogs table", (sqlConnection) -> {
			sqlConnection.executeDropTable(DropTableStatement.inst(Changelog0000Init.AUTO_PURGE_LOGS_TABLE));
		});
	}

}

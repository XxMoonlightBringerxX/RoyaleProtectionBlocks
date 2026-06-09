package company.pluginName.Modules.SQLPckg.Changelogs;

import java.sql.Types;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.CreateTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;

public class Changelog0010AddPlayerDataTable extends SQLChangelog {

	private static final Table PLAYER_DATA_TABLE = new Table("PlayerData").addColumns(
			new Column("PlayerUuid", Types.CHAR, "CHAR(36)").setPrimary(true).setNotNull(true),
			new Column("AutoFlight", Types.BOOLEAN, "BOOLEAN", "0").setNotNull(true));

	public Changelog0010AddPlayerDataTable() {
		super(10, "Adding PlayerData table", (sqlConnection) -> {
			sqlConnection.executeCreateTable(CreateTableStatement.inst(PLAYER_DATA_TABLE));
		});
	}

}

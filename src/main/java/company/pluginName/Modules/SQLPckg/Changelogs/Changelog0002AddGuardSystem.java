package company.pluginName.Modules.SQLPckg.Changelogs;

import java.sql.Types;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.AlterTableStatement.AlterTableAddColumnStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.CreateTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;

public class Changelog0002AddGuardSystem extends SQLChangelog {

	private static final Table PLAYER_GUARDS_TABLE = new Table("PlayerGuards").addColumns(
			new Column("PlayerUuid", Types.CHAR, "CHAR(36)").setPrimary(true).setNotNull(true),
			new Column("GuardExpirationDate", Types.BIGINT, "VARCHAR(256)").setNotNull(true));

	public Changelog0002AddGuardSystem() {
		super(2, "Adding guard system", (sqlConnection) -> {
			sqlConnection.executeAlterTable(
					AlterTableAddColumnStatement.inst("Protections", new Column("GuardExpirationDate", Types.BIGINT)));
			sqlConnection.executeCreateTable(CreateTableStatement.inst(PLAYER_GUARDS_TABLE));
		});
	}

}

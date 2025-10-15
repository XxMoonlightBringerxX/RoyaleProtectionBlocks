package company.pluginName.Modules.SQLPckg.Changelogs;

import java.sql.Types;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.AlterTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;

public class Changelog0015AddAsOwnerInvitationColumn extends SQLChangelog {

	public Changelog0015AddAsOwnerInvitationColumn() {
		super(15, "Adding 'AddAsOwner' column to invitations table", (sqlConnection) -> {

			sqlConnection.executeAlterTable(AlterTableStatement.AlterTableAddColumnStatement.inst(
					Changelog0012AddInvitationsTable.PROTECTION_INVITATIONS_TABLE,
					new Column("AddAsOwner", Types.BOOLEAN).setNotNull(true)));
		});
	}

}

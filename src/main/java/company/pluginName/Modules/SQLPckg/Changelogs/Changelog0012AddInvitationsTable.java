package company.pluginName.Modules.SQLPckg.Changelogs;

import java.sql.Types;
import java.util.Arrays;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.CreateTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table.UniqueConstraint;

public class Changelog0012AddInvitationsTable extends SQLChangelog {

	public static final Table PROTECTION_INVITATIONS_TABLE = new Table("ProtectionInvitations").addColumns(
			new Column("RegionId", Types.VARCHAR, "VARCHAR(256)").setNotNull(true),
			new Column("PlayerUuid", Types.CHAR, "CHAR(36)").setNotNull(true),
			new Column("CreatedDate", Types.BIGINT).setNotNull(true));

	static {
		PROTECTION_INVITATIONS_TABLE
				.addUniqueConstraint(
						new UniqueConstraint(Arrays.asList(PROTECTION_INVITATIONS_TABLE.getColumn("RegionId"),
								PROTECTION_INVITATIONS_TABLE.getColumn("PlayerUuid"))))
				.addForeignConstraint(
						new Table.ForeignConstraint().addColumn(PROTECTION_INVITATIONS_TABLE.getColumn("RegionId"))
								.addReferenceColumn(Changelog0000Init.PROTECTIONS_TABLE.getColumn("RegionId")));
	}

	public Changelog0012AddInvitationsTable() {
		super(12, "Adding invitations table", (sqlConnection) -> {
			sqlConnection.executeCreateTable(CreateTableStatement.inst(PROTECTION_INVITATIONS_TABLE));
		});
	}

}

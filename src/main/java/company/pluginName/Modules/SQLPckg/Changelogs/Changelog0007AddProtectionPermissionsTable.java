package company.pluginName.Modules.SQLPckg.Changelogs;

import java.sql.Types;
import java.util.Arrays;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.CreateTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table.UniqueConstraint;

public class Changelog0007AddProtectionPermissionsTable extends SQLChangelog {

	private static final Table PROTECTION_PERMISSIONS_TABLE = new Table("ProtectionPermissions").addColumns(
			new Column("RegionId", Types.VARCHAR, "VARCHAR(256)").setNotNull(true),
			new Column("PermissionId", Types.VARCHAR, "VARCHAR(32)").setNotNull(true),
			new Column("NonMembersValue", Types.BOOLEAN), new Column("MembersValue", Types.BOOLEAN),
			new Column("OwnersValue", Types.BOOLEAN));

	static {
		PROTECTION_PERMISSIONS_TABLE
				.addUniqueConstraint(
						new UniqueConstraint(Arrays.asList(PROTECTION_PERMISSIONS_TABLE.getColumn("RegionId"),
								PROTECTION_PERMISSIONS_TABLE.getColumn("PermissionId"))))
				.addForeignConstraint(new Table.ForeignConstraint()
						.addColumn(Changelog0000Init.PROTECTIONS_TABLE.getColumn("RegionId"))
						.addReferenceColumn(PROTECTION_PERMISSIONS_TABLE.getColumn("RegionId")));
	}

	public Changelog0007AddProtectionPermissionsTable() {
		super(7, "Add protection permissions tables", (sqlConnection) -> {
			sqlConnection.executeCreateTable(CreateTableStatement.inst(PROTECTION_PERMISSIONS_TABLE));
		});
	}

}

package company.pluginName.Modules.SQLPckg.Changelogs;

import java.sql.Types;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.CreateTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;

public class Changelog0005AddMembersAndOwnersTables extends SQLChangelog {

	private static final Table PROTECTION_MEMBERS_TABLE = new Table("ProtectionMembers").addColumns(
			new Column("RegionId", Types.VARCHAR, "VARCHAR(256)").setNotNull(true),
			new Column("MemberUuid", Types.CHAR, "CHAR(36)").setNotNull(true));

	private static final Table PROTECTION_OWNERS_TABLE = new Table("ProtectionOwners").addColumns(
			new Column("RegionId", Types.VARCHAR, "VARCHAR(256)").setNotNull(true),
			new Column("OwnerUuid", Types.CHAR, "CHAR(36)").setNotNull(true));

	static {
		PROTECTION_MEMBERS_TABLE
				.addUniqueConstraint(
						new Table.UniqueConstraint().addColumns(PROTECTION_MEMBERS_TABLE.getColumn("RegionId"),
								PROTECTION_MEMBERS_TABLE.getColumn("MemberUuid")))
				.addForeignConstraint(
						new Table.ForeignConstraint().addColumn(PROTECTION_MEMBERS_TABLE.getColumn("RegionId"))
								.addReferenceColumn(Changelog0000Init.PROTECTIONS_TABLE.getColumn("RegionId")));

		PROTECTION_OWNERS_TABLE
				.addUniqueConstraint(new Table.UniqueConstraint().addColumns(
						PROTECTION_OWNERS_TABLE.getColumn("RegionId"), PROTECTION_OWNERS_TABLE.getColumn("OwnerUuid")))
				.addForeignConstraint(
						new Table.ForeignConstraint().addColumn(PROTECTION_OWNERS_TABLE.getColumn("RegionId"))
								.addReferenceColumn(Changelog0000Init.PROTECTIONS_TABLE.getColumn("RegionId")));
	}

	public Changelog0005AddMembersAndOwnersTables() {
		super(5, "Add members and owners tables", (sqlConnection) -> {
			sqlConnection.executeCreateTable(CreateTableStatement.inst(PROTECTION_MEMBERS_TABLE));
			sqlConnection.executeCreateTable(CreateTableStatement.inst(PROTECTION_OWNERS_TABLE));
		});
	}

}

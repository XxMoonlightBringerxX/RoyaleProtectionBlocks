package company.pluginName.Modules.SQLPckg.Changelogs;

import java.sql.Types;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.AlterTableStatement.AlterTableRenameToStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.CreateTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.DropTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.ManualStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;

public class Changelog0008RegenerateMembersAndOwnersConstraint extends SQLChangelog {

	public static final Table PROTECTION_MEMBERS_TABLE = new Table("ProtectionMembers").addColumns(
			new Column("RegionId", Types.VARCHAR, "VARCHAR(256)").setNotNull(true),
			new Column("MemberUuid", Types.CHAR, "CHAR(36)").setNotNull(true));

	public static final Table PROTECTION_OWNERS_TABLE = new Table("ProtectionOwners").addColumns(
			new Column("RegionId", Types.VARCHAR, "VARCHAR(256)").setNotNull(true),
			new Column("OwnerUuid", Types.CHAR, "CHAR(36)").setNotNull(true));

	public static final Table PROTECTION_MEMBERS_COPY_TABLE = new Table("ProtectionMembersCopy").addColumns(
			new Column("RegionId", Types.VARCHAR, "VARCHAR(256)").setNotNull(true),
			new Column("MemberUuid", Types.CHAR, "CHAR(36)").setNotNull(true));

	public static final Table PROTECTION_OWNERS_COPY_TABLE = new Table("ProtectionOwnersCopy").addColumns(
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

		PROTECTION_MEMBERS_COPY_TABLE
				.addUniqueConstraint(
						new Table.UniqueConstraint().addColumns(PROTECTION_MEMBERS_COPY_TABLE.getColumn("RegionId"),
								PROTECTION_MEMBERS_COPY_TABLE.getColumn("MemberUuid")))
				.addForeignConstraint(
						new Table.ForeignConstraint().addColumn(PROTECTION_MEMBERS_COPY_TABLE.getColumn("RegionId"))
								.addReferenceColumn(Changelog0000Init.PROTECTIONS_TABLE.getColumn("RegionId")));

		PROTECTION_OWNERS_COPY_TABLE
				.addUniqueConstraint(
						new Table.UniqueConstraint().addColumns(PROTECTION_OWNERS_COPY_TABLE.getColumn("RegionId"),
								PROTECTION_OWNERS_COPY_TABLE.getColumn("OwnerUuid")))
				.addForeignConstraint(
						new Table.ForeignConstraint().addColumn(PROTECTION_OWNERS_COPY_TABLE.getColumn("RegionId"))
								.addReferenceColumn(Changelog0000Init.PROTECTIONS_TABLE.getColumn("RegionId")));
	}

	public Changelog0008RegenerateMembersAndOwnersConstraint() {
		super(8, "Regenerating members and owners foreign key constraints", (sqlConnection) -> {
			sqlConnection.executeCreateTable(CreateTableStatement.inst(PROTECTION_MEMBERS_COPY_TABLE));
			sqlConnection.executeCreateTable(CreateTableStatement.inst(PROTECTION_OWNERS_COPY_TABLE));

			sqlConnection.executeManual(ManualStatement
					.inst("INSERT INTO " + sqlConnection.getPrefix() + PROTECTION_MEMBERS_COPY_TABLE.getName()
							+ " SELECT * FROM " + sqlConnection.getPrefix() + PROTECTION_MEMBERS_TABLE.getName()));
			sqlConnection.executeManual(ManualStatement
					.inst("INSERT INTO " + sqlConnection.getPrefix() + PROTECTION_OWNERS_COPY_TABLE.getName()
							+ " SELECT * FROM " + sqlConnection.getPrefix() + PROTECTION_OWNERS_TABLE.getName()));

			sqlConnection.executeDropTable(DropTableStatement.inst(PROTECTION_MEMBERS_TABLE));
			sqlConnection.executeDropTable(DropTableStatement.inst(PROTECTION_OWNERS_TABLE));

			sqlConnection.executeAlterTable(AlterTableRenameToStatement.inst(PROTECTION_MEMBERS_COPY_TABLE,
					PROTECTION_MEMBERS_TABLE.getName()));
			sqlConnection.executeAlterTable(
					AlterTableRenameToStatement.inst(PROTECTION_OWNERS_COPY_TABLE, PROTECTION_OWNERS_TABLE.getName()));
		});
	}

}

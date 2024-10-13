package company.pluginName.Modules.SQLPckg.Changelogs;

import java.sql.Types;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.CreateTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;

public class Changelog0000Init extends SQLChangelog {

	public static final Table PROTECTIONS_TABLE = new Table("Protections").addColumns(
			new Column("RegionId", Types.VARCHAR, "VARCHAR(256)").setPrimary(true).setNotNull(true),
			new Column("ParentRegionId", Types.VARCHAR, "VARCHAR(256)").setNotNull(false),
			new Column("OwnerUuid", Types.CHAR, "CHAR(36)").setNotNull(true),
			new Column("ProtectionBlockId", Types.VARCHAR, "VARCHAR(32)").setNotNull(true),
			new Column("DisplayItem", Types.BLOB).setNotNull(false),
			new Column("WorldName", Types.VARCHAR, "VARCHAR(256)").setNotNull(true),
			new Column("DisplayName", Types.VARCHAR, "VARCHAR(256)").setNotNull(true),
			new Column("CreatedDate", Types.BIGINT).setNotNull(false),
			new Column("LocationX", Types.INTEGER).setNotNull(false),
			new Column("LocationY", Types.INTEGER).setNotNull(false),
			new Column("LocationZ", Types.INTEGER).setNotNull(false),
			new Column("Blocked", Types.BOOLEAN).setNotNull(false),
			new Column("BlockReason", Types.VARCHAR, "VARCHAR(64)").setNotNull(false));

	public static final Table PROTECTION_BANNEDS_TABLE = new Table("ProtectionBanneds").addColumns(
			new Column("RegionId", Types.VARCHAR, "VARCHAR(256)").setNotNull(true),
			new Column("BannedUuid", Types.CHAR, "CHAR(36)").setNotNull(true));

	public static final Table PROTECTION_BLOCKS_TABLE = new Table("ProtectionBlocks").addColumns(
			new Column("Id", Types.VARCHAR, "VARCHAR(32)").setPrimary(true).setUnique(true).setNotNull(true),
			new Column("Item", Types.BLOB).setNotNull(true), new Column("BlocksX", Types.INTEGER).setNotNull(true),
			new Column("BlocksY", Types.INTEGER).setNotNull(true),
			new Column("BlocksZ", Types.INTEGER).setNotNull(true),
			new Column("Permission", Types.VARCHAR, "VARCHAR(64)"), new Column("Price", Types.DECIMAL, "DECIMAL(11,2)"),
			new Column("Recipe", Types.BLOB), new Column("RecipePermission", Types.VARCHAR, "VARCHAR(64)"));

	public static final Table RECIPES_TABLE = new Table("Recipes").addColumns(
			new Column("ProtectionBlockId", Types.VARCHAR, "VARCHAR(32)").setPrimary(true).setUnique(true)
					.setNotNull(true),
			new Column("Recipe", Types.BLOB).setNotNull(true), new Column("Permission", Types.VARCHAR, "VARCHAR(64)"));

	public static final Table PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE = new Table("ProtectionBlockAllowedWorlds")
			.addColumns(new Column("ProtectionBlockId", Types.VARCHAR, "VARCHAR(32)").setNotNull(true),
					new Column("WorldName", Types.VARCHAR, "VARCHAR(256)"));

	public static final Table AUTO_PURGE_LOGS_TABLE = new Table("AutoPurgeLogs").addColumns(
			new Column("ExecutionMillis", Types.BIGINT).setUnique(true).setNotNull(true),
			new Column("OlderThanMillis", Types.BIGINT).setNotNull(true),
			new Column("RemovedProtections", Types.INTEGER).setNotNull(true));

	static {
		PROTECTIONS_TABLE
				.addUniqueConstraint(new Table.UniqueConstraint().addColumns(PROTECTIONS_TABLE.getColumn("DisplayName"),
						PROTECTIONS_TABLE.getColumn("OwnerUuid")))
				.addForeignConstraint(new Table.ForeignConstraint().addColumn(PROTECTIONS_TABLE.getColumn("RegionId"))
						.addReferenceColumn(PROTECTIONS_TABLE.getColumn("ParentRegionId")));

		PROTECTION_BANNEDS_TABLE
				.addUniqueConstraint(
						new Table.UniqueConstraint().addColumns(PROTECTION_BANNEDS_TABLE.getColumn("RegionId"),
								PROTECTION_BANNEDS_TABLE.getColumn("BannedUuid")))
				.addForeignConstraint(new Table.ForeignConstraint().addColumn(PROTECTIONS_TABLE.getColumn("RegionId"))
						.addReferenceColumn(PROTECTION_BANNEDS_TABLE.getColumn("RegionId")));

		RECIPES_TABLE.addForeignConstraint(
				new Table.ForeignConstraint().addColumn(RECIPES_TABLE.getColumn("ProtectionBlockId"))
						.addReferenceColumn(PROTECTION_BLOCKS_TABLE.getColumn("Id")));

		PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE
				.addUniqueConstraint(new Table.UniqueConstraint().addColumns(
						PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE.getColumn("ProtectionBlockId"),
						PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE.getColumn("WorldName")))
				.addForeignConstraint(new Table.ForeignConstraint()
						.addColumn(PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE.getColumn("ProtectionBlockId"))
						.addReferenceColumn(PROTECTION_BLOCKS_TABLE.getColumn("Id")));
	}

	public Changelog0000Init() {
		super(Integer.MIN_VALUE, "Init tables", (sqlConnection) -> {
			sqlConnection.executeCreateTable(CreateTableStatement.inst(PROTECTIONS_TABLE));
			sqlConnection.executeCreateTable(CreateTableStatement.inst(PROTECTION_BANNEDS_TABLE));
			sqlConnection.executeCreateTable(CreateTableStatement.inst(PROTECTION_BLOCKS_TABLE));
			sqlConnection.executeCreateTable(CreateTableStatement.inst(RECIPES_TABLE));
			sqlConnection.executeCreateTable(CreateTableStatement.inst(PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE));
			sqlConnection.executeCreateTable(CreateTableStatement.inst(AUTO_PURGE_LOGS_TABLE));
		});
	}

}

package company.pluginName.Modules.SQLPckg.Changelogs;

import java.sql.Types;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.AlterTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.AlterTableStatement.AlterTableRenameColumnStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.UpdateStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;

public class Changelog0014IncreasePriceMaximumSize extends SQLChangelog {

	public static final Table PROTECTIONS_TABLE = new Table("Protections").addColumns(
			new Column("RegionId", Types.VARCHAR, "VARCHAR(256)"),
			new Column("OldPrice", Types.DECIMAL, "DECIMAL(11, 2)"),
			new Column("Price", Types.DECIMAL, "DECIMAL(24, 2)"));

	public static final Table PROTECTION_BLOCKS_TABLE = new Table("ProtectionBlocks").addColumns(
			new Column("Id", Types.VARCHAR, "VARCHAR(32)"), new Column("OldPrice", Types.DECIMAL, "DECIMAL(12, 2)"),
			new Column("Price", Types.DECIMAL, "DECIMAL(24, 2)"));

	public Changelog0014IncreasePriceMaximumSize() {
		super(14, "Increasing price maximum size to 22 naturals and 2 decimals", (sqlConnection) -> {
			sqlConnection.executeAlterTable(AlterTableRenameColumnStatement.inst(PROTECTIONS_TABLE, "Price",
					PROTECTIONS_TABLE.getColumn("OldPrice")));
			sqlConnection.executeAlterTable(AlterTableRenameColumnStatement.inst(PROTECTION_BLOCKS_TABLE, "Price",
					PROTECTION_BLOCKS_TABLE.getColumn("OldPrice")));

			sqlConnection.executeAlterTable(AlterTableStatement.AlterTableAddColumnStatement.inst(PROTECTIONS_TABLE,
					PROTECTIONS_TABLE.getColumn("Price")));
			sqlConnection.executeAlterTable(AlterTableStatement.AlterTableAddColumnStatement
					.inst(PROTECTION_BLOCKS_TABLE, PROTECTION_BLOCKS_TABLE.getColumn("Price")));

			sqlConnection.executeUpdate(UpdateStatement.inst(PROTECTIONS_TABLE)
					.addValue(PROTECTIONS_TABLE.getColumn("Price"), PROTECTIONS_TABLE.getColumn("OldPrice")));
			sqlConnection.executeUpdate(UpdateStatement.inst(PROTECTION_BLOCKS_TABLE).addValue(
					PROTECTION_BLOCKS_TABLE.getColumn("Price"), PROTECTION_BLOCKS_TABLE.getColumn("OldPrice")));

			sqlConnection.executeAlterTable(
					AlterTableStatement.AlterTableDropColumnStatement.inst(PROTECTIONS_TABLE, "OldPrice"));
			sqlConnection.executeAlterTable(
					AlterTableStatement.AlterTableDropColumnStatement.inst(PROTECTION_BLOCKS_TABLE, "OldPrice"));
		});
	}

}

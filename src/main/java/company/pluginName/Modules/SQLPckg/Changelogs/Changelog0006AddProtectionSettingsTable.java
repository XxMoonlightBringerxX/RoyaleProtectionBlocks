package company.pluginName.Modules.SQLPckg.Changelogs;

import java.sql.Types;
import java.util.Arrays;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.CreateTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table.UniqueConstraint;

public class Changelog0006AddProtectionSettingsTable extends SQLChangelog {

	private static final Table PROTECTION_SETTINGS_TABLE = new Table("ProtectionSettings").addColumns(
			new Column("RegionId", Types.VARCHAR, "VARCHAR(256)").setNotNull(true),
			new Column("SettingId", Types.VARCHAR, "VARCHAR(32)").setNotNull(true),
			new Column("SettingGroup", Types.VARCHAR, "VARCHAR(32)").setNotNull(true),
			new Column("SettingValue", Types.BLOB).setNotNull(true));

	static {
		PROTECTION_SETTINGS_TABLE
				.addUniqueConstraint(new UniqueConstraint(Arrays.asList(PROTECTION_SETTINGS_TABLE.getColumn("RegionId"),
						PROTECTION_SETTINGS_TABLE.getColumn("SettingId"),
						PROTECTION_SETTINGS_TABLE.getColumn("SettingGroup"))))
				.addForeignConstraint(
						new Table.ForeignConstraint().addColumn(PROTECTION_SETTINGS_TABLE.getColumn("RegionId"))
								.addReferenceColumn(Changelog0000Init.PROTECTIONS_TABLE.getColumn("RegionId")));
	}

	public Changelog0006AddProtectionSettingsTable() {
		super(6, "Add protection settings tables", (sqlConnection) -> {
			sqlConnection.executeCreateTable(CreateTableStatement.inst(PROTECTION_SETTINGS_TABLE));
		});
	}

}

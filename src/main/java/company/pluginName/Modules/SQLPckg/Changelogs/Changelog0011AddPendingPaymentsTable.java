package company.pluginName.Modules.SQLPckg.Changelogs;

import java.sql.Types;
import java.util.Arrays;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.CreateTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table.UniqueConstraint;

public class Changelog0011AddPendingPaymentsTable extends SQLChangelog {

	private static final Table PENDING_PAYMENTS_TABLE = new Table("PendingPayments").addColumns(
			new Column("PlayerUuid", Types.CHAR, "CHAR(36)").setNotNull(true),
			new Column("EconomyService", Types.VARCHAR, "VARCHAR(36)").setNotNull(true),
			new Column("Amount", Types.DECIMAL, "DECIMAL(12, 2)").setNotNull(true));

	static {
		PENDING_PAYMENTS_TABLE
				.addUniqueConstraint(new UniqueConstraint(Arrays.asList(PENDING_PAYMENTS_TABLE.getColumn("PlayerUuid"),
						PENDING_PAYMENTS_TABLE.getColumn("EconomyService"))));
	}

	public Changelog0011AddPendingPaymentsTable() {
		super(11, "Adding pending payments table", (sqlConnection) -> {
			sqlConnection.executeCreateTable(CreateTableStatement.inst(PENDING_PAYMENTS_TABLE));
		});
	}

}

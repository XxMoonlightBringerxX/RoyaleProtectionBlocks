package company.pluginName.Modules.SQLPckg;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.Protection.Delete.ProtectionDeleteSQLException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveSQLException;
import company.pluginName.Exceptions.ProtectionBlocks.Delete.ProtectionBlocksDeleteSQLException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveSQLException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.ReferencedObjects.ReferencedProtectionBlock;
import darkpanda73.LibsCollection.PandaSQL.Enums.ConditionType;
import darkpanda73.LibsCollection.PandaSQL.Objects.Data;
import darkpanda73.LibsCollection.PandaSQL.Objects.Conditions.Condition;
import darkpanda73.LibsCollection.PandaSQL.Objects.DataModel.Column;
import darkpanda73.LibsCollection.PandaSQL.Objects.DataModel.Table;
import relampagorojo93.LibsCollection.SpigotPlugin.MainClass;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

public class SQLModule extends darkpanda73.LibsCollection.PandaSQL.Spigot.SQLModule {

	@Override
	protected void generateTables() {
		Table t;
		getDatabase()
				.addTable((t = new Table(getDatabase(), "Protections")).addColumns(
						new Column(t, "RegionId", "VARCHAR(256)", Types.VARCHAR).setPrimary(true).setNotNull(true),
						new Column(t, "OwnerUuid", "CHAR(36)", Types.CHAR).setNotNull(true),
						new Column(t, "ProtectionBlockId", "VARCHAR(32)", Types.VARCHAR).setNotNull(true),
						new Column(t, "WorldName", "VARCHAR(256)", Types.VARCHAR).setNotNull(true),
						new Column(t, "DisplayName", "VARCHAR(256)", Types.VARCHAR).setNotNull(true)))
				.addTable((t = new Table(getDatabase(), "ProtectionBlocks")).addColumns(
						new Column(t, "Id", "VARCHAR(32)", Types.VARCHAR).setPrimary(true).setUnique(true)
								.setNotNull(true),
						new Column(t, "Item", "BLOB", Types.BLOB).setNotNull(true),
						new Column(t, "BlocksX", "INTEGER", Types.INTEGER).setNotNull(true),
						new Column(t, "BlocksY", "INTEGER", Types.INTEGER).setNotNull(true),
						new Column(t, "BlocksZ", "INTEGER", Types.INTEGER).setNotNull(true),
						new Column(t, "Permission", "VARCHAR(64)", Types.VARCHAR)));
	}

	public List<Protection> getProtections() {
		List<Protection> protections = new ArrayList<>();
		try (ResultSet set = this.getDatabase().select(Arrays.asList(this.getDatabase().getTable("Protections")))) {
			while (set.next()) {
				protections.add(new Protection(set.getString("RegionId"), UUID.fromString(set.getString("ownerUuid")),
						new ReferencedProtectionBlock(set.getString("ProtectionBlockId")), set.getString("WorldName"),
						set.getString("DisplayName")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return protections;
	}

	public List<ProtectionBlock> getProtectionBlocks() {
		List<ProtectionBlock> protectionBlocks = new ArrayList<>();
		try (ResultSet set = this.getDatabase()
				.select(Arrays.asList(this.getDatabase().getTable("ProtectionBlocks")))) {
			while (set.next()) {
				protectionBlocks.add(new ProtectionBlock(set.getString("Id"),
						ItemStacksUtils.itemsParse(set.getBytes("Item"))[0], set.getInt("BlocksX"),
						set.getInt("BlocksY"), set.getInt("BlocksZ"), set.getString("Permission")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return protectionBlocks;
	}

	public void saveProtection(Protection protection) throws ProtectionSaveSQLException {
		Table t = this.getDatabase().getTable("Protections");
		HashMap<Column, Data> values = new HashMap<>();
		values.put(t.getColumn("RegionId"), new Data(Types.VARCHAR, protection.getRegionId()));
		values.put(t.getColumn("OwnerUuid"), new Data(Types.CHAR, protection.getOwnerUuid().toString()));
		values.put(t.getColumn("ProtectionBlockId"),
				new Data(Types.VARCHAR, protection.getProtectionBlock().getIdentifier()));
		values.put(t.getColumn("WorldName"), new Data(Types.VARCHAR, protection.getWorldName()));
		values.put(t.getColumn("DisplayName"), new Data(Types.VARCHAR, protection.getDisplayName()));
		if (!this.getDatabase().insertOrUpdate(t, values, new Condition(t.getColumn("RegionId"),
				new Data(Types.VARCHAR, protection.getRegionId()), ConditionType.EQUAL))) {
			throw new ProtectionSaveSQLException();
		}
	}

	public void deleteProtection(Protection protection) throws ProtectionDeleteSQLException {
		Table t = this.getDatabase().getTable("Protections");
		if (!this.getDatabase().delete(t, new Condition(new Condition(t.getColumn("RegionId"),
				new Data(Types.VARCHAR, protection.getRegionId()), ConditionType.EQUAL)))) {
			throw new ProtectionDeleteSQLException();
		}
	}

	public void saveProtectionBlock(ProtectionBlock protectionBlock) throws ProtectionBlocksSaveSQLException {
		Table t = this.getDatabase().getTable("ProtectionBlocks");
		HashMap<Column, Data> values = new HashMap<>();
		values.put(t.getColumn("Id"), new Data(Types.VARCHAR, protectionBlock.getId()));
		values.put(t.getColumn("Item"),
				new Data(Types.BLOB, ItemStacksUtils.itemsParse(new ItemStack[] { protectionBlock.getItem() })));
		values.put(t.getColumn("BlocksX"), new Data(Types.INTEGER, protectionBlock.getBlocksX()));
		values.put(t.getColumn("BlocksY"), new Data(Types.INTEGER, protectionBlock.getBlocksY()));
		values.put(t.getColumn("BlocksZ"), new Data(Types.INTEGER, protectionBlock.getBlocksZ()));
		values.put(t.getColumn("Permission"), new Data(
				protectionBlock.getPermission() != null ? Types.INTEGER : Types.NULL, protectionBlock.getPermission()));
		if (!this.getDatabase().insertOrUpdate(t, values,
				new Condition(t.getColumn("Id"), new Data(Types.CHAR, protectionBlock.getId()), ConditionType.EQUAL))) {
			throw new ProtectionBlocksSaveSQLException();
		}
	}

	public void deleteProtectionBlock(ProtectionBlock protectionBlock) throws ProtectionBlocksDeleteSQLException {
		Table t = this.getDatabase().getTable("ProtectionBlocks");
		if (!this.getDatabase().delete(t, new Condition(new Condition(t.getColumn("Id"),
				new Data(Types.VARCHAR, protectionBlock.getId()), ConditionType.EQUAL)))) {
			throw new ProtectionBlocksDeleteSQLException();
		}
	}

	@Override
	protected MainClass getPlugin() {
		return MainPluginClass.getPlugin();
	}

	@Override
	protected boolean isMySQLEnabled() {
		return false;
	}

	@Override
	protected String getMySQLProtocol() {
		return null;
	}

	@Override
	protected String getMySQLHost() {
		return null;
	}

	@Override
	protected int getMySQLPort() {
		return 0;
	}

	@Override
	protected String getMySQLDatabase() {
		return null;
	}

	@Override
	protected String getMySQLUsername() {
		return null;
	}

	@Override
	protected String getMySQLPassword() {
		return null;
	}

	@Override
	protected String[] getMySQLParameters() {
		return null;
	}

	@Override
	protected String getSQLiteFile() {
		return String.valueOf(MainPluginClass.getPlugin().getFileModule().PLUGIN_FOLDER.getFolder().getPath())
				+ "/DB.sqlite";
	}

	@Override
	protected int getVersion() {
		return 1;
	}

	@Override
	protected String getTablePrefix() {
		return "";
	}

}
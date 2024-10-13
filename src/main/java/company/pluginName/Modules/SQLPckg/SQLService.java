package company.pluginName.Modules.SQLPckg;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Components.ProtectionBlockAllowedWorlds;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Components.ProtectionBlockInformation;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Reference.ReferencedProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.RecipesPckg.Objects.Recipe;
import company.pluginName.Modules.SQLPckg.Changelogs.Changelog0000Init;
import company.pluginName.Modules.SQLPckg.Changelogs.Changelog0001DeleteAutoPurgeLogsTable;
import darkpanda73.PandaUtils.PandaSQLModule.v2.Annotations.PandaSQLConfigV2;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.DeleteStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.InsertStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.SelectStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions.AndCondition;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions.EqualsCondition;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;
import darkpanda73.PandaUtils.PandaSQLModule.v2.Services.PandaSQLService;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.BlockReason;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation;

@PandaSQLConfigV2(allowMySQL = true)
public class SQLService extends PandaSQLService {

	private static final Table PROTECTIONS_TABLE = new Table("Protections").addColumns(
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

	private static final Table PROTECTION_BANNEDS_TABLE = new Table("ProtectionBanneds").addColumns(
			new Column("RegionId", Types.VARCHAR, "VARCHAR(256)").setNotNull(true),
			new Column("BannedUuid", Types.CHAR, "CHAR(36)").setNotNull(true));

	private static final Table PROTECTION_BLOCKS_TABLE = new Table("ProtectionBlocks").addColumns(
			new Column("Id", Types.VARCHAR, "VARCHAR(32)").setPrimary(true).setUnique(true).setNotNull(true),
			new Column("Item", Types.BLOB).setNotNull(true), new Column("BlocksX", Types.INTEGER).setNotNull(true),
			new Column("BlocksY", Types.INTEGER).setNotNull(true),
			new Column("BlocksZ", Types.INTEGER).setNotNull(true),
			new Column("Permission", Types.VARCHAR, "VARCHAR(64)"), new Column("Price", Types.DECIMAL, "DECIMAL(11,2)"),
			new Column("Recipe", Types.BLOB), new Column("RecipePermission", Types.VARCHAR, "VARCHAR(64)"));

	private static final Table RECIPES_TABLE = new Table("Recipes").addColumns(
			new Column("ProtectionBlockId", Types.VARCHAR, "VARCHAR(32)").setPrimary(true).setUnique(true)
					.setNotNull(true),
			new Column("Recipe", Types.BLOB).setNotNull(true), new Column("Permission", Types.VARCHAR, "VARCHAR(64)"));

	private static final Table PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE = new Table("ProtectionBlockAllowedWorlds")
			.addColumns(new Column("ProtectionBlockId", Types.VARCHAR, "VARCHAR(32)").setNotNull(true),
					new Column("WorldName", Types.VARCHAR, "VARCHAR(256)"));

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

	@Override
	public Collection<SQLChangelog> getSQLChangelogs() {
		return Arrays.asList(new Changelog0000Init(), new Changelog0001DeleteAutoPurgeLogsTable());
	}

	/*
	 * Protection methods
	 */

	public List<Protection> getProtections() {
		List<Protection> protections = new ArrayList<>();
		HashMap<String, List<Protection>> parentProtections = new HashMap<>();
		try (ResultSet set = this.getSqlConnection().executeQuery(SelectStatement.inst().addTable(PROTECTIONS_TABLE))) {
			while (set.next()) {
				Protection protection;

				long createdDate = set.getLong("CreatedDate");

				protection = new Protection(set.getString("RegionId"), UUID.fromString(set.getString("ownerUuid")),
						new ReferencedProtectionBlock(set.getString("ProtectionBlockId")), set.getString("WorldName"),
						set.getString("DisplayName"), createdDate > 0 ? createdDate : System.currentTimeMillis());

				if (set.getString("ParentRegionId") != null) {
					parentProtections.computeIfAbsent(set.getString("ParentRegionId"), (key) -> new ArrayList<>())
							.add(protection);
				}

				if (set.getString("DisplayItem") != null) {
					try {
						protection.getDisplayItem().set(ItemStacksUtils.itemsParse(set.getBytes("DisplayItem"),
								new ItemStack[] { new ItemStack(Material.STONE) })[0]);
					} catch (IllegalArgumentException e) {
					}
				}

				if (set.getObject("LocationX") != null && set.getObject("LocationY") != null
						&& set.getObject("LocationZ") != null) {
					protection.setLocation(new SimpleLocation(set.getString("WorldName"), set.getInt("LocationX"),
							set.getInt("LocationY"), set.getInt("LocationZ")));
				}

				if (set.getObject("Blocked") != null && set.getObject("BlockReason") != null) {
					try {
						protection.setBlocked(set.getBoolean("Blocked"));
						protection.setBlockReason(BlockReason.valueOf(set.getString("BlockReason")));
					} catch (IllegalArgumentException e) {
						protection.setBlocked(false);
					}
				}

				protections.add(protection);

				if (createdDate == 0) {
					saveProtection(protection);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		protections.stream().filter(parentProtection -> parentProtections.containsKey(parentProtection.getRegionId()))
				.forEach(parentProtection -> parentProtections.get(parentProtection.getRegionId())
						.forEach(protection -> protection.setParentProtectionInstance(parentProtection)));

		return protections;
	}

	public void saveProtection(Protection protection) throws RoyaleProtectionBlocksExceptionImpl {
		InsertStatement insertStatement = InsertStatement
				.inst(PROTECTIONS_TABLE, "RegionId", "ParentRegionId", "OwnerUuid", "ProtectionBlockId", "DisplayItem",
						"WorldName", "DisplayName", "CreatedDate", "LocationX", "LocationY", "LocationZ", "Blocked",
						"BlockReason")
				.addEntry(protection.getRegionId(),
						(protection.getParentProtection() != protection ? protection.getParentProtection().getRegionId()
								: null),
						protection.getOwnerUuid().toString(), protection.getProtectionBlockIdentifier(),
						(protection.getDisplayItem() != null
								? ItemStacksUtils.itemsParse(new ItemStack[] { protection.getDisplayItem().get() })
								: null),
						protection.getWorldName(), protection.getDisplayName(), protection.getCreatedDate(),
						protection.getLocation().getX(), protection.getLocation().getY(),
						protection.getLocation().getZ(), protection.isBlocked(),
						(protection.getBlockReason() != null ? protection.getBlockReason().name() : null))
				.setConditionIfExists(
						EqualsCondition.inst(PROTECTIONS_TABLE.getColumn("RegionId"), protection.getRegionId()));

		try {
			this.getSqlConnection().executeInsert(insertStatement);
		} catch (Throwable e) {
			throw Exceptions.Protections.Save.SQL.generateException(e);
		}
	}

	public void deleteProtection(Protection protection) throws RoyaleProtectionBlocksExceptionImpl {
		try {
			this.getSqlConnection().executeDelete(DeleteStatement.inst(PROTECTIONS_TABLE).setCondition(
					EqualsCondition.inst(PROTECTIONS_TABLE.getColumn("RegionId"), protection.getRegionId())));
		} catch (Throwable e) {
			throw Exceptions.Protections.Delete.SQL.generateException(e);
		}
	}

	/*
	 * Protection banneds methods
	 */

	public HashMap<String, List<UUID>> getProtectionBanneds() {
		HashMap<String, List<UUID>> protectionBanneds = new HashMap<>();

		try (ResultSet set = this.getSqlConnection()
				.executeQuery(SelectStatement.inst().addTable(PROTECTION_BANNEDS_TABLE))) {
			while (set.next()) {
				protectionBanneds.computeIfAbsent(set.getString("RegionId"), (key) -> new ArrayList<>())
						.add(UUID.fromString(set.getString("BannedUuid")));
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return protectionBanneds;
	}

	public void saveProtectionBanned(Protection protection, UUID bannedUuid)
			throws RoyaleProtectionBlocksExceptionImpl {
		this.saveProtectionBanneds(protection, Arrays.asList(bannedUuid));
	}

	public void saveProtectionBanneds(Protection protection, List<UUID> bannedUuids)
			throws RoyaleProtectionBlocksExceptionImpl {
		InsertStatement insert = InsertStatement.inst(PROTECTION_BANNEDS_TABLE);
		bannedUuids.forEach(bannedUuid -> insert.addEntry(protection.getRegionId(), bannedUuid.toString()));

		try {
			this.getSqlConnection().executeInsert(insert);
		} catch (Throwable e) {
			throw Exceptions.Protections.Banneds.Save.SQL.generateException(e);
		}
	}

	public void deleteProtectionBanned(Protection protection, UUID bannedUuid)
			throws RoyaleProtectionBlocksExceptionImpl {
		try {
			this.getSqlConnection()
					.executeDelete(DeleteStatement.inst(PROTECTION_BANNEDS_TABLE)
							.setCondition(AndCondition.inst(
									EqualsCondition.inst(PROTECTION_BANNEDS_TABLE.getColumn("RegionId"),
											protection.getRegionId()),
									EqualsCondition.inst(PROTECTION_BANNEDS_TABLE.getColumn("BannedUuid"),
											bannedUuid.toString()))));
		} catch (Throwable e) {
			throw Exceptions.Protections.Banneds.Delete.SQL.generateException(e);
		}
	}

	public void deleteProtectionBanneds(Protection protection) throws RoyaleProtectionBlocksExceptionImpl {
		try {
			this.getSqlConnection().executeDelete(DeleteStatement.inst(PROTECTION_BANNEDS_TABLE).setCondition(
					EqualsCondition.inst(PROTECTION_BANNEDS_TABLE.getColumn("RegionId"), protection.getRegionId())));
		} catch (Throwable e) {
			throw Exceptions.Protections.Banneds.Delete.SQL.generateException(e);
		}
	}

	/*
	 * Protection block methods
	 */

	public List<ProtectionBlock> getProtectionBlocks() {
		List<ProtectionBlock> protectionBlocks = new ArrayList<>();
		try (ResultSet set = this.getSqlConnection()
				.executeQuery(SelectStatement.inst().addTable(PROTECTION_BLOCKS_TABLE))) {
			while (set.next()) {
				protectionBlocks.add(new ProtectionBlock(
						new ProtectionBlockInformation(set.getString("Id"),
								ItemStacksUtils.itemsParse(set.getBytes("Item"),
										new ItemStack[] { new ItemStack(Material.STONE) })[0],
								set.getInt("BlocksX"), set.getInt("BlocksY"), set.getInt("BlocksZ"),
								set.getString("Permission"),
								(set.getObject("Price") != null ? set.getDouble("Price") : null)),
						getProtectionBlockAllowedWorlds(set.getString("Id"))));
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return protectionBlocks;
	}

	private ProtectionBlockAllowedWorlds getProtectionBlockAllowedWorlds(String protectionBlockId) {
		HashSet<String> allowedWorlds = new HashSet<>();
		try (ResultSet set = this.getSqlConnection()
				.executeQuery(SelectStatement.inst().addTable(PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE)
						.setCondition(EqualsCondition.inst(
								PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE.getColumn("ProtectionBlockId"),
								protectionBlockId)))) {
			while (set.next()) {
				allowedWorlds.add(set.getString("WorldName"));
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return new ProtectionBlockAllowedWorlds(allowedWorlds);
	}

	public void saveProtectionBlock(ProtectionBlock protectionBlock) throws RoyaleProtectionBlocksExceptionImpl {
		InsertStatement insertStatement = InsertStatement
				.inst(PROTECTION_BLOCKS_TABLE, "Id", "Item", "BlocksX", "BlocksY", "BlocksZ", "Permission", "Price")
				.addEntry(protectionBlock.getInformation().getId(),
						ItemStacksUtils.itemsParse(new ItemStack[] { protectionBlock.getInformation().getItem() }),
						protectionBlock.getInformation().getBlocksX(), protectionBlock.getInformation().getBlocksY(),
						protectionBlock.getInformation().getBlocksZ(), protectionBlock.getInformation().getPermission(),
						protectionBlock.getInformation().getPrice())
				.setConditionIfExists(EqualsCondition.inst(PROTECTION_BLOCKS_TABLE.getColumn("Id"),
						protectionBlock.getInformation().getId()));

		try {
			this.getSqlConnection().executeInsert(insertStatement);
			this.getSqlConnection()
					.executeDelete(DeleteStatement.inst(PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE)
							.setCondition(EqualsCondition.inst(
									PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE.getColumn("ProtectionBlockId"),
									protectionBlock.getInformation().getId())));
		} catch (Throwable e) {
			throw Exceptions.Protections.Blocks.Save.SQL.generateException(e);
		}

		if (!protectionBlock.getAllowedWorlds().get().isEmpty()) {
			InsertStatement worldsInsertStatement = InsertStatement.inst(PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE);
			protectionBlock.getAllowedWorlds().get().forEach(allowedWorld -> worldsInsertStatement
					.addEntry(protectionBlock.getInformation().getId(), allowedWorld));

			try {
				this.getSqlConnection().executeInsert(worldsInsertStatement);
			} catch (Throwable e) {
				throw Exceptions.Protections.Blocks.Save.SQL.generateException(e);
			}
		}
	}

	public void deleteProtectionBlock(ProtectionBlock protectionBlock) throws RoyaleProtectionBlocksExceptionImpl {
		try {
			this.getSqlConnection()
					.executeDelete(DeleteStatement.inst(PROTECTION_BLOCKS_TABLE).setCondition(EqualsCondition
							.inst(PROTECTION_BLOCKS_TABLE.getColumn("Id"), protectionBlock.getInformation().getId())));
			this.getSqlConnection()
					.executeDelete(DeleteStatement.inst(PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE)
							.setCondition(EqualsCondition.inst(
									PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE.getColumn("ProtectionBlockId"),
									protectionBlock.getInformation().getId())));
			this.getSqlConnection().executeDelete(DeleteStatement.inst(RECIPES_TABLE).setCondition(EqualsCondition
					.inst(RECIPES_TABLE.getColumn("ProtectionBlockId"), protectionBlock.getInformation().getId())));
		} catch (Throwable e) {
			throw Exceptions.Protections.Blocks.Delete.SQL.generateException(e);
		}
	}

	/*
	 * Recipe methods
	 */

	public List<Recipe> getRecipes() {
		List<Recipe> recipes = new ArrayList<>();
		try (ResultSet set = this.getSqlConnection().executeQuery(SelectStatement.inst().addTable(RECIPES_TABLE))) {
			while (set.next()) {
				Recipe recipe = new Recipe(new ReferencedProtectionBlock(set.getString("ProtectionBlockId")));

				ItemStack[] recipeItems = ItemStacksUtils.itemsParse(set.getBytes("Recipe"));
				for (int i = 0; i < recipeItems.length; i++) {
					recipe.getRecipe()[i] = recipeItems[i];
				}

				recipe.setPermission(set.getString("Permission"));

				recipes.add(recipe);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return recipes;
	}

	public void saveRecipe(Recipe recipe) throws RoyaleProtectionBlocksExceptionImpl {
		InsertStatement insertStatement = InsertStatement.inst(RECIPES_TABLE)
				.addEntry(recipe.getProtectionBlock().getObject().getInformation().getId(),
						ItemStacksUtils.itemsParse(recipe.getRecipe()), recipe.getPermission())
				.setConditionIfExists(EqualsCondition.inst(RECIPES_TABLE.getColumn("ProtectionBlockId"),
						recipe.getProtectionBlock().getObject().getInformation().getId()));

		try {
			this.getSqlConnection().executeInsert(insertStatement);
		} catch (Throwable e) {
			throw Exceptions.Protections.Blocks.Save.SQL.generateException(e);
		}
	}

	public void deleteRecipe(Recipe recipe) throws RoyaleProtectionBlocksExceptionImpl {
		try {
			this.getSqlConnection()
					.executeDelete(DeleteStatement.inst(RECIPES_TABLE)
							.setCondition(EqualsCondition.inst(RECIPES_TABLE.getColumn("ProtectionBlockId"),
									recipe.getProtectionBlock().getObject().getInformation().getId())));
		} catch (Throwable e) {
			throw Exceptions.Protections.Blocks.Delete.SQL.generateException(e);
		}
	}

}
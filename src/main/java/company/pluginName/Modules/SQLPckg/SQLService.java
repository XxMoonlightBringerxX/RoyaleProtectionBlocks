package company.pluginName.Modules.SQLPckg;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Components.ProtectionBlockAllowedWorlds;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Components.ProtectionBlockInformation;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Reference.ReferencedProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.RecipesPckg.Objects.Recipe;
import darkpanda73.PandaUtils.PandaSQLModule.PandaSQLService;
import darkpanda73.PandaUtils.PandaSQLModule.Annotations.PandaSQLConfig;
import darkpanda73.PandaUtils.PandaSQLModule.Objects.Enums.ConditionType;
import darkpanda73.PandaUtils.PandaSQLModule.Objects.Objects.Data;
import darkpanda73.PandaUtils.PandaSQLModule.Objects.Objects.Conditions.Condition;
import darkpanda73.PandaUtils.PandaSQLModule.Objects.Objects.DataModel.Column;
import darkpanda73.PandaUtils.PandaSQLModule.Objects.Objects.DataModel.Table;
import darkpanda73.PandaUtils.PandaSQLModule.Objects.Objects.DataModel.Constraints.ForeignConstraint;
import darkpanda73.PandaUtils.PandaSQLModule.Objects.Objects.DataModel.Constraints.UniqueConstraint;
import darkpanda73.PandaUtils.PandaUtilities.Location.LocationReference;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

@PandaSQLConfig(allowMySQL = true, version = 7)
public class SQLService extends PandaSQLService {

	@Override
	protected void generateTables() {
		Table t;
		getDatabase()
				.addTable((t = new Table(getDatabase(), "Protections"))
						.addColumns(
								new Column(t, "RegionId", "VARCHAR(256)", Types.VARCHAR).setPrimary(true).setNotNull(
										true),
								new Column(t, "CustomRegionId", "VARCHAR(256)", Types.VARCHAR),
								new Column(t, "OwnerUuid", "CHAR(36)", Types.CHAR).setNotNull(true),
								new Column(t, "ProtectionBlockId", "VARCHAR(32)", Types.VARCHAR).setNotNull(true),
								new Column(t, "DisplayItem", "BLOB", Types.BLOB).setNotNull(false),
								new Column(t, "WorldName", "VARCHAR(256)", Types.VARCHAR).setNotNull(true),
								new Column(t, "DisplayName", "VARCHAR(256)", Types.VARCHAR).setNotNull(true),
								new Column(t, "CreatedDate", "BIGINT", Types.BIGINT).setNotNull(false),
								new Column(t, "LocationX", "INTEGER", Types.INTEGER).setNotNull(false),
								new Column(t, "LocationY", "INTEGER", Types.INTEGER).setNotNull(false),
								new Column(t, "LocationZ", "INTEGER", Types.INTEGER).setNotNull(false))
						.addUniqueConstraint(
								new UniqueConstraint(t.getColumn("CustomRegionId"), t.getColumn("OwnerUuid"))))
				.addTable(
						(t = new Table(getDatabase(), "ProtectionBlocks")).addColumns(
								new Column(t, "Id", "VARCHAR(32)", Types.VARCHAR).setPrimary(true).setUnique(true)
										.setNotNull(true),
								new Column(t, "Item", "BLOB", Types.BLOB).setNotNull(true),
								new Column(t, "BlocksX", "INTEGER", Types.INTEGER).setNotNull(true),
								new Column(t, "BlocksY", "INTEGER", Types.INTEGER).setNotNull(true),
								new Column(t, "BlocksZ", "INTEGER", Types.INTEGER).setNotNull(true),
								new Column(t, "Permission", "VARCHAR(64)", Types.VARCHAR),
								new Column(t, "Price", "DECIMAL(11,2)", Types.DECIMAL),
								new Column(t, "Recipe", "BLOB", Types.BLOB), new Column(t, "RecipePermission",
										"VARCHAR(64)", Types.VARCHAR)))
				.addTable(
						(t = new Table(getDatabase(), "Recipes"))
								.addColumns(
										new Column(t, "ProtectionBlockId", "VARCHAR(32)", Types.VARCHAR)
												.setPrimary(true).setUnique(true).setNotNull(true),
										new Column(t, "Recipe", "BLOB", Types.BLOB).setNotNull(true),
										new Column(t, "Permission", "VARCHAR(64)", Types.VARCHAR))
								.addForeignConstraint(
										new ForeignConstraint(Arrays.asList(t.getColumn("ProtectionBlockId")),
												Arrays.asList(
														getDatabase().getTable("ProtectionBlocks").getColumn("Id")))))
				.addTable((t = new Table(getDatabase(), "ProtectionBlockAllowedWorlds"))
						.addColumns(new Column(t, "ProtectionBlockId", "VARCHAR(32)", Types.VARCHAR).setNotNull(true),
								new Column(t, "WorldName", "VARCHAR(256)", Types.VARCHAR))
						.addUniqueConstraint(
								new UniqueConstraint(t.getColumn("ProtectionBlockId"), t.getColumn("WorldName")))
						.addForeignConstraint(new ForeignConstraint(Arrays.asList(t.getColumn("ProtectionBlockId")),
								Arrays.asList(getDatabase().getTable("ProtectionBlocks").getColumn("Id")))));
	}

	public List<Protection> getProtections() {
		List<Protection> protections = new ArrayList<>();
		try (ResultSet set = this.getDatabase().select(Arrays.asList(this.getDatabase().getTable("Protections")))) {
			while (set.next()) {
				Protection protection;

				long createdDate = set.getLong("CreatedDate");

				protection = new Protection(set.getString("RegionId"), UUID.fromString(set.getString("ownerUuid")),
						new ReferencedProtectionBlock(set.getString("ProtectionBlockId")), set.getString("WorldName"),
						set.getString("DisplayName"), createdDate > 0 ? createdDate : System.currentTimeMillis());

				if (set.getString("DisplayItem") != null) {
					try {
						protection.getDisplayItem().set(ItemStacksUtils.itemsParse(set.getBytes("DisplayItem"))[0]);
					} catch (IllegalArgumentException e) {
					}
				}

				if (set.getObject("LocationX") != null && set.getObject("LocationY") != null
						&& set.getObject("LocationZ") != null) {
					protection.setLocation(new LocationReference(set.getString("WorldName"), set.getInt("LocationX"),
							set.getInt("LocationY"), set.getInt("LocationZ")));
				}

				protections.add(protection);

				if (createdDate == 0) {
					saveProtection(protection);
				}
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
				protectionBlocks.add(new ProtectionBlock(
						new ProtectionBlockInformation(set.getString("Id"),
								ItemStacksUtils.itemsParse(set.getBytes("Item"))[0], set.getInt("BlocksX"),
								set.getInt("BlocksY"), set.getInt("BlocksZ"), set.getString("Permission"),
								(set.getObject("Price") != null ? set.getDouble("Price") : null)),
						getProtectionBlockAllowedWorlds(set.getString("Id"))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return protectionBlocks;
	}

	private ProtectionBlockAllowedWorlds getProtectionBlockAllowedWorlds(String protectionBlockId) {
		HashSet<String> allowedWorlds = new HashSet<>();
		Table t = this.getDatabase().getTable("ProtectionBlockAllowedWorlds");
		try (ResultSet set = this.getDatabase().select(Arrays.asList(t),
				new Condition(new Condition(t.getColumn("ProtectionBlockId"),
						new Data(Types.VARCHAR, protectionBlockId), ConditionType.EQUAL)))) {
			while (set.next()) {
				allowedWorlds.add(set.getString("WorldName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ProtectionBlockAllowedWorlds(allowedWorlds);
	}

	public List<Recipe> getRecipes() {
		List<Recipe> recipes = new ArrayList<>();
		try (ResultSet set = this.getDatabase().select(Arrays.asList(this.getDatabase().getTable("Recipes")))) {
			while (set.next()) {
				Recipe recipe = new Recipe(new ReferencedProtectionBlock(set.getString("ProtectionBlockId")));

				ItemStack[] recipeItems = ItemStacksUtils.itemsParse(set.getBytes("Recipe"));
				for (int i = 0; i < recipeItems.length; i++) {
					recipe.getRecipe()[i] = recipeItems[i];
				}

				recipe.setPermission(set.getString("Permission"));

				recipes.add(recipe);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recipes;
	}

	public void saveProtection(Protection protection) throws RoyaleProtectionBlocksException {
		Table t = this.getDatabase().getTable("Protections");
		HashMap<Column, Data> values = new HashMap<>();
		values.put(t.getColumn("RegionId"), new Data(Types.VARCHAR, protection.getRegionId()));
		values.put(t.getColumn("OwnerUuid"), new Data(Types.CHAR, protection.getOwnerUuid().toString()));
		values.put(t.getColumn("ProtectionBlockId"),
				new Data(Types.VARCHAR, protection.getProtectionBlock().getIdentifier()));
		values.put(t.getColumn("DisplayItem"),
				protection.getDisplayItem() != null
						? new Data(Types.BLOB,
								ItemStacksUtils.itemsParse(new ItemStack[] { protection.getDisplayItem().get() }))
						: new Data(Types.NULL, null));
		values.put(t.getColumn("WorldName"), new Data(Types.VARCHAR, protection.getWorldName()));
		values.put(t.getColumn("DisplayName"), new Data(Types.VARCHAR, protection.getDisplayName()));
		values.put(t.getColumn("CreatedDate"), new Data(Types.BIGINT, protection.getCreatedDate()));
		values.put(t.getColumn("LocationX"), new Data(Types.INTEGER, protection.getLocation().getBlockX()));
		values.put(t.getColumn("LocationY"), new Data(Types.INTEGER, protection.getLocation().getBlockY()));
		values.put(t.getColumn("LocationZ"), new Data(Types.INTEGER, protection.getLocation().getBlockZ()));
		if (!this.getDatabase().insertOrUpdate(t, values, new Condition(t.getColumn("RegionId"),
				new Data(Types.VARCHAR, protection.getRegionId()), ConditionType.EQUAL))) {
			throw Exceptions.Protections.Save.SQL.generateException();
		}
	}

	public void deleteProtection(Protection protection) throws RoyaleProtectionBlocksException {
		Table t = this.getDatabase().getTable("Protections");
		if (!this.getDatabase().delete(t, new Condition(new Condition(t.getColumn("RegionId"),
				new Data(Types.VARCHAR, protection.getRegionId()), ConditionType.EQUAL)))) {
			throw Exceptions.Protections.Delete.SQL.generateException();
		}
	}

	public void saveProtectionBlock(ProtectionBlock protectionBlock) throws RoyaleProtectionBlocksException {
		Table t = this.getDatabase().getTable("ProtectionBlocks");
		HashMap<Column, Data> values = new HashMap<>();
		values.put(t.getColumn("Id"), new Data(Types.VARCHAR, protectionBlock.getInformation().getId()));
		values.put(t.getColumn("Item"), new Data(Types.BLOB,
				ItemStacksUtils.itemsParse(new ItemStack[] { protectionBlock.getInformation().getItem() })));
		values.put(t.getColumn("BlocksX"), new Data(Types.INTEGER, protectionBlock.getInformation().getBlocksX()));
		values.put(t.getColumn("BlocksY"), new Data(Types.INTEGER, protectionBlock.getInformation().getBlocksY()));
		values.put(t.getColumn("BlocksZ"), new Data(Types.INTEGER, protectionBlock.getInformation().getBlocksZ()));
		values.put(t.getColumn("Permission"),
				new Data(protectionBlock.getInformation().getPermission() != null ? Types.INTEGER : Types.NULL,
						protectionBlock.getInformation().getPermission()));
		values.put(t.getColumn("Price"),
				new Data(protectionBlock.getInformation().getPrice() != null ? Types.INTEGER : Types.NULL,
						protectionBlock.getInformation().getPrice()));
		if (!this.getDatabase().insertOrUpdate(t, values, new Condition(t.getColumn("Id"),
				new Data(Types.CHAR, protectionBlock.getInformation().getId()), ConditionType.EQUAL))) {
			throw Exceptions.Protections.Blocks.Save.SQL.generateException();
		}

		t = this.getDatabase().getTable("ProtectionBlockAllowedWorlds");
		if (!this.getDatabase().delete(t, new Condition(t.getColumn("ProtectionBlockId"),
				new Data(Types.VARCHAR, protectionBlock.getInformation().getId()), ConditionType.EQUAL))) {
			throw Exceptions.Protections.Blocks.Save.SQL.generateException();
		}

		if (protectionBlock.getAllowedWorlds().get().size() > 0) {
			final Table finalT = t;
			List<Map<Column, Data>> valuesList = new ArrayList<>();
			protectionBlock.getAllowedWorlds().get().stream().forEach(allowedWorld -> {
				HashMap<Column, Data> map = new HashMap<>();
				map.put(finalT.getColumn("ProtectionBlockId"),
						new Data(Types.VARCHAR, protectionBlock.getInformation().getId()));
				map.put(finalT.getColumn("WorldName"), new Data(Types.VARCHAR, allowedWorld));
				valuesList.add(map);
			});
			if (!this.getDatabase().insertMultiple(t, valuesList)) {
				throw Exceptions.Protections.Blocks.Save.SQL.generateException();
			}
		}
	}

	public void deleteProtectionBlock(ProtectionBlock protectionBlock) throws RoyaleProtectionBlocksException {
		Table t = this.getDatabase().getTable("ProtectionBlocks");
		if (!this.getDatabase().delete(t, new Condition(new Condition(t.getColumn("Id"),
				new Data(Types.VARCHAR, protectionBlock.getInformation().getId()), ConditionType.EQUAL)))) {
			throw Exceptions.Protections.Blocks.Delete.SQL.generateException();
		}

		t = this.getDatabase().getTable("Recipes");
		if (!this.getDatabase().delete(t, new Condition(new Condition(t.getColumn("ProtectionBlockId"),
				new Data(Types.VARCHAR, protectionBlock.getInformation().getId()), ConditionType.EQUAL)))) {
			throw Exceptions.Protections.Blocks.Delete.SQL.generateException();
		}
	}

	public void saveRecipe(Recipe recipe) throws RoyaleProtectionBlocksException {
		Table t = this.getDatabase().getTable("Recipes");
		HashMap<Column, Data> values = new HashMap<>();
		values.put(t.getColumn("ProtectionBlockId"),
				new Data(Types.VARCHAR, recipe.getProtectionBlock().getObject().getInformation().getId()));
		values.put(t.getColumn("Recipe"), new Data(Types.BLOB, ItemStacksUtils.itemsParse(recipe.getRecipe())));
		values.put(t.getColumn("Permission"),
				new Data(recipe.getPermission() != null ? Types.INTEGER : Types.NULL, recipe.getPermission()));
		if (!this.getDatabase().insertOrUpdate(t, values,
				new Condition(t.getColumn("ProtectionBlockId"),
						new Data(Types.CHAR, recipe.getProtectionBlock().getObject().getInformation().getId()),
						ConditionType.EQUAL))) {
			throw Exceptions.Protections.Save.SQL.generateException();
		}
	}

	public void deleteRecipe(Recipe recipe) throws RoyaleProtectionBlocksException {
		Table t = this.getDatabase().getTable("Recipes");
		if (!this.getDatabase().delete(t,
				new Condition(new Condition(t.getColumn("ProtectionBlockId"),
						new Data(Types.VARCHAR, recipe.getProtectionBlock().getObject().getInformation().getId()),
						ConditionType.EQUAL)))) {
			throw Exceptions.Protections.Save.SQL.generateException();
		}
	}

}
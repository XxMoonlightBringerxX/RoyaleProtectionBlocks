package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.SQLPckg.SQLService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLConnection;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.PandaCachedPlayersService;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;

@PandaSubCommandAnnotation(parentCommand = AdminCommand.class)
@PandaCommandAnnotation(
		id = "dbmigrate",
		pathName = "DB-migrate",
		defaultName = "dbmigrate",
		defaultDescription = "Migrates the database from the actual source to another",
		defaultAliases = "dbm",
		defaultPermission = "protectionblocks.admin.dbmigrate",
		defaultUsage = "<sqlite|mysql>")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true)
public class DBMigrateSubCommand extends PandaSubCommand {

	public static enum Engine {
		MYSQL, SQLITE;

		public static Engine find(String name) {
			try {
				return Engine.valueOf(name.toUpperCase());
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
	}

	@PandaInject
	private static SQLService sqlService;

	private boolean migrationInProgress = false;

	public DBMigrateSubCommand() throws InstantiationException {
		super();
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		if (argIndex == 0) {
			return Arrays.stream(Engine.values()).map(val -> val.name().toLowerCase()).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Override
	protected CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		try {
			if (!migrationInProgress) {
				if (parameters.getParameters().size() > 0) {
					Engine engine = Engine.find(parameters.getParameters().get(0));

					if (engine != null) {
						if (engine == Engine.MYSQL) {
							if (sqlService.getSqlConnection().isMySQLConnection()) {
								throw Exceptions.DatabaseMigration.MYSQLINUSE.generateException();
							}
						} else if (engine == Engine.SQLITE) {
							if (sqlService.getSqlConnection().isSQLiteConnection()) {
								throw Exceptions.DatabaseMigration.SQLITEINUSE.generateException();
							}
						}

						return CommandResponse.queuedAsync(() -> {
							this.migrationInProgress = true;

							try {
								migrate(engine);

								MessageTemplate
										.inst(Messages.MESSAGE_DATABASEMIGRATION_MIGRATEDSUCCESSFULLY.applyPrefix())
										.process().sendMessage(sender);
							} catch (RoyaleProtectionBlocksException e) {
								e.sendError(sender);
							}

							this.migrationInProgress = false;
						});
					} else {
						throw Exceptions.DatabaseMigration.INVALIDOPTION.generateException()
								.setReplacements(new Replacement("{options}", () -> Arrays.stream(Engine.values())
										.map(val -> val.name().toLowerCase()).collect(Collectors.joining(", "))));
					}
				} else {
					MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process()
							.sendMessage(sender);
				}
			} else {
				throw Exceptions.DatabaseMigration.INPROGRESS.generateException();
			}
		} catch (RoyaleProtectionBlocksException e) {
			e.sendError(sender);
		}
		return CommandResponse.trueResponse();
	}

	private void migrate(Engine engine) throws RoyaleProtectionBlocksException {
		SQLConnection sqlConnection = null;

		try {
			switch (engine) {
			case MYSQL:
				sqlConnection = sqlService.generateMySQLConnection();
				break;
			case SQLITE:
				sqlConnection = sqlService.generateSQLiteConnection();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			throw Exceptions.DatabaseMigration.UNABLETOCREATECONNECTION.generateException(e);
		}

		if (sqlConnection != null) {
			if (sqlConnection.connect()) {
				try {
					sqlService.transferData(sqlConnection,
							Arrays.asList(SQLService.PENDING_PAYMENTS_TABLE, SQLService.PLAYER_DATA_TABLE,
									SQLService.PLAYER_GUARDS_TABLE, SQLService.PROTECTION_BANNEDS_TABLE,
									SQLService.PROTECTION_BLOCK_ALLOWED_WORLDS_TABLE,
									SQLService.PROTECTION_BLOCKS_TABLE, SQLService.PROTECTION_INVITATIONS_TABLE,
									SQLService.PROTECTION_MEMBERS_TABLE, SQLService.PROTECTION_OWNERS_TABLE,
									SQLService.PROTECTION_PERMISSIONS_TABLE, SQLService.PROTECTION_SETTINGS_TABLE,
									SQLService.PROTECTIONS_TABLE, SQLService.RECIPES_TABLE,
									PandaCachedPlayersService.CACHED_PLAYERS_TABLE));
				} catch (Throwable e) {
					throw Exceptions.DatabaseMigration.MIGRATIONUNKNOWNEXCEPTION.generateException(e);
				} finally {
					try {
						sqlConnection.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				throw Exceptions.DatabaseMigration.UNABLETOCONNECT.generateException();
			}
		} else {
			throw Exceptions.DatabaseMigration.UNABLETOCREATECONNECTION.generateException();
		}
	}

}

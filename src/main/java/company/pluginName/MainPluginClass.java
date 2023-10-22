package company.pluginName;

import java.util.function.Supplier;

import org.bukkit.Bukkit;

import company.pluginName.APIs.ItemsAdderAPI;
import company.pluginName.APIs.OraxenAPI;
import company.pluginName.APIs.PlaceholderAPI;
import company.pluginName.APIs.ProtectionStonesAPI;
import company.pluginName.APIs.VaultAPI;
import company.pluginName.APIs.WorldGuard.WorldGuardAPI;
import company.pluginName.Bukkit.Events.BukkitEvents;
import company.pluginName.Bukkit.Events.ItemsAdderEvents;
import company.pluginName.Bukkit.Events.OraxenEvents;
import company.pluginName.Bukkit.Events.ProtectionBlockEvents;
import company.pluginName.Bukkit.Events.RecipeEvents;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Bukkit.Inventories.Protections.Flags.Objects.Flag;
import company.pluginName.Modules.CommandsPckg.CommandsModule;
import company.pluginName.Modules.ProtectionsPckg.ProtectionSettingsModule;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsModule;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsRemoverModule;
import company.pluginName.Modules.RecipesPckg.RecipesModule;
import company.pluginName.Modules.SQLPckg.SQLModule;
import company.pluginName.TemporaryModules.FilePckg.FileModule;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingBoolean;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import relampagorojo93.LibsCollection.SpigotPlugin.MainClass;
import relampagorojo93.LibsCollection.SpigotPlugin.annotations.EventHandlers;
import relampagorojo93.LibsCollection.SpigotPlugin.interfaces.CanDisable;
import relampagorojo93.LibsCollection.SpigotPlugin.interfaces.CanEnable;
import relampagorojo93.LibsCollection.SpigotPlugin.interfaces.CanLoad;

@EventHandlers(PluginChestInventory.class)
public class MainPluginClass extends MainClass implements CanLoad, CanEnable, CanDisable {

	// ---------------------------------------------------------------//
	// MainClass methods
	// ---------------------------------------------------------------//

	public MainPluginClass() {
		super(new FileModule(), new CommandsModule(), new SQLModule(), new ProtectionSettingsModule(),
				new ProtectionsModule(), new ProtectionsRemoverModule(), new RecipesModule());
		plugin = this;
	}

	@Override
	public String getPrefix() {
		return MessageString.PREFIX.toString();
	}

	@Override
	public boolean canLoad() {
		return true;
	}

	@Override
	public boolean beforeLoad() {
		MessageBuilder
				.createMessage(getPrefix() + "",
						getPrefix() + "       _____ _____         _   _____ _         _            ",
						getPrefix() + "      | __  |  _  |___ ___| |_| __  | |___ ___| |_ ___      ",
						getPrefix() + "      |    -|   __|  _| . |  _| __ -| | . |  _| '_|_ -|     ",
						getPrefix() + "      |__|__|__|  |_| |___|_| |_____|_|___|___|_,_|___|     ",
						getPrefix() + "                       By DarkPanda73                       ",
						getPrefix() + "                                                            ",
						getPrefix() + "                 _           _ _                            ",
						getPrefix() + "                | |___ ___ _| |_|___ ___                    ",
						getPrefix() + "                | | . | .'| . | |   | . |_ _ _              ",
						getPrefix() + "                |_|___|__,|___|_|_|_|_  |_|_|_|             ",
						getPrefix() + "                                    |___|                   ", getPrefix() + "")
				.sendMessage(Bukkit.getConsoleSender());
		return true;
	}

	@Override
	public boolean load() {
		if (isFirstTime()) {
			worldGuardAPI = new WorldGuardAPI();

			if (!worldGuardAPI.isHooked()) {
				return false;
			}

			placeholderAPI = new PlaceholderAPI();
			itemsAdderAPI = new ItemsAdderAPI();
			oraxenAPI = new OraxenAPI();
		}
		return true;
	}

	@Override
	public boolean canEnable() {
		return true;
	}

	@Override
	public boolean beforeEnable() {
		if (isFirstTime()) {
			vaultAPI = new VaultAPI();
		}
		return true;
	}

	@Override
	public boolean enable() {
		if (isFirstTime()) {
			Bukkit.getPluginManager().registerEvents(new BukkitEvents(), this);
			Bukkit.getPluginManager().registerEvents(new ProtectionBlockEvents(), this);
			Bukkit.getPluginManager().registerEvents(new RecipeEvents(), this);

			protectionStonesAPI = new ProtectionStonesAPI();

			if (oraxenAPI.isHooked()) {
				Bukkit.getPluginManager().registerEvents(new OraxenEvents(), this);
			}

			if (itemsAdderAPI.isHooked()) {
				Bukkit.getPluginManager().registerEvents(new ItemsAdderEvents(), this);
			}

			if (worldGuardAPI.isHooked()) {
				worldGuardAPI.registerHandlers();
			}
		}
		Flag.initFlags();
		PluginChestInventory.initItems();
		MessageBuilder
				.createMessage(getPrefix() + "",
						getPrefix() + "                                     __                    ",
						getPrefix() + "                       _            |  |                   ",
						getPrefix() + "                     _| |___ ___ ___|  |                   ",
						getPrefix() + "                    | . | . |   | -_|__|                   ",
						getPrefix() + "                    |___|___|_|_|___|__|                   ", getPrefix() + "")
				.sendMessage(Bukkit.getConsoleSender());
		return true;
	}

	@Override
	public boolean beforeDisable() {
		return true;
	}

	@Override
	public boolean disable() {
		MessageBuilder
				.createMessage(getPrefix() + "",
						getPrefix() + "                                            __             ",
						getPrefix() + "                      _           _       _|  |            ",
						getPrefix() + "              _ _ ___| |___ ___ _| |___ _| |  |            ",
						getPrefix() + "             | | |   | | . | .'| . | -_| . |__|            ",
						getPrefix() + "             |___|_|_|_|___|__,|___|___|___|__|            ", getPrefix() + "")
				.sendMessage(Bukkit.getConsoleSender());
		return true;
	}

	// ---------------------------------------------------------------//
	// Modules
	// ---------------------------------------------------------------//

	private @Getter static MainPluginClass plugin;
	private @Getter static WorldGuardAPI worldGuardAPI;
	private @Getter static PlaceholderAPI placeholderAPI;
	private @Getter static ItemsAdderAPI itemsAdderAPI;
	private @Getter static OraxenAPI oraxenAPI;
	private @Getter static ProtectionStonesAPI protectionStonesAPI;
	private @Getter static VaultAPI vaultAPI;

	public FileModule getFileModule() {
		return (FileModule) getModule(FileModule.class);
	}

	public CommandsModule getCommandsModule() {
		return (CommandsModule) getModule(CommandsModule.class);
	}

	public ProtectionsModule getProtectionsModule() {
		return (ProtectionsModule) getModule(ProtectionsModule.class);
	}

	public ProtectionsRemoverModule getProtectionsRemoverModule() {
		return (ProtectionsRemoverModule) getModule(ProtectionsRemoverModule.class);
	}

	public ProtectionSettingsModule getProtectionSettingsModule() {
		return (ProtectionSettingsModule) getModule(ProtectionSettingsModule.class);
	}

	public RecipesModule getRecipesModule() {
		return (RecipesModule) getModule(RecipesModule.class);
	}

	public SQLModule getSqlModule() {
		return (SQLModule) getModule(SQLModule.class);
	}

	public static class Debugger {

		public static void log(MessageType messageType) {
			log(messageType, () -> new Object[0]);
		}

		public static void log(MessageType messageType, Supplier<Object[]> argsSupplier) {
			if (messageType.isAvailable() && SettingBoolean.SETTINGS_DEBUG_ENABLED.getContent()) {
				MessageBuilder
						.createMessage(
								MessageString.applyPrefix(messageType.getMessage().formatted(argsSupplier.get())))
						.sendMessage(Bukkit.getConsoleSender());
			}
		}

		@AllArgsConstructor
		@Getter
		public static enum MessageType {
			BLOCK_PLACE(SettingBoolean.SETTINGS_DEBUG_MESSAGES_BLOCKPLACE,
					"Player %s is attempting to place a block on location x(%s) y(%s) z(%s)"),

			ORAXEN_BLOCK_PLACE(SettingBoolean.SETTINGS_DEBUG_MESSAGES_ORAXENBLOCKPLACE,
					"Player %s is attempting to place an oraxen block on location x(%s) y(%s) z(%s)"),

			ITEMSADDER_BLOCK_PLACE(SettingBoolean.SETTINGS_DEBUG_MESSAGES_ITEMSADDERBLOCKPLACE,
					"Player %s is attempting to place an itemsadder block on location x(%s) y(%s) z(%s)"),

			BLOCK_PLACE_NOT_PROTECTION_BLOCK(SettingBoolean.SETTINGS_DEBUG_MESSAGES_BLOCKPLACENOTPROTECTIONBLOCK,
					"Item used by %s is currently not a protection block"),

			BLOCK_PLACE_IS_PROTECTION_BLOCK(SettingBoolean.SETTINGS_DEBUG_MESSAGES_BLOCKPLACEISPROTECTIONBLOCK,
					"Protection block the ID '%s' has been found linked to the item used by %s"),

			BLOCK_PLACE_CANCELLED_BUILD(SettingBoolean.SETTINGS_DEBUG_MESSAGES_BLOCKPLACECANCELLEDBUILD,
					"Attempt to place a protection block was cancelled due player can't build"),

			BLOCK_PLACE_CANCELLED(SettingBoolean.SETTINGS_DEBUG_MESSAGES_BLOCKPLACECANCELLED,
					"Attempt to place a protection block was already cancelled by another plugin"),

			BLOCK_BREAK(SettingBoolean.SETTINGS_DEBUG_MESSAGES_BLOCKBREAK,
					"Player %s is attempting to break a block on location x(%s) y(%s) z(%s)"),

			ORAXEN_BLOCK_BREAK(SettingBoolean.SETTINGS_DEBUG_MESSAGES_ORAXENBLOCKBREAK,
					"Player %s is attempting to break an oraxen block on location x(%s) y(%s) z(%s)"),

			ITEMSADDER_BLOCK_BREAK(SettingBoolean.SETTINGS_DEBUG_MESSAGES_ITEMSADDERBLOCKBREAK,
					"Player %s is attempting to break an itemsadder block on location x(%s) y(%s) z(%s)"),

			BLOCK_BREAK_CANCELLED(SettingBoolean.SETTINGS_DEBUG_MESSAGES_BLOCKBREAKCANCELLED,
					"Attempt to break a protection block was already cancelled by another plugin"),

			PROTECTION_CREATION_ATTEMPT(SettingBoolean.SETTINGS_DEBUG_MESSAGES_PROTECTIONCREATIONATTEMPT,
					"Player %s is attempting to create protection from location x(%s) y(%s) z(%s)"),

			PROTECTION_CREATION(SettingBoolean.SETTINGS_DEBUG_MESSAGES_PROTECTIONCREATION,
					"Player %s created protection '%s' from location x(%s) y(%s) z(%s)"),

			PROTECTION_REMOVAL_ATTEMPT(SettingBoolean.SETTINGS_DEBUG_MESSAGES_PROTECTIONREMOVALATTEMPT,
					"Player %s is attempting to remove '%s' from location x(%s) y(%s) z(%s)"),

			PROTECTION_REMOVAL(SettingBoolean.SETTINGS_DEBUG_MESSAGES_PROTECTIONREMOVAL,
					"Player %s removed protection '%s' from location x(%s) y(%s) z(%s)");

			private SettingBoolean available;
			private String message;

			public boolean isAvailable() {
				return this.available.getContent();
			}

		}

	}

}

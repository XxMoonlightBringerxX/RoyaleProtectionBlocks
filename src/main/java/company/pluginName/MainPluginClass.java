package company.pluginName;

import org.bukkit.Bukkit;

import company.pluginName.APIs.ItemsAdderAPI;
import company.pluginName.APIs.OraxenAPI;
import company.pluginName.APIs.PlaceholderAPI;
import company.pluginName.APIs.WorldGuardAPI;
import company.pluginName.Bukkit.Events.BukkitEvents;
import company.pluginName.Bukkit.Events.RecipeEvents;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Bukkit.Inventories.Protections.Flags.Objects.Flag;
import company.pluginName.Modules.CommandsPckg.CommandsModule;
import company.pluginName.Modules.FilePckg.FileModule;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.FilePckg.Settings.SettingBoolean;
import company.pluginName.Modules.ProtectionsPckg.ProtectionSettingsModule;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsModule;
import company.pluginName.Modules.RecipesPckg.RecipesModule;
import company.pluginName.Modules.SQLPckg.SQLModule;
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
				new ProtectionsModule(), new RecipesModule());
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
		return true;
	}

	@Override
	public boolean enable() {
		if (isFirstTime()) {
			Bukkit.getPluginManager().registerEvents(new BukkitEvents(), this);
			Bukkit.getPluginManager().registerEvents(new RecipeEvents(), this);
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

	public FileModule getFileModule() {
		return (FileModule) getModule(FileModule.class);
	}

	public CommandsModule getCommandsModule() {
		return (CommandsModule) getModule(CommandsModule.class);
	}

	public ProtectionsModule getProtectionsModule() {
		return (ProtectionsModule) getModule(ProtectionsModule.class);
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

		public static void log(MessageType messageType, String message, String... args) {
			if (SettingBoolean.SETTINGS_DEBUG_ENABLED.getContent() && messageType.isAvailable()) {
				MessageBuilder.createMessage(MessageString.applyPrefix(message.formatted((Object[]) args)))
						.sendMessage(Bukkit.getConsoleSender());
			}
		}

		@AllArgsConstructor
		public static enum MessageType {
			BLOCK_PLACE(SettingBoolean.SETTINGS_DEBUG_MESSAGES_BLOCKPLACE),
			BLOCK_PLACE_CANCELLED(SettingBoolean.SETTINGS_DEBUG_MESSAGES_BLOCKPLACECANCELLED),
			BLOCK_BREAK(SettingBoolean.SETTINGS_DEBUG_MESSAGES_BLOCKBREAK),
			BLOCK_BREAK_CANCELLED(SettingBoolean.SETTINGS_DEBUG_MESSAGES_BLOCKBREAKCANCELLED),
			PROTECTION_CREATION_ATTEMPT(SettingBoolean.SETTINGS_DEBUG_MESSAGES_PROTECTIONCREATIONATTEMPT),
			PROTECTION_CREATION(SettingBoolean.SETTINGS_DEBUG_MESSAGES_PROTECTIONCREATION),
			PROTECTION_REMOVAL_ATTEMPT(SettingBoolean.SETTINGS_DEBUG_MESSAGES_PROTECTIONREMOVALATTEMPT),
			PROTECTION_REMOVAL(SettingBoolean.SETTINGS_DEBUG_MESSAGES_PROTECTIONREMOVAL);

			private SettingBoolean settingBoolean;

			public boolean isAvailable() {
				return settingBoolean.getContent();
			}

		}

	}

}

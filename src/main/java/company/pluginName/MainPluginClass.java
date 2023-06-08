package company.pluginName;

import org.bukkit.Bukkit;

import company.pluginName.APIs.WorldGuardAPI;
import company.pluginName.Bukkit.Events.BukkitEvents;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Bukkit.Inventories.Protections.Flags.Objects.Flag;
import company.pluginName.Modules.CommandsPckg.CommandsModule;
import company.pluginName.Modules.FilePckg.FileModule;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsModule;
import company.pluginName.Modules.SQLPckg.SQLModule;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;
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
		super(new FileModule(), new CommandsModule(), new SQLModule(), new ProtectionsModule());
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
			wgAPI = new WorldGuardAPI();

			if (!wgAPI.isHooked()) {
				return false;
			}
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

	private static MainPluginClass plugin;
	private static WorldGuardAPI wgAPI;

	public FileModule getFileModule() {
		return (FileModule) getModule(FileModule.class);
	}

	public CommandsModule getCommandsModule() {
		return (CommandsModule) getModule(CommandsModule.class);
	}

	public ProtectionsModule getProtectionsModule() {
		return (ProtectionsModule) getModule(ProtectionsModule.class);
	}

	public SQLModule getSqlModule() {
		return (SQLModule) getModule(SQLModule.class);
	}

	public static MainPluginClass getPlugin() {
		return plugin;
	}

	public static WorldGuardAPI getWorldGuardAPI() {
		return wgAPI;
	}

	public static int getSQLVersion() {
		return 1;
	}

}

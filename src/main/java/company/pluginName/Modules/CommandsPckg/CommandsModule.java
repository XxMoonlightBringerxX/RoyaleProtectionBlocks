package company.pluginName.Modules.CommandsPckg;

import java.util.Arrays;

import org.bukkit.plugin.java.JavaPlugin;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.ProtectionBlocksCommand;

public class CommandsModule extends relampagorojo93.LibsCollection.SpigotPlugin.Defaults.CommandsModule {

	public CommandsModule() {
		super(Arrays.asList(ProtectionBlocksCommand.class));
	}

	@Override
	public JavaPlugin getPlugin() {
		return MainPluginClass.getPlugin();
	}

	public ProtectionBlocksCommand getPlotAddonCommand() {
		return (ProtectionBlocksCommand) getCommand(ProtectionBlocksCommand.class);
	}

}

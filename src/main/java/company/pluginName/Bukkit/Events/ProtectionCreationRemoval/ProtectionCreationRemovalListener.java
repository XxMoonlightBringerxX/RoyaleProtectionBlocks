package company.pluginName.Bukkit.Events.ProtectionCreationRemoval;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import company.pluginName.Hooks.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Utils.DiscordUtilities;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionCreationEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionRemovalEvent;

@PandaListener
public class ProtectionCreationRemovalListener implements Listener {

	@PandaInject
	private PlaceholderAPI placeholderApi;

	@EventHandler
	public void onProtectionCreation(ProtectionCreationEvent e) {
		Debugger.log(MessageType.PROTECTION_CREATION, () -> new Object[] { e.getPlayer().getName(),
				e.getProtection().getDisplayName() != null ? e.getProtection().getDisplayName()
						: e.getProtection().getRegionId(),
				String.valueOf(e.getProtection().getLocation().getX()),
				String.valueOf(e.getProtection().getLocation().getY()),
				String.valueOf(e.getProtection().getLocation().getZ()) });

		DiscordUtilities.sendProtectionRegisteredMessage(e.getPlayer(), (Protection) e.getProtection());

		TasksUtils.execute(() -> {
			Settings.SETTINGS_COMMANDSONCREATION.getContent().stream().filter(command -> !command.trim().isEmpty())
					.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
							placeholderApi.getHook().isHooked()
									? placeholderApi.getHook().applyPlaceholders(command, e.getPlayer())
									: command));
		});
	}

	@EventHandler
	public void onProtectionRemoval(ProtectionRemovalEvent e) {
		if (e.getCause() != RemovalCause.PURGE) {
			Debugger.log(MessageType.PROTECTION_REMOVAL, () -> new Object[] { e.getPlayer().getName(),
					e.getProtection().getDisplayName() != null ? e.getProtection().getDisplayName()
							: e.getProtection().getRegionId(),
					String.valueOf(e.getProtection().getLocation().getX()),
					String.valueOf(e.getProtection().getLocation().getY()),
					String.valueOf(e.getProtection().getLocation().getZ()) });

			DiscordUtilities.sendProtectionUnregisteredMessage(e.getPlayer(), (Protection) e.getProtection());
		}

		TasksUtils.execute(() -> {
			Settings.SETTINGS_COMMANDSONREMOVAL.getContent().stream().filter(command -> !command.trim().isEmpty())
					.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
							placeholderApi.getHook().isHooked()
									? placeholderApi.getHook().applyPlaceholders(command, e.getPlayer())
									: command));
		});
	}

}

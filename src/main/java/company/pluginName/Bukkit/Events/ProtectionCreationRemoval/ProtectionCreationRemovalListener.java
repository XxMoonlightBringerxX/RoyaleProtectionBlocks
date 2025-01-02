package company.pluginName.Bukkit.Events.ProtectionCreationRemoval;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import company.pluginName.Hooks.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import company.pluginName.Modules.ProtectionsPurgePckg.ProtectionsPurgeService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.RoyaleProtectionBlocksAPI;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.CreationCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionCreationEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionRemovalEvent;

@PandaListener
public class ProtectionCreationRemovalListener implements Listener {

	@RegisteredPandaField("lang")
	private static PandaPrefixedStringField PROTECTION_MERGE_PROTECTIONCREATIONADVICE = new PandaPrefixedStringField(
			"Message.Protection.Merge.Protection-creation-advice",
			"&7Seems like the new protection has been placed inside one or more protections! Type &e/pb merge&7 if you wish to merge this new protection.");

	@PandaInject
	private PlaceholderAPI placeholderApi;

	@PandaInject
	private ProtectionsPurgeService protectionsPurgeService;

	@EventHandler
	public void onProtectionCreation(ProtectionCreationEvent e) {
		if (e.getCause() == CreationCause.PLAYER) {
			Debugger.log(MessageType.PROTECTION_CREATION,
					() -> new Object[] { e.getExecutor().getName(),
							e.getProtection().getDisplayName() != null ? e.getProtection().getDisplayName()
									: e.getProtection().getProtectionId(),
							String.valueOf(e.getProtection().getBukkitLocation().getX()),
							String.valueOf(e.getProtection().getBukkitLocation().getY()),
							String.valueOf(e.getProtection().getBukkitLocation().getZ()) });

			TasksUtils.execute(() -> {
				Settings.SETTINGS_COMMANDSONCREATION
						.getContent().stream().filter(
								command -> !command.trim().isEmpty())
						.forEach(
								command -> Bukkit
										.dispatchCommand(Bukkit.getConsoleSender(),
												placeholderApi.getHook().isHooked()
														? placeholderApi.getHook().applyPlaceholders(command,
																OfflinePlayerUtilities.getOfflinePlayer(
																		e.getProtection().getOwnerUuid()))
														: command));

				if (e.getExecutor() != null && e.getExecutor() instanceof Player
						&& PROTECTION_MERGE_PROTECTIONCREATIONADVICE.getContent() != null
						&& !PROTECTION_MERGE_PROTECTIONCREATIONADVICE.getContent().isEmpty()) {
					RoyaleProtectionBlocksAPI.getInstance().getProtectionsService()
							.findProtectionParentsByArea(e.getProtection().getProtectionArea(), true)
							.filter(prot -> prot != e.getProtection()
									&& prot.getOwnerUuid().equals(e.getProtection().getOwnerUuid())
									&& ProtectionUtilities.canMerge(prot, (Player) e.getExecutor()))
							.findFirst().ifPresent(prot -> {
								MessageTemplate.inst(PROTECTION_MERGE_PROTECTIONCREATIONADVICE.applyPrefix()).process()
										.sendMessage(e.getExecutor());
							});
				}
			});
		}

		protectionsPurgeService.awakePurgeTask();
	}

	@EventHandler
	public void onProtectionRemoval(ProtectionRemovalEvent e) {
		if (e.getCause() == RemovalCause.PLAYER) {
			Debugger.log(MessageType.PROTECTION_REMOVAL,
					() -> new Object[] { e.getExecutor().getName(),
							e.getProtection().getDisplayName() != null ? e.getProtection().getDisplayName()
									: e.getProtection().getProtectionId(),
							String.valueOf(e.getProtection().getBukkitLocation().getX()),
							String.valueOf(e.getProtection().getBukkitLocation().getY()),
							String.valueOf(e.getProtection().getBukkitLocation().getZ()) });

			TasksUtils.execute(() -> {
				Settings.SETTINGS_COMMANDSONREMOVAL
						.getContent().stream().filter(
								command -> !command.trim().isEmpty())
						.forEach(
								command -> Bukkit
										.dispatchCommand(Bukkit.getConsoleSender(),
												placeholderApi.getHook().isHooked()
														? placeholderApi.getHook().applyPlaceholders(command,
																OfflinePlayerUtilities.getOfflinePlayer(
																		e.getProtection().getOwnerUuid()))
														: command));
			});
		}
	}

}

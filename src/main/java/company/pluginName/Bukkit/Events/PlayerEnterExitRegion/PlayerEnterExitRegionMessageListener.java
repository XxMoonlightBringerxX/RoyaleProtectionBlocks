package company.pluginName.Bukkit.Events.PlayerEnterExitRegion;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import royale.RoyaleProtectionBlocks.Plugin.InternalAPI.Events.Player.PlayerEnterExitProtectionEvent;
import royale.RoyaleProtectionBlocks.Plugin.InternalAPI.Interfaces.IProtection;

@PandaListener
public class PlayerEnterExitRegionMessageListener implements Listener {

	@RegisteredPandaField("config")
	public static final PandaStringField SETTINGS_PROTECTION_MESSAGEONENTERREGION = new PandaStringField(
			"Settings.Protection.Message-on-enter-region", "&eWelcome &7{player_name} &eto &a{protection_name}&e!");

	@RegisteredPandaField("config")
	public static final PandaStringField SETTINGS_PROTECTION_MESSAGEONEXITREGION = new PandaStringField(
			"Settings.Protection.Message-on-exit-region", "&eSee you later! :D");

	@RegisteredPandaField("config")
	public static final PandaStringField SETTINGS_PROTECTION_ACTIONBARMESSAGEONENTERREGION = new PandaStringField(
			"Settings.Protection.Actionbar-message-on-enter-region", "");
	@RegisteredPandaField("config")
	public static final PandaStringField SETTINGS_PROTECTION_ACTIONBARMESSAGEONEXITREGION = new PandaStringField(
			"Settings.Protection.Actionbar-message-on-exit-region", "");

	@RegisteredPandaField("config")
	public static final PandaStringField SETTINGS_PROTECTION_TITLEONENTERREGION = new PandaStringField(
			"Settings.Protection.Title-on-enter-region", "");
	@RegisteredPandaField("config")
	public static final PandaStringField SETTINGS_PROTECTION_SUBTITLEONENTERREGION = new PandaStringField(
			"Settings.Protection.Subtitle-on-enter-region", "");

	@RegisteredPandaField("config")
	public static final PandaStringField SETTINGS_PROTECTION_TITLEONEXITREGION = new PandaStringField(
			"Settings.Protection.Title-on-exit-region", "");
	@RegisteredPandaField("config")
	public static final PandaStringField SETTINGS_PROTECTION_SUBTITLEONEXITREGION = new PandaStringField(
			"Settings.Protection.Subtitle-on-exit-region", "");

	@PandaInject
	private ProtectionSettingsService protectionSettingsService;

	private HashMap<UUID, IProtection> lastProtections = new HashMap<>();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerEnterRegionAllowed(PlayerEnterExitProtectionEvent e) {
		IProtection lastProtection = lastProtections.get(e.getPlayer().getUniqueId());
		if (e.getCurrentProtections().size() == 0 && lastProtection != null) {
			sendExitMessage(e.getPlayer(), lastProtection);
			lastProtections.remove(e.getPlayer().getUniqueId());
		} else if (!e.getCurrentProtections().contains(lastProtection)) {
			IProtection newProtection = e.getCurrentProtections().stream()
					.sorted((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority())).findFirst().orElse(null);
			if (newProtection != null) {
				sendEnterMessage(e.getPlayer(), newProtection);
				lastProtections.put(e.getPlayer().getUniqueId(), newProtection);
			}
		} else if (e.getEnteredProtections().size() > 0) {
			IProtection newProtection = e.getEnteredProtections().stream()
					.filter(prot -> Integer.compare(prot.getPriority(), lastProtection.getPriority()) > 0).findFirst()
					.orElse(null);
			if (newProtection != null) {
				sendEnterMessage(e.getPlayer(), newProtection);
				lastProtections.put(e.getPlayer().getUniqueId(), newProtection);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		lastProtections.remove(e.getPlayer().getUniqueId());
	}

	private void sendEnterMessage(Player player, IProtection protection) {
		Replacement[] replacements = getReplacements(player, protection);

		if (SETTINGS_PROTECTION_MESSAGEONENTERREGION.getContent() != null
				&& !SETTINGS_PROTECTION_MESSAGEONENTERREGION.getContent().isEmpty()) {
			MessageTemplate.inst(SETTINGS_PROTECTION_MESSAGEONENTERREGION.getContent()).setReplacements(replacements)
					.process().sendMessage(player);
		}

		if (SETTINGS_PROTECTION_ACTIONBARMESSAGEONENTERREGION.getContent() != null
				&& !SETTINGS_PROTECTION_ACTIONBARMESSAGEONENTERREGION.getContent().isEmpty()) {
			MessageTemplate.inst(SETTINGS_PROTECTION_ACTIONBARMESSAGEONENTERREGION.getContent())
					.setReplacements(replacements).process().sendActionBar(player);
		}

		if ((SETTINGS_PROTECTION_TITLEONENTERREGION.getContent() != null
				&& !SETTINGS_PROTECTION_TITLEONENTERREGION.getContent().isEmpty())
				|| (SETTINGS_PROTECTION_SUBTITLEONENTERREGION.getContent() != null
						&& !SETTINGS_PROTECTION_SUBTITLEONENTERREGION.getContent().isEmpty())) {
			MessageTemplate
					.inst(SETTINGS_PROTECTION_TITLEONENTERREGION.getContent(),
							SETTINGS_PROTECTION_SUBTITLEONENTERREGION.getContent())
					.setReplacements(replacements).process().sendTitle(player);
		}
	}

	private void sendExitMessage(Player player, IProtection protection) {
		Replacement[] replacements = getReplacements(player, protection);

		if (SETTINGS_PROTECTION_MESSAGEONEXITREGION.getContent() != null
				&& !SETTINGS_PROTECTION_MESSAGEONEXITREGION.getContent().isEmpty()) {
			MessageTemplate.inst(SETTINGS_PROTECTION_MESSAGEONEXITREGION.getContent()).setReplacements(replacements)
					.process().sendMessage(player);
		}

		if (SETTINGS_PROTECTION_ACTIONBARMESSAGEONEXITREGION.getContent() != null
				&& !SETTINGS_PROTECTION_ACTIONBARMESSAGEONEXITREGION.getContent().isEmpty()) {
			MessageTemplate.inst(SETTINGS_PROTECTION_ACTIONBARMESSAGEONEXITREGION.getContent())
					.setReplacements(replacements).process().sendActionBar(player);
		}

		if ((SETTINGS_PROTECTION_TITLEONEXITREGION.getContent() != null
				&& !SETTINGS_PROTECTION_TITLEONEXITREGION.getContent().isEmpty())
				|| (SETTINGS_PROTECTION_SUBTITLEONEXITREGION.getContent() != null
						&& !SETTINGS_PROTECTION_SUBTITLEONEXITREGION.getContent().isEmpty())) {
			MessageTemplate
					.inst(SETTINGS_PROTECTION_TITLEONEXITREGION.getContent(),
							SETTINGS_PROTECTION_SUBTITLEONEXITREGION.getContent())
					.setReplacements(replacements).process().sendTitle(player);
		}
	}

	public Replacement[] getReplacements(Player player, IProtection protection) {
		return new Replacement[] { new Replacement("{player_name}", () -> player.getName()),
				new Replacement("{protection_name}", () -> protection.getDisplayName()),
				new Replacement("{protection_owner}", () -> protection.getOwnerName()) };
	}

}

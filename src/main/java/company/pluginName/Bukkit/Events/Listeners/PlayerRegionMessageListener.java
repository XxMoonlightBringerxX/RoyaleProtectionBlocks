package company.pluginName.Bukkit.Events.Listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import company.pluginName.Hooks.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Player.PlayerEnterExitProtectionEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.IProtection;

@PandaListener
public class PlayerRegionMessageListener implements Listener {

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

	@PandaInject
	private PlaceholdersService placeholdersService;

	@PandaInject
	private PlaceholderAPI placeholderApi;

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

		sendMessage(player, replacements, SETTINGS_PROTECTION_MESSAGEONENTERREGION.getContent());
		sendActionBar(player, replacements, SETTINGS_PROTECTION_ACTIONBARMESSAGEONENTERREGION.getContent());
		sendTitle(player, replacements, SETTINGS_PROTECTION_TITLEONENTERREGION.getContent(),
				SETTINGS_PROTECTION_SUBTITLEONENTERREGION.getContent());
	}

	private void sendExitMessage(Player player, IProtection protection) {
		Replacement[] replacements = getReplacements(player, protection);

		sendMessage(player, replacements, SETTINGS_PROTECTION_MESSAGEONEXITREGION.getContent());
		sendActionBar(player, replacements, SETTINGS_PROTECTION_ACTIONBARMESSAGEONEXITREGION.getContent());
		sendTitle(player, replacements, SETTINGS_PROTECTION_TITLEONEXITREGION.getContent(),
				SETTINGS_PROTECTION_SUBTITLEONEXITREGION.getContent());
	}

	private void sendMessage(Player player, Replacement[] replacements, String message) {
		if (message != null && !message.isEmpty()) {
			MessageTemplate.inst(
					placeholderApi.isHooked() ? placeholderApi.getHook().applyPlaceholders(message, player) : message)
					.setReplacements(replacements).process().sendMessage(player);
		}
	}

	private void sendTitle(Player player, Replacement[] replacements, String title, String subtitle) {
		if ((title != null && !title.isEmpty()) || (subtitle != null && !subtitle.isEmpty())) {
			MessageTemplate.inst(
					placeholderApi.isHooked() ? placeholderApi.getHook().applyPlaceholders(title, player) : title,
					placeholderApi.isHooked() ? placeholderApi.getHook().applyPlaceholders(subtitle, player) : subtitle)
					.setReplacements(replacements).process().sendTitle(player);
		}
	}

	private void sendActionBar(Player player, Replacement[] replacements, String message) {
		if (message != null && !message.isEmpty()) {
			MessageTemplate.inst(
					placeholderApi.isHooked() ? placeholderApi.getHook().applyPlaceholders(message, player) : message)
					.setReplacements(replacements).process().sendActionBar(player);
		}
	}

	public Replacement[] getReplacements(Player player, IProtection protection) {
		List<Replacement> replacements = new ArrayList<>();

		replacements.addAll(Arrays.asList(placeholdersService.getPlayerReplacements(player)));
		replacements.addAll(Arrays.asList(placeholdersService.getProtectionReplacements(protection)));

		return replacements.toArray(new Replacement[replacements.size()]);
	}

}

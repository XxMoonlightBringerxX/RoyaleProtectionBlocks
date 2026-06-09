package company.pluginName.Modules.ProtectionSettingsPckg.Listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import company.pluginName.Hooks.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionSettingsService;
import company.pluginName.Modules.ProtectionSettingsPckg.Objects.Settings.GreetingFarewellMessagesSetting.ConfiguredMessages;
import company.pluginName.Modules.SettingsPckg.SettingsService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Message;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Services.PandaBossBarModule.PandaBossBarService;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.PermissionGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Player.PlayerEnterExitProtectionEvent;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

@PandaListener
public class GreetingFarewellMessagesSettingListener implements Listener {

	@PandaInject
	private SettingsService protectionSettingsService;

	@PandaInject
	private PlaceholdersService placeholdersService;

	@PandaInject
	private PlayerDataService playerDataService;

	@PandaInject
	private PandaBossBarService bossBarService;

	@PandaInject
	private PlaceholderAPI placeholderApi;

	private HashMap<UUID, IProtection> lastProtections = new HashMap<>();

	private HashMap<UUID, String> lastMessages = new HashMap<>();
	private HashMap<UUID, String> lastActionBars = new HashMap<>();
	private HashMap<UUID, Pair<String, String>> lastTitles = new HashMap<>();
	private HashMap<UUID, String> lastBossBars = new HashMap<>();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (bossBarService != null) {
			PlayerData playerData = playerDataService.getPlayerData(e.getPlayer());

			if (playerData != null) {
				playerData.setBossBar(bossBarService.createTimedBossBar(100));
				playerData.getBossBar().setRemoveOnFinish(false);
				playerData.getBossBar().addPlayer(e.getPlayer());
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerExit(PlayerQuitEvent e) {
		if (bossBarService != null) {
			PlayerData playerData = playerDataService.getPlayerData(e.getPlayer());

			if (playerData != null) {
				bossBarService.removeBossBar(playerData.getBossBar().getUuid());
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMoves(PlayerEnterExitProtectionEvent e) {
		TasksUtils.executeOnAsync(() -> {
			if (ProtectionSettingsService.GREETING_FAREWELL_MESSAGES_SETTING.isEnabled()) {
				IProtection lastProtection = lastProtections.get(e.getPlayer().getUniqueId());
				if (e.getCurrentProtections().size() == 0 && lastProtection != null) {
					sendExitMessage(e.getPlayer(), lastProtection);
					lastProtections.remove(e.getPlayer().getUniqueId());
				} else if (!e.getCurrentProtections().contains(lastProtection)) {
					IProtection newProtection = e.getCurrentProtections().stream()
							.sorted((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority())).findFirst()
							.orElse(null);
					if (newProtection != null) {
						sendEnterMessage(e.getPlayer(), newProtection);
						lastProtections.put(e.getPlayer().getUniqueId(), newProtection);
					}
				} else if (e.getEnteredProtections().size() > 0) {
					IProtection newProtection = e.getEnteredProtections().stream()
							.filter(prot -> Integer.compare(prot.getPriority(), lastProtection.getPriority()) > 0)
							.findFirst().orElse(null);
					if (newProtection != null) {
						sendEnterMessage(e.getPlayer(), newProtection);
						lastProtections.put(e.getPlayer().getUniqueId(), newProtection);
					}
				}
			}
		});
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		lastProtections.remove(e.getPlayer().getUniqueId());
		lastMessages.remove(e.getPlayer().getUniqueId());
		lastActionBars.remove(e.getPlayer().getUniqueId());
		lastTitles.remove(e.getPlayer().getUniqueId());
	}

	private void sendEnterMessage(Player player, IProtection protection) {
		Replacement[] replacements = getReplacements(player, protection);

		PermissionGroup group = protection.getGroup(player);

		if (group == PermissionGroup.STAFF) {
			group = PermissionGroup.OWNERS;
		}

		ConfiguredMessages messages = protection
				.getSettingValue(ProtectionSettingsService.GREETING_FAREWELL_MESSAGES_SETTING, group);

		switch (messages.getMessageType()) {
		case MESSAGE:
			sendMessage(player, replacements, messages.getGreetingMessage());
			break;
		case ACTION_BAR:
			sendActionBar(player, replacements, messages.getGreetingMessage());
			break;
		case TITLE:
			sendTitle(player, replacements, messages.getGreetingMessage());
			break;
		case BOSS_BAR:
			sendBossBar(player, replacements, messages.getGreetingMessage());
			break;
		}
	}

	private void sendExitMessage(Player player, IProtection protection) {
		Replacement[] replacements = getReplacements(player, protection);

		PermissionGroup group = protection.getGroup(player);

		if (group == PermissionGroup.STAFF) {
			group = PermissionGroup.OWNERS;
		}

		ConfiguredMessages messages = protection
				.getSettingValue(ProtectionSettingsService.GREETING_FAREWELL_MESSAGES_SETTING, group);

		switch (messages.getMessageType()) {
		case MESSAGE:
			sendMessage(player, replacements, messages.getFarewellMessage());
			break;
		case ACTION_BAR:
			sendActionBar(player, replacements, messages.getFarewellMessage());
			break;
		case TITLE:
			sendTitle(player, replacements, messages.getFarewellMessage());
			break;
		case BOSS_BAR:
			sendBossBar(player, replacements, messages.getFarewellMessage());
			break;
		}
	}

	private void sendMessage(Player player, Replacement[] replacements, List<String> messages) {
		if (messages != null && !messages.isEmpty()) {
			String lastMessage = lastMessages.get(player.getUniqueId());
			Message messageResult = MessageTemplate.inst(
					placeholderApi.isHooked() ? placeholderApi.getHook().applyPlaceholders(messages, player) : messages)
					.setReplacements(replacements).process();
			String messageResultString = messageResult.toString();

			if (lastMessage == null || !messageResultString.equals(lastMessage)) {
				messageResult.sendMessage(player);
				lastMessages.put(player.getUniqueId(), messageResultString);
			}
		}
	}

	private void sendActionBar(Player player, Replacement[] replacements, List<String> messages) {
		if (messages != null && !messages.isEmpty()) {
			String lastActionBar = lastActionBars.get(player.getUniqueId());
			Message messageResult = MessageTemplate.inst(
					placeholderApi.isHooked() ? placeholderApi.getHook().applyPlaceholders(messages.get(0), player)
							: messages.get(0))
					.setReplacements(replacements).process();
			String messageResultString = messageResult.toString();

			if (lastActionBar == null || !messageResultString.equals(lastActionBar)) {
				messageResult.sendActionBar(player);
				lastActionBars.put(player.getUniqueId(), messageResultString);
			}
		}
	}

	private void sendTitle(Player player, Replacement[] replacements, List<String> messages) {
		if (messages != null && !messages.isEmpty()) {
			String title = messages.size() > 0 ? messages.get(0) : "";
			String subtitle = messages.size() > 1 ? messages.get(1) : "";

			Pair<String, String> lastTitle = lastTitles.get(player.getUniqueId());
			Message messageResult = MessageTemplate.inst(
					placeholderApi.isHooked() ? placeholderApi.getHook().applyPlaceholders(title, player) : title,
					placeholderApi.isHooked() ? placeholderApi.getHook().applyPlaceholders(subtitle, player) : subtitle)
					.setReplacements(replacements).process();

			String generatedTitle = messageResult.getStrings().size() > 0 ? messageResult.getStrings().get(0) : null;
			if (generatedTitle == null) {
				generatedTitle = "";
			}

			String generatedSubtitle = messageResult.getStrings().size() > 1 ? messageResult.getStrings().get(1) : null;
			if (generatedSubtitle == null) {
				generatedSubtitle = "";
			}

			Pair<String, String> messageResultPair = Pair.of(generatedTitle, generatedSubtitle);

			if (lastTitle == null || !lastTitle.getFirst().equals(messageResultPair.getFirst())
					|| !lastTitle.getSecond().equals(messageResultPair.getSecond())) {
				messageResult.sendTitle(player);
				lastTitles.put(player.getUniqueId(), messageResultPair);
			}
		}
	}

	private void sendBossBar(Player player, Replacement[] replacements, List<String> messages) {
		if (messages != null && !messages.isEmpty()) {
			PlayerData playerData = playerDataService.getPlayerData(player);

			if (playerData != null) {
				String lastActionBar = lastBossBars.get(player.getUniqueId());
				Message messageResult = MessageTemplate.inst(
						placeholderApi.isHooked() ? placeholderApi.getHook().applyPlaceholders(messages.get(0), player)
								: messages.get(0))
						.setReplacements(replacements).process();
				String messageResultString = messageResult.toString();

				if (lastActionBar == null || !messageResultString.equals(lastActionBar)) {
					playerData.getBossBar().setTitle(messageResultString);
					playerData.getBossBar().reset();
					lastBossBars.put(player.getUniqueId(), messageResultString);
				}
			}
		}
	}

	private Replacement[] getReplacements(Player player, IProtection protection) {
		List<Replacement> replacements = new ArrayList<>();

		replacements.addAll(Arrays.asList(placeholdersService.getPlayerReplacements(player)));
		replacements.addAll(Arrays.asList(placeholdersService.getProtectionReplacements(protection)));

		return replacements.toArray(new Replacement[replacements.size()]);
	}

}

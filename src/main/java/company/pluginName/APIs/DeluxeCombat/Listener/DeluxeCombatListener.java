package company.pluginName.APIs.DeluxeCombat.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import company.pluginName.APIs.DeluxeCombat.DeluxeCombatAPI;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.InternalAPI.Events.Player.PlayerTeleportToProtectionAttemptEvent;
import royale.RoyaleProtectionBlocks.Plugin.InternalAPI.Events.Protection.ProtectionCreationAttemptEvent;
import royale.RoyaleProtectionBlocks.Plugin.InternalAPI.Events.Protection.ProtectionRemovalAttemptEvent;

@PandaListener(optional = true)
public class DeluxeCombatListener implements Listener {

	@RegisteredPandaField("config")
	public static final PandaBooleanField SETTINGS_DELUXECOMBAT_CANCELPROTECTIONCREATIONINCOMBAT = new PandaBooleanField(
			"Settings.Deluxe-combat.Cancel-protection-creation-in-combat", true);

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_DELUXECOMBAT_PROTECTIONCREATIONDENIED = new PandaPrefixedStringField(
			"Message.Deluxe-combat.Protection-creation-denied", "&cYou can't create a protection while in combat!");

	@RegisteredPandaField("config")
	public static final PandaBooleanField SETTINGS_DELUXECOMBAT_CANCELPROTECTIONREMOVALINCOMBAT = new PandaBooleanField(
			"Settings.Deluxe-combat.Cancel-protection-removal-in-combat", true);

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_DELUXECOMBAT_PROTECTIONREMOVALDENIED = new PandaPrefixedStringField(
			"Message.Deluxe-combat.Protection-removal-denied", "&cYou can't remove a protection while in combat!");

	@RegisteredPandaField("config")
	public static final PandaBooleanField SETTINGS_DELUXECOMBAT_CANCELPROTECTIONTELEPORTINCOMBAT = new PandaBooleanField(
			"Settings.Deluxe-combat.Cancel-protection-teleport-in-combat", true);

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField MESSAGE_DELUXECOMBAT_PROTECTIONTELEPORTDENIED = new PandaPrefixedStringField(
			"Message.Deluxe-combat.Protection-teleport-denied",
			"&cYou can't teleport to a protection while in combat!");

	@PandaInject
	private DeluxeCombatAPI deluxeCombatApi;

	@EventHandler
	public void onProtectionBlockCreation(ProtectionCreationAttemptEvent e) {
		if (Boolean.TRUE.equals(SETTINGS_DELUXECOMBAT_CANCELPROTECTIONCREATIONINCOMBAT.getContent())
				&& deluxeCombatApi.isHooked() && deluxeCombatApi.getHook().isInCombat(e.getPlayer())) {
			e.setCancelled(true);
			MessageTemplate.inst(MESSAGE_DELUXECOMBAT_PROTECTIONCREATIONDENIED.applyPrefix()).process()
					.sendMessage(e.getPlayer());

		}
	}

	@EventHandler
	public void onProtectionBlockCreation(ProtectionRemovalAttemptEvent e) {
		if (Boolean.TRUE.equals(SETTINGS_DELUXECOMBAT_CANCELPROTECTIONREMOVALINCOMBAT.getContent())
				&& deluxeCombatApi.isHooked() && deluxeCombatApi.getHook().isInCombat(e.getPlayer())) {
			e.setCancelled(true);
			MessageTemplate.inst(MESSAGE_DELUXECOMBAT_PROTECTIONREMOVALDENIED.applyPrefix()).process()
					.sendMessage(e.getPlayer());

		}
	}

	@EventHandler
	public void onProtectionBlockCreation(PlayerTeleportToProtectionAttemptEvent e) {
		if (Boolean.TRUE.equals(SETTINGS_DELUXECOMBAT_CANCELPROTECTIONTELEPORTINCOMBAT.getContent())
				&& deluxeCombatApi.isHooked() && deluxeCombatApi.getHook().isInCombat(e.getPlayer())) {
			e.setCancelled(true);
			MessageTemplate.inst(MESSAGE_DELUXECOMBAT_PROTECTIONTELEPORTDENIED.applyPrefix()).process()
					.sendMessage(e.getPlayer());

		}
	}

}

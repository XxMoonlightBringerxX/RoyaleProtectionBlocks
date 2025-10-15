package company.pluginName.Features.PvPPckg.Utils;

import org.bukkit.entity.Player;

import company.pluginName.Hooks.CombatLogX.CombatLogXAPI;
import company.pluginName.Hooks.DeluxeCombat.DeluxeCombatAPI;
import company.pluginName.Hooks.PvPManager.PvPManagerAPI;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;

public class PvPUtilities {

	@RegisteredPandaField("config")
	public static final PandaBooleanField SETTINGS_COMBATLOGHOOK_DELUXECOMBATENABLED = new PandaBooleanField(
			"Settings.Combat-log-hook.Deluxe-combat-enabled", true);

	@RegisteredPandaField("config")
	public static final PandaBooleanField SETTINGS_COMBATLOGHOOK_COMBATLOGXENABLED = new PandaBooleanField(
			"Settings.Combat-log-hook.Combat-log-X-enabled", true);

	@RegisteredPandaField("config")
	public static final PandaBooleanField SETTINGS_COMBATLOGHOOK_PVPMANAGERENABLED = new PandaBooleanField(
			"Settings.Combat-log-hook.PvP-manager-enabled", true);

	@RegisteredPandaField("config")
	public static final PandaBooleanField SETTINGS_COMBATLOGHOOK_CANCELPROTECTIONCREATIONINCOMBAT = new PandaBooleanField(
			"Settings.Combat-log-hook.Cancel-protection-creation-in-combat",
			"Settings.Deluxe-combat.Cancel-protection-creation-in-combat", true);
	@RegisteredPandaField("config")
	public static final PandaBooleanField SETTINGS_COMBATLOGHOOK_CANCELPROTECTIONREMOVALINCOMBAT = new PandaBooleanField(
			"Settings.Combat-log-hook.Cancel-protection-removal-in-combat",
			"Settings.Deluxe-combat.Cancel-protection-removal-in-combat", true);

	@RegisteredPandaField("config")
	public static final PandaBooleanField SETTINGS_COMBATLOGHOOK_CANCELPROTECTIONTELEPORTINCOMBAT = new PandaBooleanField(
			"Settings.Combat-log-hook.Cancel-protection-teleport-in-combat",
			"Settings.Deluxe-combat.Cancel-protection-teleport-in-combat", true);

	@PandaInject
	private static DeluxeCombatAPI deluxeCombatApi;

	@PandaInject
	private static CombatLogXAPI combatLogXApi;

	@PandaInject
	private static PvPManagerAPI pvpManagerApi;

	public static boolean isInCombat(Player player) {
		if (PvPUtilities.SETTINGS_COMBATLOGHOOK_DELUXECOMBATENABLED.isTrue() && deluxeCombatApi.isHooked()
				&& deluxeCombatApi.getHook().isInCombat(player)) {
			return true;
		}

		if (PvPUtilities.SETTINGS_COMBATLOGHOOK_COMBATLOGXENABLED.isTrue() && combatLogXApi.isHooked()
				&& combatLogXApi.getHook().isInCombat(player)) {
			return true;
		}

		if (PvPUtilities.SETTINGS_COMBATLOGHOOK_PVPMANAGERENABLED.isTrue() && pvpManagerApi.isHooked()
				&& pvpManagerApi.getHook().isInCombat(player)) {
			return true;
		}

		return false;
	}

}

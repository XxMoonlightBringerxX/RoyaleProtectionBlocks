package company.pluginName.Modules.FilePckg;

import java.util.Arrays;

import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaFieldContainer;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaDoubleField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaIntegerField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaLongField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;

@RegisteredPandaFieldContainer("config")
public class Settings {

	public static final PandaStringListField SETTINGS_BANNEDWORLDS = new PandaStringListField(
			"Settings.Protection.Banned-worlds", Arrays.asList("World1", "World2", "World3"));
	public static final PandaStringListField SETTINGS_COMMANDSONCREATION = new PandaStringListField(
			"Settings.Protection.Commands-on-creation", Arrays.asList());
	public static final PandaStringListField SETTINGS_COMMANDSONREMOVAL = new PandaStringListField(
			"Settings.Protection.Commands-on-removal", Arrays.asList());

	public static final PandaStringField SETTINGS_PROTECTION_STARTERBLOCK = new PandaStringField(
			"Settings.Protection.Starter-block", "Settings.Protection.Stater-block", "");
	public static final PandaStringField SETTINGS_PROTECTION_AUTOPURGE_EXECUTEEVERY = new PandaStringField(
			"Settings.Protection.Auto-purge.Execute-every", "");
	public static final PandaStringField SETTINGS_PROTECTION_AUTOPURGE_PURGEOLDERTHAN = new PandaStringField(
			"Settings.Protection.Auto-purge.Purge-older-than", "7d");
	public static final PandaIntegerField SETTINGS_PROTECTION_BOUNDARIESVIEWDURATIONINSECONDS = new PandaIntegerField(
			"Settings.Protection.Boundaries-view-duration-in-seconds", 30);
	public static final PandaDoubleField SETTINGS_PROTECTION_TELEPORTCOST = new PandaDoubleField(
			"Settings.Protection.Teleport-cost", 0D);
	public static final PandaLongField SETTINGS_PROTECTION_TELEPORTCOOLDOWN = new PandaLongField(
			"Settings.Protection.Teleport-cooldown", 30L);

	public static final PandaBooleanField SETTINGS_PROTECTION_SETPLAYERPOSITIONASHOMEONCREATION = new PandaBooleanField(
			"Settings.Protection.Set-player-position-as-home-on-creation", true);
	public static final PandaBooleanField SETTINGS_PROTECTION_OPENINVENTORYONINTERACT = new PandaBooleanField(
			"Settings.Protection.Open-inventory-on-interact", true);
	public static final PandaBooleanField SETTINGS_PROTECTION_ALLOWREGIONSINSIDEANOTHERFROMSAMEOWNER = new PandaBooleanField(
			"Settings.Protection.Allow-regions-inside-another-from-same-owner", true);
	public static final PandaBooleanField SETTINGS_PROTECTIONBLOCK_REQUESTCONFIRMATIONONPURCHASETHROUGHGUI = new PandaBooleanField(
			"Settings.Protection-block.Request-confirmation-on-purchase-through-gui", false);

}

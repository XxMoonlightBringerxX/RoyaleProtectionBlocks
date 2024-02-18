package company.pluginName.Modules.FilePckg;

import java.util.Arrays;

import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaFieldContainer;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;

@RegisteredPandaFieldContainer("lang")
public class Messages {

	// Messages
	public static final PandaPrefixedStringField MESSAGE_RELOAD = new PandaPrefixedStringField("Message.Reload",
			"&aThe plugin has been reloaded successfully.");
	public static final PandaPrefixedStringField MESSAGE_SETSPAWNSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Set-spawn-successfully", "&aA new spawn has ben set successfully.");

	public static final PandaPrefixedStringField MESSAGE_GENERAL_NOLIMIT = new PandaPrefixedStringField(
			"Message.General.No-limit", "No limit");

	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_CREATEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Created-successfully", "&aThe protection has been created successfully!");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_REMOVEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Removed-successfully", "&aThe protection has been removed successfully!");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_HOMESETSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Home-set-successfully", "&aThe protection's home has been set successfully!");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_IDSETSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.ID-set-successfully", "&aThe protection's custom ID has been set successfully!");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_RENAMEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Renamed-successfully", "&aThe protection's name has been changed successfully!");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_HIDDENSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Hidden-successfully", "&aThe protection block has been hidden successfully!");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_SHOWNSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Shown-successfully", "&aThe protection block has been shown successfully!");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_KICKED = new PandaPrefixedStringField(
			"Message.Protections.Kicked", "&cYou've been kicked from this protection.");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_BANNED = new PandaPrefixedStringField(
			"Message.Protections.Banned", "&cYou've been banned from this protection.");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_PLAYERNOTINPROTECTION = new PandaPrefixedStringField(
			"Message.Protections.Player-not-in-protection", "&cThis player is not currently inside this protection.");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_PLAYERWITHKICKBYPASS = new PandaPrefixedStringField(
			"Message.Protections.Player-with-kick-bypass", "&cYou can't kick this player due his permissions.");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_PLAYERKICKED = new PandaPrefixedStringField(
			"Message.Protections.Player-kicked", "&aThe player has been kicked from this protection successfully!");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_LEFTSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Left-successfully", "&aYou have left the protection successfully!");

	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_MEMBERS_ADDEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Members.Added-successfully",
			"&aThe new member has been added to the protection successfully!");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_MEMBERS_REMOVEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Members.Removed-successfully",
			"&aThe member has been removed to the protection successfully!");

	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_OWNERS_ADDEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Owners.Added-successfully",
			"&aThe new owner has been added to the protection successfully!");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_OWNERS_REMOVEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Owners.Removed-successfully",
			"&aThe owner has been removed to the protection successfully!");

	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_BANNEDS_ADDEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Banneds.Added-successfully",
			"&aThe player has been banned from the protection successfully!");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_BANNEDS_REMOVEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Banneds.Removed-successfully",
			"&aThe player has been unbanned from the protection successfully!");

	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_BLOCKS_CREATEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Blocks.Created-successfully", "&aThe protection block has been created successfully!");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_BLOCKS_SAVEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Blocks.Saved-successfully",
			"&aThe protection block changes has been saved successfully!");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_BLOCKS_REMOVEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Protections.Blocks.Removed-successfully", "&aThe protection block has been removed successfully!");

	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_FLAGS_ALLOWVALUENAME = new PandaPrefixedStringField(
			"Message.Protections.Flags.Allow-value-name", "Inventory.Protection.Flags.Allow-value-name", "&aAllow");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_FLAGS_DENYVALUENAME = new PandaPrefixedStringField(
			"Message.Protections.Flags.Deny-value-name", "Inventory.Protection.Flags.Deny-value-name", "&cDeny");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_FLAGS_STRINGVALUENAME = new PandaPrefixedStringField(
			"Message.Protections.Flags.String-value-name", "Inventory.Protection.Flags.String-value-name", "&b{text}");
	public static final PandaPrefixedStringField MESSAGE_PROTECTIONS_FLAGS_NOTDEFINEDNAME = new PandaPrefixedStringField(
			"Message.Protections.Flags.Not-defined-name", "Inventory.Protection.Flags.Not-defined-name",
			"&7Not defined");

	public static final PandaPrefixedStringField MESSAGE_TRANSFER_AVAILABLELIST = new PandaPrefixedStringField(
			"Message.Transfer.Available-list", "&7Available protection plugins to transfer from: &f{list}");
	public static final PandaPrefixedStringField MESSAGE_TRANSFER_PROTECTIONSTONESNOTLOADED = new PandaPrefixedStringField(
			"Message.Transfer.Protection-stones-not-loaded",
			"&cProtectionStones is not loaded. Please load ProtectionStones to transfer its data.");
	public static final PandaPrefixedStringField MESSAGE_TRANSFER_WARNING = new PandaPrefixedStringField(
			"Message.Transfer.Warning",
			"&7You're about to transfer all the data from another plugin to RoyaleProtectionBlocks. Once it starts, it's not recomended to stop the server. This process may remove the old regions and lead to losing some in case their transfer fails. Remember to keep a backup of the WorldGuard and desired plugin folder in case something goes wrong. Make sure to remove the plugin you want to transfer from once the transfer finishes, as it can lead to unexpected behaviors if there are two protection plugins. Please, type &e{command} &7to confirm your choice.");
	public static final PandaPrefixedStringField MESSAGE_TRANSFER_START = new PandaPrefixedStringField(
			"Message.Transfer.Start", "&7Starting transfer process...");
	public static final PandaPrefixedStringField MESSAGE_TRANSFER_END = new PandaPrefixedStringField(
			"Message.Transfer.End",
			"&7Transfer finished! Remember to check the results and, if everything went okey, remove the old protection plugin.");
	public static final PandaStringListField MESSAGE_TRANSFER_RESULT = new PandaStringListField(
			"Message.Transfer.Result",
			Arrays.asList("", "&eTransfer result: ", "",
					"&e  - &aTransferred protection blocks: {transferred_protection_blocks}",
					"&e  - &cFailed protection blocks: {failed_protection_blocks}", "",
					"&e  - &aTransferred protections: {transferred_protections}",
					"&e  - &cFailed protections: {failed_protections}", "", "&e  - &aRequired time: {required_time} ms",
					"&e  - &cErrors in console: {errors_in_console}", ""));

	public static final PandaPrefixedStringField MESSAGE_PURGE_WARNING = new PandaPrefixedStringField(
			"Message.Purge.Warning",
			"&7You're about to delete a total amount of &e{amount} &7protection(s) depending on the player's last playtime, some protections may be lost if any error occurs. If there may be some important protections you want to prevent from being removed, remember to make a backup just in case. Please, type &e{command} &7to confirm your choice.");
	public static final PandaPrefixedStringField MESSAGE_PURGE_SEARCH = new PandaPrefixedStringField(
			"Message.Purge.Search", "&7Searching matching protections...");
	public static final PandaPrefixedStringField MESSAGE_PURGE_START = new PandaPrefixedStringField(
			"Message.Purge.Start", "&7Starting purge process...");
	public static final PandaPrefixedStringField MESSAGE_PURGE_END = new PandaPrefixedStringField("Message.Purge.End",
			"&aPurge finished! A total amount of &e{amount} &aprotection(s) has been purged.");
	public static final PandaPrefixedStringField MESSAGE_PURGE_ERROR = new PandaPrefixedStringField(
			"Message.Purge.Error",
			"&7A total amount of &e{amount} &7protection(s) couldn't be purged. Check the console to see the related errors.");
	public static final PandaPrefixedStringField MESSAGE_PURGE_EXPORTEND = new PandaPrefixedStringField(
			"Message.Purge.Export-end",
			"&aExportation finished! A total amount of &e{amount} &aprotection(s) has been found and exported to a new JSON file called '&e{file}' on your plugin's directory.");

	public static final PandaPrefixedStringField MESSAGE_FILES_EXPORTEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Files.Exported-successfully", "All the data has been exported successfully!");
	public static final PandaPrefixedStringField MESSAGE_FILES_IMPORTEDSUCCESSFULLY = new PandaPrefixedStringField(
			"Message.Files.Imported-successfully", "All the data has been imported successfully!");

	// Errors
	public static final PandaPrefixedStringField ERROR_ERROR = new PandaPrefixedStringField("Error.Error", "&cError!");
	public static final PandaPrefixedStringField ERROR_CONSOLEDENIED = new PandaPrefixedStringField(
			"Error.Console-denied", "&cYou can't use this commands in console!");
	public static final PandaPrefixedStringField ERROR_PLAYERNOTFOUND = new PandaPrefixedStringField(
			"Error.Player-not-found", "&cThe specified player couldn't be found!");
	public static final PandaPrefixedStringField ERROR_NOITEMINHAND = new PandaPrefixedStringField(
			"Error.No-item-in-hand", "&cYou must have an item in your hand!");
	public static final PandaPrefixedStringField ERROR_NOTABLOCK = new PandaPrefixedStringField("Error.Not-a-block",
			"&cThe item must be a block!");
	public static final PandaPrefixedStringField ERROR_INVENTORYFULL = new PandaPrefixedStringField(
			"Error.Inventory-full", "&cYou don't have enough space on your inventory!");
	public static final PandaPrefixedStringField ERROR_INSUFFICIENTBALANCE = new PandaPrefixedStringField(
			"Error.Insufficient-balance", "&cYou don't have enough balance to purchase this!");
	public static final PandaPrefixedStringField ERROR_INVALIDNUMBER = new PandaPrefixedStringField(
			"Error.Invalid-number", "&cYou must specify a valid number!");
	public static final PandaPrefixedStringField ERROR_NUMBERBELOWZERO = new PandaPrefixedStringField(
			"Error.Number-below-zero", "&cYou must specify a number higher than or equal to zero!");
	public static final PandaPrefixedStringField ERROR_SETSPAWNERROR = new PandaPrefixedStringField(
			"Error.Set-spawn-error", "&cThere was an error trying to specify a new spawn. Check the console!");

	public static final PandaPrefixedStringField ERROR_CHATPROMPT_ALREADYPROMPTED = new PandaPrefixedStringField(
			"Error.Chat-prompt.Already-prompted", "&cYou've already a pending type operation!");

	public static final PandaPrefixedStringField ERROR_FILES_EXPORT = new PandaPrefixedStringField("Error.Files.Export",
			"&cThere was an issue trying to export the data! Check the console for more information.");
	public static final PandaPrefixedStringField ERROR_FILES_IMPORT = new PandaPrefixedStringField("Error.Files.Import",
			"&cThere was an issue trying to import the data! Check the console for more information.");

	public static final PandaPrefixedStringField ERROR_PURGE_INVALIDPARAMETER = new PandaPrefixedStringField(
			"Error.Purge.Invalid-parameter",
			"&cYou must specify a valid parameter! (Available: --days, --hours, --minutes, --config, --show-ignored-players, --export-only)");
	public static final PandaPrefixedStringField ERROR_PURGE_NOVALUEFORPARAMETER = new PandaPrefixedStringField(
			"Error.Purge.No-value-for-parameter", "&cYou must specify a value for the parameter!");

	public static final PandaPrefixedStringField ERROR_PROTECTIONS_NOTFOUND = new PandaPrefixedStringField(
			"Error.Protections.Not-found", "&cThe specified protection could not be found!");
	public static final PandaPrefixedStringField ERROR_PROTECTIONS_NOTINSIDEPROTECTION = new PandaPrefixedStringField(
			"Error.Protections.Not-inside-protection", "&cYou're not inside any protection!");
	public static final PandaPrefixedStringField ERROR_PROTECTIONS_BANNEDWORLD = new PandaPrefixedStringField(
			"Error.Protections.Banned-world", "&cYou can't use protection blocks in this world!");
	public static final PandaPrefixedStringField ERROR_PROTECTIONS_NOTALLOWEDWORLD = new PandaPrefixedStringField(
			"Error.Protections.Not-allowed-world", "&cYou can't use this protection block in this world!");
	public static final PandaPrefixedStringField ERROR_PROTECTIONS_NOTMAINOWNER = new PandaPrefixedStringField(
			"Error.Protections.Not-main-owner", "&cYou're not the main owner of this protection!");
	public static final PandaPrefixedStringField ERROR_PROTECTIONS_NOTOWNER = new PandaPrefixedStringField(
			"Error.Protections.Not-owner", "&cYou're not an owner of this protection!");
	public static final PandaPrefixedStringField ERROR_PROTECTIONS_NOTMEMBER = new PandaPrefixedStringField(
			"Error.Protections.Not-member", "&cYou're not a member of this protection!");
	public static final PandaPrefixedStringField ERROR_PROTECTIONS_PERMISSIONDENIED = new PandaPrefixedStringField(
			"Error.Protections.Permission-denied", "&cYou don't have permissions to use this protection block!");
	public static final PandaPrefixedStringField ERROR_PROTECTIONS_BLOCKALREADYHIDDEN = new PandaPrefixedStringField(
			"Error.Protections.Block-already-hidden", "&cThe protection block is already hidden!");
	public static final PandaPrefixedStringField ERROR_PROTECTIONS_BLOCKALREADYSHOWN = new PandaPrefixedStringField(
			"Error.Protections.Block-already-shown", "&cThe protection block is already shown!");
	public static final PandaPrefixedStringField ERROR_PROTECTIONS_BLOCKEDBYBLOCK = new PandaPrefixedStringField(
			"Error.Protections.Blocked-by-block",
			"&cThere's another block where the protection block should be placed!");
	public static final PandaPrefixedStringField ERROR_PROTECTIONS_SAMEITEMASPROTECTION = new PandaPrefixedStringField(
			"Error.Protections.Same-item-as-protection",
			"&cYou can't place items of the same type as the protection block where the protection block is originally placed!");
	public static final PandaPrefixedStringField ERROR_PROTECTIONS_LEAVEDENIEDTOMAINOWNER = new PandaPrefixedStringField(
			"Error.Protections.Leave-denied-to-main-owner", "&cYou can't leave your own protection!");

	public static final PandaPrefixedStringField ERROR_PROTECTIONS_BLOCKS_NOTFOUND = new PandaPrefixedStringField(
			"Error.Protections.Blocks.Not-found", "&cThere's no protection block with this ID!");
	public static final PandaPrefixedStringField ERROR_PROTECTIONS_BLOCKS_NOTFORSALE = new PandaPrefixedStringField(
			"Error.Protections.Blocks.Not-for-sale", "&cThis protection block is currently not for sale!");

}

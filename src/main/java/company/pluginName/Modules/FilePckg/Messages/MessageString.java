package company.pluginName.Modules.FilePckg.Messages;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import relampagorojo93.LibsCollection.SpigotPlugin.Defaults.FileModuleObjects.FileObjFieldsEnum;

@RequiredArgsConstructor
public enum MessageString implements FileObjFieldsEnum<String> {
	PREFIX("Prefix", "&d&lRoyale&fProtectionBlocks >>&r&7 "),

	// Messages
	MESSAGE_RELOAD("Message.Reload", "&aThe plugin has been reloaded successfully."),

	MESSAGE_PROTECTIONS_CREATEDSUCCESSFULLY("Message.Protections.Created-successfully",
			"&aThe protection has been created successfully!"),
	MESSAGE_PROTECTIONS_REMOVEDSUCCESSFULLY("Message.Protections.Removed-successfully",
			"&aThe protection has been removed successfully!"),
	MESSAGE_PROTECTIONS_HOMESETSUCCESSFULLY("Message.Protections.Home-set-successfully",
			"&aThe protection's home has been set successfully!"),
	MESSAGE_PROTECTIONS_RENAMEDSUCCESSFULLY("Message.Protections.Renamed-successfully",
			"&aThe protection's name has been changed successfully!"),
	MESSAGE_PROTECTIONS_HIDDENSUCCESSFULLY("Message.Protections.Hidden-successfully",
			"&aThe protection block has been hidden successfully!"),
	MESSAGE_PROTECTIONS_SHOWNSUCCESSFULLY("Message.Protections.Shown-successfully",
			"&aThe protection block has been shown successfully!"),

	MESSAGE_PROTECTIONS_MEMBERS_ADDEDSUCCESSFULLY("Message.Protections.Members.Added-successfully",
			"&aThe new member has been added to the protection successfully!"),
	MESSAGE_PROTECTIONS_MEMBERS_REMOVEDSUCCESSFULLY("Message.Protections.Members.Removed-successfully",
			"&aThe member has been removed to the protection successfully!"),

	MESSAGE_PROTECTIONS_OWNERS_ADDEDSUCCESSFULLY("Message.Protections.Owners.Added-successfully",
			"&aThe new owner has been added to the protection successfully!"),
	MESSAGE_PROTECTIONS_OWNERS_REMOVEDSUCCESSFULLY("Message.Protections.Owners.Removed-successfully",
			"&aThe owner has been removed to the protection successfully!"),

	MESSAGE_PROTECTIONS_BLOCKS_CREATEDSUCCESSFULLY("Message.Protections.Blocks.Created-successfully",
			"&aThe protection block has been created successfully!"),
	MESSAGE_PROTECTIONS_BLOCKS_SAVEDSUCCESSFULLY("Message.Protections.Blocks.Saved-successfully",
			"&aThe protection block changes has been saved successfully!"),
	MESSAGE_PROTECTIONS_BLOCKS_REMOVEDSUCCESSFULLY("Message.Protections.Blocks.Removed-successfully",
			"&aThe protection block has been removed successfully!"),

	MESSAGE_HELPER_AVAILABLELEFTARROW("Message.Helper.Available-left-arrow", "&e«"),
	MESSAGE_HELPER_UNAVAILABLELEFTARROW("Message.Helper.Unavailable-left-arrow", "&r«"),
	MESSAGE_HELPER_AVAILABLERIGHTARROW("Message.Helper.Available-right-arrow", "&e»"),
	MESSAGE_HELPER_UNAVAILABLERIGHTARROW("Message.Helper.Unavailable-right-arrow", "&r»"),

	// Inventory general items
	INVENTORY_GENERAL_CLOSENAME("Inventory.General.Close-name", "&cClose"),
	INVENTORY_GENERAL_CONFIRMNAME("Inventory.General.Confirm-name", "&aConfirm"),
	INVENTORY_GENERAL_CANCELNAME("Inventory.General.Cancel-name", "&cCancel"),
	INVENTORY_GENERAL_PREVIOUSPAGENAME("Inventory.General.Previous-name", "&ePrevious"),
	INVENTORY_GENERAL_NEXTPAGENAME("Inventory.General.Next-name", "&eNext"),

	// Inventory confirm items
	INVENTORY_CONFIRM_TITLE("Inventory.Confirm.Title", "Are you sure?"),
	INVENTORY_CONFIRM_YESNAME("Inventory.Confirm.Yes-name", "&aYes"),
	INVENTORY_CONFIRM_NONAME("Inventory.Confirm.No-name", "&cNo"),

	// Inventory search player items
	INVENTORY_SEARCHPLAYERS_TITLE("Inventory.Search-players.Title", "Search a player"),
	INVENTORY_SEARCHPLAYERS_PLAYERNAME("Inventory.Search-players.Player-name", "&e{player}"),
	INVENTORY_SEARCHPLAYERS_SEARCHSPECIFICPLAYERNAME("Inventory.Search-players.Search-specific-player-name",
			"&eSearch an specific player"),
	INVENTORY_SEARCHPLAYERS_SEARCHSPECIFICPLAYERINFO("Inventory.Search-players.Search-specific-player-Info",
			"&7Type the name of the player you're looking for. Type 'cancel' to cancel this action."),

	// Inventory protection items
	INVENTORY_PROTECTION_TITLE("Inventory.Protection.Title", "{protection}"),
	INVENTORY_PROTECTION_PROTECTIONINFONAME("Inventory.Protection.Protection-info-name", "&e{owner}"),
	INVENTORY_PROTECTION_OWNERSNAME("Inventory.Protection.Owners-name", "&eOwners"),
	INVENTORY_PROTECTION_MEMBERSNAME("Inventory.Protection.Members-name", "&eMembers"),
	INVENTORY_PROTECTION_FLAGSNAME("Inventory.Protection.Flags-name", "&eFlags"),
	INVENTORY_PROTECTION_RENAMENAME("Inventory.Protection.Rename-name", "&eRename protection"),
	INVENTORY_PROTECTION_SHOWBLOCKNAME("Inventory.Protection.Show-block-name", "&eShow the protection block"),
	INVENTORY_PROTECTION_HIDEBLOCKNAME("Inventory.Protection.Hide-block-name", "&eHide the protection block"),
	INVENTORY_PROTECTION_SHOWBOUNDARIESNAME("Inventory.Protection.Show-boundaries-name",
			"&eShow the protection boundaries"),
	INVENTORY_PROTECTION_HIDEBOUNDARIESNAME("Inventory.Protection.Hide-boundaries-name",
			"&eHide the protection boundaries"),
	INVENTORY_PROTECTION_TYPENEWNAMEINFO("Inventory.Protection.Type-new-name-Info",
			"&7Type the new name for the protection. Type 'cancel' to cancel this action."),
	INVENTORY_PROTECTION_DELETEPROTECTIONNAME("Inventory.Protection.Delete-protection-name", "&cDelete protection"),

	// Inventory protection list items
	INVENTORY_PROTECTION_LIST_TITLE("Inventory.Protection.List.Title", "Protections"),
	INVENTORY_PROTECTION_LIST_PROTECTIONNAME("Inventory.Protection.List.Protection-name", "&e{protection}"),
	INVENTORY_PROTECTION_LIST_PROTECTIONTELEPORTNOHOMELORELINE(
			"Inventory.Protection.List.Protection-teleport-no-home-lore-line",
			" &8- &a&mLeft click&r&a: &7No home set yet"),
	INVENTORY_PROTECTION_LIST_PROTECTIONTELEPORTLORELINE("Inventory.Protection.List.Protection-teleport-lore-line",
			" &8- &aLeft click: &eTeleport to protection home"),
	INVENTORY_PROTECTION_LIST_PROTECTIONEDITLORELINE("Inventory.Protection.List.Protection-edit-lore-line",
			" &8- &aRight click: &eManage protection"),

	// Inventory protection flags items
	INVENTORY_PROTECTION_FLAGS_TITLE("Inventory.Protection.Flags.Title", "{protection} > Flags"),
	INVENTORY_PROTECTION_FLAGS_FLAGNAME("Inventory.Protection.Flags.Flag-name", "&e{flag} &8[&7{value}&8]"),
	INVENTORY_PROTECTION_FLAGS_ALLOWVALUENAME("Inventory.Protection.Flags.Allow-value-name", "&aAllow"),
	INVENTORY_PROTECTION_FLAGS_DENYVALUENAME("Inventory.Protection.Flags.Deny-value-name", "&cDeny"),
	INVENTORY_PROTECTION_FLAGS_STRINGVALUENAME("Inventory.Protection.Flags.String-value-name", "&b{text}"),
	INVENTORY_PROTECTION_FLAGS_STRINGSPECIFYINFO("Inventory.Protection.Flags.String-specify-info",
			"&7Type the text you want to specify in this flag. Type 'cancel' to cancel this action."),

	// Inventory protection members items
	INVENTORY_PROTECTION_MEMBERS_TITLE("Inventory.Protection.Members.Title", "{protection} > Members"),
	INVENTORY_PROTECTION_MEMBERS_MEMBERNAME("Inventory.Protection.Members.Member-name", "&e{player}"),
	INVENTORY_PROTECTION_MEMBERS_SEARCHMEMBERNAME("Inventory.Protection.Members.Search-member-name",
			"&eSearch for a new member"),
	INVENTORY_PROTECTION_MEMBERS_REMOVEMEMBERLORELINE("Inventory.Protection.Members.Remove-member-lore-line",
			"&8[&cClick to remove member&8]"),

	// Inventory protection owners items
	INVENTORY_PROTECTION_OWNERS_TITLE("Inventory.Protection.Owners.Title", "{protection} > Owners"),
	INVENTORY_PROTECTION_OWNERS_OWNERNAME("Inventory.Protection.Owners.Owner-name", "&e{player}"),
	INVENTORY_PROTECTION_OWNERS_SEARCHOWNERNAME("Inventory.Protection.Owners.Search-owner-name",
			"&eSearch for a new owner"),
	INVENTORY_PROTECTION_OWNERS_REMOVEOWNERLORELINE("Inventory.Protection.Owners.Remove-owner-lore-line",
			"&8[&cClick to remove owner&8]"),

	// Inventory protection list items
	INVENTORY_PROTECTIONBLOCKS_LIST_TITLE("Inventory.Protection-blocks.List.Title", "Protection Blocks"),
	INVENTORY_PROTECTIONBLOCKS_LIST_CREATEBLOCKITEM("Inventory.Protection-blocks.List.Create-block-item",
			"&eCreate a new protection block"),
	INVENTORY_PROTECTIONBLOCKS_LIST_BLOCKCOPYLORELINE("Inventory.Protection-blocks.List.Block-copy-lore-line",
			" &8&o- &aLeft click: &eGet a copy"),
	INVENTORY_PROTECTIONBLOCKS_LIST_BLOCKEDITLORELINE("Inventory.Protection-blocks.List.Block-edit-lore-line",
			" &8&o- &aLeft click + Shift: &eEdit the block"),
	INVENTORY_PROTECTIONBLOCKS_LIST_BLOCKREMOVELORELINE("Inventory.Protection-blocks.List.Block-remove-lore-line",
			" &8&o- &aRight click: &eRemove the block"),

	// Inventory protection list items
	INVENTORY_PROTECTIONBLOCKS_MANAGE_TITLE("Inventory.Protection-blocks.Manage.Title", "Protection Blocks > {block}"),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_ITEMNOTSETNAME("Inventory.Protection-blocks.Manage.Item-not-set-name",
			"&7&oNot set yet"),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_BLOCKSXNAME("Inventory.Protection-blocks.Manage.Blocks-x-name",
			"&aCurrent blocks on X: &e{blocks_x}"),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_BLOCKSYNAME("Inventory.Protection-blocks.Manage.Blocks-y-name",
			"&aCurrent blocks on Y: &e{blocks_y}"),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_BLOCKSZNAME("Inventory.Protection-blocks.Manage.Blocks-z-name",
			"&aCurrent blocks on Z: &e{blocks_z}"),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_IDNAME("Inventory.Protection-blocks.Manage.Id-name",
			"&aCurrent ID: &e{block_id}"),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_IDNOTSETNAME("Inventory.Protection-blocks.Manage.Id-not-set-name",
			"&aCurrent ID: &7&oNot set yet"),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_IDNOTMODIFIABLENAME("Inventory.Protection-blocks.Manage.Id-not-modifiable-name",
			"&aCurrent ID: &e{block_id} &7&o[Not modifiable]"),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_PERMISSIONNAME("Inventory.Protection-blocks.Manage.Permission-name",
			"&aCurrent permission: &e{block_permission}"),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_PERMISSIONNOTSETNAME("Inventory.Protection-blocks.Manage.Permission-not-set-name",
			"&aCurrent permission: &7&oNot set yet"),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_IDSPECIFYINFO("Inventory.Protection-blocks.Manage.Id-specify-info",
			"&7Type the ID you wish to set on your protection block. Type 'cancel' to cancel this action."),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_PERMISSIONSPECIFYINFO(
			"Inventory.Protection-blocks.Manage.Permission-specify-info",
			"&7Type the permission you wish to set on your protection block. Type 'cancel' to cancel this action."),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_BLOCKSSPECIFYINFO("Inventory.Protection-blocks.Manage.Blocks-specify-info",
			"&7Type the amount of blocks you wish to set on your protection block. Type 'cancel' to cancel this action."),

	// Errors
	ERROR_ERROR("Error.Error", "&cError!"),
	ERROR_CONSOLEDENIED("Error.Console-denied", "&cYou can't use this commands in console!"),
	ERROR_PLAYERNOTFOUND("Error.Player-not-found", "&cThe specified player couldn't be found!"),
	ERROR_NOITEMINHAND("Error.No-item-in-hand", "&cYou must have an item in your hand!"),
	ERROR_NOTABLOCK("Error.Not-a-block", "&cThe item must be a block!"),
	ERROR_INVALIDNUMBER("Error.Invalid-number", "&cYou must specify a valid number!"),
	ERROR_NUMBERBELOWZERO("Error.Number-below-zero", "&cYou must specify a number higher than or equal to zero!"),

	ERROR_CHATPROMPT_ALREADYPROMPTED("Error.Chat-prompt.Already-prompted",
			"&cYou've already a pending type operation!"),

	ERROR_PROTECTIONS_NOTINSIDEPROTECTION("Error.Protections.Not-inside-protection",
			"&cYou're not inside any protection!"),
	ERROR_PROTECTIONS_BANNEDWORLD("Error.Protections.Banned-world", "&cYou can't use protection blocks in this world!"),
	ERROR_PROTECTIONS_NOTMAINOWNER("Error.Protections.Not-main-owner",
			"&cYou're not the main owner of this protection!"),
	ERROR_PROTECTIONS_PERMISSIONDENIED("Error.Protections.Permission-denied",
			"&cYou don't have permissions to use this protection block!"),
	ERROR_PROTECTIONS_BLOCKALREADYHIDDEN("Error.Protections.Block-already-hidden",
			"&cThe protection block is already hidden!"),
	ERROR_PROTECTIONS_BLOCKALREADYSHOWN("Error.Protections.Block-already-shown",
			"&cThe protection block is already shown!"),
	ERROR_PROTECTIONS_BLOCKEDBYBLOCK("Error.Protections.Blocked-by-block",
			"&cThere's another block where the protection block should be placed!"),
	ERROR_PROTECTIONS_ORAXENINCOMPATIBILITY("Error.Protections.Oraxen-incompatibility",
			"&cYou can't do this with this kind of protection blocks!"),
	ERROR_PROTECTIONS_SAMEITEMASPROTECTION("Error.Protections.Same-item-as-protection",
			"&cYou can't place items of the same type as the protection block where the protection block is originally placed!"),

	ERROR_PROTECTIONS_BLOCKS_NOTFOUND("Error.Protections.Blocks.Not-found",
			"&cThere's no protection block with this ID!"),
	ERROR_PROTECTIONS_BLOCKS_ITEMTYPESWAPNOTALLOWED("Error.Protections.Blocks.Item-type-swap-not-allowed",
			"&cYou can't switch the item type of a registered protection block!"),

	// Exceptions
	ERROR_EXCEPTION_PROTECTION_DELETE_PERMISSIONDENIED("Error.Exception.protection.delete.PermissionDenied",
			"&cYou aren't allowed to delete this protection!"),
	ERROR_EXCEPTION_PROTECTION_DELETE_SQL("Error.Exception.protection.delete.SQL",
			"&cThere was an error trying to delete this protection in the database. Please contact with an administrator!"),
	ERROR_EXCEPTION_PROTECTION_DELETE_UNKNOWN("Error.Exception.protection.delete.Unknown",
			"&cAn unknown error happened while trying to delete this protection. Please contact with an administrator!"),

	ERROR_EXCEPTION_PROTECTION_SAVE_DENIED("Error.Exception.protection.save.PermissionDenied",
			"&cYou aren't allowed to save this protection!"),
	ERROR_EXCEPTION_PROTECTION_SAVE_MAXREACHED("Error.Exception.protection.save.MaxReached",
			"&cYou've reached the maximum amount of protections allowed!"),
	ERROR_EXCEPTION_PROTECTION_SAVE_NAMEINUSE("Error.Exception.protection.save.NameInUse",
			"&cThere's already a protection using this name!"),
	ERROR_EXCEPTION_PROTECTION_SAVE_OVERLAPS("Error.Exception.protection.save.Overlaps",
			"&cYou can't put protections inside other existing ones!"),
	ERROR_EXCEPTION_PROTECTION_SAVE_ALREADYOCCUPIED("Error.Exception.protection.save.AlreadyOccupied",
			"&cThere's already a protection placed on this location!"),
	ERROR_EXCEPTION_PROTECTION_SAVE_RENAMEDENIED("Error.Exception.protection.save.RenamePermissionDenied",
			"&cYou aren't allowed to rename this protection!"),
	ERROR_EXCEPTION_PROTECTION_SAVE_SQL("Error.Exception.protection.save.SQL",
			"&cThere was an error trying to save this protection in the database. Please contact with an administrator!"),
	ERROR_EXCEPTION_PROTECTION_SAVE_UNKNOWN("Error.Exception.protection.save.Unknown",
			"&cAn unknown error happened while trying to save this protection. Please contact with an administrator!"),
	ERROR_EXCEPTION_PROTECTION_BLOCKS_SAVE_NOVISIBLETEXT("Error.Exception.protection.save.NoVisibleText",
			"&cThe text you're typing must be visible!"),

	ERROR_EXCEPTION_PROTECTION_MEMBERS_DELETE_PERMISSIONDENIED(
			"Error.Exception.protection.members.delete.PermissionDenied",
			"&cYou aren't allowed to remove this member!"),
	ERROR_EXCEPTION_PROTECTION_MEMBERS_DELETE_UNKNOWN("Error.Exception.protection.members.delete.Unknown",
			"&cAn unknown error happened while trying to remove this member. Please contact with an administrator!"),

	ERROR_EXCEPTION_PROTECTION_MEMBERS_SAVE_CANNOTADDPROTECTIONOWNER(
			"Error.Exception.protection.members.save.CannotAddProtectionOwner",
			"&cYou can't add the owner of this protection as a member!"),
	ERROR_EXCEPTION_PROTECTION_MEMBERS_SAVE_CANNOTADDYOURSELF(
			"Error.Exception.protection.members.save.CannotAddYourself", "&cYou can't add yourself as a member!"),
	ERROR_EXCEPTION_PROTECTION_MEMBERS_SAVE_PERMISSIONDENIED("Error.Exception.protection.members.save.PermissionDenied",
			"&cYou aren't allowed to add members in this protection!"),
	ERROR_EXCEPTION_PROTECTION_MEMBERS_SAVE_UNKNOWN("Error.Exception.protection.members.save.Unknown",
			"&cAn unknown error happened while trying to add this member. Please contact with an administrator!"),

	ERROR_EXCEPTION_PROTECTION_OWNERS_DELETE_CANNOTDELETEPROTECTIONOWNER(
			"Error.Exception.protection.owners.delete.CannotDeleteProtectionOwner",
			"&cYou can't remove the owner of this protection from the owners list!"),
	ERROR_EXCEPTION_PROTECTION_OWNERS_DELETE_PERMISSIONDENIED(
			"Error.Exception.protection.owners.delete.PermissionDenied", "&cYou aren't allowed to remove this owner!"),
	ERROR_EXCEPTION_PROTECTION_OWNERS_DELETE_UNKNOWN("Error.Exception.protection.owners.delete.Unknown",
			"&cAn unknown error happened while trying to remove this owner. Please contact with an administrator!"),

	ERROR_EXCEPTION_PROTECTION_OWNERS_SAVE_CANNOTADDPROTECTIONOWNER(
			"Error.Exception.protection.owners.save.CannotAddProtectionOwner",
			"&cYou can't add the owner of this protection as an owner!"),
	ERROR_EXCEPTION_PROTECTION_OWNERS_SAVE_CANNOTADDYOURSELF("Error.Exception.protection.owners.save.CannotAddYourself",
			"&cYou can't add yourself as an owner!"),
	ERROR_EXCEPTION_PROTECTION_OWNERS_SAVE_PERMISSIONDENIED("Error.Exception.protection.owners.save.PermissionDenied",
			"&cYou aren't allowed to add owners in this protection!"),
	ERROR_EXCEPTION_PROTECTION_OWNERS_SAVE_UNKNOWN("Error.Exception.protection.owners.save.Unknown",
			"&cAn unknown error happened while trying to add this owner. Please contact with an administrator!"),

	ERROR_EXCEPTION_PROTECTION_BLOCKS_DELETE_PERMISSIONDENIED(
			"Error.Exception.protection.blocks.delete.PermissionDenied",
			"&cYou aren't allowed to remove this protection block!"),
	ERROR_EXCEPTION_PROTECTION_BLOCKS_DELETE_SQL("Error.Exception.protection.blocks.delete.SQL",
			"&cThere was an error trying to delete this protection block from the database. Please contact with an administrator!"),
	ERROR_EXCEPTION_PROTECTION_BLOCKS_DELETE_UNKNOWN("Error.Exception.protection.blocks.delete.Unknown",
			"&cAn unknown error happened while trying to remove this protection block. Please contact with an administrator!"),

	ERROR_EXCEPTION_PROTECTION_BLOCKS_SAVE_DENIED("Error.Exception.protection.blocks.save.PermissionDenied",
			"&cYou aren't allowed to save this protection block!"),
	ERROR_EXCEPTION_PROTECTION_BLOCKS_SAVE_IDINUSE("Error.Exception.protection.blocks.save.IdInUse",
			"&cThere's already a protection block using this ID!"),
	ERROR_EXCEPTION_PROTECTION_BLOCKS_SAVE_IDNULL("Error.Exception.protection.blocks.save.IdNull",
			"&cThere's no ID specified for this protection block!"),
	ERROR_EXCEPTION_PROTECTION_BLOCKS_SAVE_ITEMNULL("Error.Exception.protection.blocks.save.ItemNull",
			"&cThere's no item specified for this protection block!"),
	ERROR_EXCEPTION_PROTECTION_BLOCKS_SAVE_ITEMNOTALLOWED("Error.Exception.protection.blocks.save.ItemNotAllowed",
			"&cYou can't use this kind of items as a protection block!"),
	ERROR_EXCEPTION_PROTECTION_BLOCKS_SAVE_SQL("Error.Exception.protection.blocks.save.SQL",
			"&cThere was an error trying to save this protection block in the database. Please contact with an administrator!"),
	ERROR_EXCEPTION_PROTECTION_BLOCKS_SAVE_UNKNOWN("Error.Exception.protection.blocks.save.Unknown",
			"&cAn unknown error happened while trying to save this protection block. Please contact with an administrator!");

	private @NonNull @Getter String path;
	private @NonNull @Getter String oldPath;
	private @NonNull @Getter String defaultContent;
	private @Setter String content;

	MessageString(String path, String defaultcontent) {
		this(path, path, defaultcontent);
	}

	@Override
	public String toString() {
		return String.valueOf(getContent());
	}

	@Override
	public String getContent() {
		return ((this.content != null) ? this.content : this.defaultContent).replace("&", "\u00A7");
	}

	@Override
	public Type getType() {
		return Type.STRING;
	}

	public String applyPrefix() {
		return MessageString.applyPrefix(this);
	}

	public static String applyPrefix(MessageString msg) {
		return PREFIX.getContent() + msg.getContent();
	}

	public static String applyPrefix(String msg) {
		return PREFIX.getContent() + msg;
	}

	public static String translate(String path) {
		for (MessageString message : MessageString.values()) {
			if (message.getPath().equalsIgnoreCase(path)) {
				return message.getContent();
			}
		}
		return path;
	}

}

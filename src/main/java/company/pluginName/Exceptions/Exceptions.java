package company.pluginName.Exceptions;

import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaFieldContainer;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaThrowableField;
import lombok.Getter;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;

@RegisteredPandaFieldContainer("lang")
public class Exceptions {

	public static class ThrowableField extends PandaThrowableField<RoyaleProtectionBlocksExceptionImpl> {

		private @Getter RoyaleProtectionBlocksException.Type exceptionType;

		public ThrowableField(RoyaleProtectionBlocksException.Type exceptionType, String path, String defaultContent) {
			super(path, defaultContent);
			this.exceptionType = exceptionType;
		}

		public ThrowableField(RoyaleProtectionBlocksException.Type exceptionType, String path, String oldPath,
				String defaultContent) {
			super(path, oldPath, defaultContent);
			this.exceptionType = exceptionType;
		}

		@Override
		public RoyaleProtectionBlocksExceptionImpl generateException(Throwable e) {
			return new RoyaleProtectionBlocksExceptionImpl(this, e);
		}

	}

	public static class Protections {

		public static final ThrowableField KICKDENIED = new ThrowableField(
				RoyaleProtectionBlocksException.Type.PROTECTIONS_KICKDENIED, "Error.Exception.protection.kick.Denied",
				"&cYou can't kick this player in this protection!");

		public static final ThrowableField NOTENOUGHBALANCE = new ThrowableField(
				RoyaleProtectionBlocksException.Type.PROTECTIONS_NOTENOUGHBALANCE,
				"Error.Exception.protection.NotEnoughBalance", "&cYou don't have enough balance for this!");

		public static final ThrowableField BLOCKED = new ThrowableField(
				RoyaleProtectionBlocksException.Type.PROTECTIONS_BLOCKED,
				"Error.Exception.protection.ProtectionBlocked", "&cYou can't do this while the protection is blocked!");

		public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
				RoyaleProtectionBlocksException.Type.PROTECTIONS_PERMISSIONDENIED,
				"Error.Exception.protection.PermissionDenied", "&cYou aren't allowed to do this in this protection!");

		public static final ThrowableField NOTFOUND = new ThrowableField(
				RoyaleProtectionBlocksException.Type.PROTECTIONS_NOTFOUND, "Error.Exception.protection.NotFound",
				"&cThe requested protection could not be found!");

		public static final ThrowableField INCOMBAT = new ThrowableField(
				RoyaleProtectionBlocksException.Type.PROTECTIONS_INCOMBAT, "Error.Exception.protection.InCombat",
				"&cYou can't do this while in combat!");

		public static class Delete {

			public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_DELETE_PERMISSIONDENIED,
					"Error.Exception.protection.delete.PermissionDenied",
					"&cYou aren't allowed to delete this protection!");
			public static final ThrowableField SQL = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_DELETE_SQL,
					"Error.Exception.protection.delete.SQL",
					"&cThere was an error trying to delete this protection in the database. Please contact with an administrator!");
			public static final ThrowableField UNKNOWN = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_DELETE_UNKNOWN,
					"Error.Exception.protection.delete.Unknown",
					"&cAn unknown error happened while trying to delete this protection. Please contact with an administrator!");
			public static final ThrowableField INPROGRESS = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_DELETE_INPROGRESS,
					"Error.Exception.protection.delete.InProgress",
					"&cThere's already a removal of a protection in this location in progress!");
			public static final ThrowableField VIEWACTIVE = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_DELETE_VIEWACTIVE,
					"Error.Exception.protection.delete.ViewActive",
					"&cTo remove the protection, first turn off the view!");
			public static final ThrowableField BLOCKSHOWN = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_DELETE_BLOCKSHOWN,
					"Error.Exception.protection.delete.BlockShown",
					"&cTo remove the protection, first hide the block!");
			public static final ThrowableField CANCELLED = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_DELETE_CANCELLED,
					"Error.Exception.protection.delete.Cancelled",
					"&cAnother plugin has cancelled the removal of this protection!");

		}

		public static class Save {

			public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_SAVE_PERMISSIONDENIED,
					"Error.Exception.protection.save.PermissionDenied",
					"&cYou aren't allowed to save this protection!");
			public static final ThrowableField MAXREACHED = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_SAVE_MAXREACHED,
					"Error.Exception.protection.save.MaxReached",
					"&cYou've reached the maximum amount of protections allowed!");
			public static final ThrowableField BLOCKMAXREACHED = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_SAVE_BLOCKMAXREACHED,
					"Error.Exception.protection.save.BlockMaxReached",
					"&cYou've reached the maximum amount of protections allowed with this protection block!");
			public static final ThrowableField NAMEINUSE = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_SAVE_NAMEINUSE,
					"Error.Exception.protection.save.NameInUse", "&cThere's already a protection using this name!");
			public static final ThrowableField OVERLAPS = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_SAVE_OVERLAPS,
					"Error.Exception.protection.save.Overlaps",
					"&cYou can't put protections inside other existing ones!");
			public static final ThrowableField OVERLAPSOFFSET = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_SAVE_OVERLAPSOFFSET,
					"Error.Exception.protection.save.OverlapsOffset",
					"&cYou can't put protections close to other protections which are &e%offset% blocks&c from here!");
			public static final ThrowableField OVERLAPSWORLDGUARD = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_SAVE_OVERLAPSWORLDGUARD,
					"Error.Exception.protection.save.OverlapsWorldguard",
					"&cYou can't put protections on protected areas!");
			public static final ThrowableField ALREADYOCCUPIED = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_SAVE_ALREADYOCCUPIED,
					"Error.Exception.protection.save.AlreadyOccupied",
					"&cThere's already a protection placed on this location!");
			public static final ThrowableField SQL = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_SAVE_SQL, "Error.Exception.protection.save.SQL",
					"&cThere was an error trying to save this protection in the database. Please contact with an administrator!");
			public static final ThrowableField UNKNOWN = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_SAVE_UNKNOWN,
					"Error.Exception.protection.save.Unknown",
					"&cAn unknown error happened while trying to save this protection. Please contact with an administrator!");
			public static final ThrowableField NOVISIBLETEXT = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_SAVE_NOVISIBLETEXT,
					"Error.Exception.protection.save.NoVisibleText", "&cThe text you're typing must be visible!");
			public static final ThrowableField INPROGRESS = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_SAVE_INPROGRESS,
					"Error.Exception.protection.save.InProgress",
					"&cThere's already a creation of a protection in this location in progress!");
			public static final ThrowableField CANCELLED = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_SAVE_CANCELLED,
					"Error.Exception.protection.save.Cancelled",
					"&cAnother plugin has cancelled the creation of this protection!");

		}

		public static class Teleport {
			public static final ThrowableField NOHOMESET = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_TELEPORT_NOHOMESET,
					"Error.Exception.protection.teleport.NoHomeSet", "&cThis protection does not have a home set!");
			public static final ThrowableField COOLDOWNACTIVE = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_TELEPORT_COOLDOWNACTIVE,
					"Error.Exception.protection.teleport.CooldownActive",
					"&cYou have to wait &e%seconds% seconds &cto teleport again!");
			public static final ThrowableField UNKNOWN = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_TELEPORT_UNKNOWN,
					"Error.Exception.protection.teleport.Unknown",
					"&cAn unknown error happened while trying to teleport. Please contact with an administrator!");
			public static final ThrowableField CANCELLEDDUEMOVEMENT = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_TELEPORT_CANCELLEDDUEMOVEMENT,
					"Error.Exception.protection.teleport.CancelledDueMovement",
					"&cThe teleportation has been cancelled due a detected movement!");
		}

		public static class Members {

			public static class Delete {

				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_MEMBERS_DELETE_PERMISSIONDENIED,
						"Error.Exception.protection.members.delete.PermissionDenied",
						"&cYou aren't allowed to remove this member!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_MEMBERS_DELETE_UNKNOWN,
						"Error.Exception.protection.members.delete.Unknown",
						"&cAn unknown error happened while trying to remove this member. Please contact with an administrator!");

			}

			public static class Save {

				public static final ThrowableField CANNOTADDPROTECTIONOWNER = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_MEMBERS_SAVE_CANNOTADDPROTECTIONOWNER,
						"Error.Exception.protection.members.save.CannotAddProtectionOwner",
						"&cYou can't add the owner of this protection as a member!");
				public static final ThrowableField CANNOTADDYOURSELF = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_MEMBERS_SAVE_CANNOTADDYOURSELF,
						"Error.Exception.protection.members.save.CannotAddYourself",
						"&cYou can't add yourself as a member!");
				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_MEMBERS_SAVE_PERMISSIONDENIED,
						"Error.Exception.protection.members.save.PermissionDenied",
						"&cYou aren't allowed to add members in this protection!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_MEMBERS_SAVE_UNKNOWN,
						"Error.Exception.protection.members.save.Unknown",
						"&cAn unknown error happened while trying to add this member. Please contact with an administrator!");

			}

		}

		public static class Owners {

			public static class Delete {

				public static final ThrowableField CANNOTDELETEPROTECTIONOWNER = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_OWNERS_DELETE_CANNOTDELETEPROTECTIONOWNER,
						"Error.Exception.protection.owners.delete.CannotDeleteProtectionOwner",
						"&cYou can't remove the owner of this protection from the owners list!");
				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_OWNERS_DELETE_PERMISSIONDENIED,
						"Error.Exception.protection.owners.delete.PermissionDenied",
						"&cYou aren't allowed to remove this owner!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_OWNERS_DELETE_UNKNOWN,
						"Error.Exception.protection.owners.delete.Unknown",
						"&cAn unknown error happened while trying to remove this owner. Please contact with an administrator!");

			}

			public static class Save {

				public static final ThrowableField CANNOTADDPROTECTIONOWNER = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_OWNERS_SAVE_CANNOTADDPROTECTIONOWNER,
						"Error.Exception.protection.owners.save.CannotAddProtectionOwner",
						"&cYou can't add the owner of this protection as an owner!");
				public static final ThrowableField CANNOTADDYOURSELF = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_OWNERS_SAVE_CANNOTADDYOURSELF,
						"Error.Exception.protection.owners.save.CannotAddYourself",
						"&cYou can't add yourself as an owner!");
				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_OWNERS_SAVE_PERMISSIONDENIED,
						"Error.Exception.protection.owners.save.PermissionDenied",
						"&cYou aren't allowed to add owners in this protection!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_OWNERS_SAVE_UNKNOWN,
						"Error.Exception.protection.owners.save.Unknown",
						"&cAn unknown error happened while trying to add this owner. Please contact with an administrator!");

			}

		}

		public static class Banneds {

			public static class Delete {

				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_BANNEDS_DELETE_PERMISSIONDENIED,
						"Error.Exception.protection.banneds.delete.PermissionDenied",
						"&cYou aren't allowed to remove this banned!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_BANNEDS_DELETE_UNKNOWN,
						"Error.Exception.protection.banneds.delete.Unknown",
						"&cAn unknown error happened while trying to remove this banned. Please contact with an administrator!");

			}

			public static class Save {

				public static final ThrowableField CANNOTADDPROTECTIONOWNER = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_BANNEDS_SAVE_CANNOTADDPROTECTIONOWNER,
						"Error.Exception.protection.banneds.save.CannotAddProtectionOwner",
						"&cYou can't add the owner of this protection as a banned!");
				public static final ThrowableField CANNOTADDYOURSELF = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_BANNEDS_SAVE_CANNOTADDYOURSELF,
						"Error.Exception.protection.banneds.save.CannotAddYourself",
						"&cYou can't add yourself as a banned!");
				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_BANNEDS_SAVE_PERMISSIONDENIED,
						"Error.Exception.protection.banneds.save.PermissionDenied",
						"&cYou aren't allowed to add banneds in this protection!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONS_BANNEDS_SAVE_UNKNOWN,
						"Error.Exception.protection.banneds.save.Unknown",
						"&cAn unknown error happened while trying to add this banned. Please contact with an administrator!");

			}

		}

		public static class Blocks {

			public static class Delete {

				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONBLOCKS_DELETE_PERMISSIONDENIED,
						"Error.Exception.protection.blocks.delete.PermissionDenied",
						"&cYou aren't allowed to remove this protection block!");
				public static final ThrowableField SQL = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONBLOCKS_DELETE_SQL,
						"Error.Exception.protection.blocks.delete.SQL",
						"&cThere was an error trying to delete this protection block from the database. Please contact with an administrator!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONBLOCKS_DELETE_UNKNOWN,
						"Error.Exception.protection.blocks.delete.Unknown",
						"&cAn unknown error happened while trying to remove this protection block. Please contact with an administrator!");

			}

			public static class Save {

				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONBLOCKS_SAVE_PERMISSIONDENIED,
						"Error.Exception.protection.blocks.save.PermissionDenied",
						"&cYou aren't allowed to save this protection block!");
				public static final ThrowableField IDINUSE = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONBLOCKS_SAVE_IDINUSE,
						"Error.Exception.protection.blocks.save.IdInUse",
						"&cThere's already a protection block using this ID!");
				public static final ThrowableField IDNULL = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONBLOCKS_SAVE_IDNULL,
						"Error.Exception.protection.blocks.save.IdNull",
						"&cThere's no ID specified for this protection block!");
				public static final ThrowableField ITEMNULL = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONBLOCKS_SAVE_ITEMNULL,
						"Error.Exception.protection.blocks.save.ItemNull",
						"&cThere's no item specified for this protection block!");
				public static final ThrowableField SQL = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONBLOCKS_SAVE_SQL,
						"Error.Exception.protection.blocks.save.SQL",
						"&cThere was an error trying to save this protection block in the database. Please contact with an administrator!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						RoyaleProtectionBlocksException.Type.PROTECTIONBLOCKS_SAVE_UNKNOWN,
						"Error.Exception.protection.blocks.save.Unknown",
						"&cAn unknown error happened while trying to save this protection block. Please contact with an administrator!");

			}

			public static final ThrowableField GENERATEITEM = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONBLOCKS_GENERATEITEM,
					"Error.Exception.protection.blocks.GenerateItem",
					"&cAn unknown error happened while trying to generate the item. Please contact with an administrator!");

		}

		public static class Flags {

			public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_FLAGS_PERMISSIONDENIED,
					"Error.Exception.protection.flags.PermissionDenied",
					"&cYou don't have permissions to modify this flag!");
			public static final ThrowableField NOTENOUGHBALANCE = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_NOTENOUGHBALANCE,
					"Error.Exception.protection.flags.NotEnoughBalance",
					"&cYou don't have enough balance to change this flag!");
			public static final ThrowableField UNKNOWN = new ThrowableField(
					RoyaleProtectionBlocksException.Type.PROTECTIONS_FLAGS_UNKNOWN,
					"Error.Exception.protection.flags.Unknown",
					"&cAn unknown error happened while trying to modify the flag. Please contact with an administrator!");

		}

	}

}

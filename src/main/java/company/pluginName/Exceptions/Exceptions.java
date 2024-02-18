package company.pluginName.Exceptions;

import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaFieldContainer;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaThrowableField;

@RegisteredPandaFieldContainer("lang")
public class Exceptions {

	public static class ThrowableField extends PandaThrowableField<RoyaleProtectionBlocksException> {

		public ThrowableField(String path, String defaultContent) {
			super(path, defaultContent);
		}

		public ThrowableField(String path, String oldPath, String defaultContent) {
			super(path, oldPath, defaultContent);
		}

		@Override
		public RoyaleProtectionBlocksException generateException(Exception e) {
			return new RoyaleProtectionBlocksException(this, e);
		}

	}

	public static class Protections {

		public static class Delete {

			public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
					"Error.Exception.protection.delete.PermissionDenied",
					"&cYou aren't allowed to delete this protection!");
			public static final ThrowableField NOTFOUND = new ThrowableField(
					"Error.Exception.protection.delete.NotFound",
					"&cThe region you're trying to delete could not be found!");
			public static final ThrowableField SQL = new ThrowableField("Error.Exception.protection.delete.SQL",
					"&cThere was an error trying to delete this protection in the database. Please contact with an administrator!");
			public static final ThrowableField UNKNOWN = new ThrowableField("Error.Exception.protection.delete.Unknown",
					"&cAn unknown error happened while trying to delete this protection. Please contact with an administrator!");

		}

		public static class Save {

			public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
					"Error.Exception.protection.save.PermissionDenied",
					"&cYou aren't allowed to save this protection!");
			public static final ThrowableField MAXREACHED = new ThrowableField(
					"Error.Exception.protection.save.MaxReached",
					"&cYou've reached the maximum amount of protections allowed!");
			public static final ThrowableField NAMEINUSE = new ThrowableField(
					"Error.Exception.protection.save.NameInUse", "&cThere's already a protection using this name!");
			public static final ThrowableField OVERLAPS = new ThrowableField("Error.Exception.protection.save.Overlaps",
					"&cYou can't put protections inside other existing ones!");
			public static final ThrowableField ALREADYOCCUPIED = new ThrowableField(
					"Error.Exception.protection.save.AlreadyOccupied",
					"&cThere's already a protection placed on this location!");
			public static final ThrowableField RENAMEDENIED = new ThrowableField(
					"Error.Exception.protection.save.RenamePermissionDenied",
					"&cYou aren't allowed to rename this protection!");
			public static final ThrowableField SQL = new ThrowableField("Error.Exception.protection.save.SQL",
					"&cThere was an error trying to save this protection in the database. Please contact with an administrator!");
			public static final ThrowableField UNKNOWN = new ThrowableField("Error.Exception.protection.save.Unknown",
					"&cAn unknown error happened while trying to save this protection. Please contact with an administrator!");
			public static final ThrowableField NOVISIBLETEXT = new ThrowableField(
					"Error.Exception.protection.save.NoVisibleText", "&cThe text you're typing must be visible!");

		}

		public static class Teleport {
			public static final ThrowableField NOHOMESET = new ThrowableField(
					"Error.Exception.protection.teleport.NoHomeSet", "&cThis protection does not have a home set!");
			public static final ThrowableField COOLDOWNACTIVE = new ThrowableField(
					"Error.Exception.protection.teleport.CooldownActive",
					"&cYou have to wait &e%seconds% seconds &cto teleport again!");
			public static final ThrowableField UNKNOWN = new ThrowableField(
					"Error.Exception.protection.teleport.Unknown",
					"&cAn unknown error happened while trying to teleport. Please contact with an administrator!");
			public static final ThrowableField CANCELLEDDUEMOVEMENT = new ThrowableField(
					"Error.Exception.protection.teleport.CancelledDueMovement",
					"&cThe teleportation has been cancelled due a detected movement!");
		}

		public static final ThrowableField PROTECTION_KICK_DENIED = new ThrowableField(
				"Error.Exception.protection.kick.Denied", "&cYou can't kick this player in this protection!");
		public static final ThrowableField PROTECTION_NOTENOUGHBALANCE = new ThrowableField(
				"Error.Exception.protection.NotEnoughBalance", "&cYou don't have enough balance for this!");

		public static class Members {

			public static class Delete {

				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						"Error.Exception.protection.members.delete.PermissionDenied",
						"&cYou aren't allowed to remove this member!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						"Error.Exception.protection.members.delete.Unknown",
						"&cAn unknown error happened while trying to remove this member. Please contact with an administrator!");

			}

			public static class Save {

				public static final ThrowableField CANNOTADDPROTECTIONOWNER = new ThrowableField(
						"Error.Exception.protection.members.save.CannotAddProtectionOwner",
						"&cYou can't add the owner of this protection as a member!");
				public static final ThrowableField CANNOTADDYOURSELF = new ThrowableField(
						"Error.Exception.protection.members.save.CannotAddYourself",
						"&cYou can't add yourself as a member!");
				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						"Error.Exception.protection.members.save.PermissionDenied",
						"&cYou aren't allowed to add members in this protection!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						"Error.Exception.protection.members.save.Unknown",
						"&cAn unknown error happened while trying to add this member. Please contact with an administrator!");

			}

		}

		public static class Owners {

			public static class Delete {

				public static final ThrowableField CANNOTDELETEPROTECTIONOWNER = new ThrowableField(
						"Error.Exception.protection.owners.delete.CannotDeleteProtectionOwner",
						"&cYou can't remove the owner of this protection from the owners list!");
				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						"Error.Exception.protection.owners.delete.PermissionDenied",
						"&cYou aren't allowed to remove this owner!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						"Error.Exception.protection.owners.delete.Unknown",
						"&cAn unknown error happened while trying to remove this owner. Please contact with an administrator!");

			}

			public static class Save {

				public static final ThrowableField CANNOTADDPROTECTIONOWNER = new ThrowableField(
						"Error.Exception.protection.owners.save.CannotAddProtectionOwner",
						"&cYou can't add the owner of this protection as an owner!");
				public static final ThrowableField CANNOTADDYOURSELF = new ThrowableField(
						"Error.Exception.protection.owners.save.CannotAddYourself",
						"&cYou can't add yourself as an owner!");
				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						"Error.Exception.protection.owners.save.PermissionDenied",
						"&cYou aren't allowed to add owners in this protection!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						"Error.Exception.protection.owners.save.Unknown",
						"&cAn unknown error happened while trying to add this owner. Please contact with an administrator!");

			}

		}

		public static class Banneds {

			public static class Delete {

				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						"Error.Exception.protection.banneds.delete.PermissionDenied",
						"&cYou aren't allowed to remove this banned!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						"Error.Exception.protection.banneds.delete.Unknown",
						"&cAn unknown error happened while trying to remove this banned. Please contact with an administrator!");

			}

			public static class Save {

				public static final ThrowableField CANNOTADDPROTECTIONOWNER = new ThrowableField(
						"Error.Exception.protection.banneds.save.CannotAddProtectionOwner",
						"&cYou can't add the owner of this protection as a banned!");
				public static final ThrowableField CANNOTADDYOURSELF = new ThrowableField(
						"Error.Exception.protection.banneds.save.CannotAddYourself",
						"&cYou can't add yourself as a banned!");
				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						"Error.Exception.protection.banneds.save.PermissionDenied",
						"&cYou aren't allowed to add banneds in this protection!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						"Error.Exception.protection.banneds.save.Unknown",
						"&cAn unknown error happened while trying to add this banned. Please contact with an administrator!");

			}

		}

		public static class Blocks {

			public static class Delete {

				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						"Error.Exception.protection.blocks.delete.PermissionDenied",
						"&cYou aren't allowed to remove this protection block!");
				public static final ThrowableField SQL = new ThrowableField(
						"Error.Exception.protection.blocks.delete.SQL",
						"&cThere was an error trying to delete this protection block from the database. Please contact with an administrator!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						"Error.Exception.protection.blocks.delete.Unknown",
						"&cAn unknown error happened while trying to remove this protection block. Please contact with an administrator!");

			}

			public static class Save {

				public static final ThrowableField PERMISSIONDENIED = new ThrowableField(
						"Error.Exception.protection.blocks.save.PermissionDenied",
						"&cYou aren't allowed to save this protection block!");
				public static final ThrowableField IDINUSE = new ThrowableField(
						"Error.Exception.protection.blocks.save.IdInUse",
						"&cThere's already a protection block using this ID!");
				public static final ThrowableField IDNULL = new ThrowableField(
						"Error.Exception.protection.blocks.save.IdNull",
						"&cThere's no ID specified for this protection block!");
				public static final ThrowableField ITEMNULL = new ThrowableField(
						"Error.Exception.protection.blocks.save.ItemNull",
						"&cThere's no item specified for this protection block!");
				public static final ThrowableField SQL = new ThrowableField(
						"Error.Exception.protection.blocks.save.SQL",
						"&cThere was an error trying to save this protection block in the database. Please contact with an administrator!");
				public static final ThrowableField UNKNOWN = new ThrowableField(
						"Error.Exception.protection.blocks.save.Unknown",
						"&cAn unknown error happened while trying to save this protection block. Please contact with an administrator!");

			}

			public static final ThrowableField GENERATEITEM = new ThrowableField(
					"Error.Exception.protection.blocks.GenerateItem",
					"&cAn unknown error happened while trying to generate the item. Please contact with an administrator!");

		}

	}

}

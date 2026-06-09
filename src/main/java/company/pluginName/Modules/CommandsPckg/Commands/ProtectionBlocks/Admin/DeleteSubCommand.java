package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Modules.FilePckg.Messages;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.RemovalCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.Protections.Objects.ProtectionRemovalData;

@PandaSubCommandAnnotation(parentCommand = AdminCommand.class)
@PandaCommandAnnotation(
		id = "delete",
		pathName = "Delete",
		defaultName = "delete",
		defaultDescription = "Allows to delete a specific protection through the command",
		defaultAliases = "d",
		defaultPermission = "protectionblocks.admin.delete")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = false)
public class DeleteSubCommand extends PandaSubCommand {

	public DeleteSubCommand() throws InstantiationException {
		super();
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		if (argIndex == 0) {
			return new ArrayList<>(RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService()
					.getProtectionByRegion().keySet());
		}
		return super.generateAutocompleteList(sender, argIndex);
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		if (parameters.getParameters().size() > 0) {
			try {
				RoyaleProtectionBlocksAPIImpl.getInstance().getProtectionsService().delete(
						ProtectionRemovalData.inst(sender, parameters.getParameters().get(0), RemovalCause.STAFF));

				MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_REMOVEDSUCCESSFULLY.applyPrefix()).process()
						.sendMessage(sender);
			} catch (RoyaleProtectionBlocksException e) {
				e.sendError(sender);
			}
		} else {
			MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process().sendMessage(sender);
		}
		return new TrueResponse();
	}

}

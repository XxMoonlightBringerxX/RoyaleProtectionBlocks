package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Files;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import company.pluginName.Modules.FilePckg.FilesService;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Utils.ProtectionBlocksUtils;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;

@PandaSubCommandAnnotation(parentCommand = FilesCommand.class)
@PandaCommandAnnotation(
		id = "export",
		pathName = "Export",
		defaultName = "export",
		defaultDescription = "Export all the information of an specific type of data to its respective file",
		defaultUsage = "<blocks>",
		defaultPermission = "protectionblocks.files.export",
		defaultAliases = "e")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class ExportSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private static FilesService filesService;

	private static final List<String> DATA_TO_EXPORT_NAMES = Arrays.stream(DataToExport.values())
			.map(value -> value.name().toLowerCase()).collect(Collectors.toList());

	private static enum DataToExport {
		BLOCKS
	}

	public ExportSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public List<String> tabComplete(PandaCommand cmd, CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			return DATA_TO_EXPORT_NAMES;
		default:
			return EMPTY_LIST;
		}
	}

	@Override
	public CommandResponse executeCommandProcess(PandaCommand cmd, CommandSender sender, String[] args,
			boolean useids) {
		if (args.length > 1) {
			try {
				switch (DataToExport.valueOf(args[1].toUpperCase())) {
				case BLOCKS:
					PandaYaml yaml = new PandaYaml();

					protectionBlocksService.getProtectionBlocks().forEach((id, block) -> {
						ProtectionBlocksUtils.protectionBlockToMap(block)
								.forEach((key, value) -> yaml.getRoot().set("%s.%s".formatted(id, key), value));
					});

					try {
						yaml.saveYAML(filesService.getBlocksFile().getFile());

						MessageTemplate.inst(Messages.MESSAGE_FILES_EXPORTEDSUCCESSFULLY.applyPrefix()).process()
								.sendMessage(sender);
					} catch (IOException e) {
						MessageTemplate.inst(Messages.ERROR_FILES_EXPORT.applyPrefix()).process().sendMessage(sender);
						e.printStackTrace();
					}
					break;
				default:
					MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process()
							.sendMessage(sender);
					break;
				}
			} catch (IllegalArgumentException e) {
				MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process()
						.sendMessage(sender);
			}
		} else {
			MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process().sendMessage(sender);
		}
		return new TrueResponse();
	}
}

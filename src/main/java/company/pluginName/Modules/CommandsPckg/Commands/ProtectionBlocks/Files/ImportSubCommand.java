package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Files;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Modules.FilePckg.FilesService;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Utils.ProtectionBlocksUtils;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;

@PandaSubCommandAnnotation(parentCommand = FilesCommand.class)
@PandaCommandAnnotation(
		id = "import",
		pathName = "Import",
		defaultName = "import",
		defaultDescription = "Import all the information of an specific type of data from its respective file",
		defaultUsage = "<blocks>",
		defaultPermission = "protectionblocks.files.import",
		defaultAliases = "i"
)
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true
)
public class ImportSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private static FilesService filesService;

	private static final List<String> DATA_TO_IMPORT_NAMES = Arrays.stream(DataToExport.values())
			.map(value -> value.name().toLowerCase()).collect(Collectors.toList());

	private static enum DataToExport {
		BLOCKS
	}

	public ImportSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public List<String> tabComplete(PandaCommand cmd, CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			return DATA_TO_IMPORT_NAMES;
		default:
			return Collections.emptyList();
		}
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (parameters.getParameters().size() > 0) {
			try {
				switch (DataToExport.valueOf(parameters.getParameters().get(0).toUpperCase())) {
				case BLOCKS:
					try {
						PandaYaml yaml = new PandaYaml(filesService.getBlocksFile().getFile());

						yaml.getRoot().getNamedChilds().forEach(child -> {
							if (child.isYamlSection()) {
								ProtectionBlock block;
								try {
									block = ProtectionBlocksUtils.mapToProtectionBlock(child.getName(),
											child.asYamlSection().toMap());
									ProtectionBlock originalBlock = protectionBlocksService
											.getProtectionBlockById(child.getName());

									if (originalBlock != null) {
										originalBlock.copy(block);

										originalBlock.save(pl);
									} else {
										block.save(pl);
									}
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
							}
						});

						MessageTemplate.inst(Messages.MESSAGE_FILES_IMPORTEDSUCCESSFULLY.applyPrefix()).process()
								.sendMessage(sender);
					} catch (Exception e) {
						MessageTemplate.inst(Messages.ERROR_FILES_IMPORT.applyPrefix()).process().sendMessage(sender);
						e.printStackTrace();
					}
					break;
				default:
					MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process()
							.sendMessage(sender);
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

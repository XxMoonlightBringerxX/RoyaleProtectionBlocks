package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.FilePckg.FilesService;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsServiceImpl;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Utils.ProtectionBlocksUtils;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;

@PandaSubCommandAnnotation(parentCommand = FilesCommand.class)
@PandaCommandAnnotation(
		id = "import",
		pathName = "Import",
		defaultName = "import",
		defaultDescription = "Import all the information of an specific type of data from its respective file",
		defaultUsage = "<blocks>",
		defaultPermission = "protectionblocks.files.import",
		defaultAliases = "i")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class ImportSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionsServiceImpl protectionsService;

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
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		switch (argIndex) {
		case 0:
			return DATA_TO_IMPORT_NAMES;
		}
		return super.generateAutocompleteList(sender, argIndex);
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

						List<Pair<ProtectionBlock, ProtectionBlock>> blocks = new ArrayList<>();

						yaml.getRoot().getNamedChilds().forEach(child -> {
							if (child.isYamlSection()) {
								try {
									ProtectionBlock block = ProtectionBlocksUtils.mapToProtectionBlock(child.getName(),
											child.asYamlSection().toMap());
									blocks.add(Pair.of(protectionBlocksService.getProtectionBlockById(child.getName()),
											block));
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
							}
						});

						List<Pair<ProtectionBlock, Exception>> exceptions = new ArrayList<>();

						blocks.forEach(pair -> {
							List<Protection> protectionsToRefresh = new ArrayList<>();
							try {
								if (pair.getFirst() != null) {
									ProtectionBlock copy = new ProtectionBlock().copy(pair.getFirst());
									try {
										protectionsService.getProtectionsByWorld().values()
												.forEach(protections -> protections.stream()
														.filter(protection -> protection.getProtectionBlockId()
																.equals(pair.getFirst().getId())
																&& protection.getUtils().isProtectionBlockShown())
														.forEach(prot -> {
															prot.getUtils().hideProtectionBlock();
															protectionsToRefresh.add(prot);
														}));

										pair.getFirst().copy(pair.getSecond());
										pair.getFirst().save(pl);
									} catch (RoyaleProtectionBlocksExceptionImpl ex) {
										pair.getFirst().copy(copy);
									}
								} else {
									pair.getSecond().save(pl);
								}
							} catch (Exception ex) {
								exceptions.add(Pair.of(pair.getSecond(), ex));
							} finally {
								protectionsToRefresh.forEach(prot -> prot.getUtils().showProtectionBlock());
							}
						});

						if (exceptions.isEmpty()) {
							MessageTemplate.inst(Messages.MESSAGE_FILES_IMPORTEDSUCCESSFULLY.applyPrefix()).process()
									.sendMessage(sender);
						} else {
							MessageTemplate.inst(Messages.MESSAGE_FILES_IMPORTEDWARNING.applyPrefix()).process()
									.sendMessage(sender);
							exceptions.forEach(pair -> {
								MessageTemplate.inst(String.format("&8- &c%s: &e%s", pair.getFirst().getId(),
										pair.getSecond().getMessage())).process().sendMessage(sender);
								pair.getSecond().printStackTrace();
							});
						}
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

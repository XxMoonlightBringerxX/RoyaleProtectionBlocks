package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Files;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import company.pluginName.Utils.ProtectionBlocksUtils;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;

public class ImportSubCommand extends SubCommand {

	private static final List<String> DATA_TO_IMPORT_NAMES = Arrays.stream(DataToExport.values())
			.map(value -> value.name().toLowerCase()).collect(Collectors.toList());

	private static enum DataToExport {
		BLOCKS
	}

	public ImportSubCommand(Command command) {
		super(command, "export", SettingString.COMMANDS_PROTECTIONBLOCKS_FILES_IMPORT_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_FILES_IMPORT_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_FILES_IMPORT_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_FILES_IMPORT_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_FILES_IMPORT_ALIASES.getContent());
	}

	@Override
	public List<String> tabComplete(Command cmd, CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			return DATA_TO_IMPORT_NAMES;
		default:
			return EMPTY_LIST;
		}
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (args.length > 1) {
			try {
				switch (DataToExport.valueOf(args[1].toUpperCase())) {
				case BLOCKS:
					try {
						PandaYaml yaml = new PandaYaml(
								MainPluginClass.getPlugin().getFileModule().BLOCKS_FILE.getFile());

						yaml.getRoot().getNamedChilds().forEach(child -> {
							if (child.isYamlSection()) {
								ProtectionBlock block;
								try {
									block = ProtectionBlocksUtils.mapToProtectionBlock(child.getName(),
											child.asYamlSection().toMap());
									ProtectionBlock originalBlock = MainPluginClass.getPlugin().getProtectionsModule()
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

						MessageBuilder.createMessage(MessageString.MESSAGE_FILES_IMPORTEDSUCCESSFULLY.applyPrefix())
								.sendMessage(sender);
					} catch (Exception e) {
						MessageBuilder.createMessage(MessageString.ERROR_FILES_IMPORT.applyPrefix())
								.sendMessage(sender);
						e.printStackTrace();
					}
					break;
				default:
					MessageBuilder.createMessage(MessageString.applyPrefix(getUsage())).sendMessage(sender);
				}
			} catch (IllegalArgumentException e) {
				MessageBuilder.createMessage(MessageString.applyPrefix(getUsage())).sendMessage(sender);
			}
		} else {
			MessageBuilder.createMessage(MessageString.applyPrefix(getUsage())).sendMessage(sender);
		}
		return true;
	}
}

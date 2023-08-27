package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Files;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import company.pluginName.MainPluginClass;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingList;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingString;
import company.pluginName.Utils.ProtectionBlocksUtils;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.Command;
import relampagorojo93.LibsCollection.SpigotCommands.Objects.SubCommand;

public class ExportSubCommand extends SubCommand {

	private static final List<String> DATA_TO_EXPORT_NAMES = Arrays.stream(DataToExport.values())
			.map(value -> value.name().toLowerCase()).collect(Collectors.toList());

	private static enum DataToExport {
		BLOCKS
	}

	public ExportSubCommand(Command command) {
		super(command, "export", SettingString.COMMANDS_PROTECTIONBLOCKS_FILES_EXPORT_NAME.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_FILES_EXPORT_PERMISSION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_FILES_EXPORT_DESCRIPTION.toString(),
				SettingString.COMMANDS_PROTECTIONBLOCKS_FILES_EXPORT_USAGE.toString(),
				SettingList.COMMANDS_PROTECTIONBLOCKS_FILES_EXPORT_ALIASES.getContent());
	}

	@Override
	public List<String> tabComplete(Command cmd, CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			return DATA_TO_EXPORT_NAMES;
		default:
			return EMPTY_LIST;
		}
	}

	@Override
	public boolean execute(Command cmd, CommandSender sender, String[] args, boolean useids) {
		if (args.length > 1) {
			try {
				switch (DataToExport.valueOf(args[1].toUpperCase())) {
				case BLOCKS:
					PandaYaml yaml = new PandaYaml();

					MainPluginClass.getPlugin().getProtectionsModule().getProtectionBlocks().forEach((id, block) -> {
						ProtectionBlocksUtils.protectionBlockToMap(block)
								.forEach((key, value) -> yaml.getRoot().set("%s.%s".formatted(id, key), value));
					});

					try {
						yaml.saveYAML(MainPluginClass.getPlugin().getFileModule().BLOCKS_FILE.getFile());

						MessageBuilder.createMessage(MessageString.MESSAGE_FILES_EXPORTEDSUCCESSFULLY.applyPrefix())
								.sendMessage(sender);
					} catch (IOException e) {
						MessageBuilder.createMessage(MessageString.ERROR_FILES_EXPORT.applyPrefix())
								.sendMessage(sender);
						e.printStackTrace();
					}
					break;
				default:
					MessageBuilder.createMessage(MessageString.applyPrefix(getUsage())).sendMessage(sender);
					break;
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

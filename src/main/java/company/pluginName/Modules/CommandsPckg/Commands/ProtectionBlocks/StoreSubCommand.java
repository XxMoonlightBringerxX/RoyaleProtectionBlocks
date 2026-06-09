package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Bukkit.Inventories.Store.ProtectionBlocksStoreInventory;
import company.pluginName.Bukkit.Inventories.Store.ProtectionsStoreInventory;
import company.pluginName.Bukkit.Inventories.Store.StoreInventory;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "store",
		pathName = "Store",
		defaultName = "store",
		defaultDescription = "Open the store to purchase protections or protection blocks",
		defaultUsage = "['protections'|'blocks']")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class StoreSubCommand extends PandaSubCommand {

	@RegisteredPandaField("config")
	private static final PandaBooleanField SETTINGS_STORE_ENABLEPROTECTIONSSTORE = new PandaBooleanField(
			"Settings.Store.Enable-protections-store", true);

	@RegisteredPandaField("config")
	private static final PandaBooleanField SETTINGS_STORE_ENABLEPROTECTIONBLOCKSSTORE = new PandaBooleanField(
			"Settings.Store.Enable-protection-blocks-store", true);

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	public StoreSubCommand() throws InstantiationException {
		super();
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		if (argIndex == 0) {
			List<StoreOption> options = new ArrayList<>();

			boolean protectionStoreAvailable = SETTINGS_STORE_ENABLEPROTECTIONSSTORE.isTrue()
					&& RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
							.getProtectionsStoreEconomyService() != null;
			boolean protectionBlocksStoreAvailable = SETTINGS_STORE_ENABLEPROTECTIONBLOCKSSTORE.isTrue()
					&& RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
							.getProtectionBlocksStoreEconomyService() != null;

			if (protectionStoreAvailable) {
				options.add(StoreOption.PROTECTIONS);
			}

			if (protectionBlocksStoreAvailable) {
				options.add(StoreOption.BLOCKS);
			}

			return options.stream().map(option -> option.name().toLowerCase()).collect(Collectors.toList());

		}
		return super.generateAutocompleteList(sender, argIndex);
	}

	@Override
	public boolean precondition() {
		boolean protectionStoreAvailable = SETTINGS_STORE_ENABLEPROTECTIONSSTORE.isTrue()
				&& RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
						.getProtectionsStoreEconomyService() != null;
		boolean protectionBlocksStoreAvailable = SETTINGS_STORE_ENABLEPROTECTIONBLOCKSSTORE.isTrue()
				&& RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
						.getProtectionBlocksStoreEconomyService() != null;
		return protectionStoreAvailable || protectionBlocksStoreAvailable;
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (parameters.getParameters().isEmpty()) {
				boolean protectionBlocksStoreAvailable = SETTINGS_STORE_ENABLEPROTECTIONBLOCKSSTORE.isTrue()
						&& RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
								.getProtectionBlocksStoreEconomyService() != null;
				boolean protectionStoreAvailable = SETTINGS_STORE_ENABLEPROTECTIONSSTORE.isTrue()
						&& RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
								.getProtectionsStoreEconomyService() != null;

				if (protectionStoreAvailable && protectionBlocksStoreAvailable) {
					new StoreInventory(pl).openInventory();
				} else if (protectionBlocksStoreAvailable) {
					new ProtectionBlocksStoreInventory(pl).openInventory();
				} else if (protectionStoreAvailable) {
					new ProtectionsStoreInventory(pl).openInventory();
				} else {
					MessageTemplate.inst(Messages.ERROR_UNAVAILABLESTORE.applyPrefix()).process().sendMessage(sender);
				}
			} else {
				try {
					StoreOption option = StoreOption.valueOf(parameters.getParameters().get(0).toUpperCase());

					boolean protectionBlocksStoreAvailable = SETTINGS_STORE_ENABLEPROTECTIONBLOCKSSTORE.isTrue()
							&& RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
									.getProtectionBlocksStoreEconomyService() != null;
					boolean protectionStoreAvailable = SETTINGS_STORE_ENABLEPROTECTIONSSTORE.isTrue()
							&& RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
									.getProtectionsStoreEconomyService() != null;

					switch (option) {
					case BLOCKS:
						if (protectionBlocksStoreAvailable) {
							new ProtectionBlocksStoreInventory(pl).openInventory();
						} else {
							MessageTemplate.inst(Messages.ERROR_UNAVAILABLESTORE.applyPrefix()).process()
									.sendMessage(sender);
						}
						break;
					case PROTECTIONS:
						if (protectionStoreAvailable) {
							new ProtectionsStoreInventory(pl).openInventory();
						} else {
							MessageTemplate.inst(Messages.ERROR_UNAVAILABLESTORE.applyPrefix()).process()
									.sendMessage(sender);
						}
						break;
					}
				} catch (IllegalArgumentException e) {
					MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process()
							.sendMessage(sender);
				}
			}
		} else {
			MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}

	public static enum StoreOption {
		PROTECTIONS, BLOCKS
	}

}

package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin.Guard;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Bukkit.Inventories.Shared.SearchProtectionInventory;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PlayerGuardPckg.PlayerGuardService;
import company.pluginName.Modules.PlayersDataPckg.PlayerDataService;
import company.pluginName.Modules.PlayersDataPckg.Objects.PlayerData;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Utilities.Java.Time.TimeUtilities;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.Protections.IProtection;

@PandaSubCommandAnnotation(parentCommand = GuardCommand.class)
@PandaCommandAnnotation(
		id = "protection",
		pathName = "Protection",
		defaultName = "protection",
		defaultDescription = "Manage the guard status of the current protection, setting a new guard time.",
		defaultAliases = "pl",
		defaultUsage = "['set'|'add'|'remove'|'unlimited'] <time>",
		defaultPermission = "protectionblocks.admin.guard.protection")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true)
public class ProtectionSubCommand extends PandaSubCommand {

	private static enum Action {
		SET, ADD, REMOVE, UNLIMITED;

		public static Action find(String name) {
			return name != null ? Arrays.stream(Action.values()).filter(action -> action.name().equals(name))
					.findFirst().orElse(null) : null;
		}
	}

	private static final List<String> ACTION_ARGS = Arrays.asList("set", "add", "remove", "unlimited");
	private static final List<String> TIME_ARGS = Arrays.asList("1s", "1m", "1h", "1d", "1w", "1M", "1y");

	@PandaInject
	private static PlayerGuardService playerGuardService;

	@PandaInject
	private static PlayerDataService playerDataService;

	public ProtectionSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public List<String> getAutocompleteList(Player sender, String[] args) {
		return args.length != 2 || args[0].toLowerCase().equals("set") || args[0].toLowerCase().equals("add")
				? super.getAutocompleteList(sender, args)
				: Collections.emptyList();
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		switch (argIndex) {
		case 0:
			return ACTION_ARGS;
		case 1:
			return TIME_ARGS;
		}
		return super.generateAutocompleteList(sender, argIndex);
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (parameters.getParameters().size() > 0) {
				Action action = Action.find(parameters.getParameters().get(0).toUpperCase());

				if (action != null) {
					PlayerData playerData = playerDataService.getPlayerData((Player) sender);

					if (playerData.getCurrentProtections().size() > 0) {
						if (playerData.getCurrentProtections().size() == 1) {
							execute(sender, action, playerData.getCurrentProtections().get(0).getParentProtection(),
									parameters.getParameters().size() > 1 ? parameters.getParameters().get(1) : null);
						} else {
							new SearchProtectionInventory(pl, playerData.getCurrentProtections(), (prot) -> {
								execute(sender, action, prot,
										parameters.getParameters().size() > 1 ? parameters.getParameters().get(1)
												: null);
							}).openInventory();
						}
					} else {
						MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
								.sendMessage(sender);
					}
				} else {
					MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process()
							.sendMessage(sender);
				}
			} else {
				MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process()
						.sendMessage(sender);
			}
		} else {

		}
		return new TrueResponse();
	}

	private void execute(CommandSender sender, Action action, IProtection protection, String timeString) {
		switch (action) {
		case ADD:
		case SET:
			if (timeString != null) {
				try {
					long currentTimeMillis = System.currentTimeMillis();
					long time = TimeUtilities.stringToSeconds(timeString);

					if (time > 0) {
						long total = (time * 1000) + (action == Action.ADD
								? Math.max(protection.getGuardExpirationDate(), currentTimeMillis)
								: currentTimeMillis);

						protection.setGuardExpirationDateAndSave(total);
						MessageTemplate.inst(Messages.MESSAGE_GUARD_MODIFIEDSUCCESSFULLY.applyPrefix())
								.setReplacements(new Replacement("{time}",
										() -> TimeUtilities.secondsToString((total - currentTimeMillis) / 1000)))
								.process().sendMessage(sender);
					} else {
						MessageTemplate.inst(Messages.ERROR_NOTIMESPECIFIED.applyPrefix()).process()
								.sendMessage(sender);
					}
				} catch (NumberFormatException e) {
					MessageTemplate.inst(Messages.ERROR_NOTIMESPECIFIED.applyPrefix()).process().sendMessage(sender);
				}
			} else {
				MessageTemplate.inst(Messages.ERROR_NOTIMESPECIFIED.applyPrefix()).process().sendMessage(sender);
			}
			break;
		case UNLIMITED:
			protection.setGuardExpirationDateAndSave(Long.MAX_VALUE);
			MessageTemplate.inst(Messages.MESSAGE_GUARD_MODIFIEDUNLIMITEDSUCCESSFULLY.applyPrefix()).process()
					.sendMessage(sender);
			break;
		case REMOVE:
			protection.removeGuardExpirationDateAndSave();
			MessageTemplate.inst(Messages.MESSAGE_GUARD_REMOVEDSUCCESSFULLY.applyPrefix()).process()
					.sendMessage(sender);
			break;
		default:
			MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process().sendMessage(sender);
			break;
		}
	}

}

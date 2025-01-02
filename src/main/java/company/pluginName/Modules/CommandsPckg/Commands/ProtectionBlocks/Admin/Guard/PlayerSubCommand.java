package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Admin.Guard;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.PlayerGuardPckg.PlayerGuardService;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Utilities.Java.Time.TimeUtilities;

@PandaSubCommandAnnotation(parentCommand = GuardCommand.class)
@PandaCommandAnnotation(
		id = "player",
		pathName = "Player",
		defaultName = "player",
		defaultDescription = "Manage the guard status of a player, setting a new guard time.",
		defaultAliases = "pl",
		defaultUsage = "[player name] ['set'|'add'|'remove'|'unlimited'] <time>",
		defaultPermission = "protectionblocks.admin.guard.player")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true)
public class PlayerSubCommand extends PandaSubCommand {

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

	public PlayerSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public List<String> getAutocompleteList(Player sender, String[] args) {
		return args.length != 3 || args[1].equals("set") || args[1].equals("add")
				? super.getAutocompleteList(sender, args)
				: Collections.emptyList();
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		switch (argIndex) {
		case 0:
			return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
		case 1:
			return ACTION_ARGS;
		case 2:
			return TIME_ARGS;
		}
		return super.generateAutocompleteList(sender, argIndex);
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		if (parameters.getParameters().size() > 1) {
			return CommandResponse.queuedAsync(() -> {
				OfflinePlayer pl = OfflinePlayerUtilities.getOfflinePlayer(parameters.getParameters().get(0));

				if (pl != null) {
					Action action = Action.find(parameters.getParameters().get(1).toUpperCase());

					if (action != null) {
						switch (action) {
						case ADD:
						case SET:
							if (parameters.getParameters().size() > 2) {
								try {
									long currentTimeMillis = System.currentTimeMillis();
									long time = TimeUtilities.stringToSeconds(parameters.getParameters().get(2));

									if (time > 0) {
										long total = (time * 1000)
												+ (action == Action.ADD
														? Math.max(playerGuardService.getGuardExpirationDate(
																pl.getUniqueId()), currentTimeMillis)
														: currentTimeMillis);

										playerGuardService.setGuardExpirationDate(pl.getUniqueId(), total);
										MessageTemplate.inst(Messages.MESSAGE_GUARD_MODIFIEDSUCCESSFULLY.applyPrefix())
												.setReplacements(new Replacement("{time}",
														() -> TimeUtilities
																.secondsToString((total - currentTimeMillis) / 1000)))
												.process().sendMessage(sender);
									} else {
										MessageTemplate.inst(Messages.ERROR_NOTIMESPECIFIED.applyPrefix()).process()
												.sendMessage(sender);
									}
								} catch (NumberFormatException e) {
									MessageTemplate.inst(Messages.ERROR_NOTIMESPECIFIED.applyPrefix()).process()
											.sendMessage(sender);
								}
							} else {
								MessageTemplate.inst(Messages.ERROR_NOTIMESPECIFIED.applyPrefix()).process()
										.sendMessage(sender);
							}
							break;
						case UNLIMITED:
							playerGuardService.setGuardExpirationDate(pl.getUniqueId(), Long.MAX_VALUE);
							MessageTemplate.inst(Messages.MESSAGE_GUARD_MODIFIEDUNLIMITEDSUCCESSFULLY.applyPrefix())
									.process().sendMessage(sender);
							break;
						case REMOVE:
							playerGuardService.removeGuardExpirationDate(pl.getUniqueId());
							MessageTemplate.inst(Messages.MESSAGE_GUARD_REMOVEDSUCCESSFULLY.applyPrefix()).process()
									.sendMessage(sender);
							break;
						default:
							MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process()
									.sendMessage(sender);
							break;
						}
					} else {
						MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process()
								.sendMessage(sender);
					}
				} else {
					MessageTemplate.inst(Messages.ERROR_PLAYERNOTFOUND.applyPrefix()).process().sendMessage(sender);
				}
			});
		} else {
			MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process().sendMessage(sender);
		}
		return new TrueResponse();
	}

}

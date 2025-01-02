package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks.Blocks;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.ItemsAdderAPI.ItemsAdderAPI;
import company.pluginName.Hooks.ItemsAdderAPI.Hook.ItemsAdderHook;
import company.pluginName.Hooks.OraxenAPI.OraxenAPI;
import company.pluginName.Hooks.OraxenAPI.Hook.OraxenHook;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Components.ProtectionBlockInformation;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

@PandaSubCommandAnnotation(parentCommand = BlocksCommand.class)
@PandaCommandAnnotation(
		id = "add",
		pathName = "Add",
		defaultName = "add",
		defaultDescription = "Create a new block",
		defaultUsage = "<id> <x> <y> <z> [permission]",
		defaultPermission = "protectionblocks.blocks.create",
		defaultAliases = "a")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true,
		usage = true)
public class AddSubCommand extends PandaSubCommand {

	@PandaInject
	private static ItemsAdderAPI itemsAdderApi;

	@PandaInject
	private static OraxenAPI oraxenApi;

	public AddSubCommand() throws InstantiationException {
		super();
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		switch (argIndex) {
		case 0:
			return Arrays.asList("<id>");
		case 1:
			return Arrays.asList("<x>");
		case 2:
			return Arrays.asList("<y>");
		case 3:
			return Arrays.asList("<z>");
		case 4:
			return Arrays.asList("[permission]");
		default:
			return super.generateAutocompleteList(sender, argIndex);
		}
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			if (parameters.getParameters().size() > 3) {
				ItemStack i = ItemStacksUtils.getItemInMainHand(pl);
				if (i != null && i.getType() != Material.AIR) {
					if (i.getType().isBlock()
							|| itemsAdderApi.getHook().isCustomBlock(i) == ItemsAdderHook.CheckingResult.IS_CUSTOM_ITEM
							|| oraxenApi.getHook().isCustomBlock(i) == OraxenHook.CheckingResult.IS_CUSTOM_ITEM) {
						ItemStack protectionBlockItemstack = i.clone();
						protectionBlockItemstack.setAmount(1);
						try {
							try {
								int x = Integer.parseInt(parameters.getParameters().get(1));
								int y = Integer.parseInt(parameters.getParameters().get(2));
								int z = Integer.parseInt(parameters.getParameters().get(3));

								if (x < 0 || y < -1 || z < 0) {
									MessageTemplate.inst(Messages.ERROR_NUMBERBELOWZERO.applyPrefix()).process()
											.sendMessage(pl);
									return new TrueResponse();
								}

								String permission = parameters.getParameters().size() > 4
										? parameters.getParameters().get(4)
										: null;
								ProtectionBlock protectionBlock = new ProtectionBlock(new ProtectionBlockInformation(
										parameters.getParameters().get(0).toLowerCase(), protectionBlockItemstack,
										x / 2, (y == -1 ? y : y / 2), z / 2, permission, null));

								protectionBlockItemstack = protectionBlock.generateItem();

								protectionBlock.save(pl);
								protectionBlockItemstack.setAmount(i.getAmount());

								ItemStacksUtils.setItemInMainHand(pl, protectionBlockItemstack);
								MessageTemplate
										.inst(Messages.MESSAGE_PROTECTIONS_BLOCKS_CREATEDSUCCESSFULLY.applyPrefix())
										.process().sendMessage(pl);
							} catch (NumberFormatException e) {
								MessageTemplate.inst(Messages.ERROR_INVALIDNUMBER.applyPrefix()).process()
										.sendMessage(pl);
							}
						} catch (RoyaleProtectionBlocksExceptionImpl e) {
							e.sendError(pl);
						}
					} else {
						MessageTemplate.inst(Messages.ERROR_NOTABLOCK.applyPrefix()).process().sendMessage(pl);
					}
				} else {
					MessageTemplate.inst(Messages.ERROR_NOITEMINHAND.applyPrefix()).process().sendMessage(pl);
				}
			} else {
				MessageTemplate.inst(PandaPrefixedStringField.applyPrefix(getCommandUsage())).process().sendMessage(pl);
			}
		} else {
			MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}
}

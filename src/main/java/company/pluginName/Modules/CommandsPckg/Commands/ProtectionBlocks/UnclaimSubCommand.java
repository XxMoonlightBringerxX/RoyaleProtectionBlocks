package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(
		id = "unclaim",
		pathName = "Unclaim",
		defaultName = "unclaim",
		defaultDescription = "Remove the protection you're currently in",
		defaultAliases = "uc")
@PandaCommandAnnotation.Customizable(
		cooldown = true,
		aliases = true,
		description = true,
		name = true,
		permission = true)
public class UnclaimSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionsService protectionsService;

	public UnclaimSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(PandaCommand cmd, CommandSender sender, String[] args,
			boolean useids) {
		Player pl = sender instanceof Player ? (Player) sender : null;
		if (pl != null) {
			Protection protection = protectionsService.getProtectionByLocation(pl.getLocation());
			if (protection != null) {
				if (protection.canDelete(pl)) {
					try {
						ProtectionBlock protectionBlock = protection.getProtectionBlock().getObject();
						ItemStack protectionBlockItem = null;

						if (protectionBlock != null) {
							protectionBlockItem = protection.getProtectionBlock().getObject().getInformation()
									.generateItem();
						}

						if (protection.isProtectionBlockShown()) {
							protection.hideProtectionBlock();
						}

						protectionsService.removeProtection(protection);

						if (protectionBlockItem != null) {
							HashMap<Integer, ItemStack> items = pl.getInventory().addItem(protectionBlockItem);

							if (items != null && !items.isEmpty()) {
								items.values()
										.forEach(item -> pl.getLocation().getWorld().dropItem(pl.getLocation(), item));
							}
						}

						MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_REMOVEDSUCCESSFULLY.applyPrefix()).process()
								.sendMessage(pl);
					} catch (RoyaleProtectionBlocksException e1) {
						e1.sendError(pl);
					}
				} else {
					MessageTemplate.inst(Messages.ERROR_PROTECTIONS_LEAVEDENIEDTOMAINOWNER.applyPrefix()).process()
							.sendMessage(sender);
				}
			} else {
				MessageTemplate.inst(Messages.ERROR_PROTECTIONS_NOTINSIDEPROTECTION.applyPrefix()).process()
						.sendMessage(sender);
			}
		} else {
			MessageTemplate.inst(Messages.ERROR_CONSOLEDENIED.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}
}

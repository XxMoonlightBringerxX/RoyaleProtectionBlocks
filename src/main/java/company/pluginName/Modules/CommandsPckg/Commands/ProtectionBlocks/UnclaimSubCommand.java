package company.pluginName.Modules.CommandsPckg.Commands.ProtectionBlocks;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksException;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import royale.RoyaleProtectionBlocks.Plugin.InternalAPI.Events.Protection.ProtectionRemovalAttemptEvent;

@PandaSubCommandAnnotation(parentCommand = ProtectionBlocksCommand.class)
@PandaCommandAnnotation(id = "unclaim", pathName = "Unclaim", defaultName = "unclaim", defaultDescription = "Remove the protection you're currently in", defaultAliases = "uc")
@PandaCommandAnnotation.Customizable(cooldown = true, aliases = true, description = true, name = true, permission = true)
public class UnclaimSubCommand extends PandaSubCommand {

	@PandaInject
	private static ProtectionsService protectionsService;

	public UnclaimSubCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(PandaCommand cmd, CommandSender sender, String[] args,
			boolean useids) {
		Player player = sender instanceof Player ? (Player) sender : null;
		if (player != null) {
			Protection protection = protectionsService.findProtectionByLocation(player.getLocation());
			if (protection != null) {
				if (ProtectionUtilities.canDelete(protection, player)) {
					try {
						ProtectionRemovalAttemptEvent attemptEvent = new ProtectionRemovalAttemptEvent(player,
								protection);
						Bukkit.getPluginManager().callEvent(attemptEvent);

						if (attemptEvent.isCancelled()) {
							throw Exceptions.Protections.Delete.CANCELLED.generateException();
						}

						if (protection.getUtils().isProtectionBlockShown()) {
							protection.getUtils().hideProtectionBlock();
						}

						if (protection.getBoundaries().isProtectionViewActive()) {
							protection.getBoundaries().toggleProtectionView();
						}

						ProtectionBlock protectionBlock = protection.getProtectionBlock().getObject();
						ItemStack protectionBlockItem = (protectionBlock != null)
								? ((ProtectionBlock) protection.getProtectionBlock().getObject()).getInformation()
										.generateItem()
								: null;

						TasksUtils.executeOnAsync(() -> {
							try {
								protection.delete(player).subscribe((deletedProtection) -> {
									MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_REMOVEDSUCCESSFULLY.applyPrefix())
											.process().sendMessage(player);

									TasksUtils.execute(() -> {
										if (player.isOnline() && protectionBlockItem != null) {
											player.getInventory().addItem(protectionBlockItem)
													.forEach((index, remainingItem) -> protection.getLocation()
															.getWorld()
															.dropItem(protection.getLocation(), remainingItem));
										}

										if (player.isOnline()
												&& player.getOpenInventory().getType() != InventoryType.CRAFTING) {
											player.closeInventory();
										}
									});
								}, (throwable) -> {
									if (!(throwable instanceof RoyaleProtectionBlocksException)) {
										throwable = Exceptions.Protections.Delete.UNKNOWN.generateException(throwable);
									}

									((RoyaleProtectionBlocksException) throwable).sendError(player);
								});
							} catch (RoyaleProtectionBlocksException e) {
								e.sendError(player);
							}
						});
					} catch (RoyaleProtectionBlocksException e) {
						e.sendError(player);
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

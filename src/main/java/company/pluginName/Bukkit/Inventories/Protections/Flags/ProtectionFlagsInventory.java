package company.pluginName.Bukkit.Inventories.Protections.Flags;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.protection.flags.StateFlag.State;

import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionFlagsPckg.ProtectionFlagsService;
import company.pluginName.Modules.ProtectionFlagsPckg.Objects.ProtectionFlag;
import company.pluginName.Modules.ProtectionFlagsPckg.Utils.ProtectionFlagUtilities;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionFlags.FlagModificationRequest;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Defaults.Messages.Events.MessagesListener;
import darkpanda73.PandaUtils.PandaPlugin.Defaults.Messages.Exceptions.PlayerAlreadyListeningException;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Paged.PagedChestInventoryObject;

@Inventory("protections_flags")
public class ProtectionFlagsInventory extends PagedChestInventoryObject<ProtectionFlag> {

	private static final String MESSAGES_FLAGSTRINGSPECIFYINFO_PATH = "Messages.Flag-string-specify-info";

	@PandaInject
	private static MessagesListener messagesListener;

	@PandaInject
	private static ProtectionFlagsService protectionFlagsService;

	private Protection protection;

	public ProtectionFlagsInventory(Player player, Protection protection) {
		super(player);

		this.protection = protection;
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).setReplacements(new Replacement("{protection}",
				() -> protection.getDisplayName() != null ? protection.getDisplayName() : protection.getRegionId()))
				.process().toString();
	}

	@Override
	protected List<ProtectionFlag> getEntityList() {
		return protectionFlagsService.getFlags().stream()
				.filter(flag -> !flag.isHidden() && flag.getDisplayItem() != null
						&& (!flag.isHideIfNoValue() || flag.retrieveValue(protection.getProtectedRegion()) != null)
						&& (!flag.isHideIfNoPermission() || getPlayer().hasPermission(flag.getPermission())))
				.collect(Collectors.toList());
	}

	@Override
	protected ItemStack generateEntityItem(ProtectionFlag entity) {
		return ItemBuilder
				.inst().fromItem(
						entity.getDisplayItem())
				.setReplacements(new Replacement("{value}", () -> ProtectionFlagUtilities
						.valueToString(entity.retrieveValue(protection.getProtectedRegion()))))
				.build();
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, ProtectionFlag entity) {
		try {
			if (entity.isEditable()) {
				if (ProtectionFlagUtilities.isStateFlag(entity.getWorldGuardFlag())) {
					protection.getFlags()
							.setFlag(FlagModificationRequest.inst(getPlayer(), entity,
									entity.retrieveValue(protection.getProtectedRegion()) == State.ALLOW ? State.DENY
											: State.ALLOW));
					updateInventory();
				} else {
					try {
						messagesListener.startListening(e.getWhoClicked().getUniqueId(), (message) -> {
							if (!message.equalsIgnoreCase("cancel")) {
								try {
									protection.getFlags()
											.setFlag(
													FlagModificationRequest
															.inst(entity,
																	ProtectionFlagUtilities.stringToValue(
																			entity.getWorldGuardFlag(), message))
															.setExecutor(getPlayer()));
								} catch (RoyaleProtectionBlocksExceptionImpl ex) {
									ex.sendError(getPlayer());
								}
							}
							openInventory();
							return true;
						});
						closeInventory();
						MessageTemplate
								.inst(PandaPrefixedStringField.applyPrefix(getChestInventoryData().getCustomFields()
										.get(MESSAGES_FLAGSTRINGSPECIFYINFO_PATH).toString()))
								.process().sendMessage(e.getWhoClicked());
					} catch (PlayerAlreadyListeningException ex) {
						MessageTemplate.inst(Messages.ERROR_CHATPROMPT_ALREADYPROMPTED.toString()).process()
								.sendMessage(e.getWhoClicked());
						return;
					}
				}
			}
		} catch (RoyaleProtectionBlocksExceptionImpl ex) {
			ex.sendError(getPlayer());
		}
	}

	@ItemExecutor("Close-button")
	private void executeCloseButton() {
		goToPreviousHolder();
	}

}

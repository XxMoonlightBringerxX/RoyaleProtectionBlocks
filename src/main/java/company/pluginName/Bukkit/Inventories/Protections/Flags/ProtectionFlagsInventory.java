package company.pluginName.Bukkit.Inventories.Protections.Flags;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.Bukkit.Inventories.Protections.Flags.Objects.Flag;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
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
public class ProtectionFlagsInventory extends PagedChestInventoryObject<Flag<?>> {

	private static final String MESSAGES_FLAGSTRINGSPECIFYINFO_PATH = "Messages.Flag-string-specify-info";

	@PandaInject
	private static MessagesListener messagesListener;

	private Protection protection;
	private ProtectedRegion protectedRegion;

	public ProtectionFlagsInventory(Player player, Protection protection) {
		super(player);

		this.protection = protection;
		this.protectedRegion = this.protection.getProtectedRegion();
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).setReplacements(new Replacement("{protection}",
				() -> protection.getDisplayName() != null ? protection.getDisplayName() : protection.getRegionId()))
				.process().toString();
	}

	@Override
	protected List<Flag<?>> getEntityList() {
		return Flag.FLAGS;
	}

	@Override
	protected ItemStack generateEntityItem(Flag<?> entity) {
		return ItemBuilder.inst().fromItem(entity.getItemData().getItem())
				.setReplacements(new Replacement("{value}", () -> entity.getFlagValueAsString(protectedRegion)))
				.build();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onEntityClick(InventoryClickEvent e, Flag<?> entity) {
		if (entity.getWorldGuardFlag() instanceof StateFlag) {
			Flag<State> stateFlag = (Flag<State>) entity;
			stateFlag.setFlagValue(protectedRegion,
					stateFlag.getFlagValue(protectedRegion) == State.ALLOW ? State.DENY : State.ALLOW);
			updateInventory();
		} else if (entity.getWorldGuardFlag() instanceof StringFlag) {
			Flag<String> stringFlag = (Flag<String>) entity;
			try {
				messagesListener.startListening(e.getWhoClicked().getUniqueId(), (message) -> {
					if (!message.equalsIgnoreCase("cancel")) {
						stringFlag.setFlagValue(protectedRegion, message);
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

	@ItemExecutor("Close-button")
	private void executeCloseButton() {
		goToPreviousHolder();
	}

}

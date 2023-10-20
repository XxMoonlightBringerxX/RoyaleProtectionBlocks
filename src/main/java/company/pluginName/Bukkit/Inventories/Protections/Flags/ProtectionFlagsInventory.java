package company.pluginName.Bukkit.Inventories.Protections.Flags;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Bukkit.Inventories.Protections.Flags.Objects.Flag;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import darkpanda73.PandaUtils.PandaColors.Objects.TextReplacement;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackUtilities;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.Messages.Exceptions.PlayerAlreadyListeningException;

@Data
@EqualsAndHashCode(callSuper = false)
@Setter(lombok.AccessLevel.NONE)
public class ProtectionFlagsInventory extends PluginChestInventory {

	private Protection protection;
	private ProtectedRegion protectedRegion;
	private List<Flag<?>> flags;
	private int page = 1;

	public ProtectionFlagsInventory(Player player, Protection protection) {
		super(player);

		this.protection = protection;
		this.protectedRegion = this.protection.getProtectedRegion();
		this.flags = Flag.FLAGS;

		setSize(27);
		setName(MessageBuilder.createMessage(TextInput.inst().text(MessageString.INVENTORY_PROTECTION_FLAGS_TITLE.toString())
				.replacements(new TextReplacement("{protection}", () -> protection.getDisplayName()))).toString());
	}

	@Override
	public void updateContent() {
		clearSlots();

		for (int i = getSize() - 9; i < getSize(); i++) {
			setSlot(i, GRAY_STAINED_GLASS_PANE);
		}

		int maxPage = getMaxPage();

		if (page < 1) {
			page = 1;
		} else if (page > maxPage) {
			page = maxPage;
		}

		if (page > 1) {
			setSlot(getSize() - 6, new Button(LEFT_ARROW_ITEM) {
				@Override
				public void onClick(InventoryClickEvent e) {
					page--;
					updateInventory();
				}
			});
		}

		if (page < maxPage) {
			setSlot(getSize() - 4, new Button(RIGHT_ARROW_ITEM) {
				@Override
				public void onClick(InventoryClickEvent e) {
					page++;
					updateInventory();
				}
			});
		}

		setSlot(getSize() - 9, new Button(CLOSE_ITEM) {

			@Override
			public void onClick(InventoryClickEvent e) {
				goToPreviousHolder();
			}
		});

		if (flags.size() != 0) {
			int slot = 0;
			for (Flag<?> flag : Arrays.copyOfRange(flags.toArray(new Flag<?>[flags.size()]), ((page - 1) * (getSize() - 9)),
					(page * (getSize() - 9)))) {
				if (flag == null) {
					break;
				}

				setSlot(slot++, new Button(
						ItemStackUtilities.setReplacements(flag.getItemData().getItem().clone(), new TextReplacement("{value}", () -> {
							if (flag.getType() == State.class) {
								return ((State) flag.getFlagValue(protectedRegion) == State.ALLOW
										? MessageString.INVENTORY_PROTECTION_FLAGS_ALLOWVALUENAME.toString()
										: MessageString.INVENTORY_PROTECTION_FLAGS_DENYVALUENAME.toString()).toString();
							} else if (flag.getType() == String.class) {
								String text = (String) flag.getFlagValue(protectedRegion);
								return MessageBuilder
										.createMessage(
												TextInput.inst().text(MessageString.INVENTORY_PROTECTION_FLAGS_STRINGVALUENAME.toString())
														.replacements(new TextReplacement("{text}", () -> text != null ? text : "---")))
										.toString();
							}
							return "&7???";
						}))) {

					@SuppressWarnings("unchecked")
					@Override
					public void onClick(InventoryClickEvent e) {
						if (flag.getType() == State.class) {
							Flag<State> stateFlag = (Flag<State>) flag;
							stateFlag.setFlagValue(protectedRegion,
									stateFlag.getFlagValue(protectedRegion) == State.ALLOW ? State.DENY : State.ALLOW);
							updateInventory();
						} else if (flag.getType() == String.class) {
							Flag<String> stringFlag = (Flag<String>) flag;
							try {
								MainPluginClass.getPlugin().getMessagesListener().startListening(e.getWhoClicked().getUniqueId(),
										(message) -> {
											if (!message.equalsIgnoreCase("cancel")) {
												stringFlag.setFlagValue(protectedRegion, message);
											}
											openInventory();
											return true;
										});
								closeInventory();
								MessageBuilder
										.createMessage(MessageString
												.applyPrefix(MessageString.INVENTORY_PROTECTION_FLAGS_STRINGSPECIFYINFO.toString()))
										.sendMessage(e.getWhoClicked());
							} catch (PlayerAlreadyListeningException ex) {
								MessageBuilder.createMessage(MessageString.ERROR_CHATPROMPT_ALREADYPROMPTED.toString())
										.sendMessage(e.getWhoClicked());
								return;
							}
						}
					}
				});
			}
		}
	}

	public int getMaxPage() {
		return (int) ((flags.size() + (getSize() - 10)) / (double) (getSize() - 9));
	}

}

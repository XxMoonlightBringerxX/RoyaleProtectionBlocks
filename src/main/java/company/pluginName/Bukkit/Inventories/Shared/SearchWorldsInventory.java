package company.pluginName.Bukkit.Inventories.Shared;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import darkpanda73.PandaUtils.PandaColors.Objects.TextReplacement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;
import relampagorojo93.LibsCollection.Utils.Bukkit.Messages.Exceptions.PlayerAlreadyListeningException;

@Data
@EqualsAndHashCode(callSuper = false)
@Setter(lombok.AccessLevel.NONE)
public class SearchWorldsInventory extends PluginChestInventory {

	private static ItemStack SEARCH_SPECIFIC_PLAYER_ITEM;

	public static void initItems() {
		SEARCH_SPECIFIC_PLAYER_ITEM = ItemStacksUtils
				.createItemStack(ItemStacksUtils.setSkin(Material.PLAYER_HEAD.getItemStack(), SEARCH_SKIN),
						MessageBuilder
								.createMessage(MessageString.INVENTORY_SEARCHWORLDS_SEARCHSPECIFICWORLDNAME.toString())
								.toString());
	}

	private Action action;
	private List<World> worlds;
	private int page = 1;

	public SearchWorldsInventory(Player player, Action action) {
		super(player);

		setSize(54);
		setName(MessageBuilder.createMessage(MessageString.INVENTORY_SEARCHWORLDS_TITLE.toString()).toString());

		this.action = action;
		this.worlds = Bukkit.getWorlds();
	}

	@Override
	public void updateContent() {
		clearSlots();

		for (int i = getSize() - 9; i < getSize(); i++) {
			setSlot(i, GRAY_STAINED_GLASS_PANE);
		}

		ItemStack i;
		ItemMeta im;

		int maxPage = getMaxPage();

		if (page < 1) {
			page = 1;
		} else if (page > maxPage) {
			page = maxPage;
		}

		if (page > 1) {
			setSlot(getSize() - 9, new Button(LEFT_ARROW_ITEM) {
				@Override
				public void onClick(InventoryClickEvent e) {
					page -= 1;
					updateInventory();
				}
			});
		}

		if (page < maxPage) {
			setSlot(getSize() - 1, new Button(RIGHT_ARROW_ITEM) {
				@Override
				public void onClick(InventoryClickEvent e) {
					page += 1;
					updateInventory();
				}
			});
		}

		setSlot(getSize() - 4, new Button(SEARCH_SPECIFIC_PLAYER_ITEM) {
			@Override
			public void onClick(InventoryClickEvent e) {
				try {
					MainPluginClass.getPlugin().getMessagesListener().startListening(e.getWhoClicked().getUniqueId(),
							(message) -> {
								if (!message.equalsIgnoreCase("cancel")) {
									Optional<World> world = worlds.stream()
											.filter(w -> w.getName().equalsIgnoreCase(message)).findFirst();
									if (world.isEmpty()) {
										MessageBuilder.createMessage(MessageString.ERROR_WORLDNOTFOUND.applyPrefix())
												.sendMessage(e.getWhoClicked());
									}
									action.execute(world.orElse(null));
								} else {
									openInventory();
								}
								return true;
							});
					closeInventory();
					MessageBuilder
							.createMessage(MessageString.INVENTORY_SEARCHWORLDS_SEARCHSPECIFICWORLDINFO.applyPrefix())
							.sendMessage(e.getWhoClicked());
				} catch (PlayerAlreadyListeningException ex) {
					MessageBuilder.createMessage(MessageString.ERROR_CHATPROMPT_ALREADYPROMPTED.toString())
							.sendMessage(e.getWhoClicked());
				}
			}
		});

		setSlot(getSize() - 6, new Button(CLOSE_ITEM) {
			@Override
			public void onClick(InventoryClickEvent e) {
				goToPreviousHolder();
			}
		});

		if (worlds.size() != 0) {
			int slot = 0;
			for (World world : Arrays.copyOfRange(worlds.toArray(new World[worlds.size()]), ((page - 1) * 45),
					(page * 45))) {
				if (world == null) {
					break;
				}

				i = ItemStacksUtils.setSkin(Material.PLAYER_HEAD.getItemStack(), WORLD_SKIN);
				im = i.getItemMeta();

				im.setDisplayName(
						MessageBuilder
								.createMessage(
										TextInput.inst().text(MessageString.INVENTORY_SEARCHWORLDS_WORLDNAME.toString())
												.replacements(new TextReplacement("{world}", () -> world.getName())))
								.toString());
				i.setItemMeta(im);

				setSlot(slot++, new Button(i) {

					@Override
					public void onClick(InventoryClickEvent e) {
						closeInventory();
						action.execute(world);
					}

				});
			}
		}
	}

	public int getMaxPage() {
		return (int) ((worlds.size() + 44) / 45D);
	}

	public static interface Action {
		public abstract void execute(World selectedWorld);
	}

}

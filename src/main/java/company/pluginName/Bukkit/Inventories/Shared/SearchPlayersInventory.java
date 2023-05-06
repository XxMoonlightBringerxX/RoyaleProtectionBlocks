package company.pluginName.Bukkit.Inventories.Shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotMessages.Objects.TextInput;
import relampagorojo93.LibsCollection.SpigotMessages.Objects.TextReplacement;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;
import relampagorojo93.LibsCollection.Utils.Bukkit.Messages.Exceptions.PlayerAlreadyListeningException;

@Data
@EqualsAndHashCode(callSuper = false)
@Setter(lombok.AccessLevel.NONE)
public class SearchPlayersInventory extends PluginChestInventory {

	private static ItemStack SEARCH_SPECIFIC_PLAYER_ITEM;

	public static void initItems() {
		SEARCH_SPECIFIC_PLAYER_ITEM = ItemStacksUtils.createItemStack(
				ItemStacksUtils.setSkin(Material.PLAYER_HEAD.getItemStack(), SEARCH_SKIN),
				MessageBuilder.createMessage(MessageString.INVENTORY_SEARCHPLAYERS_SEARCHSPECIFICPLAYERNAME.toString())
						.toString());
	}

	private Action action;
	private List<Player> players;
	private int page = 1;

	public SearchPlayersInventory(Player player, Action action) {
		super(player);

		setSize(54);
		setName(MessageBuilder.createMessage(MessageString.INVENTORY_SEARCHPLAYERS_TITLE.toString()).toString());

		this.action = action;
		this.players = new ArrayList<>(Bukkit.getOnlinePlayers());
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
									@SuppressWarnings("deprecation")
									OfflinePlayer pl = Bukkit.getOfflinePlayer(message);
									if (pl == null || pl.getName() == null || !pl.hasPlayedBefore()) {
										pl = null;
										MessageBuilder.createMessage(MessageString.ERROR_PLAYERNOTFOUND.applyPrefix())
												.sendMessage(e.getWhoClicked());
									}
									action.execute(pl);
								} else {
									openInventory();
								}
								return true;
							});
					closeInventory();
					MessageBuilder
							.createMessage(MessageString.INVENTORY_SEARCHPLAYERS_SEARCHSPECIFICPLAYERINFO.applyPrefix())
							.sendMessage(e.getWhoClicked());
				} catch (PlayerAlreadyListeningException ex) {
					MessageBuilder.createMessage(MessageString.ERROR_CHATSEARCH_ALREADYSEARCHING.toString())
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

		if (players.size() != 0) {
			int slot = 0;
			for (Player player : Arrays.copyOfRange(players.toArray(new Player[players.size()]), ((page - 1) * 45),
					(page * 45))) {
				if (player == null) {
					break;
				}

				i = ItemStacksUtils.getPlayerHead(player);
				im = i.getItemMeta();

				im.setDisplayName(MessageBuilder
						.createMessage(
								TextInput.inst().text(MessageString.INVENTORY_SEARCHPLAYERS_PLAYERNAME.toString())
										.replacements(new TextReplacement("{player}", () -> player.getName())))
						.toString());
				i.setItemMeta(im);

				setSlot(slot++, new Button(i) {

					@Override
					public void onClick(InventoryClickEvent e) {
						closeInventory();
						action.execute(player);
					}

				});
			}
		}
	}

	public int getMaxPage() {
		return (int) ((players.size() + 44) / 45D);
	}

	public static interface Action {
		public abstract void execute(OfflinePlayer selectedPlayer);
	}

}

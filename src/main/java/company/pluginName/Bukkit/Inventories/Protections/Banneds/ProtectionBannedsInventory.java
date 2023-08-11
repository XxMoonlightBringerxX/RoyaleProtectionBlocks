package company.pluginName.Bukkit.Inventories.Protections.Banneds;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.Permissions;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Bukkit.Inventories.Shared.SearchPlayersInventory;
import company.pluginName.Exceptions.ProtectionBanneds.Delete.ProtectionBannedsDeleteException;
import company.pluginName.Exceptions.ProtectionBanneds.Save.ProtectionBannedsSaveException;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Utils.OfflinePlayerUtils;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import darkpanda73.PandaUtils.PandaColors.Objects.TextReplacement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

@Data
@EqualsAndHashCode(callSuper = false)
@Setter(lombok.AccessLevel.NONE)
public class ProtectionBannedsInventory extends PluginChestInventory {

	public static ItemStack SEARCH_PLAYER_BUTTON;

	public static void initItems() {
		SEARCH_PLAYER_BUTTON = ItemStacksUtils
				.createItemStack(ItemStacksUtils.setSkin(Material.PLAYER_HEAD.getItemStack(), SEARCH_SKIN),
						MessageBuilder
								.createMessage(MessageString.INVENTORY_PROTECTION_BANNEDS_SEARCHBANNEDNAME.toString())
								.toString());
	}

	private Protection protection;
	private List<UUID> banneds;
	private int page = 1;

	public ProtectionBannedsInventory(Player player, Protection protection) {
		super(player);

		setSize(27);
		setName(MessageBuilder
				.createMessage(TextInput.inst().text(MessageString.INVENTORY_PROTECTION_BANNEDS_TITLE.toString())
						.replacements(new TextReplacement("{protection}", () -> protection.getDisplayName())))
				.toString());

		this.protection = protection;
	}

	@Override
	public void updateContent() {
		clearSlots();

		banneds = getList();

		for (int i = getSize() - 9; i < getSize(); i++) {
			setSlot(i, GRAY_STAINED_GLASS_PANE);
		}

		setSlot(18, new Button(CLOSE_ITEM) {
			@Override
			public void onClick(InventoryClickEvent e) {
				goToPreviousHolder();
			}
		});

		setSlot(22, new Button(SEARCH_PLAYER_BUTTON) {
			@Override
			public void onClick(InventoryClickEvent e) {
				closeInventory();
				new SearchPlayersInventory(getPlayer(), player -> {
					if (player != null) {
						try {
							protection.getBanneds().add(getPlayer(), player.getUniqueId());
						} catch (ProtectionBannedsSaveException e1) {
							e1.sendError(getPlayer());
						}
						openInventory();
					} else {
						openInventory();
					}
				}).setPreviousHolder(getHolder()).openInventory(MainPluginClass.getPlugin());
			}
		});

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

		if (banneds.size() != 0) {
			int slot = 0;
			for (UUID banned : Arrays.copyOfRange(banneds.toArray(new UUID[banneds.size()]), ((page - 1) * 18),
					(page * 18))) {
				if (banned == null) {
					break;
				}

				OfflinePlayer pl = OfflinePlayerUtils.getOfflinePlayer(banned);

				setSlot(slot++,
						new Button(
								ItemStacksUtils.createItemStack(ItemStacksUtils.getPlayerHead(pl),
										MessageBuilder
												.createMessage(TextInput.inst()
														.text(MessageString.INVENTORY_PROTECTION_BANNEDS_BANNEDNAME
																.toString())
														.replacements(
																new TextReplacement("{player}", () -> pl.getName())))
												.toString(),
										MessageBuilder.createMessage(
												MessageString.INVENTORY_PROTECTION_BANNEDS_REMOVEBANNEDLORELINE
														.toString())
												.getStrings())) {
							@Override
							public void onClick(InventoryClickEvent e) {
								if (protection.isOwner(getPlayer().getUniqueId())
										|| getPlayer().hasPermission(Permissions.PROTECTION_BANNEDS_REMOVE_OTHERS)) {
									new ConfirmationInventory(getPlayer(), () -> {
										try {
											protection.getBanneds().remove(getPlayer(), banned);
										} catch (ProtectionBannedsDeleteException e1) {
											e1.sendError(getPlayer());
										}
										openInventory();
									}).openInventory();
								}
							}
						});
			}
		}
	}

	public int getMaxPage() {
		return (int) ((banneds.size() + 17) / 18D);
	}

	private List<UUID> getList() {
		try {
			return protection.getBanneds().list().stream().map(string -> UUID.fromString(string))
					.collect(Collectors.toList());
		} catch (Exception e) {
			return new LinkedList<>();
		}
	}

}

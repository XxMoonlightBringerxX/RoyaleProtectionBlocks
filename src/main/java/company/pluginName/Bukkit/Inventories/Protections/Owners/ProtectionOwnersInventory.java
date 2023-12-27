package company.pluginName.Bukkit.Inventories.Protections.Owners;

import java.util.ArrayList;
import java.util.Arrays;
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
import company.pluginName.Exceptions.ProtectionOwners.Delete.ProtectionOwnersDeleteException;
import company.pluginName.Exceptions.ProtectionOwners.Save.ProtectionOwnersSaveException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.Utils.OfflinePlayerUtils;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import darkpanda73.PandaUtils.PandaColors.Objects.TextReplacement;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.SkinUtilities;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

@Data
@EqualsAndHashCode(callSuper = false)
@Setter(lombok.AccessLevel.NONE)
public class ProtectionOwnersInventory extends PluginChestInventory {

	public static ItemStack SEARCH_PLAYER_BUTTON;

	public static void initItems() {
		SEARCH_PLAYER_BUTTON = ItemStacksUtils
				.createItemStack(SkinUtilities.NMS.setSkinSafe(Material.PLAYER_HEAD.getItemStack(), SEARCH_SKIN),
						MessageBuilder
								.createMessage(MessageString.INVENTORY_PROTECTION_OWNERS_SEARCHOWNERNAME.toString())
								.toString());
	}

	private Protection protection;
	private List<UUID> owners;
	private int page = 1;

	public ProtectionOwnersInventory(Player player, Protection protection) {
		super(player);

		setSize(27);
		setName(MessageBuilder
				.createMessage(TextInput.inst().text(MessageString.INVENTORY_PROTECTION_OWNERS_TITLE.toString())
						.replacements(new TextReplacement("{protection}", () -> protection.getDisplayName())))
				.toString());

		this.protection = protection;
	}

	@Override
	public void updateContent() {
		clearSlots();

		owners = getList();

		for (int i = getSize() - 9; i < getSize(); i++) {
			setSlot(i, GRAY_STAINED_GLASS_PANE);
		}

		setSlot(18, new Button(CLOSE_ITEM) {
			@Override
			public void onClick(InventoryClickEvent e) {
				goToPreviousHolder();
			}
		});

		if (protection.isMainOwner(getPlayer().getUniqueId())
				|| getPlayer().hasPermission(Permissions.PROTECTION_OWNERS_ADD_OTHERS)) {
			setSlot(22, new Button(SEARCH_PLAYER_BUTTON) {
				@Override
				public void onClick(InventoryClickEvent e) {
					closeInventory();
					new SearchPlayersInventory(getPlayer(), player -> {
						if (player != null) {
							try {
								protection.getOwners().add(getPlayer(), player.getUniqueId());
							} catch (ProtectionOwnersSaveException e1) {
								e1.sendError(getPlayer());
							}
							openInventory();
						} else {
							openInventory();
						}
					}).setPreviousHolder(getHolder()).openInventory(MainPluginClass.getPlugin());
				}
			});
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

		if (owners.size() != 0) {
			int slot = 0;
			for (UUID owner : Arrays.copyOfRange(owners.toArray(new UUID[owners.size()]), ((page - 1) * 18),
					(page * 18))) {
				if (owner == null) {
					break;
				}

				OfflinePlayer pl = OfflinePlayerUtils.getOfflinePlayer(owner);

				final boolean canRemove = protection.isMainOwner(getPlayer().getUniqueId())
						|| (protection.getOwners().list().contains(getPlayer().getUniqueId())
								&& pl.getUniqueId().equals(owner))
						|| getPlayer().hasPermission(Permissions.PROTECTION_OWNERS_ADD_OTHERS);

				List<String> lore = new ArrayList<>();
				if (canRemove) {
					lore.add(MessageString.INVENTORY_PROTECTION_OWNERS_REMOVEOWNERLORELINE.toString());
				}

				setSlot(slot++,
						new Button(
								ItemStacksUtils.createItemStack(ItemStacksUtils.getPlayerHead(pl),
										MessageBuilder
												.createMessage(TextInput.inst()
														.text(MessageString.INVENTORY_PROTECTION_OWNERS_OWNERNAME
																.toString())
														.replacements(
																new TextReplacement("{player}", () -> pl.getName())))
												.toString(),
										MessageBuilder.createMessage(lore.toArray(new String[lore.size()]))
												.getStrings())) {
							@Override
							public void onClick(InventoryClickEvent e) {
								if (canRemove) {
									new ConfirmationInventory(getPlayer(), () -> {
										try {
											protection.getOwners().remove(getPlayer(), owner);
										} catch (ProtectionOwnersDeleteException e1) {
											e1.sendError(getPlayer());
										}

										if (!owner.equals(getPlayer().getUniqueId())) {
											openInventory();
										}
									}).setPreviousInventory(null).openInventory(MainPluginClass.getPlugin());
								}
							}
						});
			}
		}
	}

	public int getMaxPage() {
		return (int) ((owners.size() + 17) / 18D);
	}

	private List<UUID> getList() {
		return protection.getOwners().list().stream().filter(uuid -> !this.protection.isMainOwner(uuid))
				.collect(Collectors.toList());
	}

}

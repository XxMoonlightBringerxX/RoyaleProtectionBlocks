package company.pluginName.Bukkit.Inventories.Protections;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Bukkit.Inventories.Protections.Banneds.ProtectionBannedsInventory;
import company.pluginName.Bukkit.Inventories.Protections.Flags.ProtectionFlagsInventory;
import company.pluginName.Bukkit.Inventories.Protections.Members.ProtectionMembersInventory;
import company.pluginName.Bukkit.Inventories.Protections.Owners.ProtectionOwnersInventory;
import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Exceptions.Protection.Delete.ProtectionDeleteException;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveException;
import company.pluginName.Modules.FilePckg.Messages.MessageList;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.Utils.OfflinePlayerUtils;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import darkpanda73.PandaUtils.PandaColors.Objects.TextReplacement;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Item;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Slot;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;
import relampagorojo93.LibsCollection.Utils.Bukkit.Messages.Exceptions.PlayerAlreadyListeningException;

public class ProtectionsManagerInventory extends PluginChestInventory {

	private Protection protection;

	public ProtectionsManagerInventory(Player player, Protection protection) {
		super(player);

		this.protection = protection;

		setSize(36);
	}

	@Override
	public Inventory getInventory() {
		setName(MessageBuilder
				.createMessage(TextInput.inst().text(MessageString.INVENTORY_PROTECTION_TITLE.toString())
						.replacements(new TextReplacement("{protection}", () -> protection.getDisplayName())))
				.toString());
		return super.getInventory();
	}

	@Override
	public void updateContent() {
		Location loc = protection.getProtectionBlockLocation();
		ProtectionBlock block = protection.getProtectionBlock().getObject();
		OfflinePlayer owner = OfflinePlayerUtils.getOfflinePlayer(protection.getOwnerUuid());

		TextReplacement[] protectionOwnerReplacements = new TextReplacement[] {
				new TextReplacement("{owner}", () -> owner.getName()),
				new TextReplacement("{world}", () -> protection.getWorldName()),
				new TextReplacement("{location_x}", () -> loc != null ? String.valueOf(loc.getBlockX()) : "???"),
				new TextReplacement("{location_y}", () -> loc != null ? String.valueOf(loc.getBlockY()) : "???"),
				new TextReplacement("{location_z}", () -> loc != null ? String.valueOf(loc.getBlockZ()) : "???"),
				new TextReplacement("{size_x}", () -> block != null ? String.valueOf(block.getBlocksX()) : "???"),
				new TextReplacement("{size_y}", () -> block != null ? String.valueOf(block.getBlocksY()) : "???"),
				new TextReplacement("{size_z}", () -> block != null ? String.valueOf(block.getBlocksZ()) : "???") };

		Slot protectionInfo = getSlot(11);
		if (protectionInfo == null) {
			protectionInfo = new Item(
					ItemStacksUtils
							.createItemStack(ItemStacksUtils.getPlayerHead(owner),
									MessageBuilder
											.createMessage(TextInput.inst()
													.text(MessageString.INVENTORY_PROTECTION_PROTECTIONINFONAME
															.toString())
													.replacements(protectionOwnerReplacements))
											.toString(),
									MessageBuilder.createMessage(TextInput.inst()
											.text(MessageList.INVENTORY_PROTECTION_PROTECTIONINFOLORE.toArray())
											.replacements(protectionOwnerReplacements)).getStrings()));
		}

		clearSlots();

		for (int i = 0; i < getSize(); i++) {
			setSlot(i, GRAY_STAINED_GLASS_PANE);
		}

		if (getPreviousHolder() != null && !(getPreviousHolder() instanceof ProtectionsManagerInventory)) {
			setSlot(getSize() - 9, new Button(CLOSE_ITEM) {
				@Override
				public void onClick(InventoryClickEvent e) {
					goToPreviousHolder();
				}
			});
		}

		setSlot(11, protectionInfo);

		setSlot(13, new Button(ItemStacksUtils.createItemStack(Material.ENDER_CHEST,
				MessageBuilder.createMessage(MessageString.INVENTORY_PROTECTION_OWNERSNAME.toString()).toString())) {

			@Override
			public void onClick(InventoryClickEvent e) {
				new ProtectionOwnersInventory(getPlayer(), protection).setPreviousHolder(getHolder())
						.openInventory(MainPluginClass.getPlugin());
			}

		});

		setSlot(14, new Button(ItemStacksUtils.createItemStack(Material.CHEST,
				MessageBuilder.createMessage(MessageString.INVENTORY_PROTECTION_MEMBERSNAME.toString()).toString())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				new ProtectionMembersInventory(getPlayer(), protection).setPreviousHolder(getHolder())
						.openInventory(MainPluginClass.getPlugin());
			}
		});

		setSlot(15, new Button(ItemStacksUtils.createItemStack(Material.TNT,
				MessageBuilder.createMessage(MessageString.INVENTORY_PROTECTION_BANNEDSNAME.toString()).toString())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				new ProtectionBannedsInventory(getPlayer(), protection).setPreviousHolder(getHolder())
						.openInventory(MainPluginClass.getPlugin());
			}
		});

		setSlot(16, new Button(ItemStacksUtils.createItemStack(Material.COMMAND_BLOCK,
				MessageBuilder.createMessage(MessageString.INVENTORY_PROTECTION_FLAGSNAME.toString()).toString())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				new ProtectionFlagsInventory(getPlayer(), protection).setPreviousHolder(getHolder())
						.openInventory(MainPluginClass.getPlugin());
			}
		});

		setSlot(22, new Button(ItemStacksUtils.createItemStack(Material.NAME_TAG,
				MessageBuilder.createMessage(MessageString.INVENTORY_PROTECTION_RENAMENAME.toString()).toString())) {

			@Override
			public void onClick(InventoryClickEvent e) {
				try {
					MainPluginClass.getPlugin().getMessagesListener().startListening(e.getWhoClicked().getUniqueId(),
							(message) -> {
								if (!message.equalsIgnoreCase("cancel")) {
									try {
										MainPluginClass.getPlugin().getProtectionsModule().renameProtection(getPlayer(),
												protection, message);
										MessageBuilder.createMessage(
												MessageString.MESSAGE_PROTECTIONS_RENAMEDSUCCESSFULLY.applyPrefix())
												.sendMessage(getPlayer());
									} catch (ProtectionSaveException e1) {
										e1.sendError(getPlayer());
									}
								}
								openInventory();
								return true;
							});
					closeInventory();
					MessageBuilder.createMessage(MessageString.INVENTORY_PROTECTION_TYPENEWNAMEINFO.applyPrefix())
							.sendMessage(e.getWhoClicked());
				} catch (PlayerAlreadyListeningException ex) {
					MessageBuilder.createMessage(MessageString.ERROR_CHATPROMPT_ALREADYPROMPTED.toString())
							.sendMessage(e.getWhoClicked());
				}
			}
		});

		if (protection.canToggleBlock(getPlayer())) {
			Boolean isProtectionBlock = protection.isProtectionBlock();

			if (isProtectionBlock != null) {
				ItemStack hideShowBlock = isProtectionBlock
						? ItemStacksUtils.createItemStack(Material.GRASS_BLOCK, MessageBuilder
								.createMessage(MessageString.INVENTORY_PROTECTION_HIDEBLOCKNAME.toString()).toString())
						: ItemStacksUtils.createItemStack(Material.GLASS, MessageBuilder
								.createMessage(MessageString.INVENTORY_PROTECTION_SHOWBLOCKNAME.toString()).toString());

				setSlot(23, new Button(hideShowBlock) {
					@Override
					public void onClick(InventoryClickEvent e) {
						if (protection.isProtectionBlockShown()) {
							protection.hideProtectionBlock();
						} else {
							Block block = protection.getProtectionBlockLocation().getBlock();
							if (block.getType() == Material.AIR.getMaterial()) {
								protection.showProtectionBlock();
							} else {
								MessageBuilder
										.createMessage(MessageString.ERROR_PROTECTIONS_BLOCKEDBYBLOCK.applyPrefix())
										.sendMessage(getPlayer());
								return;
							}
						}
						updateInventory();
					}
				});
			}
		}

		if (protection.canViewBoundaries(getPlayer())) {
			ItemStack viewActive = protection.isProtectionViewActive()
					? ItemStacksUtils.createItemStack(Material.ENDER_EYE, MessageBuilder
							.createMessage(MessageString.INVENTORY_PROTECTION_HIDEBOUNDARIESNAME.toString()).toString())
					: ItemStacksUtils.createItemStack(Material.ENDER_PEARL,
							MessageBuilder
									.createMessage(MessageString.INVENTORY_PROTECTION_SHOWBOUNDARIESNAME.toString())
									.toString());

			setSlot(24, new Button(viewActive) {

				@Override
				public void onClick(InventoryClickEvent e) {
					protection.toggleProtectionView();
					updateInventory();
				}
			});
		}

		if (protection.canDelete(getPlayer())) {
			setSlot(getSize() - 1,
					new Button(ItemStacksUtils.createItemStack(
							ItemStacksUtils.setSkin(Material.PLAYER_HEAD.getItemStack(), TRASH_SKIN),
							MessageBuilder
									.createMessage(MessageString.INVENTORY_PROTECTION_DELETEPROTECTIONNAME.toString())
									.toString())) {
						@Override
						public void onClick(InventoryClickEvent e) {
							new ConfirmationInventory(getPlayer(), () -> {
								try {
									if (protection.isProtectionBlockShown()) {
										protection.hideProtectionBlock();
									}

									MainPluginClass.getPlugin().getProtectionsModule().removeProtection(protection);

									HashMap<Integer, ItemStack> items = getPlayer().getInventory()
											.addItem(protection.getProtectionBlock().getObject().generateItem());

									if (items != null && !items.isEmpty()) {
										items.values().forEach(item -> getPlayer().getLocation().getWorld()
												.dropItem(getPlayer().getLocation(), item));
									}

									MessageBuilder
											.createMessage(
													MessageString.MESSAGE_PROTECTIONS_REMOVEDSUCCESSFULLY.applyPrefix())
											.sendMessage(getPlayer());
								} catch (ProtectionDeleteException e1) {
									e1.sendError(getPlayer());
									openInventory();
								}
							}).setPreviousHolder(getPreviousHolder()).openInventory(MainPluginClass.getPlugin());
						}
					});
		}
	}

}
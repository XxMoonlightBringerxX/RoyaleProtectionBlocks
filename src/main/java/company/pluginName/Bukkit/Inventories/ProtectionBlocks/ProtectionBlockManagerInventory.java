package company.pluginName.Bukkit.Inventories.ProtectionBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Exceptions.ProtectionBlocks.Delete.ProtectionBlocksDeleteException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveException;
import company.pluginName.Modules.FilePckg.Messages.MessageList;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.RecipesPckg.Objects.Recipe;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import darkpanda73.PandaUtils.PandaColors.Objects.TextReplacement;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;
import relampagorojo93.LibsCollection.Utils.Bukkit.Messages.Exceptions.PlayerAlreadyListeningException;

public class ProtectionBlockManagerInventory extends PluginChestInventory {

	public static ItemStack NO_ITEM_SET_ITEMSTACK;

	public static void initItems() {
		NO_ITEM_SET_ITEMSTACK = ItemStacksUtils.createItemStack(Material.RED_STAINED_GLASS_PANE, MessageBuilder
				.createMessage(MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_ITEMNOTSETNAME.toString()).toString());
	}

	private ProtectionBlock originalProtectionBlock;
	private ProtectionBlock copyOriginalProtectionBlock;
	private ProtectionBlock newProtectionBlock;
	private Recipe newRecipe;

	public ProtectionBlockManagerInventory(Player player) {
		this(player, null);
	}

	public ProtectionBlockManagerInventory(Player player, ProtectionBlock protectionBlock) {
		super(player);
		this.originalProtectionBlock = protectionBlock;
		this.copyOriginalProtectionBlock = protectionBlock != null ? new ProtectionBlock(originalProtectionBlock)
				: null;
		this.newProtectionBlock = protectionBlock != null ? new ProtectionBlock(originalProtectionBlock)
				: new ProtectionBlock();

		setSize(45);

		for (int i = 0; i < getSize(); i++) {
			setSlot(i, GRAY_STAINED_GLASS_PANE);
		}

		setSlot(getSize() - 9, new Button(CANCEL_ITEM) {
			@Override
			public void onClick(InventoryClickEvent e) {
				goToPreviousHolder();
			}
		});

		setSlot(getSize() - 1, new Button(CONFIRM_ITEM) {
			@Override
			public void onClick(InventoryClickEvent e) {
				try {
					if (originalProtectionBlock != null) {
						originalProtectionBlock.copy(newProtectionBlock);
						originalProtectionBlock.save(getPlayer());
					} else {
						newProtectionBlock.save(getPlayer());
					}

					if (newRecipe != null) {
						newRecipe.save();
					}

					if (originalProtectionBlock != null) {
						MessageBuilder
								.createMessage(MessageString.MESSAGE_PROTECTIONS_BLOCKS_SAVEDSUCCESSFULLY.applyPrefix())
								.sendMessage(getPlayer());
					} else {
						MessageBuilder
								.createMessage(
										MessageString.MESSAGE_PROTECTIONS_BLOCKS_CREATEDSUCCESSFULLY.applyPrefix())
								.sendMessage(getPlayer());
					}
					goToPreviousHolder();
				} catch (ProtectionBlocksSaveException | ProtectionBlocksDeleteException ex) {
					if (originalProtectionBlock != null) {
						originalProtectionBlock.copy(copyOriginalProtectionBlock);
					}
					ex.sendError(getPlayer());
				}
			}
		});
	}

	@Override
	public Inventory getInventory() {
		setName(MessageBuilder
				.createMessage(TextInput.inst().text(MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_TITLE.toString())
						.replacements(new TextReplacement("{block}",
								() -> newProtectionBlock.getId() != null ? newProtectionBlock.getId() : "???")))
				.toString());
		return super.getInventory();
	}

	@Override
	public void updateContent() {
		ItemStack i = (newProtectionBlock.getItem() != null ? newProtectionBlock.getItem() : NO_ITEM_SET_ITEMSTACK)
				.clone();

		ItemMeta im = i.getItemMeta();
		List<String> lore = im.hasLore() ? im.getLore() : new ArrayList<>();
		MessageList extraLore = newProtectionBlock.getItem() != null
				? MessageList.INVENTORY_PROTECTIONBLOCKS_MANAGE_ITEMLORE
				: MessageList.INVENTORY_PROTECTIONBLOCKS_MANAGE_ITEMNOTSETLORE;
		lore.addAll(MessageBuilder.createMessage(extraLore.getContent()).getStrings());
		im.setLore(lore);
		i.setItemMeta(im);

		setSlot(20, new Button(i) {
			@Override
			public void onClick(InventoryClickEvent e) {
				try {
					if (e.getClick() == ClickType.LEFT) {
						if (e.getCursor() != null && e.getCursor().getType() != Material.AIR.getMaterial()) {
							if (originalProtectionBlock != null
									&& e.getCursor().getType() != originalProtectionBlock.getItem().getType()) {
								MessageBuilder.createMessage(
										MessageString.ERROR_PROTECTIONS_BLOCKS_ITEMTYPESWAPNOTALLOWED.applyPrefix())
										.sendMessage(e.getWhoClicked());
								return;
							}

							newProtectionBlock.setItem(e.getCursor().clone());
							e.getWhoClicked().getOpenInventory().setCursor(null);
							updateInventory();
						} else if (newProtectionBlock.getItem() != null) {
							e.getWhoClicked().getOpenInventory().setCursor(newProtectionBlock.getItem());
							newProtectionBlock.setItem(null);
							updateInventory();
						}
					}
				} catch (ProtectionBlocksSaveException ex) {
					ex.sendError(getPlayer());
				}
			}
		});

		TextReplacement blocksXReplacement = new TextReplacement("{blocks_x}",
				() -> String.valueOf((newProtectionBlock.getBlocksX() * 2) + 1));

		setSlot(13,
				new Button(
						ItemStacksUtils
								.setSkin(ItemStacksUtils.createItemStack(Material.PLAYER_HEAD,
										MessageBuilder.createMessage(TextInput.inst()
												.text(MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_BLOCKSXNAME
														.toString())
												.replacements(blocksXReplacement)).toString(),
										MessageBuilder
												.createMessage(TextInput.inst()
														.text(MessageList.INVENTORY_PROTECTIONBLOCKS_MANAGE_BLOCKSXLORE
																.toArray())
														.replacements(blocksXReplacement))
												.getStrings()),
										X_LETTER_SKIN)) {
					@Override
					public void onClick(InventoryClickEvent e) {
						setBlocks(e, () -> newProtectionBlock.getBlocksX(),
								(blocks) -> newProtectionBlock.setBlocksX(blocks));
					}
				});

		TextReplacement blocksYReplacement = new TextReplacement("{blocks_y}",
				() -> newProtectionBlock.getBlocksY() == -1 ? MessageString.MESSAGE_GENERAL_NOLIMIT.toString()
						: String.valueOf((newProtectionBlock.getBlocksY() * 2) + 1));

		setSlot(14,
				new Button(
						ItemStacksUtils
								.setSkin(ItemStacksUtils.createItemStack(Material.PLAYER_HEAD,
										MessageBuilder.createMessage(TextInput.inst()
												.text(MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_BLOCKSYNAME
														.toString())
												.replacements(blocksYReplacement)).toString(),
										MessageBuilder
												.createMessage(TextInput.inst()
														.text(MessageList.INVENTORY_PROTECTIONBLOCKS_MANAGE_BLOCKSYLORE
																.toArray())
														.replacements(blocksYReplacement))
												.getStrings()),
										Y_LETTER_SKIN)) {
					@Override
					public void onClick(InventoryClickEvent e) {
						setBlocks(e, () -> newProtectionBlock.getBlocksY(),
								(blocks) -> newProtectionBlock.setBlocksY(blocks), true);
					}
				});

		TextReplacement blocksZReplacement = new TextReplacement("{blocks_z}",
				() -> String.valueOf((newProtectionBlock.getBlocksZ() * 2) + 1));

		setSlot(15,
				new Button(
						ItemStacksUtils
								.setSkin(ItemStacksUtils.createItemStack(Material.PLAYER_HEAD,
										MessageBuilder.createMessage(TextInput.inst()
												.text(MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_BLOCKSZNAME
														.toString())
												.replacements(blocksZReplacement)).toString(),
										MessageBuilder
												.createMessage(TextInput.inst()
														.text(MessageList.INVENTORY_PROTECTIONBLOCKS_MANAGE_BLOCKSZLORE
																.toArray())
														.replacements(blocksZReplacement))
												.getStrings()),
										Z_LETTER_SKIN)) {
					@Override
					public void onClick(InventoryClickEvent e) {
						setBlocks(e, () -> newProtectionBlock.getBlocksZ(),
								(blocks) -> newProtectionBlock.setBlocksZ(blocks));
					}
				});

		MessageString idName = this.originalProtectionBlock != null
				? MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_IDNOTMODIFIABLENAME
				: (this.newProtectionBlock.getId() != null ? MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_IDNAME
						: MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_IDNOTSETNAME);

		setSlot(31,
				new Button(ItemStacksUtils.createItemStack(Material.NAME_TAG, MessageBuilder
						.createMessage(TextInput.inst().text(idName.toString())
								.replacements(new TextReplacement("{block_id}",
										() -> newProtectionBlock.getId() != null ? newProtectionBlock.getId() : "")))
						.toString())) {

					@Override
					public void onClick(InventoryClickEvent e) {
						if (originalProtectionBlock == null) {
							try {
								MainPluginClass.getPlugin().getMessagesListener()
										.startListening(getPlayer().getUniqueId(), (message) -> {
											if (!message.equalsIgnoreCase("cancel")) {
												newProtectionBlock.setId(!message.isEmpty() ? message : null);
											}
											openInventory();
											return true;
										});
								closeInventory();
								MessageBuilder.createMessage(
										MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_IDSPECIFYINFO.applyPrefix())
										.sendMessage(e.getWhoClicked());
							} catch (PlayerAlreadyListeningException e1) {
								MessageBuilder
										.createMessage(MessageString.ERROR_CHATPROMPT_ALREADYPROMPTED.applyPrefix())
										.sendMessage(e.getWhoClicked());
							}
						}
					}
				});

		setSlot(32, new Button(ItemStacksUtils.createItemStack(Material.CRAFTING_TABLE, MessageBuilder
				.createMessage(MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_RECIPENAME.toString()).toString())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				new ProtectionBlockRecipeInventory(getPlayer(), newProtectionBlock, (recipe) -> newRecipe = recipe)
						.openInventory();
			}
		});

		MessageString permissionName = this.newProtectionBlock.getPermission() != null
				? MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_PERMISSIONNAME
				: MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_PERMISSIONNOTSETNAME;

		setSlot(33, new Button(ItemStacksUtils.createItemStack(Material.PAPER, MessageBuilder.createMessage(TextInput
				.inst().text(permissionName.toString())
				.replacements(new TextReplacement("{block_permission}",
						() -> newProtectionBlock.getPermission() != null ? newProtectionBlock.getPermission() : "")))
				.toString())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				if (e.getClick() == ClickType.LEFT) {
					try {
						MainPluginClass.getPlugin().getMessagesListener().startListening(getPlayer().getUniqueId(),
								(message) -> {
									if (!message.equalsIgnoreCase("cancel")) {
										newProtectionBlock.setPermission(!message.isEmpty() ? message : null);
									}
									openInventory();
									return true;
								});
						closeInventory();
						MessageBuilder.createMessage(
								MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_PERMISSIONSPECIFYINFO.applyPrefix())
								.sendMessage(e.getWhoClicked());
					} catch (PlayerAlreadyListeningException e1) {
						MessageBuilder.createMessage(MessageString.ERROR_CHATPROMPT_ALREADYPROMPTED.applyPrefix())
								.sendMessage(e.getWhoClicked());
					}
				} else if (e.getClick() == ClickType.RIGHT && newProtectionBlock.getPermission() != null) {
					newProtectionBlock.setPermission(null);
					updateInventory();
				}
			}
		});
	}

	public void setBlocks(InventoryClickEvent e, IntSupplier getBlocks, IntConsumer setBlocks) {
		this.setBlocks(e, getBlocks, setBlocks, false);
	}

	public void setBlocks(InventoryClickEvent e, IntSupplier getBlocks, IntConsumer setBlocks, boolean allowUnlimited) {
		int cur = getBlocks.getAsInt();
		if (e.getClick() == ClickType.SHIFT_LEFT) {
			try {
				MainPluginClass.getPlugin().getMessagesListener().startListening(getPlayer().getUniqueId(),
						(message) -> {
							if (!message.equalsIgnoreCase("cancel")) {
								try {
									int value = Integer.parseInt(message);

									if (value < 0 && (value != -1 || !allowUnlimited)) {
										MessageBuilder.createMessage(MessageString.ERROR_NUMBERBELOWZERO.applyPrefix())
												.sendMessage(e.getWhoClicked());
										return true;
									}

									setBlocks.accept(value == -1 ? value : value / 2);
								} catch (NumberFormatException e1) {
									MessageBuilder.createMessage(MessageString.ERROR_INVALIDNUMBER.applyPrefix())
											.sendMessage(e.getWhoClicked());
								}
							}
							openInventory();
							return true;
						});
				closeInventory();
				MessageBuilder
						.createMessage(MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_BLOCKSSPECIFYINFO.applyPrefix())
						.sendMessage(e.getWhoClicked());
			} catch (PlayerAlreadyListeningException e1) {
				MessageBuilder.createMessage(MessageString.ERROR_CHATPROMPT_ALREADYPROMPTED.applyPrefix())
						.sendMessage(e.getWhoClicked());
			}
		} else if (e.getClick() == ClickType.LEFT) {
			setBlocks.accept(++cur);
			updateInventory();
		} else if (e.getClick() == ClickType.RIGHT && (cur > (allowUnlimited ? -1 : 0))) {
			setBlocks.accept(--cur);
			updateInventory();
		}
	}

}

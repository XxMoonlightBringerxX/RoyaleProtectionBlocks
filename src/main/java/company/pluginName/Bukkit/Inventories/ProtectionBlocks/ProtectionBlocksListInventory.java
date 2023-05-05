package company.pluginName.Bukkit.Inventories.ProtectionBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import company.pluginName.MainPluginClass;
import company.pluginName.Permissions;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Exceptions.ProtectionBlocks.Delete.ProtectionBlocksDeleteException;
import company.pluginName.Modules.FilePckg.Messages.MessageList;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotMessages.Objects.TextInput;
import relampagorojo93.LibsCollection.SpigotMessages.Objects.TextReplacement;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

@Data
@EqualsAndHashCode(callSuper = false)
@Setter(lombok.AccessLevel.NONE)
public class ProtectionBlocksListInventory extends PluginChestInventory {

	private int page = 1;

	public ProtectionBlocksListInventory(Player player) {
		super(player);

		setSize(27);
		setName(MessageBuilder
				.createMessage(TextInput.inst().text(MessageString.INVENTORY_PROTECTIONBLOCKS_LIST_TITLE.toString()))
				.toString());
	}

	@Override
	public void updateContent() {
		clearSlots();

		Collection<ProtectionBlock> blocks = getList();

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
			setSlot(getSize() - 9, new Button(LEFT_ARROW_ITEM) {
				@Override
				public void onClick(InventoryClickEvent e) {
					page--;
					updateInventory();
				}
			});
		}

		if (page < maxPage) {
			setSlot(getSize() - 1, new Button(RIGHT_ARROW_ITEM) {
				@Override
				public void onClick(InventoryClickEvent e) {
					page++;
					updateInventory();
				}
			});
		}

		if (blocks.size() != 0) {
			int slot = 0;
			for (ProtectionBlock block : Arrays.copyOfRange(blocks.toArray(new ProtectionBlock[blocks.size()]),
					((page - 1) * 18), (page * 18))) {
				if (block == null) {
					break;
				}

				ItemStack item = block.generateItem();
				ItemMeta im = item.getItemMeta();

				List<String> lore = new ArrayList<>();
				if (im.hasLore()) {
					lore.addAll(im.getLore());
				}
				lore.addAll(MessageList.INVENTORY_PROTECTIONBLOCKS_LIST_BLOCKLORE.getContent());

				boolean canGet = getPlayer().hasPermission(Permissions.PROTECTION_BLOCKS_GIVE);
				boolean canDelete = getPlayer().hasPermission(Permissions.PROTECTION_BLOCKS_DELETE);

				if (canGet || canDelete) {
					lore.add("&0");

					if (canGet) {
						lore.add(MessageString.INVENTORY_PROTECTIONBLOCKS_LIST_BLOCKCOPYLORELINE.toString());
					}

					if (canDelete) {
						lore.add(MessageString.INVENTORY_PROTECTIONBLOCKS_LIST_BLOCKREMOVELORELINE.toString());
					}
				}

				setSlot(slot++,
						new Button(
								ItemStacksUtils
										.createItemStack(item,
												im.hasDisplayName()
														? MessageBuilder.createMessage(im.getDisplayName()).toString()
														: null,
												MessageBuilder
														.createMessage(TextInput.inst()
																.text(lore.toArray(new String[lore.size()]))
																.replacements(
																		new TextReplacement("{block_id}",
																				() -> block.getId()),
																		new TextReplacement("{blocks_x}",
																				() -> String.valueOf(
																						(block.getBlocksX() * 2) + 1)),
																		new TextReplacement(
																				"{blocks_y}",
																				() -> String.valueOf(
																						(block.getBlocksY() * 2) + 1)),
																		new TextReplacement("{blocks_z}",
																				() -> String.valueOf(
																						(block.getBlocksZ() * 2) + 1))))
														.getStrings())) {
							@Override
							public void onClick(InventoryClickEvent e) {
								if (e.getClick() == ClickType.LEFT && canGet) {
									e.getWhoClicked().getOpenInventory().setCursor(block.generateItem());
								} else if (e.getClick() == ClickType.RIGHT && canDelete) {
									new ConfirmationInventory(getPlayer(), () -> {
										try {
											MainPluginClass.getPlugin().getProtectionsModule()
													.removeProtectionBlock(getPlayer(), block);
										} catch (ProtectionBlocksDeleteException e1) {
											e1.sendError(getPlayer());
										}
									}).openInventory();
								}
							}
						});
			}
		}
	}

	public int getMaxPage() {
		return (int) ((getList().size() + 17) / 18D);
	}

	private Collection<ProtectionBlock> getList() {
		return MainPluginClass.getPlugin().getProtectionsModule().getProtectionBlockById().values();
	}

}

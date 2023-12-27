package company.pluginName.Bukkit.Inventories.ProtectionBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageList;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import company.pluginName.TemporaryModules.FilePckg.Settings.SettingBoolean;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import darkpanda73.PandaUtils.PandaColors.Objects.TextReplacement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;
import relampagorojo93.LibsCollection.Utils.Shared.Java.StringsHelper;

@Data
@EqualsAndHashCode(callSuper = false)
@Setter(lombok.AccessLevel.NONE)
public class ProtectionBlocksShopInventory extends PluginChestInventory {

	private int page = 1;

	public ProtectionBlocksShopInventory(Player player) {
		super(player);

		setSize(27);
		setName(MessageBuilder
				.createMessage(TextInput.inst().text(MessageString.INVENTORY_PROTECTIONBLOCKS_SHOP_TITLE.toString()))
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

				ItemStack item = block.getInformation().getItem().clone();
				ItemMeta im = item.getItemMeta();

				List<String> lore = new ArrayList<>();
				if (im.hasLore()) {
					lore.addAll(im.getLore());
				}
				lore.addAll(MessageList.INVENTORY_PROTECTIONBLOCKS_SHOP_PROTECTIONBLOCKLORE.getContent());

				setSlot(slot++,
						new Button(
								ItemStacksUtils.createItemStack(item,
										im.hasDisplayName() ? MessageBuilder.createMessage(im.getDisplayName())
												.toString() : null,
										MessageBuilder
												.createMessage(TextInput.inst()
														.text(lore.toArray(new String[lore.size()])).replacements(
																new TextReplacement(
																		"{blocks_x}",
																		() -> String.valueOf(
																				(block.getInformation().getBlocksX()
																						* 2) + 1)),
																new TextReplacement("{blocks_y}",
																		() -> block.getInformation().getBlocksY() == -1
																				? MessageString.MESSAGE_GENERAL_NOLIMIT
																						.toString()
																				: String.valueOf((block.getInformation()
																						.getBlocksY() * 2) + 1)),
																new TextReplacement("{blocks_z}", () -> String.valueOf(
																		(block.getInformation().getBlocksZ() * 2) + 1)),
																new TextReplacement("{block_id}",
																		() -> block.getInformation().getId()),
																new TextReplacement("{block_permission}",
																		() -> block.getInformation().getPermission()),
																new TextReplacement("{block_allowed_worlds}",
																		() -> block.getAllowedWorlds().get().stream()
																				.collect(Collectors.joining(", "))),
																new TextReplacement("{block_price}",
																		() -> StringsHelper.toCurrency(
																				block.getInformation().getPrice()))))
												.getStrings())) {
							@Override
							public void onClick(InventoryClickEvent e) {
								if (SettingBoolean.SETTINGS_PROTECTIONBLOCK_REQUESTCONFIRMATIONONPURCHASETHROUGHGUI
										.getContent()) {
									new ConfirmationInventory(getPlayer(), () -> {
										block.purchase(getPlayer());
									}).openInventory();
								} else {
									block.purchase(getPlayer());
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
		return MainPluginClass.getPlugin().getProtectionsModule().getProtectionBlocks().values().stream()
				.filter(block -> block.getInformation().isForSale()).collect(Collectors.toList());
	}

}

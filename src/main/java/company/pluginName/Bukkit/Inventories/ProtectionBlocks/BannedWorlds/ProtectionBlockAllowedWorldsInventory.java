package company.pluginName.Bukkit.Inventories.ProtectionBlocks.BannedWorlds;

import java.util.Arrays;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Bukkit.Inventories.Shared.ConfirmationInventory;
import company.pluginName.Bukkit.Inventories.Shared.SearchWorldsInventory;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
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
public class ProtectionBlockAllowedWorldsInventory extends PluginChestInventory {

	public static ItemStack SEARCH_PLAYER_BUTTON;

	public static void initItems() {
		SEARCH_PLAYER_BUTTON = ItemStacksUtils
				.createItemStack(SkinUtilities.NMS.setSkinSafe(Material.PLAYER_HEAD.getItemStack(), SEARCH_SKIN),
						MessageBuilder.createMessage(
								MessageString.INVENTORY_PROTECTIONBLOCKS_ALLOWEDWORLDS_SEARCHWORLDNAME.toString())
								.toString());
	}

	private ProtectionBlock protectionBlock;
	private Set<String> worlds;
	private int page = 1;

	public ProtectionBlockAllowedWorldsInventory(Player player, ProtectionBlock protectionBlock) {
		super(player);

		this.protectionBlock = protectionBlock;

		setSize(27);
		setName(MessageBuilder.createMessage(
				TextInput.inst().text(MessageString.INVENTORY_PROTECTIONBLOCKS_ALLOWEDWORLDS_TITLE.toString())
						.replacements(new TextReplacement("{block}",
								() -> protectionBlock.getInformation().getId() != null
										? protectionBlock.getInformation().getId()
										: "???")))
				.toString());
	}

	@Override
	public void updateContent() {
		clearSlots();

		worlds = getList();

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
				new SearchWorldsInventory(getPlayer(), world -> {
					if (world != null) {
						protectionBlock.getAllowedWorlds().add(world.getName());
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

		if (worlds.size() != 0) {
			int slot = 0;
			for (String allowed : Arrays.copyOfRange(worlds.toArray(new String[worlds.size()]), ((page - 1) * 18),
					(page * 18))) {
				if (allowed == null) {
					break;
				}

				setSlot(slot++, new Button(ItemStacksUtils.createItemStack(
						SkinUtilities.NMS.setSkinSafe(Material.PLAYER_HEAD.getItemStack(), WORLD_SKIN),
						MessageBuilder.createMessage(TextInput.inst()
								.text(MessageString.INVENTORY_PROTECTIONBLOCKS_ALLOWEDWORLDS_ALLOWEDWORLDNAME
										.toString())
								.replacements(new TextReplacement("{world}", () -> allowed))).toString(),
						MessageBuilder.createMessage(
								MessageString.INVENTORY_PROTECTIONBLOCKS_ALLOWEDWORLDS_REMOVEALLOWEDWORLDLORELINE
										.toString())
								.getStrings())) {
					@Override
					public void onClick(InventoryClickEvent e) {
						new ConfirmationInventory(getPlayer(), () -> {
							protectionBlock.getAllowedWorlds().remove(allowed);
						}).openInventory();
					}
				});
			}
		}
	}

	public int getMaxPage() {
		return (int) ((worlds.size() + 17) / 18D);
	}

	private Set<String> getList() {
		return protectionBlock.getAllowedWorlds().get();
	}

}

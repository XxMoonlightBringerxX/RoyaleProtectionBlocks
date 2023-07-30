package company.pluginName.Bukkit.Inventories.Protections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.sk89q.worldguard.protection.flags.Flags;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Modules.FilePckg.Messages.MessageList;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.Utils.ReflectUtils;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import darkpanda73.PandaUtils.PandaColors.Objects.TextReplacement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

@Data
@EqualsAndHashCode(callSuper = false)
@Setter(lombok.AccessLevel.NONE)
public class ProtectionsListInventory extends PluginChestInventory {

	private List<Protection> protections;
	private int page = 1;
	private OfflinePlayer owner;

	public ProtectionsListInventory(Player player) {
		this(player, player);
	}

	public ProtectionsListInventory(Player player, OfflinePlayer owner) {
		super(player);
		this.owner = owner;

		setSize(27);
		setName(MessageBuilder
				.createMessage(TextInput.inst().text(MessageString.INVENTORY_PROTECTION_LIST_TITLE.toString()))
				.toString());
	}

	@Override
	public void updateContent() {
		clearSlots();

		protections = getList();

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

		if (protections.size() != 0) {
			int slot = 0;
			for (Protection protection : Arrays.copyOfRange(protections.toArray(new Protection[protections.size()]),
					((page - 1) * 18), (page * 18))) {
				if (protection == null) {
					break;
				}

				final Object unknownLocation = protection.getProtectedRegion().getFlag(Flags.TELE_LOC);

				List<String> lore = new ArrayList<>();
				lore.addAll(MessageList.INVENTORY_PROTECTION_LIST_PROTECTIONLORE.getContent());
				lore.add("&0");
				if (unknownLocation != null || protection.isMainOwner(getPlayer().getUniqueId())) {
					lore.add(unknownLocation != null
							? MessageString.INVENTORY_PROTECTION_LIST_PROTECTIONTELEPORTLORELINE.toString()
							: MessageString.INVENTORY_PROTECTION_LIST_PROTECTIONTELEPORTNOHOMELORELINE.toString());
				}
				if (protection.canManage(getPlayer())) {
					lore.add(MessageString.INVENTORY_PROTECTION_LIST_PROTECTIONEDITLORELINE.toString());
				}

				Location loc = protection.getProtectionBlockLocation();

				ProtectionBlock block = protection.getProtectionBlock().getObject();

				TextReplacement[] replacements = {
						new TextReplacement("{protection}", () -> protection.getDisplayName()),
						new TextReplacement("{world}", () -> protection.getWorldName()),
						new TextReplacement("{location_x}",
								() -> loc != null ? String.valueOf(loc.getBlockX()) : "???"),
						new TextReplacement("{location_y}",
								() -> loc != null ? String.valueOf(loc.getBlockY()) : "???"),
						new TextReplacement("{location_z}",
								() -> loc != null ? String.valueOf(loc.getBlockZ()) : "???") };

				setSlot(slot++,
						new Button(ItemStacksUtils.createItemStack(block.getItem().clone(),
								MessageBuilder.createMessage(TextInput
										.inst().text(MessageString.INVENTORY_PROTECTION_LIST_PROTECTIONNAME.toString())
										.replacements(replacements)).toString(),
								MessageBuilder.createMessage(TextInput.inst()
										.text(lore.toArray(new String[lore.size()])).replacements(replacements))
										.getStrings())) {
							@Override
							public void onClick(InventoryClickEvent e) {
								if (e.getClick() == ClickType.LEFT) {
									try {
										World world = Bukkit.getWorld(protection.getWorldName());
										if (unknownLocation != null) {
											e.getWhoClicked().teleport(ReflectUtils.unknownLocationToBukkitLocation(
													world, protection.getProtectedRegion().getFlag(Flags.TELE_LOC)));
										}
									} catch (Exception e1) {
										MessageBuilder.createMessage(MessageString.ERROR_ERROR.applyPrefix())
												.sendMessage(e.getWhoClicked());
										e1.printStackTrace();
									}
								} else {
									new ProtectionsManagerInventory(getPlayer(), protection).openInventory();
								}
							}
						});
			}
		}
	}

	public int getMaxPage() {
		return (int) ((protections.size() + 17) / 18D);
	}

	private List<Protection> getList() {
		return MainPluginClass.getPlugin().getProtectionsModule().getAllowedProtections(getOwner());
	}

}

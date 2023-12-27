package company.pluginName.Bukkit.Inventories.Protections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageList;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import darkpanda73.PandaUtils.PandaColors.Objects.TextReplacement;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.SkinUtilities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;

@Data
@EqualsAndHashCode(callSuper = false)
@Setter(lombok.AccessLevel.NONE)
public class ProtectionsListInventory extends PluginChestInventory {

	public static ItemStack ALL_FILTER_ITEM;
	public static ItemStack OWN_FILTER_ITEM;
	public static ItemStack OTHERS_FILTER_ITEM;

	public static void initItems() {
		ALL_FILTER_ITEM = ItemStacksUtils.createItemStack(SkinUtilities.NMS.setSkinSafe(
				Material.PLAYER_HEAD.getItemStack(),
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmFmYTMxOGFjMDhmNjdhNWJjMjQ0M2QwM2VhZjI0ZWQyMjBkNDNiODMzYjNhZDU3YzNhNDk5Njk4ZmU3ZjUzZCJ9fX0="),
				MessageBuilder.createMessage(MessageString.INVENTORY_PROTECTION_LIST_FILTER_ALL.toString()).toString());
		OWN_FILTER_ITEM = ItemStacksUtils.createItemStack(SkinUtilities.NMS.setSkinSafe(
				Material.PLAYER_HEAD.getItemStack(),
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGRjYzE4OTYzM2M3ODljYjZkNWU3OGQxM2E1MDQzYjI2ZTdiNDBjZGI3Y2ZjNGUyM2FhMjI3OTU3NDk2N2I0In19fQ=="),
				MessageBuilder.createMessage(MessageString.INVENTORY_PROTECTION_LIST_FILTER_OWN.toString()).toString());
		OTHERS_FILTER_ITEM = ItemStacksUtils.createItemStack(SkinUtilities.NMS.setSkinSafe(
				Material.PLAYER_HEAD.getItemStack(),
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjk4YmM2M2YwNWY2Mzc4YmYyOWVmMTBlM2Q4MmFjYjNjZWI3M2E3MjBiZjgwZjMwYmM1NzZkMGFkOGM0MGNmYiJ9fX0="),
				MessageBuilder.createMessage(MessageString.INVENTORY_PROTECTION_LIST_FILTER_OTHERS.toString())
						.toString());
	}

	@AllArgsConstructor
	public static enum Filter {
		ALL(() -> ALL_FILTER_ITEM), OWN(() -> OWN_FILTER_ITEM), OTHERS(() -> OTHERS_FILTER_ITEM);

		private @Getter Supplier<ItemStack> headSupplier;

		public Filter previous() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}

		public Filter next() {
			return values()[(values().length + ordinal() + 1) % values().length];
		}
	}

	private List<Protection> protections;
	private int page = 1;
	private OfflinePlayer owner;
	private Filter filter = Filter.ALL;

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

		setSlot(getSize() - 5, new Button(filter.getHeadSupplier().get()) {
			@Override
			public void onClick(InventoryClickEvent e) {
				filter = filter.next();
				updateInventory();
			}
		});

		if (protections.size() != 0) {
			int slot = 0;
			for (Protection protection : Arrays.copyOfRange(protections.toArray(new Protection[protections.size()]),
					((page - 1) * 18), (page * 18))) {
				if (protection == null) {
					break;
				}

				final boolean canTeleport = protection.canTeleport(getPlayer());
				final boolean canManage = protection.canManage(getPlayer());

				Location homeLocation = protection.getHome();

				List<String> lore = new ArrayList<>();
				lore.addAll(MessageList.INVENTORY_PROTECTION_LIST_PROTECTIONLORE.getContent());
				lore.add("&0");

				if ((homeLocation != null && canTeleport) || protection.isMainOwner(getPlayer().getUniqueId())) {
					lore.add(homeLocation != null
							? MessageString.INVENTORY_PROTECTION_LIST_PROTECTIONTELEPORTLORELINE.toString()
							: MessageString.INVENTORY_PROTECTION_LIST_PROTECTIONTELEPORTNOHOMELORELINE.toString());
				}

				if (canManage) {
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

				setSlot(slot++, new Button(ItemStacksUtils.createItemStack(
						block != null ? block.getInformation().getItem().clone()
								: SkinUtilities.NMS.setSkinSafe(Material.PLAYER_HEAD.getItemStack(), UNKNOWN_SKIN),
						MessageBuilder.createMessage(
								TextInput.inst().text(MessageString.INVENTORY_PROTECTION_LIST_PROTECTIONNAME.toString())
										.replacements(replacements))
								.toString(),
						MessageBuilder.createMessage(
								TextInput.inst().text(lore.toArray(new String[lore.size()])).replacements(replacements))
								.getStrings())) {
					@Override
					public void onClick(InventoryClickEvent e) {
						if (e.getClick() == ClickType.LEFT) {
							if (homeLocation != null && protection.canTeleport(getPlayer())) {
								e.getWhoClicked().teleport(homeLocation);
							}
						} else {
							if (protection.canManage(getPlayer())) {
								new ProtectionsManagerInventory(getPlayer(), protection).openInventory();
							}
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
		return MainPluginClass.getPlugin().getProtectionsModule().getAllowedProtections(getOwner()).stream()
				.filter(protection -> {
					switch (filter) {
					case ALL:
						return true;
					case OWN:
						return protection.isMainOwner(getPlayer().getUniqueId());
					case OTHERS:
						return !protection.isMainOwner(getPlayer().getUniqueId());
					default:
						return false;
					}
				}).collect(Collectors.toList());
	}

}

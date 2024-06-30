package company.pluginName.Modules.ProtectionsPckg.Objects.Components;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import company.pluginName.Modules.SQLPckg.SQLService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ProtectionDisplayItem {

	private static final ItemStack UNKNOWN_DISPLAY_ITEM = ItemBuilder.inst().setMaterial(Material.PLAYER_HEAD)
			.setAmount(1)
			.setSkin(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcwNWZkOTRhMGM0MzE5MjdmYjRlNjM5YjBmY2ZiNDk3MTdlNDEyMjg1YTAyYjQzOWUwMTEyZGEyMmIyZTJlYyJ9fX0=")
			.build();

	@PandaInject
	private static SQLService sqlService;

	private @NonNull Protection protection;

	private @Getter(lombok.AccessLevel.NONE) @Setter(lombok.AccessLevel.NONE) ItemStack displayItem;
	private @Getter(lombok.AccessLevel.NONE) @Setter(lombok.AccessLevel.NONE) ItemStack protectionBlockDisplayItem;

	public void setAndSave(ItemStack displayItem) throws RoyaleProtectionBlocksExceptionImpl {
		this.setAndSave(null, displayItem);
	}

	public void setAndSave(Player pl, ItemStack displayItem) throws RoyaleProtectionBlocksExceptionImpl {
		this.set(pl, displayItem);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtection(this.protection);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public void set(ItemStack displayItem) throws RoyaleProtectionBlocksExceptionImpl {
		this.set(null, displayItem);
	}

	public void set(Player pl, ItemStack displayItem) throws RoyaleProtectionBlocksExceptionImpl {
		if (pl != null) {
			if (!ProtectionUtilities.canChangeDisplayItem(this.protection, pl)) {
				throw Exceptions.Protections.Save.PERMISSIONDENIED.generateException();
			}
		}

		this.displayItem = displayItem != null ? displayItem.clone() : null;

		if (this.displayItem != null) {
			this.displayItem.setAmount(1);
		}
	}

	public void reset() throws RoyaleProtectionBlocksExceptionImpl {
		this.reset(null);
	}

	public void reset(Player pl) throws RoyaleProtectionBlocksExceptionImpl {
		this.set(pl, null);
	}

	public void resetAndSave() throws RoyaleProtectionBlocksExceptionImpl {
		this.resetAndSave(null);
	}

	public void resetAndSave(Player pl) throws RoyaleProtectionBlocksExceptionImpl {
		this.set(pl, null);

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtection(this.protection);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public ItemStack get() {
		return this.displayItem;
	}

	public ItemStack getOrDefault() {
		if (this.displayItem != null) {
			return this.displayItem.clone();
		} else {
			if (this.protectionBlockDisplayItem == null) {
				ProtectionBlock block = this.protection.getProtectionBlock().getObject();
				if (block != null) {
					this.protectionBlockDisplayItem = block.getInformation().getItem().clone();
				}
			}

			if (this.protectionBlockDisplayItem != null) {
				return this.protectionBlockDisplayItem;
			}
		}
		return UNKNOWN_DISPLAY_ITEM.clone();
	}

}

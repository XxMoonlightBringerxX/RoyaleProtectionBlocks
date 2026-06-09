package company.pluginName.Modules.PermissionsPckg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.Services.PandaPermissionsModule.PandaPermissionsService;
import darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects.PandaCustomizablePermission;
import darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects.PandaParametizedPermission;
import darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects.PandaPermission;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import royale.RoyaleProtectionBlocks.Plugin.API.Interfaces.ProtectionBlocks.IProtectionBlock;

public class PermissionsService extends PandaPermissionsService {

	// Normal user permissions
	public static final PandaPermission TELEPORT = new PandaCustomizablePermission("Teleport.Value",
			"protectionblocks.teleport");
	public static final PandaPermission TELEPORT_OTHERS = new PandaCustomizablePermission("Teleport.Others",
			"protectionblocks.teleport.others");
	public static final PandaPermission TELEPORT_BYPASS = new PandaCustomizablePermission("Teleport.Bypass",
			"protectionblocks.teleport.bypass");

	public static final PandaPermission LIST_OTHERS = new PandaCustomizablePermission("List.Others",
			"protectionblocks.list.others");

	public static final PandaPermission DELETE_OTHERS = new PandaCustomizablePermission("Delete.Others",
			"protectionblocks.delete.others");

	public static final PandaPermission MANAGE_OTHERS = new PandaCustomizablePermission("Manage.Others",
			"protectionblocks.manage.others");

	public static final PandaPermission MEMBERS_ADD_OTHERS = new PandaCustomizablePermission("Members.Add.Others",
			"protectionblocks.members.add.others");
	public static final PandaPermission MEMBERS_REMOVE_OTHERS = new PandaCustomizablePermission("Members.Remove.Others",
			"protectionblocks.members.remove.others");

	public static final PandaPermission BANNEDS_ADD_OTHERS = new PandaCustomizablePermission("Banneds.Add.Others",
			"protectionblocks.banneds.add.others");
	public static final PandaPermission BANNEDS_REMOVE_OTHERS = new PandaCustomizablePermission("Banneds.Remove.Others",
			"protectionblocks.banneds.remove.others");
	public static final PandaPermission BANNEDS_BYPASS = new PandaCustomizablePermission("Banneds.Bypass",
			"protectionblocks.banneds.bypass");

	public static final PandaPermission KICK_OTHERS = new PandaCustomizablePermission("Kick.Others",
			"protectionblocks.kick.others");
	public static final PandaPermission KICK_BYPASS = new PandaCustomizablePermission("Kick.Bypass",
			"protectionblocks.kick.bypass");

	public static final PandaPermission OWNERS_ADD_OTHERS = new PandaCustomizablePermission("Owners.Add.Others",
			"protectionblocks.owners.add.others");
	public static final PandaPermission OWNERS_REMOVE_OTHERS = new PandaCustomizablePermission("Owners.Remove.Others",
			"protectionblocks.owners.remove.others");

	public static final PandaPermission INFO_OTHERS = new PandaCustomizablePermission("Info.Others",
			"protectionblocks.info.others");

	public static final PandaPermission FLY = new PandaCustomizablePermission("Fly.Value", "protectionblocks.fly");
	public static final PandaPermission FLY_BYPASS = new PandaCustomizablePermission("Fly.Bypass",
			"protectionblocks.fly.bypass");

	public static final PandaPermission ECONOMY_BYPASS = new PandaCustomizablePermission("Economy.Bypass",
			"protectionblocks.economy.bypass");

	public static final PandaPermission PRIORITY_OTHERS = new PandaCustomizablePermission("Priority.Others",
			"protectionblocks.priority.others");

	public static final PandaPermission MERGE_OTHERS = new PandaCustomizablePermission("Merge.Others",
			"protectionblocks.merge.others");

	public static final PandaPermission SPLIT_OTHERS = new PandaCustomizablePermission("Split.Others",
			"protectionblocks.split.others");

	public static final PandaPermission HIDE_OTHERS = new PandaCustomizablePermission("Hide.Others",
			"protectionblocks.hide.others");

	public static final PandaPermission SHOW_OTHERS = new PandaCustomizablePermission("Show.Others",
			"protectionblocks.show.others");

	public static final PandaPermission TRANSFER_OTHERS = new PandaCustomizablePermission("Transfer.Others",
			"protectionblocks.transfer.others");

	public static final PandaPermission SELL_OTHERS = new PandaCustomizablePermission("Sell.Others",
			"protectionblocks.sell.others");

	public static final PandaPermission ADMIN_BLOCK_BYPASS = new PandaCustomizablePermission("Admin.Block.Bypass",
			"protectionblocks.admin.block.bypass");

	public static final PandaPermission BLOCKS_CREATE = new PandaCustomizablePermission("Blocks.Create",
			"protectionblocks.blocks.create");
	public static final PandaPermission BLOCKS_EDIT = new PandaCustomizablePermission("Blocks.Edit",
			"protectionblocks.blocks.edit");
	public static final PandaPermission BLOCKS_DELETE = new PandaCustomizablePermission("Blocks.Delete",
			"protectionblocks.blocks.delete");

	public static final PandaPermission STAFFMODE = new PandaCustomizablePermission("Staff-mode",
			"protectionblocks.staffmode");

	public static final PandaParametizedPermission PRIORITY_MAX_TEMPLATE = new PandaParametizedPermission(
			"Priority.Max.Template", "protectionblocks.priority.max.{max}");
	public static final PandaPermission PRIORITY_MAX_BYPASS = new PandaCustomizablePermission("Priority.Max.Bypass",
			"protectionblocks.priority.max.bypass");

	public static final PandaParametizedPermission MAX_TEMPLATE = new PandaParametizedPermission("Max.Template",
			"protectionblocks.max.{max}");
	public static final PandaPermission MAX_BYPASS = new PandaCustomizablePermission("Max.Bypass",
			"protectionblocks.max.bypass");

	public static final PandaParametizedPermission BLOCK_MAX_TEMPLATE = new PandaParametizedPermission(
			"Blocks.Max.Template", "protectionblocks.{block}.max.{max}");
	public static final PandaParametizedPermission BLOCK_MAX_BYPASS = new PandaParametizedPermission(
			"Blocks.Max.Bypass", "protectionblocks.{block}.max.bypass");

	public static Integer getPriorityMaxAvailable(Player pl) {
		return getPriorityMaxAvailable(pl, null);
	}

	public static Integer getPriorityMaxAvailable(Player pl, Integer defaultIfNull) {
		return getMaxCapacity(
				PRIORITY_MAX_TEMPLATE.findPermissions(pl).entrySet().stream()
						.map(entry -> Pair.of(entry.getValue().get("max"), stringToInt(entry.getValue().get("max")))),
				defaultIfNull);
	}

	public static Integer getGeneralMaxCapacity(Player pl) {
		return getGeneralMaxCapacity(pl, null);
	}

	public static Integer getGeneralMaxCapacity(Player pl, Integer defaultIfNull) {
		return getMaxCapacity(
				MAX_TEMPLATE.findPermissions(pl).entrySet().stream()
						.map(entry -> Pair.of(entry.getValue().get("max"), stringToInt(entry.getValue().get("max")))),
				defaultIfNull);
	}

	public static Integer getPerBlockMaxCapacity(Player pl, IProtectionBlock block) {
		return getPerBlockMaxCapacity(pl, block, null);
	}

	public static Integer getPerBlockMaxCapacity(Player pl, IProtectionBlock block, Integer defaultIfNull) {
		return getMaxCapacity(
				BLOCK_MAX_TEMPLATE.findPermissions(pl).entrySet().stream()
						.filter(entry -> block.getId().equalsIgnoreCase(entry.getValue().getOrDefault("block", null)))
						.map(entry -> Pair.of(entry.getValue().get("max"), stringToInt(entry.getValue().get("max")))),
				defaultIfNull);
	}

	public static Map<String, Integer> getAllPerBlockMaxCapacity(Player pl) {
		Map<String, List<Pair<String, Integer>>> map = new HashMap<>();

		BLOCK_MAX_TEMPLATE.findPermissions(pl).entrySet().stream()
				.filter(entry -> entry.getValue().containsKey("block"))
				.forEach(entry -> map.computeIfAbsent(entry.getValue().get("block"), (key) -> new ArrayList<>())
						.add(Pair.of(entry.getValue().get("max"), stringToInt(entry.getValue().get("max")))));

		return map.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, entry -> getMaxCapacity(entry.getValue().stream(), null)));
	}

	public static Integer stringToInt(String number) {
		try {
			int hashIndex = number != null ? number.indexOf("#") : -1;
			return number != null ? Integer.parseInt(hashIndex != -1 ? number.substring(0, hashIndex) : number) : null;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static Integer getMaxCapacity(Stream<Pair<String, Integer>> stream, Integer defaultIfNull) {
		AtomicReference<Integer> incrementAmount = new AtomicReference<>();
		AtomicReference<Integer> maxAmount = new AtomicReference<>();

		stream.forEach(pair -> {
			if (pair.getFirst() != null && pair.getSecond() != null) {
				if (pair.getFirst().charAt(0) == '+' || pair.getFirst().charAt(0) == '-') {
					incrementAmount.updateAndGet((i) -> i != null ? (i + pair.getSecond()) : pair.getSecond());
				} else {
					maxAmount.updateAndGet((i) -> (i == null || i < pair.getSecond()) ? pair.getSecond() : i);
				}
			}
		});

		if (incrementAmount.get() != null) {
			maxAmount.updateAndGet((i) -> i != null ? (i + incrementAmount.get()) : null);
		}

		return maxAmount.get() != null ? maxAmount.get() : defaultIfNull;
	}

}

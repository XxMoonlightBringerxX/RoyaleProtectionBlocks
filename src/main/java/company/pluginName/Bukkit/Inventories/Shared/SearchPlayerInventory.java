package company.pluginName.Bukkit.Inventories.Shared;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Defaults.DefaultSearchPlayerInventory;

@Inventory("searchplayer")
public class SearchPlayerInventory extends DefaultSearchPlayerInventory {

	private Function<OfflinePlayer, Boolean> filterFunction;

	public SearchPlayerInventory(Player player, Consumer<OfflinePlayer> action) {
		super(player, action);
	}

	public SearchPlayerInventory(Player player, Consumer<OfflinePlayer> action,
			Function<OfflinePlayer, Boolean> filterFunction) {
		super(player, action);
		this.filterFunction = filterFunction;
	}

	@Override
	protected List<? extends OfflinePlayer> getEntityList() {
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		players.removeIf(p -> !getPlayer().canSee(p) || (filterFunction != null && !filterFunction.apply(p)));

		return players;
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle()).process().toString();
	}

}

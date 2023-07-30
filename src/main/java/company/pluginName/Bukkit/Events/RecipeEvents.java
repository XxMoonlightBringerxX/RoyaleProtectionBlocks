package company.pluginName.Bukkit.Events;

import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.RecipesPckg.Objects.Recipe;
import darkpanda73.PandaUtils.PandaCraftableItems.Events.CustomRecipeEvent;
import darkpanda73.PandaUtils.PandaCraftableItems.Module.CustomRecipeModule;

public class RecipeEvents extends CustomRecipeEvent<Recipe> {

	@Override
	public CustomRecipeModule<Recipe> getCustomRecipeModule() {
		return MainPluginClass.getPlugin().getRecipesModule();
	}

	@Override
	public boolean canCraft(Player player, Recipe customRecipe) {
		return customRecipe.getPermission() == null || customRecipe.getPermission().isEmpty()
				|| player.hasPermission(customRecipe.getPermission());
	}

}

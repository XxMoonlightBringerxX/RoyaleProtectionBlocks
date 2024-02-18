package company.pluginName.Bukkit.Events;

import org.bukkit.entity.Player;

import company.pluginName.Modules.RecipesPckg.RecipesService;
import company.pluginName.Modules.RecipesPckg.Objects.Recipe;
import darkpanda73.PandaUtils.PandaCraftableItems.CustomRecipesService;
import darkpanda73.PandaUtils.PandaCraftableItems.Events.CustomRecipesListener;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;

@PandaListener
public class RecipeEvents extends CustomRecipesListener<Recipe> {

	@PandaInject
	private RecipesService recipesService;

	@Override
	public CustomRecipesService<Recipe> getCustomRecipeService() {
		return recipesService;
	}

	@Override
	public boolean canCraft(Player player, Recipe customRecipe) {
		return customRecipe.getPermission() == null || customRecipe.getPermission().isEmpty()
				|| player.hasPermission(customRecipe.getPermission());
	}

}

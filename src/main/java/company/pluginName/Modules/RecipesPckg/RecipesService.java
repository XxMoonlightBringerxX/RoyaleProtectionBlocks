package company.pluginName.Modules.RecipesPckg;

import java.util.List;

import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.RecipesPckg.Objects.Recipe;
import company.pluginName.Modules.SQLPckg.SQLService;
import darkpanda73.PandaUtils.PandaCraftableItems.CustomRecipesService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;

public class RecipesService extends CustomRecipesService<Recipe> {

	@PandaInject
	private SQLService sqlService;

	@Override
	public List<Recipe> getCustomRecipes() {
		return sqlService.getRecipes();
	}

	public Recipe findRecipeByProtectionBlock(ProtectionBlock protectionBlock) {
		return this.customRecipes.values().stream().filter(
				recipe -> recipe.getProtectionBlock().getIdentifier().equals(protectionBlock.getInformation().getId()))
				.findFirst().orElse(null);
	}

}

package company.pluginName.Modules.RecipesPckg;

import java.util.List;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.RecipesPckg.Objects.Recipe;
import darkpanda73.PandaUtils.PandaCraftableItems.Module.CustomRecipeModule;

public class RecipesModule extends CustomRecipeModule<Recipe> {

	@Override
	public List<Recipe> getCustomRecipes() {
		return MainPluginClass.getPlugin().getSqlModule().getRecipes();
	}

	public Recipe findRecipeByProtectionBlock(ProtectionBlock protectionBlock) {
		return this.customRecipes.values().stream().filter(
				recipe -> recipe.getProtectionBlock().getIdentifier().equals(protectionBlock.getInformation().getId()))
				.findFirst().orElse(null);
	}

}

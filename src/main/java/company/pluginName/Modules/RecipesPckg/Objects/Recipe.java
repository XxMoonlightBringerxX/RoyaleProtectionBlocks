package company.pluginName.Modules.RecipesPckg.Objects;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.PermissionsPckg.PermissionsService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Reference.ReferencedProtectionBlock;
import company.pluginName.Modules.RecipesPckg.RecipesService;
import company.pluginName.Modules.SQLPckg.SQLService;
import darkpanda73.PandaUtils.PandaCraftableItems.Objects.CustomRecipe;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RequiredArgsConstructor
@Setter(lombok.AccessLevel.NONE)
public class Recipe extends CustomRecipe {

	@PandaInject
	private static MainPluginClass plugin;

	@PandaInject
	private static SQLService sqlService;

	@PandaInject
	private static RecipesService recipesService;

	private @NonNull ReferencedProtectionBlock protectionBlock;
	private @Setter String permission;

	public void copy(Recipe customRecipe) {
		this.protectionBlock = customRecipe.protectionBlock;
		this.recipe = Arrays.copyOf(customRecipe.recipe, customRecipe.recipe.length);
		this.permission = customRecipe.getPermission();
	}

	@Override
	public ItemStack getItemStack() {
		return protectionBlock.getObject().getInformation().getItem();
	}

	@Override
	public MainPluginClass getPlugin() {
		return plugin;
	}

	@Override
	public String getRecipeId() {
		return "protectionblocks_recipe_" + protectionBlock.getObject().getInformation().getId();
	}

	@Override
	public ItemStack getResult() {
		try {
			return protectionBlock.getObject().getInformation().generateItem();
		} catch (RoyaleProtectionBlocksExceptionImpl e) {
			e.sendError(Bukkit.getConsoleSender());
			return null;
		}
	}

	public void save() throws RoyaleProtectionBlocksExceptionImpl {
		this.save(null);
	}

	public void save(Player player) throws RoyaleProtectionBlocksExceptionImpl, RoyaleProtectionBlocksExceptionImpl {
		if (player != null) {
			if (!PermissionsService.BLOCKS_EDIT.hasPermission(player)) {
				throw Exceptions.Protections.Blocks.Save.PERMISSIONDENIED.generateException();
			}
		}

		if (this.hasRecipe()) {
			sqlService.saveRecipe(this);
			recipesService.registerRecipe(this);
		} else {
			sqlService.deleteRecipe(this);
			recipesService.unregisterRecipe(this);
		}
	}

}

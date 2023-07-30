package company.pluginName.Modules.RecipesPckg.Objects;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.Permissions;
import company.pluginName.Exceptions.ProtectionBlocks.Delete.ProtectionBlocksDeleteException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveDeniedException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveException;
import company.pluginName.Modules.ProtectionsPckg.Objects.ReferencedObjects.ReferencedProtectionBlock;
import darkpanda73.PandaUtils.PandaCraftableItems.Objects.CustomRecipe;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import relampagorojo93.LibsCollection.SpigotPlugin.MainClass;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RequiredArgsConstructor
@Setter(lombok.AccessLevel.NONE)
public class Recipe extends CustomRecipe {

	private @NonNull ReferencedProtectionBlock protectionBlock;
	private @Setter String permission;

	public void copy(Recipe customRecipe) {
		this.protectionBlock = customRecipe.protectionBlock;
		this.recipe = Arrays.copyOf(customRecipe.recipe, customRecipe.recipe.length);
	}

	@Override
	public ItemStack getItemStack() {
		return protectionBlock.getObject().getItem();
	}

	@Override
	public MainClass getPlugin() {
		return MainPluginClass.getPlugin();
	}

	@Override
	public String getRecipeId() {
		return "protectionblocks_recipe_" + protectionBlock.getObject().getId();
	}

	@Override
	public ItemStack getResult() {
		return protectionBlock.getObject().generateItem();
	}

	public void save() throws ProtectionBlocksSaveException, ProtectionBlocksDeleteException {
		this.save(null);
	}

	public void save(Player player) throws ProtectionBlocksSaveException, ProtectionBlocksDeleteException {
		if (player != null) {
			if (!player.hasPermission(Permissions.PROTECTION_BLOCKS_EDIT)) {
				throw new ProtectionBlocksSaveDeniedException();
			}
		}

		if (this.hasRecipe()) {
			MainPluginClass.getPlugin().getSqlModule().saveRecipe(this);
			MainPluginClass.getPlugin().getRecipesModule().registerRecipe(this);
		} else {
			MainPluginClass.getPlugin().getSqlModule().deleteRecipe(this);
			MainPluginClass.getPlugin().getRecipesModule().unregisterRecipe(this);
		}
	}

}

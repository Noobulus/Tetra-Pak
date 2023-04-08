package mod.noobulus.tetrapak.jei;

/*import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mod.noobulus.tetrapak.BuildConfig;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.Collection;

@MethodsReturnNonnullByDefault
public interface CompatJeiRecipe<T extends Recipe<Container>> extends IRecipeCategory<T> {

	default void addRecipes(IRecipeRegistration recipeRegistration, RecipeManager manager) {
		recipeRegistration.addRecipes(manager.byType(getVanillaRecipeType()).values(), getUid());
	}

	default void addRecipeCatalysts(IRecipeCatalystRegistration recipeCatalystRegistration) {
		getCatalystIcons().forEach(stack -> recipeCatalystRegistration.addRecipeCatalyst(stack, getUid()));
	}

	default void registerCategory(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(this);
	}

	default Collection<ItemStack> getCatalystIcons() {
		return new ArrayList<>();
	}

	RecipeType<T> getVanillaRecipeType();

	@Override
	default Component getTitle() {
		return new TranslatableComponent(BuildConfig.MODID + ".recipe." + getUid().getPath());
	}
}
*/
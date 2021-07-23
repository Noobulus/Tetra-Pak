package mod.noobulus.tetrapak.jei;

import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mod.noobulus.tetrapak.BuildConfig;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collection;

@MethodsReturnNonnullByDefault
public interface CompatJeiRecipe<T extends IRecipe<IInventory>> extends IRecipeCategory<T> {

	default void addRecipes(IRecipeRegistration recipeRegistration, RecipeManager manager) {
		recipeRegistration.addRecipes(manager.byType(getRecipeType()).values(), getUid());
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

	IRecipeType<T> getRecipeType();

	@Override
	default String getTitle() {
		return new TranslationTextComponent(BuildConfig.MODID + ".recipe." + getUid().getPath()).getString();
	}
}

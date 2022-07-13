package mod.noobulus.tetrapak.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mod.noobulus.tetrapak.TetraPak;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.ParametersAreNonnullByDefault;

@JeiPlugin
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class TetraPakJei implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return TetraPak.asId("jei_plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		DynamicJeiCompat.getAllLoadedCategories().forEach(category -> category.registerCategory(registration));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		DynamicJeiCompat.getAllLoadedCategories().forEach(category -> category.addRecipeCatalysts(registration));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (Minecraft.getInstance().level == null) {
			TetraPak.LOGGER.warn("Can not register JEI recipes: world is null");
			return;
		}
		RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
		DynamicJeiCompat.getAllLoadedCategories().forEach(category -> category.addRecipes(registration, manager));
	}
}

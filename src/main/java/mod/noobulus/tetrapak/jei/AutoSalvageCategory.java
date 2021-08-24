package mod.noobulus.tetrapak.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.animations.AnimatedDeployer;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.create.recipes.SalvagingRecipe;
import mod.noobulus.tetrapak.util.LootLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import se.mickelus.tetra.items.modular.IModularItem;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AutoSalvageCategory implements CompatJeiRecipe<SalvagingRecipe> {
	private static final ItemStack hammer = makeHammer();
	private final IDrawable background;
	private final IDrawable icon;
	private final AnimatedDeployer deployer;

	public AutoSalvageCategory() {
		background = new EmptyBackground(177, 90);
		icon = new DoubleItemIcon(() -> new ItemStack(AllBlocks.DEPLOYER.get()), () -> hammer);
		deployer = new AnimatedDeployer();
	}

	private static ItemStack makeHammer() {
		Item modularDouble = ForgeRegistries.ITEMS.getValue(new ResourceLocation("tetra:modular_double"));
		if (modularDouble == null)
			return ItemStack.EMPTY;
		ItemStack itemStack = new ItemStack(modularDouble);

		IModularItem.putModuleInSlot(itemStack, "double/head_left", "double/basic_hammer_left", "double/basic_hammer_left_material", "basic_hammer/iron");
		IModularItem.putModuleInSlot(itemStack, "double/head_right", "double/basic_hammer_right", "double/basic_hammer_right_material", "basic_hammer/iron");
		IModularItem.putModuleInSlot(itemStack, "double/handle", "double/basic_handle", "double/basic_handle_material", "basic_handle/spruce");
		IModularItem.updateIdentifier(itemStack);

		return itemStack;
	}

	@Override
	public ResourceLocation getUid() {
		return SalvagingRecipe.ID;
	}

	@Override
	public Class<? extends SalvagingRecipe> getRecipeClass() {
		return SalvagingRecipe.class;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setIngredients(SalvagingRecipe salvagingRecipe, IIngredients iIngredients) {
		iIngredients.setInputIngredients(Arrays.asList(salvagingRecipe.startingItem, Ingredient.of(salvagingRecipe.toolExamples.get().stream())));
		iIngredients.setOutputs(VanillaTypes.ITEM, salvagingRecipe.contents.get().stream().map(LootLoader.LootSlot::asStack).collect(Collectors.toList()));
	}

	@Override
	public void setRecipe(IRecipeLayout iRecipeLayout, SalvagingRecipe salvagingRecipe, IIngredients iIngredients) {
		IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();

		itemStacks.init(0, true, 26, 66);
		itemStacks.set(0, iIngredients.getInputs(VanillaTypes.ITEM).get(0));
		itemStacks.init(1, true, 50, 19);
		itemStacks.set(1, iIngredients.getInputs(VanillaTypes.ITEM).get(1));

		Map<ItemStack, Supplier<ITextComponent>> tooltips = salvagingRecipe.contents.get()
			.stream()
			.collect(Collectors.toMap(LootLoader.LootSlot::asStack, lootSlot -> lootSlot));
		List<ItemStack> entries = new ArrayList<>(tooltips.keySet());
		for (int i = 0; i < entries.size(); i++) {
			int xOffset = i % 2 == 0 ? 0 : 19;
			int yOffset = (i / 2) * -19;
			itemStacks.init(i + 2, false, 117 + xOffset, 67 + yOffset);
			itemStacks.set(i + 2, entries.get(i));
		}

		iIngredients.getInputs(VanillaTypes.ITEM)
			.get(1)
			.stream()
			.findFirst()
			.ifPresent(t -> tooltips.put(t, () -> new TranslationTextComponent(BuildConfig.MODID + ".tool_type." + salvagingRecipe.toolType.getName())));

		iRecipeLayout.getItemStacks().addTooltipCallback(new JeiTooltipEntry<>(tooltips));
	}

	@Override
	public IRecipeType<SalvagingRecipe> getRecipeType() {
		return SalvagingRecipe.SalvagingRecipeType.INSTANCE;
	}

	@Override
	public Collection<ItemStack> getCatalystIcons() {
		return Collections.singletonList(new ItemStack(AllBlocks.DEPLOYER.get()));
	}

	@Override
	public void draw(SalvagingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SLOT.draw(matrixStack, 50, 19);
		AllGuiTextures.JEI_SLOT.draw(matrixStack, 26, 65);
		AllGuiTextures.JEI_SHADOW.draw(matrixStack, 62, 72);
		deployer.draw(matrixStack, getBackground().getWidth() / 2 - 13, 37);
	}
}

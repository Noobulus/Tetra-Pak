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
import mod.noobulus.tetrapak.TetraPak;
import mod.noobulus.tetrapak.create.recipes.SalvagingRecipe;
import mod.noobulus.tetrapak.util.LootLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import se.mickelus.tetra.items.modular.IModularItem;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AutoSalvageCategory implements CompatJeiRecipe<SalvagingRecipe> {
	public static final ResourceLocation ID = TetraPak.asId("auto_salvage");
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
		return ID;
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
		LootTableManager manager = LootLoader.getManager(Minecraft.getInstance().level);
		LootTable table = manager.get(salvagingRecipe.lootTable);
		List<LootLoader.LootSlot> lootSlots = LootLoader.crawlTable(table, manager);
		iIngredients.setInputIngredients(Arrays.asList(salvagingRecipe.startingItem, Ingredient.of(hammer)));
		iIngredients.setOutputs(VanillaTypes.ITEM, lootSlots.stream().map(slot -> new ItemStack(slot.item, slot.min)).collect(Collectors.toList()));
	}

	@Override
	public void setRecipe(IRecipeLayout iRecipeLayout, SalvagingRecipe salvagingRecipe, IIngredients iIngredients) {
		LootTableManager manager = LootLoader.getManager(Minecraft.getInstance().level);
		LootTable table = manager.get(salvagingRecipe.lootTable);
		List<LootLoader.LootSlot> lootSlots = LootLoader.crawlTable(table, manager);
		IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();

		itemStacks.init(0, true, 26, 66);
		itemStacks.set(0, iIngredients.getInputs(VanillaTypes.ITEM).get(0));
		itemStacks.init(1, true, 50, 19);
		itemStacks.set(1, iIngredients.getInputs(VanillaTypes.ITEM).get(1));

		for (int i = 0; i < lootSlots.size(); i++) {
			int xOffset = i % 2 == 0 ? 0 : 19;
			int yOffset = (i / 2) * -19;
			itemStacks.init(i + 2, false, 117 + xOffset, 57 + yOffset);
			itemStacks.set(i + 2, new ItemStack(lootSlots.get(i).item, lootSlots.get(i).min));
		}
	}

	@Override
	public IRecipeType<SalvagingRecipe> getRecipeType() {
		return SalvagingRecipe.SalvagingRecipeType.AUTOMATIC_SALVAGING;
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

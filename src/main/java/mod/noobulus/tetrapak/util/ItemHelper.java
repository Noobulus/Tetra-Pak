package mod.noobulus.tetrapak.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemHelper {
	private static final LazyLoadedValue<Method> arrowStackGetter = new LazyLoadedValue<>(() -> ObfuscationReflectionHelper.findMethod(AbstractArrow.class, "func_184550_j"));

	private ItemHelper() {
	}

	@Nullable
	public static ItemStack getThrownItemStack(@Nullable Entity e) {
		if (!(e instanceof AbstractArrow))
			return null;
		Method lookup = arrowStackGetter.get();
		lookup.setAccessible(true);
		Object result;
		try {
			result = lookup.invoke(e);
		} catch (IllegalAccessException | InvocationTargetException ignored) {
			return null;
		}
		if (!(result instanceof ItemStack))
			return null;
		return (ItemStack) result;
	}

	public static ItemStack smelt(ItemStack stack, Level world) { // this is just forge example code switched over to my mappings but we won't talk about that
		return world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), world)
			.map(SmeltingRecipe::getResultItem)
			.filter(itemStack -> !itemStack.isEmpty())
			.map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
			.orElse(stack);
	}

	public static ItemStack getItemOfDamgeSource(@Nullable DamageSource source) {
		if (source == null)
			return ItemStack.EMPTY;
		ItemStack thrownItem = ItemHelper.getThrownItemStack(source.getDirectEntity());
		if (thrownItem != null)
			return thrownItem;
		if (!(source.getEntity() instanceof LivingEntity livingEntity))
			return ItemStack.EMPTY;
		return livingEntity.getMainHandItem();
	}
}

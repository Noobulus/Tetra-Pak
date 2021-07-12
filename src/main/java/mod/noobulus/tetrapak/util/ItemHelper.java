package mod.noobulus.tetrapak.util;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.LazyValue;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.ItemHandlerHelper;
import se.mickelus.tetra.items.modular.ModularItem;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemHelper {
	private static final LazyValue<Method> arrowStackGetter = new LazyValue<>(() -> ObfuscationReflectionHelper.findMethod(AbstractArrowEntity.class, "func_184550_j"));

	private ItemHelper() {
	}

	@Nullable
	public static ItemStack getThrownItemStack(@Nullable Entity e) {
		if (!(e instanceof AbstractArrowEntity))
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

	public static ItemStack smelt(ItemStack stack, World world) { // this is just forge example code switched over to my mappings but we won't talk about that
		return world.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, new Inventory(stack), world)
			.map(FurnaceRecipe::getResultItem)
			.filter(itemStack -> !itemStack.isEmpty())
			.map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
			.orElse(stack);
	}

	@Nullable
	public static ItemStack getModularItemOfDamgeSource(@Nullable DamageSource source) {
		if (source == null)
			return null;
		if (!(source.getEntity() instanceof LivingEntity))
			return null;
		LivingEntity user = (LivingEntity) source.getEntity();
		ItemStack heldItem = user.getMainHandItem();

		if (heldItem.getItem() instanceof ModularItem)
			return heldItem;

		ItemStack thrownItem = ItemHelper.getThrownItemStack(source.getDirectEntity());
		if (thrownItem != null && thrownItem.getItem() instanceof ModularItem)
			return thrownItem;

		return null;
	}
}

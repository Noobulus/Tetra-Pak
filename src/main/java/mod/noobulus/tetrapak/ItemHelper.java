package mod.noobulus.tetrapak;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.LazyValue;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ItemHelper {
	private static final LazyValue<Method> arrowStackGetter = new LazyValue<>(() -> ObfuscationReflectionHelper.findMethod(AbstractArrowEntity.class, "func_184550_j"));

	private ItemHelper() {
	}

	/**
	 * @return the effect level of the given Item. 0 if the effect isn't on the item.
	 */
	public static int getEffectLevel(@Nullable ItemStack test, ItemEffect effect) {
		if (test == null || test.isEmpty() || !(test.getItem() instanceof ModularItem))
			return 0;
		ModularItem item = (ModularItem) test.getItem();
		return item.getEffectLevel(test, effect);
	}

	@Nullable
	public static ItemStack getThrownItemStack(@Nullable Entity e) { // i should make this a helper method but for now doing it twice is fine
		if (!(e instanceof AbstractArrowEntity))
			return null;
		Method lookup = arrowStackGetter.getValue();
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
}

package mod.noobulus.tetrapak.util;

import net.minecraft.item.ItemStack;
import se.mickelus.tetra.effect.ItemEffect;

import javax.annotation.Nullable;

public interface ITetraEffect {
	ItemEffect getEffect();

	default boolean hasEffect(@Nullable ItemStack stack) {
		return ItemHelper.getEffectLevel(stack, getEffect()) > 0;
	}
}

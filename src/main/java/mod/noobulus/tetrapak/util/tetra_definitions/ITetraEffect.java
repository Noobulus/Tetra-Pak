package mod.noobulus.tetrapak.util.tetra_definitions;

import mod.noobulus.tetrapak.TetraPak;
import mod.noobulus.tetrapak.util.DamageBufferer;
import mod.noobulus.tetrapak.util.ItemHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;

import javax.annotation.Nullable;

public interface ITetraEffect {
	static ItemEffect get(String key) {
		return ItemEffect.get(TetraPak.MODID + ":" + key);
	}

	ItemEffect getEffect();

	default boolean hasEffect(@Nullable ItemStack stack) {
		return getEffectLevel(stack) > 0;
	}

	default void doBeltTick(PlayerEntity player, int effectLevel) {
	}

	default void doBeltTick(PlayerEntity player, @Nullable ItemStack belt) {
		doBeltTick(player, getEffectLevel(belt));
	}

	default float getEffectEfficiency() {
		return getEffectEfficiency(DamageBufferer.getLastActiveDamageSource());
	}

	default float getEffectEfficiency(@Nullable DamageSource source) {
		ItemEffect effect = getEffect();
		if (source == null)
			return 0;
		if (source.getTrueSource() instanceof LivingEntity) {
			LivingEntity user = (LivingEntity) source.getTrueSource();
			ItemStack heldItem = user.getHeldItemMainhand();

			if (heldItem.getItem() instanceof ModularItem) {
				ModularItem heldModularitem = (ModularItem) heldItem.getItem();
				return (float) heldModularitem.getEffectEfficiency(heldItem, effect);
			}
			ItemStack thrownItem = ItemHelper.getThrownItemStack(source.getImmediateSource());
			if (thrownItem != null && thrownItem.getItem() instanceof ModularItem) {
				ModularItem thrownModularItem = (ModularItem) thrownItem.getItem();
				return (float) thrownModularItem.getEffectEfficiency(thrownItem, effect);
			}
		}
		return 0;
	}

	default String getStatsPath() {
		return TetraPak.MODID + ".stats." + new ResourceLocation(getEffect().getKey()).getPath();
	}

	default int getEffectLevel(@Nullable ItemStack test) {
		if (test == null || test.isEmpty() || !(test.getItem() instanceof ModularItem))
			return 0;
		ModularItem item = (ModularItem) test.getItem();
		return item.getEffectLevel(test, getEffect());
	}

	default String getTooltipPath() {
		return getStatsPath() + ".tooltip";
	}
}

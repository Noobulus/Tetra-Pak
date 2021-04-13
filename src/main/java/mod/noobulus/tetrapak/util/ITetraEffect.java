package mod.noobulus.tetrapak.util;

import mod.noobulus.tetrapak.TetraPak;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.toolbelt.ToolbeltHelper;

import javax.annotation.Nullable;

public interface ITetraEffect {
	static ItemEffect get(String key) {
		return ItemEffect.get(TetraPak.MODID + ":" + key);
	}

	ItemEffect getEffect();

	default boolean hasEffect(@Nullable ItemStack stack) {
		return ItemHelper.getEffectLevel(stack, getEffect()) > 0;
	}

	default int getBeltEffectLevel(LivingEntity e) {
		if (!(e instanceof PlayerEntity))
			return 0;
		return ItemHelper.getEffectLevel(ToolbeltHelper.findToolbelt((PlayerEntity) e), getEffect());
	}

	default boolean hasBeltEffect(LivingEntity entity) {
		return getBeltEffectLevel(entity) > 0;
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

	default String getTooltipPath() {
		return getStatsPath() + ".tooltip";
	}
}

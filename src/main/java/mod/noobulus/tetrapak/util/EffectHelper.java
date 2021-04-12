package mod.noobulus.tetrapak.util;

import mod.noobulus.tetrapak.TetraPak;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;

import javax.annotation.Nullable;

public class EffectHelper {
	private EffectHelper() {
	}

	public static void updateEffect(boolean active, ModifiableAttributeInstance attributeInstance, AttributeModifier modifier) {
		if (active) {
			if (!attributeInstance.hasModifier(modifier))
				attributeInstance.addTemporaryModifier(modifier);
		} else if (attributeInstance.hasModifier(modifier)) {
			attributeInstance.removeModifier(modifier);
		}
	}

	public static float getEffectEfficiency(@Nullable DamageSource source, ItemEffect effect) {
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

	public static ItemEffect get(String key) {
		return ItemEffect.get(TetraPak.MODID + ":" + key);
	}

	public static String getStatsPath(ItemEffect effect) {
		return TetraPak.MODID + ".stats." + new ResourceLocation(effect.getKey()).getPath();
	}

	public static String getTooltipPath(ItemEffect effect) {
		return getStatsPath(effect) + ".tooltip";
	}
}

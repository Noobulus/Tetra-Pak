package mod.noobulus.tetrapak.create;

import mod.noobulus.tetrapak.util.IHoloDescription;
import mod.noobulus.tetrapak.util.ITetraEffect;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.IStatGetter;
import se.mickelus.tetra.gui.statbar.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.statbar.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.statbar.getter.TooltipGetterPercentageDecimal;

import java.util.UUID;

public class NullifyingEffect implements IHoloDescription {
	public static final AttributeModifier beltGravityModifier = new AttributeModifier(UUID.fromString("678c7388-ba1d-45c8-9f51-d6e4f1c4e3ac"), "Gravity modifier", 0.25 - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
	public static final AttributeModifier beltDoubleGravityModifier = new AttributeModifier(UUID.fromString("778c7388-ba1d-45c8-9f51-d6e4f1c4e3ac"), "Gravity modifier", 0.125 - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);

	private static void updateEffect(boolean active, ModifiableAttributeInstance attributeInstance, AttributeModifier modifier) {
		if (active) {
			if (!attributeInstance.hasModifier(modifier))
				attributeInstance.addTemporaryModifier(modifier);
		} else if (attributeInstance.hasModifier(modifier)) {
			attributeInstance.removeModifier(modifier);
		}
	}

	@SubscribeEvent
	public void nullifierAtrributeModifiers(LivingEvent.LivingUpdateEvent event) {
		ModifiableAttributeInstance gravityAttribute = event.getEntityLiving().getAttribute(ForgeMod.ENTITY_GRAVITY.get());
		int nullifierLevel = getBeltEffectLevel(event.getEntityLiving());

		if (nullifierLevel <= 0 || gravityAttribute == null)
			return;

		updateEffect(nullifierLevel == 1, gravityAttribute, beltGravityModifier);
		updateEffect(nullifierLevel == 2, gravityAttribute, beltDoubleGravityModifier);
	}

	@SubscribeEvent
	public void nullifyingRemovesFallDamage(LivingAttackEvent event) {
		if (event.getSource().damageType.equals(DamageSource.FALL.damageType) && getBeltEffectLevel(event.getEntityLiving()) > 0) {
			event.setCanceled(true);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiStatBar getStatBar() {
		final IStatGetter nullifyingGetter = new StatGetterEffectLevel(getEffect(), 12.5, 62.5);
		return new GuiStatBar(0, 0, 59, getStatsPath(),
			0D, 100D, false, nullifyingGetter, LabelGetterBasic.percentageLabelDecimal,
			new TooltipGetterPercentageDecimal(getTooltipPath(), nullifyingGetter));
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("nullifying");
	}
}

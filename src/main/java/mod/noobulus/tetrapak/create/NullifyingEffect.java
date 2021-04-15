package mod.noobulus.tetrapak.create;

import mod.noobulus.tetrapak.util.IHoloDescription;
import mod.noobulus.tetrapak.util.ITetraEffect;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
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
	public static final AttributeModifier beltGravityModifierSlowfall = new AttributeModifier(UUID.fromString("878c7388-ba1d-45c8-9f51-d6e4f1c4e3ac"), "Gravity modifier", 0.35 - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);

	private static void updateEffect(boolean active, ModifiableAttributeInstance attributeInstance, AttributeModifier modifier) {
		if (active) {
			if (!attributeInstance.hasModifier(modifier))
				attributeInstance.addTemporaryModifier(modifier);
		} else if (attributeInstance.hasModifier(modifier)) {
			attributeInstance.removeModifier(modifier);
		}
	}

	@Override
	public void doBeltTick(PlayerEntity player, int nullifierLevel) {
		ModifiableAttributeInstance gravityAttribute = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
		if (nullifierLevel < 0 || gravityAttribute == null)
			return;

		boolean slowfall = player.isPotionActive(Effects.SLOW_FALLING);
		updateEffect(nullifierLevel == 1 && !slowfall, gravityAttribute, beltGravityModifier);
		updateEffect(nullifierLevel == 2 && !slowfall, gravityAttribute, beltDoubleGravityModifier);
		updateEffect(nullifierLevel > 0 && slowfall, gravityAttribute, beltGravityModifierSlowfall);
		if (nullifierLevel > 0 && player.getMotion().getY() < 0) // extra check for fall speed to make crits work correctly
			player.fallDistance = 1;
		if (nullifierLevel > 0 && player.getMotion().getY() >= 0)
			player.fallDistance = 0;
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

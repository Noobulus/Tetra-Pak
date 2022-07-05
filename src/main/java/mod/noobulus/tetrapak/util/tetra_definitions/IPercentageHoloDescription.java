package mod.noobulus.tetrapak.util.tetra_definitions;

import se.mickelus.tetra.gui.stats.getter.*;

public interface IPercentageHoloDescription extends IHoloDescription {
	@Override
	default double getStatMax() {
		return 100;
	}

	@Override
	default ILabelGetter getStatLabel() {
		return LabelGetterBasic.percentageLabelDecimal;
	}

	@Override
	default ITooltipGetter getStatTooltipGetter(IStatGetter statGetter) {
		return new TooltipGetterPercentageDecimal(getTooltipPath(), statGetter);
	}

	@Override
	default IStatGetter getStatGetter() {
		return new StatGetterEffectEfficiency(getEffect(), getStatMultiplier(), getStatBase());
	}
}

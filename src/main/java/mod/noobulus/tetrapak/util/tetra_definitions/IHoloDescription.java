package mod.noobulus.tetrapak.util.tetra_definitions;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.*;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

public interface IHoloDescription extends ITetraEffect {
	@OnlyIn(Dist.CLIENT)
	default void clientInit() {
		GuiStatBar bar = getStatBar();
		WorkbenchStatsGui.addBar(bar);
		HoloStatsGui.addBar(bar);
	}

	@OnlyIn(Dist.CLIENT)
	default GuiStatBar getStatBar() {
		final IStatGetter statGetter = getStatGetter();
		return new GuiStatBar(0, 0, 59, getStatsPath(),
			getStatMin(), getStatMax(), false, statGetter, getStatLabel(),
			getStatTooltipGetter(statGetter));
	}

	default double getStatMax() {
		return 1;
	}

	default double getStatMin() {
		return 0;
	}

	default double getStatBase() {
		return 0;
	}

	default double getStatMultiplier() {
		return 1;
	}

	default ILabelGetter getStatLabel() {
		return LabelGetterBasic.integerLabel;
	}

	default ITooltipGetter getStatTooltipGetter(IStatGetter statGetter) {
		return new TooltipGetterInteger(getTooltipPath(), statGetter);
	}

	default IStatGetter getStatGetter() {
		return new StatGetterEffectLevel(getEffect(), getStatMultiplier(), getStatBase());
	}
}

package mod.noobulus.tetrapak.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.IStatGetter;
import se.mickelus.tetra.gui.statbar.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.statbar.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.statbar.getter.TooltipGetterInteger;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

public interface IHoloDescription extends ITetraEffect {
	@OnlyIn(Dist.CLIENT)
	default void clientInit() {
		GuiStatBar bar = getStatBar();
		WorkbenchStatsGui.addBar(bar);
		HoloStatsGui.addBar(bar);
	}

	@OnlyIn(Dist.CLIENT)
	// default for binary effects
	default GuiStatBar getStatBar() {
		final IStatGetter scorchingGetter = new StatGetterEffectLevel(getEffect(), 1, 0);
		return new GuiStatBar(0, 0, 59, getStatsPath(),
			0, 1, false, scorchingGetter, LabelGetterBasic.integerLabel,
			new TooltipGetterInteger(getTooltipPath(), scorchingGetter));
	}
}

package mod.noobulus.tetrapak.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
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
		ItemEffect effect = getEffect();
		final IStatGetter scorchingGetter = new StatGetterEffectLevel(effect, 1, 0);
		return new GuiStatBar(0, 0, 59, EffectHelper.getStatsPath(effect),
			0, 1, false, scorchingGetter, LabelGetterBasic.integerLabel,
			new TooltipGetterInteger(EffectHelper.getTooltipPath(effect), scorchingGetter));
	}
}

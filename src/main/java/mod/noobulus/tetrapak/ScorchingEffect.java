package mod.noobulus.tetrapak;

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

public class ScorchingEffect {
	public static final ItemEffect SCORCHING_EFFECT = ItemEffect.get("tetrapak:scorching");

	@OnlyIn(Dist.CLIENT)
	public static void clientInit() {
		final IStatGetter scorchingGetter = new StatGetterEffectLevel(SCORCHING_EFFECT, 1, 0);
		final GuiStatBar scorchingBar = new GuiStatBar(0, 0, 59, "tetrapak.stats.scorching",
			0, 1, false, scorchingGetter, LabelGetterBasic.integerLabel,
			new TooltipGetterInteger("tetrapak.stats.scorching.tooltip", scorchingGetter));

		WorkbenchStatsGui.addBar(scorchingBar);
		HoloStatsGui.addBar(scorchingBar);
	}
}

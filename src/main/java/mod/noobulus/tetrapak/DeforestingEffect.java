package mod.noobulus.tetrapak;

import com.simibubi.create.content.curiosities.tools.DeforesterItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.IStatGetter;
import se.mickelus.tetra.gui.statbar.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.statbar.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.statbar.getter.TooltipGetterInteger;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

public class DeforestingEffect {
	private static final ItemEffect deforesting = ItemEffect.get("tetrapak:deforesting");

	@OnlyIn(Dist.CLIENT)
	public static void clientInit() {
		final IStatGetter deforestingGetter = new StatGetterEffectLevel(deforesting, 1, 0);
		final GuiStatBar deforestingBar = new GuiStatBar(0, 0, 59, "tetrapak.stats.deforesting",
			0, 1, false, deforestingGetter, LabelGetterBasic.integerLabel,
			new TooltipGetterInteger("tetrapak.stats.deforesting.tooltip", deforestingGetter));

		WorkbenchStatsGui.addBar(deforestingBar);
		HoloStatsGui.addBar(deforestingBar);
	}

	@SubscribeEvent
	public static void deforestWhenBlockBroken(BlockEvent.BreakEvent event) {
		if (ItemHelper.getEffectLevel(event.getPlayer().getHeldItemMainhand(), deforesting) > 0) {
			DeforesterItem.destroyTree(event.getWorld(), event.getState(), event.getPos(), event.getPlayer());
		}
	}
}

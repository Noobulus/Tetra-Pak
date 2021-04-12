package mod.noobulus.tetrapak.create;

import com.simibubi.create.content.curiosities.tools.DeforesterItem;
import mod.noobulus.tetrapak.util.IClientInit;
import mod.noobulus.tetrapak.util.ItemHelper;
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

public class DeforestingEffect implements IClientInit {
	public static final ItemEffect DEFORESTING_EFFECT = ItemEffect.get("tetrapak:deforesting");

	@SubscribeEvent
	public void deforestWhenBlockBroken(BlockEvent.BreakEvent event) {
		if (ItemHelper.getEffectLevel(event.getPlayer().getHeldItemMainhand(), DEFORESTING_EFFECT) > 0) {
			DeforesterItem.destroyTree(event.getWorld(), event.getState(), event.getPos(), event.getPlayer());
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientInit() {
		final IStatGetter deforestingGetter = new StatGetterEffectLevel(DEFORESTING_EFFECT, 1, 0);
		final GuiStatBar deforestingBar = new GuiStatBar(0, 0, 59, "tetrapak.stats.deforesting",
			0, 1, false, deforestingGetter, LabelGetterBasic.integerLabel,
			new TooltipGetterInteger("tetrapak.stats.deforesting.tooltip", deforestingGetter));

		WorkbenchStatsGui.addBar(deforestingBar);
		HoloStatsGui.addBar(deforestingBar);
	}
}

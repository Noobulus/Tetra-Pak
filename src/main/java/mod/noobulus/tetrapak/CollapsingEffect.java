package mod.noobulus.tetrapak;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;

public class CollapsingEffect {
	private static final ItemEffect COLLAPSING_EFFECT = ItemEffect.get("tetrapak:collapsing");
	private static boolean collapsing = false; // required as to not run into "recursions" over forge events on tree cutting

    /*@OnlyIn(Dist.CLIENT)
    public static void clientInit() {
        final IStatGetter collapsingGetter = new StatGetterEffectLevel(collapsing, 1, 0);
        final GuiStatBar collapsingBar = new GuiStatBar(0, 0, 59, "tetrapak.stats.collapsing",
                0, 1, false, collapsingGetter, LabelGetterBasic.integerLabel,
                new TooltipGetterInteger("tetrapak.stats.collapsing.tooltip", collapsingGetter));

        WorkbenchStatsGui.addBar(collapsingBar);
        HoloStatsGui.addBar(collapsingBar);
    }*/

	@SubscribeEvent
	public static void collapseWhenBlockBroken(BlockEvent.BreakEvent event) {
		if (collapsing)
			return;
		ItemStack heldItemMainhand = event.getPlayer().getHeldItemMainhand();
		if (ItemHelper.getEffectLevel(heldItemMainhand, COLLAPSING_EFFECT) > 0) {
			collapsing = true;
			// grimm put fancy collapsing code here
			collapsing = false;
		}
	}
}

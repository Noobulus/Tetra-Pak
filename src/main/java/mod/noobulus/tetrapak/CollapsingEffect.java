package mod.noobulus.tetrapak;

import net.minecraft.item.ItemStack;
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
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

public class CollapsingEffect {
    private static final ItemEffect collapsing = ItemEffect.get("tetrapak:collapsing");

    @OnlyIn(Dist.CLIENT)
    public static void clientInit() {
        final IStatGetter collapsingGetter = new StatGetterEffectLevel(collapsing, 1, 0);
        final GuiStatBar collapsingBar = new GuiStatBar(0, 0, 59, "tetrapak.stats.collapsing",
                0, 1, false, collapsingGetter, LabelGetterBasic.integerLabel,
                new TooltipGetterInteger("tetrapak.stats.collapsing.tooltip", collapsingGetter));

        WorkbenchStatsGui.addBar(collapsingBar);
        HoloStatsGui.addBar(collapsingBar);
    }

    @SubscribeEvent
    public static void collapseWhenBlockBroken(BlockEvent.BreakEvent event) {
        ItemStack heldItemMainhand = event.getPlayer().getHeldItemMainhand();
        if (!(heldItemMainhand.getItem() instanceof ModularItem))
            return;
        ModularItem item = (ModularItem) heldItemMainhand.getItem();
        int level = item.getEffectLevel(heldItemMainhand, collapsing);
        if (level > 0) {
            // grimm put fancy collapsing code here
        }
    }
}

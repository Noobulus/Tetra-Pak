package mod.noobulus.tetrapak;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.simibubi.create.content.curiosities.tools.DeforesterItem;

import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;

public class DeforestEffect {
    private static final ItemEffect deforesting = ItemEffect.get("tetrapak:deforesting");

    @SubscribeEvent
    public static void onBlockDestroyed(BlockEvent.BreakEvent event) {
        ItemStack heldItemMainhand = event.getPlayer().getHeldItemMainhand();
        if (!(heldItemMainhand.getItem() instanceof ModularItem))
            return;
        ModularItem item = (ModularItem) heldItemMainhand.getItem();
        int level = item.getEffectLevel(heldItemMainhand, deforesting);
        if (level > 0) {
            DeforesterItem.destroyTree(heldItemMainhand, event.getWorld(), event.getState(), event.getPos(), event.getPlayer());
        }
    }
}

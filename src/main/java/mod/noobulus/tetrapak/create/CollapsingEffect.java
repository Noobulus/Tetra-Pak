package mod.noobulus.tetrapak.create;

import mod.noobulus.tetrapak.util.EffectHelper;
import mod.noobulus.tetrapak.util.IHoloDescription;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;

public class CollapsingEffect implements IHoloDescription {
	private static boolean collapsing = false; // required as to not run into "recursions" over forge events on tree cutting

	@SubscribeEvent
	public void collapseWhenBlockBroken(BlockEvent.BreakEvent event) {
		if (collapsing)
			return;
		ItemStack heldItemMainhand = event.getPlayer().getHeldItemMainhand();
		if (hasEffect(heldItemMainhand)) {
			collapsing = true;
			// grimm put fancy collapsing code here
			collapsing = false;
		}
	}

	@Override
	public ItemEffect getEffect() {
		return EffectHelper.get("collapsing");
	}
}

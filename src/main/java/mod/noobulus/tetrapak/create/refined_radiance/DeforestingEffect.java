package mod.noobulus.tetrapak.create.refined_radiance;

import com.simibubi.create.content.curiosities.tools.DeforesterItem;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;

public class DeforestingEffect implements IHoloDescription {
	@SubscribeEvent
	public void deforestWhenBlockBroken(BlockEvent.BreakEvent event) {
		if (hasEffect(event.getPlayer().getHeldItemMainhand())) {
			DeforesterItem.destroyTree(event.getWorld(), event.getState(), event.getPos(), event.getPlayer());
		}
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("deforesting");
	}
}

package mod.noobulus.tetrapak.create.refined_radiance;

import mod.noobulus.tetrapak.util.EffectHelper;
import mod.noobulus.tetrapak.util.IHoloDescription;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;

public class CollapsingEffect implements IHoloDescription {
	private static boolean collapsing = false; // required as to not run into "recursions" over forge events on tree cutting

	public static void collapse(IWorld iWorld, BlockPos pos, PlayerEntity entity) {
		if (collapsing || entity.isSneaking() || !(iWorld instanceof World))
			return;
		World world = (World) iWorld;
		collapsing = true;
		DirtCollapser.findDirtCollumn(world, pos).destroyBlocksFancy(world, entity);
		collapsing = false;
	}

	@SubscribeEvent
	public void collapseWhenBlockBroken(BlockEvent.BreakEvent event) {
		if (!collapsing && hasEffect(event.getPlayer().getHeldItemMainhand())) {
			collapse(event.getWorld(), event.getPos(), event.getPlayer());
		}
	}

	@Override
	public ItemEffect getEffect() {
		return EffectHelper.get("collapsing");
	}
}

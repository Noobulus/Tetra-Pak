package mod.noobulus.tetrapak.create.refined_radiance;

import mod.noobulus.tetrapak.util.EffectHelper;
import mod.noobulus.tetrapak.util.IHoloDescription;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;

public class CollapsingEffect implements IHoloDescription {
	private static boolean collapsing = false; // required as to not run into "recursions" over forge events on tree cutting

	private static void dropItemsFromColumn(World world, BlockPos breakingPos, BlockPos pos, ItemStack stack) {
		ItemEntity entity = new ItemEntity(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, stack);
		entity.setMotion(0, (breakingPos.getY() - pos.getY()) / 20f, 0);
		world.addEntity(entity);
	}

	public static void collapse(IWorld iWorld, BlockPos pos, LivingEntity entity) {
		if (collapsing || entity.isSneaking() || !(iWorld instanceof World))
			return;
		World world = (World) iWorld;
		collapsing = true;
		DirtCollapser.findDirtCollumn(world, pos).destroyBlocks(world, entity, (dropPos, item) -> dropItemsFromColumn(world, pos, dropPos, item));
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

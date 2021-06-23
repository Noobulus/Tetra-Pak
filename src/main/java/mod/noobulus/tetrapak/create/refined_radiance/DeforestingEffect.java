package mod.noobulus.tetrapak.create.refined_radiance;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.utility.TreeCutter;
import com.simibubi.create.foundation.utility.VecHelper;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;

public class DeforestingEffect implements IHoloDescription {
	private static boolean deforesting = false; // required as to not run into "recursions" over forge events on tree cutting

	@SubscribeEvent
	public void deforestWhenBlockBroken(BlockEvent.BreakEvent event) {
		if (hasEffect(event.getPlayer().getMainHandItem())) {
			destroyTree(event.getWorld(), event.getState(), event.getPos(), event.getPlayer());
		}
	}

	public static void destroyTree(IWorld iWorld, BlockState state, BlockPos pos,
								   PlayerEntity player) {

		if (deforesting ||!(state.is(BlockTags.LOGS) || AllTags.AllBlockTags.SLIMY_LOGS.matches(state)) || player.isShiftKeyDown() || !(iWorld instanceof World))
			return;
		World worldIn = (World) iWorld;
		Vector3d vec = player.getLookAngle();

		deforesting = true;
		TreeCutter.findTree(worldIn, pos).destroyBlocks(worldIn, player, (dropPos, item) -> dropItemFromCutTree(worldIn, pos, vec, dropPos, item));
		deforesting = false;
	}

	public static void dropItemFromCutTree(World world, BlockPos breakingPos, Vector3d fallDirection, BlockPos pos,
										   ItemStack stack) {
		float distance = (float) Math.sqrt(pos.distSqr(breakingPos));
		Vector3d dropPos = VecHelper.getCenterOf(pos);
		ItemEntity entity = new ItemEntity(world, dropPos.x, dropPos.y, dropPos.z, stack);
		entity.setDeltaMovement(fallDirection.scale(distance / 20f));
		world.addFreshEntity(entity);
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("deforesting");
	}
}

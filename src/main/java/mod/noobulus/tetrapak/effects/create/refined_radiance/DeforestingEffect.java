package mod.noobulus.tetrapak.effects.create.refined_radiance;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.utility.TreeCutter;
import com.simibubi.create.foundation.utility.VecHelper;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;

public class DeforestingEffect implements IHoloDescription, IEventBusListener {
	private static boolean deforesting = false; // required as to not run into "recursions" over forge events on tree cutting

	public static void destroyTree(LevelAccessor iWorld, BlockState state, BlockPos pos,
								   Player player) {

		if (deforesting || !(state.is(BlockTags.LOGS) || AllTags.AllBlockTags.SLIMY_LOGS.matches(state)) || player.isCrouching() || !(iWorld instanceof Level))
			return;
		Level worldIn = (Level) iWorld;
		Vec3 vec = player.getLookAngle();

		deforesting = true;
		TreeCutter.findTree(worldIn, pos).destroyBlocks(worldIn, player, (dropPos, item) -> dropItemFromCutTree(worldIn, pos, vec, dropPos, item));
		deforesting = false;
	}

	public static void dropItemFromCutTree(Level world, BlockPos breakingPos, Vec3 fallDirection, BlockPos pos,
										   ItemStack stack) {
		float distance = (float) Math.sqrt(pos.distSqr(breakingPos));
		Vec3 dropPos = VecHelper.getCenterOf(pos);
		ItemEntity entity = new ItemEntity(world, dropPos.x, dropPos.y, dropPos.z, stack);
		entity.setDeltaMovement(fallDirection.scale(distance / 20f));
		world.addFreshEntity(entity);
	}

	@SubscribeEvent
	public void deforestWhenBlockBroken(BlockEvent.BreakEvent event) {
		if (hasEffect(event.getPlayer().getMainHandItem())) {
			destroyTree(event.getWorld(), event.getState(), event.getPos(), event.getPlayer());
		}
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("deforesting");
	}
}

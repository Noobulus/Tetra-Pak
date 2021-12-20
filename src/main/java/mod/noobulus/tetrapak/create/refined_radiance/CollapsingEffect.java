package mod.noobulus.tetrapak.create.refined_radiance;

import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import se.mickelus.tetra.effect.ItemEffect;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class CollapsingEffect implements IHoloDescription, IEventBusListener {
	private static boolean collapsing = false; // required as to not run into "recursions" over forge events on tree cutting

	public static void collapse(LevelAccessor iWorld, BlockPos pos, Player entity) {
		if (collapsing || entity.isShiftKeyDown() || !(iWorld instanceof Level))
			return;
		Level world = (Level) iWorld;
		collapsing = true;
		findDirtCollumn(world, pos).destroyBlocksFancy(world, entity);
		collapsing = false;
	}

	public static BlockCollection findDirtCollumn(@Nullable BlockGetter world, BlockPos pos) {
		if (world == null)
			return BlockCollection.EMPTY;

		Set<BlockPos> visited = new HashSet<>();
		BlockState startState = world.getBlockState(pos);

		while (canBreakWithStart(startState, world.getBlockState(pos))) {
			visited.add(pos);
			pos = pos.above();
		}

		return new BlockCollection(visited);
	}

	public static boolean canBreakWithStart(BlockState start, BlockState test) {
		if (!ToolType.SHOVEL.equals(test.getHarvestTool()))
			return false;
		if (test.getBlock() instanceof FallingBlock)
			return true;
		if (start.getBlock().equals(test.getBlock()))
			return true;
		return Material.DIRT.equals(getBlockMaterial(start)) && (Material.DIRT.equals(getBlockMaterial(test)) || test.getBlock().equals(Blocks.GRASS_BLOCK));
	}

	@Nullable
	private static Material getBlockMaterial(BlockState state) {
		Object mat = ObfuscationReflectionHelper.getPrivateValue(BlockBehaviour.class, state.getBlock(), "field_149764_J");
		return mat instanceof Material ? (Material) mat : null;
	}

	@SubscribeEvent
	public void collapseWhenBlockBroken(BlockEvent.BreakEvent event) {
		if (!collapsing && hasEffect(event.getPlayer().getMainHandItem())) {
			collapse(event.getWorld(), event.getPos(), event.getPlayer());
		}
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("collapsing");
	}
}

package mod.noobulus.tetrapak.effects.create.refined_radiance;

import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import se.mickelus.tetra.effect.ItemEffect;

public class CollapsingEffect implements IHoloDescription, IEventBusListener {
	private static boolean collapsing = false; // required as to not run into "recursions" over forge events on tree cutting

	/*public static void collapse(LevelAccessor iWorld, BlockPos pos, Player entity) {
		if (collapsing || entity.isShiftKeyDown() || !(iWorld instanceof Level world))
			return;
		collapsing = true;
		findDirtColumn(world, pos).destroyBlocksFancy(world, entity);
		collapsing = false;
	}

	public static BlockCollection findDirtColumn(@Nullable BlockGetter world, BlockPos pos) {
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
		if (!isEffectiveOn(ToolActions.SHOVEL_DIG, test))
			return false;
		if (test.getBlock() instanceof FallingBlock)
			return true;
		if (start.getBlock().equals(test.getBlock()))
			return true;
		return Material.DIRT.equals(getBlockMaterial(start)) && (Material.DIRT.equals(getBlockMaterial(test)) || test.getBlock().equals(Blocks.GRASS_BLOCK));
	}

	private static Material getBlockMaterial(BlockState state) {
		return state.getMaterial();
	}

	@SubscribeEvent
	public void collapseWhenBlockBroken(BlockEvent.BreakEvent event) {
		if (!collapsing && hasEffect(event.getPlayer().getMainHandItem())) {
			collapse(event.getLevel(), event.getPos(), event.getPlayer());
		}
	}*/

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("collapsing");
	}
}

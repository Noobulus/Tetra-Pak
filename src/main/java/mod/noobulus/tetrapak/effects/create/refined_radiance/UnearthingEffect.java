package mod.noobulus.tetrapak.effects.create.refined_radiance;

import mod.noobulus.tetrapak.Config;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.client.resources.language.I18n;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.ITooltipGetter;

public class UnearthingEffect implements IHoloDescription, IEventBusListener {
	private static boolean unearthing = false; // required as to not run into "recursions" over forge events on tree cutting

	/*public static void unearth(LevelAccessor iWorld, BlockPos pos, Player entity) {
		if (unearthing || entity.isShiftKeyDown() || !(iWorld instanceof Level world))
			return;
		unearthing = true;
		findOreVein(world, pos).destroyBlocks(world, entity, (dropPos, stack) -> Block.popResource(world, dropPos, stack));
		unearthing = false;
	}

	public static BlockCollection findOreVein(@Nullable BlockGetter world, BlockPos pos) {
		if (world == null)
			return BlockCollection.EMPTY;

		Set<BlockPos> visited = new HashSet<>();
		List<BlockPos> frontier = new LinkedList<>();

		BlockState startState = world.getBlockState(pos);
		if (!startState.is(Tags.Blocks.ORES))
			return BlockCollection.EMPTY;

		frontier.add(pos);
		while (!frontier.isEmpty()) {
			BlockPos current = frontier.remove(0);
			visited.add(current);
			for (Direction direction : Direction.values()) {
				BlockPos offset = current.relative(direction);
				if (!visited.contains(offset) && startState.getBlock().equals(world.getBlockState(offset).getBlock()))
					frontier.add(offset);
			}
			if (visited.size() >= Config.MAX_RADIANT_BLOCKS.get()) return new BlockCollection(visited);
		}
		return new BlockCollection(visited);
	}

	@SubscribeEvent
	public void unearthWhenBlockBroken(BlockEvent.BreakEvent event) {
		if (!unearthing && hasEffect(event.getPlayer().getMainHandItem())) {
			unearth(event.getLevel(), event.getPos(), event.getPlayer());
		}
	}*/

	@Override
	public ITooltipGetter getStatTooltipGetter(IStatGetter statGetter) {
		return (player, itemStack) -> I18n.get(getTooltipPath(), Config.MAX_RADIANT_BLOCKS.get());
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("unearthing");
	}
}

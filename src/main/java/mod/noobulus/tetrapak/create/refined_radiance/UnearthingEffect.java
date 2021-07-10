package mod.noobulus.tetrapak.create.refined_radiance;

import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class UnearthingEffect implements IHoloDescription {
	private static boolean unearthing = false; // required as to not run into "recursions" over forge events on tree cutting

	public static void unearth(IWorld iWorld, BlockPos pos, PlayerEntity entity) {
		if (unearthing || entity.isShiftKeyDown() || !(iWorld instanceof World))
			return;
		World world = (World) iWorld;
		unearthing = true;
		findOreVein(world, pos).destroyBlocks(world, entity, (dropPos, stack) -> Block.popResource(world, dropPos, stack));
		unearthing = false;
	}

	public static BlockCollection findOreVein(@Nullable IBlockReader world, BlockPos pos) {
		if (world == null)
			return BlockCollection.EMPTY;

		Set<BlockPos> visited = new HashSet<>();
		List<BlockPos> frontier = new LinkedList<>();

		Block start = world.getBlockState(pos).getBlock();
		if (!Tags.Blocks.ORES.contains(start))
			return BlockCollection.EMPTY;

		frontier.add(pos);
		while (!frontier.isEmpty()) {
			BlockPos current = frontier.remove(0);
			visited.add(current);
			for (Direction direction : Direction.values()) {
				BlockPos offset = current.relative(direction);
				if (!visited.contains(offset) && start.equals(world.getBlockState(offset).getBlock()))
					frontier.add(offset);
			}
		}
		return new BlockCollection(visited);
	}

	@SubscribeEvent
	public void unearthWhenBlockBroken(BlockEvent.BreakEvent event) {
		if (!unearthing && hasEffect(event.getPlayer().getMainHandItem())) {
			unearth(event.getWorld(), event.getPos(), event.getPlayer());
		}
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("unearthing");
	}
}

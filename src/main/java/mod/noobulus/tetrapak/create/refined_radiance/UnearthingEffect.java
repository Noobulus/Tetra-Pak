package mod.noobulus.tetrapak.create.refined_radiance;

import mod.noobulus.tetrapak.Config;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.ITooltipGetter;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class UnearthingEffect implements IHoloDescription, IEventBusListener {
	private static boolean unearthing = false; // required as to not run into "recursions" over forge events on tree cutting

	public static void unearth(LevelAccessor iWorld, BlockPos pos, Player entity) {
		if (unearthing || entity.isShiftKeyDown() || !(iWorld instanceof Level))
			return;
		Level world = (Level) iWorld;
		unearthing = true;
		findOreVein(world, pos).destroyBlocks(world, entity, (dropPos, stack) -> Block.popResource(world, dropPos, stack));
		unearthing = false;
	}

	public static BlockCollection findOreVein(@Nullable BlockGetter world, BlockPos pos) {
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
			if (visited.size() >= Config.MAX_RADIANT_BLOCKS.get()) return new BlockCollection(visited);
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
	public ITooltipGetter getStatTooltipGetter(IStatGetter statGetter) {
		return (player, itemStack) -> I18n.get(getTooltipPath(), Config.MAX_RADIANT_BLOCKS.get());
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("unearthing");
	}
}

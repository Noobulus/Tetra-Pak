package mod.noobulus.tetrapak.create.refined_radiance;

import com.simibubi.create.foundation.utility.AbstractBlockBreakQueue;
import com.simibubi.create.foundation.utility.Pair;
import mod.noobulus.tetrapak.entities.FragileFallingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;

public class BlockCollection extends AbstractBlockBreakQueue {
	public static final BlockCollection EMPTY = new BlockCollection(Collections.emptyList());

	private final Collection<BlockPos> blocks;

	public BlockCollection(Collection<BlockPos> blocks) {
		this.blocks = blocks;
	}

	@Override
	public void destroyBlocks(World world, ItemStack itemStack, @Nullable PlayerEntity playerEntity, BiConsumer<BlockPos, ItemStack> drop) {
		blocks.forEach(makeCallbackFor(world, 1 / 8f, itemStack, playerEntity, drop));
	}

	public void destroyBlocksFancy(World world, @Nullable LivingEntity entity) {
		Map<Pair<BlockPos, BlockState>, Collection<ItemStack>> drops = new HashMap<>();
		destroyBlocks(world, entity, (pos, stack) -> {
			Pair<BlockPos, BlockState> key = Pair.of(pos, world.getBlockState(pos));
			Collection<ItemStack> dropsAt = drops.containsKey(key) ? drops.get(key) : new ArrayList<>();
			dropsAt.add(stack);
			drops.put(key, dropsAt);
		});

		drops.forEach((inf, itemStacks) -> world.addEntity(new FragileFallingBlock(world, inf.getFirst(), inf.getSecond(), itemStacks)));
	}
}

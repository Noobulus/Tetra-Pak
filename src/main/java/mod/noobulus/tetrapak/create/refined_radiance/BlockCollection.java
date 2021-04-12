package mod.noobulus.tetrapak.create.refined_radiance;

import com.simibubi.create.foundation.utility.AbstractBlockBreakQueue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
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
}

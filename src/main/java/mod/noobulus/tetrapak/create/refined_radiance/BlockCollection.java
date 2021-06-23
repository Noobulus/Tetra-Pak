package mod.noobulus.tetrapak.create.refined_radiance;

import com.simibubi.create.foundation.utility.AbstractBlockBreakQueue;
import com.simibubi.create.foundation.utility.Pair;
import mod.noobulus.tetrapak.entities.FragileFallingBlock;
import mod.noobulus.tetrapak.util.BlockHelper;
import mod.noobulus.tetrapak.util.DropSimulationWorld;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BlockCollection extends AbstractBlockBreakQueue {
	public static final BlockCollection EMPTY = new BlockCollection(Collections.emptyList());

	private final Collection<BlockPos> blocks;

	public BlockCollection(Collection<BlockPos> blocks) {
		this.blocks = blocks;
	}

	public void destroyBlocks(World world, ItemStack itemStack, @Nullable PlayerEntity playerEntity, TriConsumer<BlockPos, BlockState, ItemStack> drop) {
		blocks.forEach(makeCallbackFor(world, 1 / 8f, itemStack, playerEntity, drop));
	}

	public void destroyBlocks(World world, @Nullable LivingEntity entity, TriConsumer<BlockPos, BlockState, ItemStack> drop) {
		PlayerEntity playerEntity = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;
		ItemStack toDamage = playerEntity != null && !playerEntity.isCreative() ? playerEntity.getHeldItemMainhand() : ItemStack.EMPTY;
		this.destroyBlocks(world, toDamage, playerEntity, drop);
	}

	public void destroyBlocksFancy(World world, @Nullable LivingEntity entity) {
		Map<Pair<BlockPos, BlockState>, Collection<ItemStack>> drops = new HashMap<>();
		destroyBlocks(world, entity, (pos, state, stack) -> {
			Pair<BlockPos, BlockState> key = Pair.of(pos, state);
			Collection<ItemStack> dropsAt = drops.containsKey(key) ? drops.get(key) : new ArrayList<>();
			dropsAt.add(stack);
			drops.put(key, dropsAt);
		});

		if (!world.isRemote)
			drops.forEach((inf, itemStacks) -> world.addEntity(new FragileFallingBlock(world, inf.getFirst(), inf.getSecond(), itemStacks)));
	}

	protected Consumer<BlockPos> makeCallbackFor(World world, float effectChance, ItemStack toDamage, @Nullable PlayerEntity playerEntity, TriConsumer<BlockPos, BlockState, ItemStack> drop) {
		World simulationWorld = DropSimulationWorld.of(world);
		return pos -> {
			ItemStack usedTool = toDamage.copy();
			BlockHelper.destroyBlockAs(simulationWorld, pos, playerEntity, toDamage, effectChance, (stack, state) ->
				drop.accept(pos, state, stack)
			);
			if (toDamage.isEmpty() && !usedTool.isEmpty()) {
				ForgeEventFactory.onPlayerDestroyItem(playerEntity, usedTool, Hand.MAIN_HAND);
			}
		};
	}

	@Override
	public void destroyBlocks(World world, ItemStack itemStack, @Nullable PlayerEntity playerEntity, BiConsumer<BlockPos, ItemStack> biConsumer) {
		destroyBlocks(world, itemStack, playerEntity, (pos, state, stack) -> biConsumer.accept(pos, stack));
	}
}

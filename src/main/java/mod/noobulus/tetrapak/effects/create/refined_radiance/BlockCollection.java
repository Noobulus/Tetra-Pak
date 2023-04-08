package mod.noobulus.tetrapak.effects.create.refined_radiance;

/*import com.simibubi.create.foundation.utility.AbstractBlockBreakQueue;
import com.simibubi.create.foundation.utility.Pair;
import mod.noobulus.tetrapak.entities.FragileFallingBlock;
import mod.noobulus.tetrapak.util.BlockHelper;
import mod.noobulus.tetrapak.util.DropSimulationWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

	public void destroyBlocks(Level world, ItemStack itemStack, @Nullable Player playerEntity, TriConsumer<BlockPos, BlockState, ItemStack> drop) {
		blocks.forEach(makeCallbackFor(world, 1 / 8f, itemStack, playerEntity, drop));
	}

	public void destroyBlocks(Level world, @Nullable LivingEntity entity, TriConsumer<BlockPos, BlockState, ItemStack> drop) {
		Player playerEntity = entity instanceof Player player ? player : null;
		ItemStack toDamage = playerEntity != null && !playerEntity.isCreative() ? playerEntity.getMainHandItem() : ItemStack.EMPTY;
		this.destroyBlocks(world, toDamage, playerEntity, drop);
	}

	public void destroyBlocksFancy(Level world, @Nullable LivingEntity entity) {
		Map<Pair<BlockPos, BlockState>, Collection<ItemStack>> drops = new HashMap<>();
		destroyBlocks(world, entity, (pos, state, stack) -> {
			Pair<BlockPos, BlockState> key = Pair.of(pos, state);
			Collection<ItemStack> dropsAt = drops.containsKey(key) ? drops.get(key) : new ArrayList<>();
			dropsAt.add(stack);
			drops.put(key, dropsAt);
		});

		if (!world.isClientSide)
			drops.forEach((inf, itemStacks) -> world.addFreshEntity(new FragileFallingBlock(world, inf.getFirst(), inf.getSecond(), itemStacks)));
	}

	protected Consumer<BlockPos> makeCallbackFor(Level world, float effectChance, ItemStack toDamage, @Nullable Player playerEntity, TriConsumer<BlockPos, BlockState, ItemStack> drop) {
		Level simulationWorld = DropSimulationWorld.of(world);
		return pos -> {
			ItemStack usedTool = toDamage.copy();
			BlockHelper.destroyBlockAs(simulationWorld, pos, playerEntity, toDamage, effectChance, (stack, state) ->
				drop.accept(pos, state, stack)
			);
			if (toDamage.isEmpty() && !usedTool.isEmpty()) {
				ForgeEventFactory.onPlayerDestroyItem(playerEntity, usedTool, InteractionHand.MAIN_HAND);
			}
		};
	}

	@Override
	public void destroyBlocks(Level world, ItemStack itemStack, @Nullable Player playerEntity, BiConsumer<BlockPos, ItemStack> biConsumer) {
		destroyBlocks(world, itemStack, playerEntity, (pos, state, stack) -> biConsumer.accept(pos, stack));
	}
}
*/
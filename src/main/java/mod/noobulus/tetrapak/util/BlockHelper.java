package mod.noobulus.tetrapak.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.ForgeHooks;
import se.mickelus.mutil.util.CastOptional;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.items.modular.ItemModularHandheld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;

import static se.mickelus.tetra.util.ToolActionHelper.playerCanDestroyBlock;

public class BlockHelper {
	private BlockHelper() {
	}

	public static void destroyBlockAs(Level world, BlockPos pos, @Nullable Player player, ItemStack usedTool,
									  float effectChance, BiConsumer<ItemStack, BlockState> droppedItemCallback) {
		Level unwrappedWorld = WrappedServerWorld.unwrap(world);
		FluidState fluidState = unwrappedWorld.getFluidState(pos);
		BlockState state = unwrappedWorld.getBlockState(pos);
		if (unwrappedWorld.random.nextFloat() < effectChance)
			unwrappedWorld.levelEvent(2001, pos, Block.getId(state));
		if (player != null) {
			if (!(usedTool.getItem() instanceof ItemModularHandheld))
				usedTool.mineBlock(unwrappedWorld, state, pos, player);
			breakBlock(world, player, usedTool, pos);
		} else {
			unwrappedWorld.setBlockAndUpdate(pos, fluidState.createLegacyBlock());
		}

		if (world instanceof DropSimulationWorld dropSimulationWorld)
			dropSimulationWorld.getItems().forEach(stack -> droppedItemCallback.accept(stack, state));
	}

	private static boolean breakBlock(Level world, @Nonnull Player player, ItemStack tool, BlockPos pos) {
		Level unwrappedWorld = WrappedServerWorld.unwrap(world);
		BlockState offsetState = unwrappedWorld.getBlockState(pos);

		if (playerCanDestroyBlock(player, offsetState, pos, tool)
			&& breakBlock(world, player, tool, pos, offsetState, true)) {

			EffectHelper.sendEventToPlayer((ServerPlayer) player, 2001, pos, Block.getId(offsetState));
			CastOptional.cast(tool.getItem(), ItemModularHandheld.class).ifPresent(itemHandheld -> itemHandheld.applyBreakEffects(tool, unwrappedWorld, offsetState, pos, player));
			return true;
		} else {
			return false;
		}
	}

	public static boolean breakBlock(Level world, Player breakingPlayer, ItemStack toolStack, BlockPos pos, BlockState blockState, boolean harvest) {
		Level unwrappedWorld = WrappedServerWorld.unwrap(world);
		if (!unwrappedWorld.isClientSide && unwrappedWorld instanceof ServerLevel serverWorld) {
			ServerPlayer serverPlayer = (ServerPlayer) breakingPlayer;
			GameType gameType = serverPlayer.gameMode.getGameModeForPlayer();
			int exp = ForgeHooks.onBlockBreakEvent(serverWorld, gameType, serverPlayer, pos);
			BlockEntity tileEntity = serverWorld.getBlockEntity(pos);
			if (exp == -1) {
				return false;
			} else {
				boolean canRemove = !toolStack.onBlockStartBreak(pos, breakingPlayer) && !breakingPlayer.blockActionRestricted(serverWorld, pos, gameType) && (!harvest || blockState.canHarvestBlock(serverWorld, pos, breakingPlayer)) && blockState.getBlock().onDestroyedByPlayer(blockState, serverWorld, pos, breakingPlayer, harvest, serverWorld.getFluidState(pos));
				if (canRemove) {
					blockState.getBlock().destroy(serverWorld, pos, blockState);
					if (harvest) {
						blockState.getBlock().playerDestroy(world, breakingPlayer, pos, blockState, tileEntity, toolStack);
						if (exp > 0) {
							blockState.getBlock().popExperience(serverWorld, pos, exp);
						}
					}
				}

				return canRemove;
			}
		} else {
			return blockState.getBlock().onDestroyedByPlayer(blockState, unwrappedWorld, pos, breakingPlayer, harvest, unwrappedWorld.getFluidState(pos));
		}
	}
}

package mod.noobulus.tetrapak.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolType;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.items.modular.ItemModularHandheld;
import se.mickelus.tetra.util.CastOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class BlockHelper {
	private BlockHelper() {
	}

	public static void destroyBlockAs(World world, BlockPos pos, @Nullable PlayerEntity player, ItemStack usedTool,
									  float effectChance, BiConsumer<ItemStack, BlockState> droppedItemCallback) {
		World unwrappedWorld = WrappedServerWorld.unwrap(world);
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

		if (world instanceof DropSimulationWorld)
			((DropSimulationWorld) world).getItems().forEach(stack -> droppedItemCallback.accept(stack, state));
	}

	private static boolean breakBlock(World world, @Nonnull PlayerEntity player, ItemStack tool, BlockPos pos) {
		World unwrappedWorld = WrappedServerWorld.unwrap(world);
		BlockState offsetState = unwrappedWorld.getBlockState(pos);
		ToolType effectiveTool = ItemModularHandheld.getEffectiveTool(offsetState);

		float blockHardness = offsetState.getDestroySpeed(unwrappedWorld, pos);
		int toolLevel = tool.getItem().getHarvestLevel(tool, effectiveTool, player, offsetState);
		if ((toolLevel >= 0 && toolLevel >= offsetState.getBlock().getHarvestLevel(offsetState) || tool.isCorrectToolForDrops(offsetState))
			&& blockHardness != -1.0F
			&& breakBlock(world, player, tool, pos, offsetState, true)) {

			EffectHelper.sendEventToPlayer((ServerPlayerEntity) player, 2001, pos, Block.getId(offsetState));
			CastOptional.cast(tool.getItem(), ItemModularHandheld.class).ifPresent(itemHandheld -> itemHandheld.applyBreakEffects(tool, unwrappedWorld, offsetState, pos, player));
			return true;
		} else {
			return false;
		}
	}

	public static boolean breakBlock(World world, PlayerEntity breakingPlayer, ItemStack toolStack, BlockPos pos, BlockState blockState, boolean harvest) {
		World unwrappedWorld = WrappedServerWorld.unwrap(world);
		if (!unwrappedWorld.isClientSide && unwrappedWorld instanceof ServerWorld) {
			ServerWorld serverWorld = (ServerWorld) unwrappedWorld;
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) breakingPlayer;
			GameType gameType = serverPlayer.gameMode.getGameModeForPlayer();
			int exp = ForgeHooks.onBlockBreakEvent(serverWorld, gameType, serverPlayer, pos);
			TileEntity tileEntity = serverWorld.getBlockEntity(pos);
			if (exp == -1) {
				return false;
			} else {
				boolean canRemove = !toolStack.onBlockStartBreak(pos, breakingPlayer) && !breakingPlayer.blockActionRestricted(serverWorld, pos, gameType) && (!harvest || blockState.canHarvestBlock(serverWorld, pos, breakingPlayer)) && blockState.getBlock().removedByPlayer(blockState, serverWorld, pos, breakingPlayer, harvest, serverWorld.getFluidState(pos));
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
			return blockState.getBlock().removedByPlayer(blockState, unwrappedWorld, pos, breakingPlayer, harvest, unwrappedWorld.getFluidState(pos));
		}
	}
}

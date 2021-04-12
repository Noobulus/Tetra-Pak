package mod.noobulus.tetrapak.create.refined_radiance;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class DirtCollapser {
	private DirtCollapser() {
	}

	public static BlockCollection findDirtCollumn(@Nullable IBlockReader world, BlockPos pos) {
		if (world == null)
			return BlockCollection.EMPTY;

		Set<BlockPos> visited = new HashSet<>();
		BlockState startState = world.getBlockState(pos);

		while (canBreakWithStart(startState, world.getBlockState(pos))) {
			visited.add(pos);
			pos = pos.up();
		}

		return new BlockCollection(visited);
	}

	public static boolean canBreakWithStart(BlockState start, BlockState test) {
		if (!ToolType.SHOVEL.equals(test.getHarvestTool()))
			return false;
		if (test.getBlock() instanceof FallingBlock)
			return true;
		if (start.getBlock().equals(test.getBlock()))
			return true;
		return Material.EARTH.equals(getBlockMaterial(start)) && (Material.EARTH.equals(getBlockMaterial(test)) || test.getBlock().equals(Blocks.GRASS_BLOCK));
	}

	@Nullable
	private static Material getBlockMaterial(BlockState state) {
		Object mat = ObfuscationReflectionHelper.getPrivateValue(AbstractBlock.class, state.getBlock(), "field_149764_J");
		return mat instanceof Material ? (Material) mat : null;
	}
}

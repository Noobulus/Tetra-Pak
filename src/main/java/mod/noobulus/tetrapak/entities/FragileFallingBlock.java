package mod.noobulus.tetrapak.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Collections;

public class FragileFallingBlock extends FallingBlockEntity {
	private final Collection<ItemStack> drops;

	public FragileFallingBlock(EntityType<? extends FallingBlockEntity> entityType, World world) {
		super(entityType, world);
		drops = Collections.emptyList();
	}

	public FragileFallingBlock(World world, BlockPos pos, BlockState state, Collection<ItemStack> drops) {
		super(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, state);
		this.drops = drops;
	}

	@Override
	public void tick() {
		BlockState fallTile = getBlockState();

		if (fallTile.isAir()) {
			this.dropItems();
			return;
		}

		if (!this.hasNoGravity()) {
			this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
		}

		this.move(MoverType.SELF, this.getMotion());
		if (!this.world.isRemote) {
			BlockPos blockpos1 = this.getBlockPos();
			boolean flag = fallTile.getBlock() instanceof ConcretePowderBlock;
			boolean flag1 = flag && this.world.getFluidState(blockpos1).isTagged(FluidTags.WATER);
			double d0 = this.getMotion().lengthSquared();
			if (flag && d0 > 1.0D) {
				BlockRayTraceResult blockraytraceresult = this.world.rayTraceBlocks(new RayTraceContext(new Vector3d(this.prevPosX, this.prevPosY, this.prevPosZ), this.getPositionVec(), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.SOURCE_ONLY, this));
				if (blockraytraceresult.getType() != RayTraceResult.Type.MISS && this.world.getFluidState(blockraytraceresult.getPos()).isTagged(FluidTags.WATER)) {
					blockpos1 = blockraytraceresult.getPos();
					flag1 = true;
				}
			}

			if (!this.onGround && !flag1) {
				if (!this.world.isRemote && (this.fallTime > 100 && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.fallTime > 600)) {
					if (this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
						dropItems();
					}
					this.remove();
				}
			} else {
				BlockState blockstate = this.world.getBlockState(blockpos1);
				this.setMotion(this.getMotion().mul(0.7D, -0.5D, 0.7D));
				if (!blockstate.isIn(Blocks.MOVING_PISTON)) {
					if (!this.dontSetBlock && this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
						dropItems();
					}
					this.remove();
				}
			}
		}

		this.setMotion(this.getMotion().scale(0.98D));
	}

	private void dropItems() {
		drops.stream()
			.map(stack -> new ItemEntity(world, getX() + .5, getY() + .5, getZ() + .5, stack))
			.forEach(world::addEntity);
		this.remove();
	}
}
